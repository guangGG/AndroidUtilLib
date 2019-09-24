package gapp.season.util.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import gapp.season.encryptlib.hash.HashUtil;

/**
 * 图片处理工具类
 */
public class ImgUtil {
    public static Bitmap getBitmap(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Drawable getDrawable(Bitmap bmp) {
        return new BitmapDrawable(bmp);
    }

    public static Bitmap getBitmap(String filePath) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
            fis.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String compressBitmap(@NonNull Bitmap bm, @NonNull File f) {
        if (f.exists()) {
            f.delete();
        }
        if (null != f.getParentFile() && !f.getParentFile().exists()) {
            f.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (baos.toByteArray().length > 1024 * 1024) {
                baos.reset();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, out);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    /**
     * 获取压缩后的小图片并保存到指定位置
     */
    public static String compressImage(String filePath, String targetPath, int quality) {
        Bitmap bm = getSmallBitmap(filePath);//获取一定尺寸的图片
        int degree = readPictureDegree(filePath);//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bm = rotateBitmap(bm, degree);
        }
        File outputFile = new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            } else {
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 获取照片旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 获取缩放比例
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 在不加载图片的前提下获得图片的宽高
     */
    public static Rect getImageWidthHeight(String imgPath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //最关键在此，把options.inJustDecodeBounds = true;
            //这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options); // 此时返回的bitmap为null
            //options.outHeight为原始图片的高
            return new Rect(0, 0, options.outWidth, options.outHeight);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //image的Exif信息

    private static Map<String, String> sExifNameMap;

    static {
        sExifNameMap = new HashMap<>();
        sExifNameMap.put("Orientation", "旋转类型");//详见下面的方法
        sExifNameMap.put("DateTime", "拍摄时间");
        sExifNameMap.put("Make", "设备厂商");
        sExifNameMap.put("Model", "设备型号");
        sExifNameMap.put("Flash", "闪光类型");//
        sExifNameMap.put("ImageWidth", "图片宽度");
        sExifNameMap.put("ImageLength", "图片高度");
        sExifNameMap.put("ExposureTime", "快门");
        sExifNameMap.put("ShutterSpeedValue", "快门速度");
        sExifNameMap.put("FNumber", "光圈");
        sExifNameMap.put("ApertureValue", "光圈值");
        sExifNameMap.put("MaxApertureValue", "最大光圈");
        sExifNameMap.put("ISOSpeedRatings", "ISO");
        sExifNameMap.put("WhiteBalance", "白平衡类型");//0自动、1手动
        sExifNameMap.put("FocalLength", "焦距");
        sExifNameMap.put("FocalLengthIn35mmFilm", "等效全幅焦距");
        sExifNameMap.put("ExposureProgram", "曝光程序");//Sutter Priority（快门优先）、Aperture Priority（快门优先）等等
        sExifNameMap.put("ExposureMode", "曝光模式");
        sExifNameMap.put("ExifVersion", "Exif版本");
        sExifNameMap.put("DateTimeOriginal", "创建时间");
        sExifNameMap.put("DateTimeDigitized", "数字化时间");
        sExifNameMap.put("ExposureBiasValue", "曝光补偿");
        sExifNameMap.put("MeteringMode", "测光方式");//平均式测光、中央重点测光、点测光等
        sExifNameMap.put("ColorSpace", "颜色空间");//sRGB
        sExifNameMap.put("ExifImageWidth", "横向像素");
        sExifNameMap.put("PixelXDimension", "横向像素");
        sExifNameMap.put("ExifImageLength", "纵向像素");
        sExifNameMap.put("PixelYDimension", "纵向像素");
        sExifNameMap.put("FileSource", "源文件");
        sExifNameMap.put("Compression", "压缩比");
        sExifNameMap.put("ComponentsConfiguration", "相机配置");//图像构造（多指色彩组合方案）
        sExifNameMap.put("CompressedBitsPerPixel", "压缩程度");//(BPP)压缩时每像素色彩位,指压缩程度
        sExifNameMap.put("Lightsource", "光源");//光源,指白平衡设置
        sExifNameMap.put("SceneType", "场景类型");
        sExifNameMap.put("SceneCaptureType", "拍摄场景");
        sExifNameMap.put("Software", "处理软件");//如相机内置软件、PS等
        sExifNameMap.put("SensingMethod", "");//传感器方法
        sExifNameMap.put("FlashpixVersion", "");//闪光类型版本
        sExifNameMap.put("BrightnessValue", "");//亮度
        //经纬度信息
        sExifNameMap.put("GPSLongitude", "经度");//度分秒格式：113/1,58/1,41255493/1000000
        sExifNameMap.put("GPSLongitudeRef", "经度标识");//E，W
        sExifNameMap.put("GPSLatitude", "纬度");//度分秒格式：22/1,31/1,15723266/1000000
        sExifNameMap.put("GPSLatitudeRef", "纬度标识");//N，S
        sExifNameMap.put("GPSAltitude", "海拔");//格式：5600/100
        sExifNameMap.put("GPSAltitudeRef", "海拔标识");//1负海拔，0正海拔
    }

    //Exif字段中文含义
    public static String getExifChineseName(String key) {
        if (!TextUtils.isEmpty(key)) {
            String keyStr = sExifNameMap.get(key);
            if (!TextUtils.isEmpty(keyStr))
                return keyStr;
        }
        return key;
    }

    //图片Orientation方向字段属性值含义
    public static String getExifOrientationInfo(String value) {
        try {
            int orientation = Integer.valueOf(value);
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    return "未旋转";
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    return "左右翻转";
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    return "上下翻转";
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    return "左上-右下翻转";
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    return "右上-左下翻转";
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return "旋转90°";
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return "旋转180°";
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return "旋转270°";
                case ExifInterface.ORIENTATION_UNDEFINED:
                    return "未设置";
                default:
                    return "未知";
            }
        } catch (Exception e) {
            return "";
        }
    }

    //图片光圈值含义
    public static String getExifApertureInfo(String aperture) {
        if (!TextUtils.isEmpty(aperture)) {
            return String.format("F/%s", aperture);
        }
        return "";
    }

    //图片快门值含义
    public static String getExifExposureTimeInfo(String exposureTime) {
        String kuaimen;
        try {
            float f = Float.valueOf(exposureTime);
            if (f >= 2) {
                kuaimen = String.format("%s″", Math.round(f));
            } else if (f <= 0.5) {
                kuaimen = String.format("(1/%s)″", Math.round(1 / f));
            } else {
                DecimalFormat df = new DecimalFormat("#.#");
                kuaimen = String.format("%s″", df.format(f));
            }
        } catch (Exception e) {
            kuaimen = exposureTime;
        }
        return kuaimen;
    }

    //图片画幅计算(传入焦距和等效35mm相机焦距)
    public static String getExifSensingSizeInfo(String fl, String fl_35) {
        if (!TextUtils.isEmpty(fl) && !TextUtils.isEmpty(fl_35)) {
            float flNum = getStrFloat(fl);
            float fl35Num = getStrFloat(fl_35);
            if (flNum != 0 && fl35Num != 0) {
                float f = flNum * flNum / fl35Num / fl35Num * 100;
                if (f >= 80) {
                    return Math.round(f) + "%";
                } else if (f >= 20) {
                    return new DecimalFormat("#.#").format(f) + "%";
                } else {
                    return new DecimalFormat("#.##").format(f) + "%";
                }
            }
        }
        return "";
    }

    //获取纬度，跳转到地图定位位置的Intent如下
	/*Uri uri = Uri.parse("geo:39.899533,116.036476"); //打开地图定位(顺序:纬度，经度)
	Intent it = new Intent(Intent.ACTION_VIEW, uri);
	startActivity(it);*/
    public static float getExifGPSLatitude(ExifInterface exifInfo) {
        float GPSLatitude = getGPSFloat(exifInfo.getAttribute("GPSLatitude"));
        if ("S".equalsIgnoreCase(exifInfo.getAttribute("GPSLatitudeRef"))) {
            return -1 * GPSLatitude;
        } else {
            return GPSLatitude;//北纬为正
        }
    }

    //获取经度
    public static float getExifGPSLongitude(ExifInterface exifInfo) {
        float GPSLongitude = getGPSFloat(exifInfo.getAttribute("GPSLongitude"));
        if ("W".equalsIgnoreCase(exifInfo.getAttribute("GPSLongitudeRef"))) {
            return -1 * GPSLongitude;
        } else {
            return GPSLongitude;//东经为正
        }
    }

    //获取海拔值
    public static float getExifGPSAltitude(ExifInterface exifInfo) {
        float GPSAltitude = getStrFloat(exifInfo.getAttribute("GPSAltitude"));
        float GPSAltitudeRef = getStrFloat(exifInfo.getAttribute("GPSAltitudeRef"));
        if (GPSAltitudeRef == 1) {
            return -1 * GPSAltitude;
        } else {
            return GPSAltitude;
        }
    }

    //"22/1,30/1,30000000/1000000"-->22.508333333°
    public static float getGPSFloat(String gpsStr) {
        if (!TextUtils.isEmpty(gpsStr)) {
            String[] ss = gpsStr.split(",");
            if (ss.length == 3) {
                float du = getStrFloat(ss[0]);
                float fen = getStrFloat(ss[1]);
                float miao = getStrFloat(ss[2]);
                return du + fen / 60 + miao / 3600;
            }
        }
        return 0;
    }

    //计算图片属性中数值格式字符串的实际值(比如"355/100"-->3.55)
    public static float getStrFloat(String numStr) {
        if (!TextUtils.isEmpty(numStr)) {
            try {
                if (numStr.contains("/")) { //such as:366/100
                    String[] ss = numStr.split("/");
                    if (ss.length == 2) {
                        return Float.valueOf(ss[0]) / Float.valueOf(ss[1]);
                    }
                } else { //27
                    return Float.valueOf(numStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    /**
     * bitmap转为base64，quality取0-100，format默认为jpg格式
     */
    public static String bitmapToBase64(Bitmap bitmap, int quality, @Nullable Bitmap.CompressFormat format) {
        String result = "";
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(format == null ? Bitmap.CompressFormat.JPEG : format, quality, baos);
                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    private static LruCache<String, Bitmap> sMemoryCache = null;
    private static long sCacheSize = 0;

    /**
     * base64转为bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        try {
            //移除前缀
            if (base64Data != null && base64Data.matches("data:.*;base64,.*")) {
                base64Data = base64Data.split("base64,")[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = HashUtil.md5(base64Data); //缓存的key
        if (TextUtils.isEmpty(key)) return null;
        if (sCacheSize == 0) {
            // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
            // LruCache通过构造函数传入缓存值，以KB为单位。
            long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
            // 使用最大可用内存值的1/8作为缓存的大小。
            sCacheSize = (maxMemory / 8);
        }

        if (sMemoryCache == null) {
            sMemoryCache = new LruCache<String, Bitmap>((int) sCacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量
                    return bitmap == null ? 0 : bitmap.getByteCount() / 1024;
                }
            };
        }

        Bitmap bitmap = null;
        byte[] imgByte;
        InputStream inputStream = null;
        try {
            bitmap = sMemoryCache.get(key);
            if (bitmap == null) {
                imgByte = Base64.decode(base64Data, Base64.DEFAULT);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 2;
                option.inTempStorage = new byte[5 * 1024 * 1024];
                inputStream = new ByteArrayInputStream(imgByte);
                SoftReference<Bitmap> softReference = new SoftReference<>(BitmapFactory.decodeStream(inputStream, null, option));
                bitmap = softReference.get();
                softReference.clear();
                if (bitmap != null) sMemoryCache.put(key, bitmap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                System.gc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
