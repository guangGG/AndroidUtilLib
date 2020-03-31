package gapp.season.util.file;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 工具类把支持的扩展名都放到一个静态的map里，在map里查找输入的文件的扩展名（作为map key)，得到相应的type（FILE_TYPE)
 */
public class FileTypeUtil {
    // 建立一个MIME类型与文件后缀名的匹配表
    private static final String[][] MIME_MapTable = MimeTypes.MIME_MapTable;

    // 后缀对应的文件类型
    private static final String apk_suffix = ".apk";
    private static final String pack_suffix = ".BT,.RAR,.zip";
    private static final String video_suffix = ".3gp,.avi,.flv,.m4v,.mkv,.mov,.mp4,.rm,.rmvb,.swf,.xv,.3g2,.asf,.ask,.c3d,.dat,.divx,.dvr-ms,.f4v,.flc,.fli,.flx,.m2p,.m2t,.m2ts,.m2v,.mlv,.mpe,.mpeg,.mpg,.mpv,.mts,.ogm,.qt,.ra,.tp,.trp,.ts,.uis,.uisx,.uvp,.vob,.vsp,.webm,.wmv,.wmvhd,.wtv,.xvid";
    private static final String music_suffix = ".APE,.flac,.mp3,.wav,.wma";
    private static final String document_suffix = ".txt,.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.html,.wps,.wpt,.xps,.xml";
    private static final String image_suffix = ".jpg,.jpeg,.png,.bmp,.gif,.ico,.raw,.pcx";
    private static final String bt_suffix = ".torrent";
    private static final HashMap<String, Integer> mTypeMap = new HashMap<>();

    public interface FILE_TYPE {
        int UN_KNOWN = 0;
        int CLASSIFY = 1;
        int VIDEO = 2;
        int MUSIC = 3;
        int DOC = 4;
        int IMAGE = 5;
        int TORRENT = 6;
        int APK = 7;
    }

    static {
        // init mTypeMap
        addToMap(apk_suffix, FILE_TYPE.APK);
        addToMap(pack_suffix, FILE_TYPE.CLASSIFY);
        addToMap(video_suffix, FILE_TYPE.VIDEO);
        addToMap(music_suffix, FILE_TYPE.MUSIC);
        addToMap(document_suffix, FILE_TYPE.DOC);
        addToMap(image_suffix, FILE_TYPE.IMAGE);
        addToMap(bt_suffix, FILE_TYPE.TORRENT);
    }

    private static void addToMap(String suffixCollection, int fileType) {
        String[] split = suffixCollection.split(",");
        for (int i = 0; i < split.length; i++) {
            mTypeMap.put(split[i].toLowerCase(), fileType);
        }
    }

    //获取文件的扩展名(文件名或文件路径均可)
    private static String getSuffix(String dirName) {
        if (dirName == null) {
            return null;
        }

        int lastIndexOfDot = dirName.lastIndexOf('.');
        if (lastIndexOfDot < 0) {
            return null;
        }

        return dirName.substring(lastIndexOfDot);
    }

    /**
     * 判断是否未知类型的文件
     */
    public static boolean isUnknownType(@Nullable String dirName) {
        if (TextUtils.isEmpty(dirName)) {
            return true;
        }

        String suffix = getSuffix(dirName);
        if (TextUtils.isEmpty(suffix)) {
            return true;
        }

        Integer ret = mTypeMap.get(suffix.toLowerCase());
        return ret == null;
    }

    /**
     * 通过扩展名判断文件是否是图片文件
     *
     * @param fileName 文件名或者路径都行
     */
    public static boolean isMediaFile(@Nullable String fileName) {
        int type = getFileType(fileName);
        return type == FILE_TYPE.IMAGE || type == FILE_TYPE.VIDEO || type == FILE_TYPE.MUSIC;
    }

    /**
     * @param fileName 文件名或者路径都行
     * @return 文件类型，FILE_TYPE
     */
    public static int getFileType(@Nullable String fileName) {
        try {
            if (TextUtils.isEmpty(fileName)) return FILE_TYPE.UN_KNOWN;
            String suffix = getSuffix(fileName);
            if (TextUtils.isEmpty(suffix)) return FILE_TYPE.UN_KNOWN;
            Integer fileType = mTypeMap.get(suffix.toLowerCase());
            return fileType == null ? FILE_TYPE.UN_KNOWN : fileType;
        } catch (NullPointerException ignore) {
            return FILE_TYPE.UN_KNOWN;
        }
    }

    /**
     * 根据文件名称/后缀名获得对应的MIME类型
     */
    public static String getMimeType(@Nullable String fileName) {
        String type = "*/*";
        String extension = FileUtil.getExtName(fileName);
        if (!TextUtils.isEmpty(extension)) {
            // 系统MimeTypeMap可获取一部分常用文件类型的MimeType
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (!TextUtils.isEmpty(mime)) {
                type = mime;
            } else {
                // 先根据文件类型获取MimeType,获取不到再从map中取
                switch (getFileType(fileName)) {
                    case FILE_TYPE.VIDEO:
                        type = "video/*";
                        break;
                    case FILE_TYPE.MUSIC:
                        type = "audio/*";
                        break;
                    case FILE_TYPE.DOC:
                        type = "text/*";
                        break;
                    case FILE_TYPE.IMAGE:
                        type = "image/*";
                        break;
                    case FILE_TYPE.APK:
                    case FILE_TYPE.CLASSIFY:
                    case FILE_TYPE.TORRENT:
                    default:
                        type = getMimeTypeFromMap(fileName);
                        break;
                }
            }
        }
        return type;
    }

    /**
     * 根据文件后缀名获得对应的MIME类型
     */
    public static String getMimeTypeFromMap(@Nullable String fileName) {
        String type = "*/*";
        if (fileName == null) return type;
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) return type;
        //获取文件的后缀名(带.)
        String end = fileName.substring(dotIndex).toLowerCase();
        if (TextUtils.isEmpty(end)) return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * 打开文件的Intent
     */
    public static Intent getOpenFileIntent(@NonNull File file) {
        Intent intent = new Intent();
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        // 获取文件file的MIME类型
        String type = getMimeType(file.getName());
        // 设置intent的data和Type属性
        intent.setDataAndType(Uri.fromFile(file), type);
        return intent;
    }

    /**
     * 获取可打开对应文件类型的所有ResolveInfo
     * packageName = ResolveInfo.activityInfo.packageName;
     * className = ResolveInfo.activityInfo.name;
     * labelName = ResolveInfo.loadLabel(packageManager).toString();// 比较耗时
     * icon = ResolveInfo.loadIcon(packageManager);// 比较耗时
     */
    public static List<ResolveInfo> getOpenFileResolveInfos(Context context, File file) {
        return context.getPackageManager().queryIntentActivities(getOpenFileIntent(file), 0);
    }

    /**
     * 用指定的打开方式打开文件
     */
    public static boolean openFileOfComponent(Context context, File file, String pkg, String cls) {
        try {
            Intent intent = getOpenFileIntent(file);
            intent.setComponent(new ComponentName(pkg, cls));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
