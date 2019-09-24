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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.List;

import gapp.season.encryptlib.code.HexUtil;
import gapp.season.encryptlib.hash.HashUtil;

public class FileUtil {
    public static final String UTF_8 = "UTF-8";
    public static final String GBK = "GBK";
    private static final int BUFFER_SIZE = 10240;

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

    /**
     * 读取文件文本内容(UTF-8编码)
     */
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

    /**
     * 获取文件名
     */
    public static String getFileName(String strFilePath) {
        if (TextUtils.isEmpty(strFilePath))
            return "";

        File f = new File(strFilePath);
        return f.getName();
    }

    /**
     * 获取文件父目录path
     */
    public static String getParentPath(String strFilePath) {
        if (TextUtils.isEmpty(strFilePath))
            return "";

        File f = new File(strFilePath);
        return f.getParent();
    }

    /**
     * 获取截断的文件名（spliterChar传'/'表示获取文件名）
     */
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

    /**
     * 获取后缀名（不带点）
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
     * 复制单个文件到指定文件位置
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

    /**
     * 批量复制文件到指定目录
     */
    public static boolean copyFiles(List<File> files, File dir) {
        if (files != null && dir != null && dir.isDirectory()) {
            for (File file : files) {
                // 检测文件夹是否是要复制的文件的子文件夹
                if (file.isDirectory() && isChildFile(file, dir)) {
                    return false;
                }
                // 检测是否在原文件夹中复制
                if (dir.getAbsolutePath().equals(file.getParent())) {
                    return false;
                }
            }
            // 复制文件
            boolean rst = true;
            for (File file : files) {
                File f = new File(dir, file.getName());
                if (file.isDirectory()) {
                    if (!f.mkdirs())
                        rst = false;
                    if (!copyDir(file, f))
                        rst = false;
                } else {
                    try {
                        if (!copyFile(file, f))
                            rst = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        rst = false;
                    }
                }
            }
            return rst;
        }
        return false;
    }

    /**
     * 复制文件夹中的文件到指定的文件夹中(耗时操作,需在子线程中进行)<br>
     * 要复制到的文件夹中已存在重名文件时时会覆盖重名的文件
     *
     * @param inDir  要复制的文件夹
     * @param outDir 目标文件夹
     * @return 复制成功返回true；文件夹不存在或复制失败返回false
     */
    private static boolean copyDir(File inDir, File outDir) {
        // 判断文件夹是否存在
        if (inDir == null || !inDir.exists() || !inDir.isDirectory()) {
            return false;
        }
        if (!outDir.exists()) {
            if (!outDir.mkdirs())
                return false;
        } else if (!outDir.isDirectory()) {
            return false;
        }
        // 方便记录层级结构文件名
        int rootdirlen = inDir.getAbsolutePath().length();
        try {
            // 复制文件（文件夹一一对应）
            copyDir(inDir, outDir, rootdirlen);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void copyDir(File inDir, File outDir, int rootdirlen) throws Exception {
        File[] files = inDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 创建对应级的文件夹
                    String name = file.getAbsolutePath().substring(rootdirlen);
                    File dir = new File(outDir, name);
                    if (!dir.exists()) {
                        if (!dir.mkdirs())
                            throw new IOException("can not make dir!");
                    }
                    // 复制文件夹下的文件
                    copyDir(file, outDir, rootdirlen);
                } else {
                    String name = file.getAbsolutePath().substring(rootdirlen);
                    File outFile = new File(outDir, name);
                    if (!copyFile(file, outFile))
                        throw new IOException("can not copy file!");
                }
            }
        }
    }

    private static boolean copyFile(File file, File newFile) throws IOException {
        if (file == null || !file.exists())
            return false;

        InputStream in = new FileInputStream(file);
        OutputStream out = new FileOutputStream(newFile);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        return true;
    }

    private static boolean isChildFile(File dir, File file) {
        if (dir != null && dir.isDirectory() && file != null) {
            // 这里简单判断绝对路径
            if (file.getAbsolutePath().contains(dir.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建文件，如果文件目录不存在则创建目录
     */
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

    /**
     * 删除单个文件
     */
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

    /**
     * 删除文件或目录
     */
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

    /**
     * 判断文件的编码"UTF-8"还是"GBK"，准确率不是非常高（原理是判断文件中汉字是否会乱码）
     *
     * @return 文件不存在返回null，无法判断则默认返回"UTF-8"
     */
    public static String getFileCharset(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        // 常见编码{"UTF-8", "GBK", "UNICODE", "BIG5","ISO-8859-1", "ASCII" }
		/* unicode编码范围：
			汉字：[0x4e00,0x9fa5](或十进制[19968,40869])
			数字：[0x30,0x39](或十进制[48, 57])
			小写字母：[0x61,0x7a](或十进制[97, 122])
			大写字母：[0x41,0x5a](或十进制[65, 90])*/
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            // 通过检查前2k个字节判断编码
            int length = fis.read(buffer);
            fis.close();
            if (length == -1) {
                // 文件内容为空，返回默认编码
                return UTF_8;
            }
            // 判断是否UTF-8编码(非UTF-8编码的汉字用UTF-8读取会出现未知字符unicode码:65533)
            String text = new String(buffer, 0, length, UTF_8);
            boolean isU8 = true;
            int chineseWord = 0, unUTF8Word = 0;
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (65533 == c) {
                    unUTF8Word++;
                    if (unUTF8Word >= 5) {
                        // UTF8不能识别的字符不少于5个，则断定文件不是UTF8编码
                        isU8 = false;
                        break;
                    }
                } else if (c > 19967 && c < 40870) {
                    chineseWord++;
                }
            }
            if (isU8 && unUTF8Word > 0) {
                // UTF8不能识别的字符占可识别汉字字符的20%以上,则断定文件不是UTF8编码
                if (chineseWord == 0) {
                    isU8 = false;
                } else {
                    float uPer = ((float) unUTF8Word) / chineseWord;
                    if (uPer >= 0.2) {
                        isU8 = false;
                    }
                }
            }
            if (isU8) {
                return UTF_8;
            } else {
                return GBK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return UTF_8;
    }

    /**
     * 计算文件的FID校验码(前、中、后各取一段进行sha1校验)
     */
    public static String getFileFid(File file) {
        if (file == null)
            return null;

        try {
            if (file.length() <= BUFFER_SIZE * 3) {
                return HashUtil.encode(new FileInputStream(file), HashUtil.ALGORITHM_SHA_1);
            } else {
                MessageDigest messageDigest = MessageDigest.getInstance(HashUtil.ALGORITHM_SHA_1);
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

                // 每次读取一段
                byte[] buffer = new byte[BUFFER_SIZE];
                //前段
                long position = 0;
                updateDigest(messageDigest, randomAccessFile, buffer, position);
                //中间段
                position = file.length() / 2 - BUFFER_SIZE / 2;
                updateDigest(messageDigest, randomAccessFile, buffer, position);
                //后段
                position = file.length() - BUFFER_SIZE;
                updateDigest(messageDigest, randomAccessFile, buffer, position);

                randomAccessFile.close();
                byte[] digest = messageDigest.digest();
                return HexUtil.toHexStr(digest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void updateDigest(MessageDigest messageDigest, RandomAccessFile randomAccessFile, byte[] buffer, long position) throws IOException {
        randomAccessFile.seek(position);
        int length = randomAccessFile.read(buffer);
        messageDigest.update(buffer, 0, length);
    }
}
