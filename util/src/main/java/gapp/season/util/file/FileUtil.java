package gapp.season.util.file;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
    /**
     * 将文件读入到字节数组中(文件过大时可能重新内存溢出)
     */
    public static byte[] readFileBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return readInputStream(fis);
    }

    /**
     * 从输入流中读取数据
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        inputStream.close();
        return data;
    }

    /**
     * 将输入流保存到文件中
     */
    public static void saveToFile(File file, InputStream inputStream) throws IOException {
        createFile(file);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
            bos.flush();
        }
        inputStream.close();
        bos.close();
    }

    /**
     * 将字节数组保存到文件中
     */
    public static void saveToFile(File file, byte[] bs) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(bs);
        bos.close();
    }

    /**
     * 读取文件的文本内容
     */
    public static String getFileContent(File file, String charset) throws IOException {
        if (!file.exists()) {
            return null;
        }
        InputStreamReader isr = null;
        StringBuffer sb = new StringBuffer();
        FileInputStream fis = new FileInputStream(file);
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        isr = new InputStreamReader(fis, charset);
        char[] buffer = new char[1024];
        int length;
        while ((length = isr.read(buffer)) != -1) {
            sb.append(buffer, 0, length);
        }
        isr.close();
        return sb.toString();
    }

    /**
     * 保存文本到文件
     */
    public static void saveToFile(File file, String content, String charset) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), charset);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    public static String readFile(File file) {
        String result = null;
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            try {
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                br.close();
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public static String getFileName(String strFilePath) {
        if (TextUtils.isEmpty(strFilePath))
            return "";

        File f = new File(strFilePath);
        return f.getName();
    }

    public static String getParentPath(String strFilePath) {
        if (TextUtils.isEmpty(strFilePath))
            return "";

        File f = new File(strFilePath);
        return f.getParent().toString();
    }

    public static String getFileName(String strFilePath, char spliterChar) {
        if (TextUtils.isEmpty(strFilePath))
            return "";

        String strFileName = strFilePath;
        int lastIndex = strFilePath.lastIndexOf(spliterChar);
        if (-1 != lastIndex) {
            strFileName = strFilePath.substring(lastIndex + 1);
        }
        return strFileName;
    }

    public static String getFileExtendName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return "unknown";
        } else {
            return fileName.substring(index + 1);
        }
    }

    /**
     * 获取后缀名
     */
    public static String getExtName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 获取不带后缀的文件名
     */
    public static String getFileNameWithoutExtName(String pathName) {
        String fileName = getFileName(pathName);
        if (fileName != null && fileName.length() > 0) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > 0) && (dot < (fileName.length() - 1))) {
                return fileName.substring(0, dot);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 从路径中提取文件名
     */
    public static String extractFileNameFromPath(String filePath) {
        int start = filePath.lastIndexOf("/");
        if (-1 == start) {
            return filePath;
        } else {
            return filePath.substring(start + 1);
        }
    }

    /**
     * 把多余的"/"去掉(系统File类的方法)
     * (如："///a//b/c/"-->"/a/b/c")
     */
    public static String fixSlashes(String origPath) {
        if (origPath == null) {
            return null;
        }
        // Remove duplicate adjacent slashes.
        boolean lastWasSlash = false;
        char[] newPath = origPath.toCharArray();
        int length = newPath.length;
        int newLength = 0;
        for (int i = 0; i < length; ++i) {
            char ch = newPath[i];
            if (ch == '/') {
                if (!lastWasSlash) {
                    newPath[newLength++] = '/';
                    lastWasSlash = true;
                }
            } else {
                newPath[newLength++] = ch;
                lastWasSlash = false;
            }
        }
        // Remove any trailing slash (unless this is the root of the file system).
        if (lastWasSlash && newLength > 1) {
            newLength--;
        }
        // Reuse the original string if possible.
        return (newLength != length) ? new String(newPath, 0, newLength) : origPath;
    }

    /**
     * 复制待上传的文件到待上传文件的文件夹
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        if (TextUtils.isEmpty(sourcePath) || TextUtils.isEmpty(targetPath)) {
            return false;
        }
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        if (!sourceFile.exists()) {
            return false;
        }
        if (!sourceFile.canRead()) {
            return false;
        }
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        if (targetFile.exists()) {
            targetFile.delete();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourcePath);
            fos = new FileOutputStream(targetPath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean createFile(File file) {
        if (file != null && !file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    return false;
                }
            }
            try {
                boolean create = file.createNewFile();
                if (create) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean deleteFile(String path) {
        if (null == path) {
            return false;
        }
        boolean ret = false;

        File file = new File(path);
        if (file.exists()) {
            ret = file.delete();
        }
        return ret;
    }

    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    boolean success = deleteFile(f);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}
