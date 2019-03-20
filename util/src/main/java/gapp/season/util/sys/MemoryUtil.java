package gapp.season.util.sys;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 存储相关的辅助类
 */
public class MemoryUtil {
    private MemoryUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取系统存储路径
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取SD卡的剩余容量,单位byte
     */
    public static long getSDFreeSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取单个数据块的大小（byte）
            long blockSize = stat.getBlockSize();
            // 获取空闲的数据块的数量(内部保留4个数据块)
            long availableBlocks = stat.getAvailableBlocks() - 4;
            // 返回SD卡空闲大小
            return blockSize * availableBlocks;
        }
        return 0;
    }

    /**
     * 获得SD卡总容量,单位byte
     */
    public static long getSDTotalSize() {
        if (isSDCardEnable()) {
            StatFs sf = new StatFs(getSDCardPath());
            // 获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSize();
            // 获取所有数据块数
            long allBlocks = sf.getBlockCount();
            // 返回SD卡大小
            return allBlocks * blockSize;
        }
        return 0;
    }

    /**
     * 获取手机内部剩余存储空间(包括内部SD卡)
     */
    public static long getInternalMemoryAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks() - 4;
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间(包括内部SD卡)
     */
    public static long getInternalMemoryTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机内置存储剩余存储空间(系统文件存储空间)
     */
    public static long getInternalSystemMemoryAvailableSize() {
        File path = Environment.getRootDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks() - 4;
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内置存储总的存储空间(系统文件存储空间)
     */
    public static long getInternalSystemMemoryTotalSize() {
        File path = Environment.getRootDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取指定分区的剩余可用容量字节数，单位byte
     */
    static long getFreeBytes(String filePath) {
        if (filePath.startsWith(getSDCardPath())) {
            // 如果是sd卡的下的路径，则获取sd卡可用容量
            filePath = getSDCardPath();
        } else {
            // 如果是内部存储的路径，则获取内存存储的可用容量(有些手机的SD卡会内置在内部存储空间中)
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取总内存大小(Api16以上)
     *
     * @return 无法获取返回0
     */
    public static long getTotalMem(Context context) {
        MemoryInfo memUsableInfo = new MemoryInfo();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(memUsableInfo);
        long totalMem = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Api16以上可直接获取总内存大小
            totalMem = memUsableInfo.totalMem;
        }
        return totalMem;
    }

    /**
     * 获取可用内存大小
     */
    public static long getAvailMem(Context context) {
        MemoryInfo memUsableInfo = new MemoryInfo();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(memUsableInfo);
        long availMem = memUsableInfo.availMem;
        return availMem;
    }

    /**
     * 格式化存储空间大小显示，保留digits位小数
     */
    public static String formatMemorySize(long memorySize, int digits) {
        if (memorySize < 1024) {
            return memorySize + "B";
        } else if (memorySize < 1048576) {
            return formatNumberDigits(((float) memorySize) / 1024, digits) + "KB";
        } else if (memorySize < 1073741824) {
            return formatNumberDigits((float) memorySize / 1048576, digits) + "MB";
        } else {
            return formatNumberDigits((float) memorySize / 1073741824, digits) + "GB";
        }
    }

    /**
     * 格式化一个小数，小数点后保留digits位小数
     */
    private static String formatNumberDigits(float number, int digits) {
        if (digits <= 0) {
            return "" + Math.round(number);
        }
        StringBuilder sb = new StringBuilder("0.");
        for (int i = 0; i < digits; i++) {
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(number);
    }

    /**
     * 将数字10000000转为10,000,000格式字符串
     */
    public static String FormatLongNum(long num) {
        String string = String.valueOf(num);
        int length = string.length();
        int n = (length - 1) / 3;
        int m = length % 3;
        m = m == 0 ? 3 : m;
        StringBuilder sb = new StringBuilder(string);
        for (int i = 0; i < n; i++) {
            sb.insert(m + i * 4, ',');
        }
        return sb.toString();
    }

    /**
     * 将10,000,000格式字符串转为数字10000000
     */
    public static long FormatLongNum(String num) {
        try {
            String string = num.replaceAll(",", "");
            return Long.parseLong(string.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
