package gapp.season.util.file;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.webkit.MimeTypeMap;

import java.util.List;

/**
 * 媒体文件更新(增删改)后，通知系统媒体库更新的方法
 * 在小于api-19(4.4)的版本可以通过发广播方式通知媒体库更新：
 * ----sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)))
 * 添加了媒体文件时可以通过发下面的广播方式通知媒体库更新，删除文件无效，该广播在api-29废弃：
 * ----sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
 * 通用做法是通过MediaScannerConnection更新媒体库：
 * ----应用启动时创建全局的MediaScannerConnection并连接，
 */
public class MediaScanUtil {
    private static MediaScannerConnection sConnection = null;

    public static void init(Context context) {
        sConnection = new MediaScannerConnection(context, null);
        sConnection.connect(); //连接过程中使用scanFile(String)方法无效
    }

    /**
     * 使用全局的MediaScannerConnection扫描媒体文件
     */
    public static void scanFile(String filePath) {
        if (sConnection != null && sConnection.isConnected()) {
            sConnection.scanFile(filePath, getMimeType(filePath));
        }
    }

    /**
     * 使用新建的MediaScannerConnection批量扫描媒体文件，带回调(使用系统方法)
     */
    public static void scanFile(Context context, List<String> filePaths, MediaScannerConnection.OnScanCompletedListener callback) {
        if (filePaths != null) {
            String[] paths = new String[filePaths.size()];
            String[] mimeTypes = new String[filePaths.size()];
            for (int i = 0; i < filePaths.size(); i++) {
                paths[i] = filePaths.get(i);
                mimeTypes[i] = getMimeType(filePaths.get(i));
            }
            MediaScannerConnection.scanFile(context, paths, mimeTypes, callback);
        }
    }

    private static String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
