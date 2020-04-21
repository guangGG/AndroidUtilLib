package gapp.season.util.net;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import gapp.season.util.task.ThreadPoolExecutor;

/**
 * 简单Http请求的工具类(使用HttpURLConnection)
 */
public class HttpSimpleUtil {
    private static final int TIMEOUT_IN_MILLIONS = 30000;

    public interface CallBack {
        /**
         * 请求成功result为获取到的数据流，请求失败result为null
         * 回调方法在HTTP请求的线程中运行，不能直接在方法中更新UI
         *
         * @param result       返回值
         * @param responseCode HTTP请求返回码(200为请求成功)
         */
        void onRequestComplete(InputStream result, int responseCode);
    }

    public static abstract class StringCallBack implements CallBack {
        private String charsetName;

        public StringCallBack() {
            charsetName = "UTF-8";
        }

        public StringCallBack(String charset) {
            charsetName = charset;
        }

        @Override
        public void onRequestComplete(InputStream result, final int responseCode) {
            Handler handler = new Handler(Looper.getMainLooper());
            try {
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(result, charsetName));
                    final StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        sb.append(System.getProperty("line.separator"));
                    }
                    in.close();
                    final String data = sb.toString();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(data);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onRequestFailed(responseCode);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onRequestFailed(-1);
                    }
                });
            }
        }

        /**
         * 请求成功result为获取到的数据
         * 回调方法在主线程中运行
         */
        public abstract void onRequestSuccess(String result);

        /**
         * 请求失败(HTTP请求返回码不为200)
         * 回调方法在主线程中运行
         *
         * @param errorCode HTTP错误码，-1表示解析失败
         */
        public abstract void onRequestFailed(int errorCode);
    }

    /**
     * 异步的Get请求
     */
    public static void doGetAsync(final String url, final Map<String, String> headers, final CallBack callBack) {
        ThreadPoolExecutor.getInstance().execute(new Runnable() {
            public void run() {
                doGetSync(url, headers, callBack);
            }
        });
    }

    /**
     * Get请求(耗时操作，需要子线程进行)
     */
    public static void doGetSync(String url, Map<String, String> headers, CallBack callBack) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", getUserAgent());
            if (headers != null) {
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();
            }
            if (callBack != null) {
                callBack.onRequestComplete(is, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 异常情况返回-1
            if (callBack != null) {
                callBack.onRequestComplete(null, -1);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步的Post请求
     *
     * @param body 请求body，默认是 key=value&key=value 的形式
     */
    public static void doPostAsync(final String url, final Map<String, String> headers, final String body, final CallBack callBack) {
        ThreadPoolExecutor.getInstance().execute(new Runnable() {
            public void run() {
                doPostSync(url, headers, body, callBack);
            }
        });
    }

    /**
     * Post请求(耗时操作，需要子线程进行)
     *
     * @param body 请求body，默认是 key=value&key=value 的形式
     */
    public static void doPostSync(String url, Map<String, String> headers, String body, CallBack callBack) {
        PrintWriter out = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("POST");
            // 设置通用的请求Header
            conn.setRequestProperty("User-Agent", getUserAgent());
            //json格式： Content-Type: application/json; charset=utf-8 (这里忽略：Content-Length: num)
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            if (headers != null) {
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }
            conn.setUseCaches(false);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            if (body != null && !body.trim().equals("")) {
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(conn.getOutputStream());
                // 发送请求参数
                out.print(body);
                // flush输出流的缓冲
                out.flush();
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();
            }
            if (callBack != null) {
                callBack.onRequestComplete(is, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 异常情况返回-1
            if (callBack != null) {
                callBack.onRequestComplete(null, -1);
            }
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (is != null) {
                    is.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * User-Agent:
     * Mozilla/5.0 (Linux; U; Android 5.0.2; zh-cn; Redmi Note 3 Build/LRX22G)
     * AppleWebKit/537.36 (KHTML, like Gecko) Version/5.0 Mobile Safari/537.36
     */
    public static String getUserAgent() {
        String webUserAgent = "Mozilla/5.0 (Linux; U; Android %s)";
        StringBuffer buffer = new StringBuffer();
        // add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append("1.0");
        }
        buffer.append("; ");
        // add language
        Locale locale = Locale.getDefault();
        final String language = locale.getLanguage();
        if (language != null) {
            buffer.append(language.toLowerCase(locale));
            final String country = locale.getCountry();
            if (!TextUtils.isEmpty(country)) {
                buffer.append("-");
                buffer.append(country.toLowerCase(locale));
            }
        } else {
            // default to "en"
            buffer.append("en");
        }
        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (model.length() > 0) {
                buffer.append("; ");
                buffer.append(model);
            }
        }
        final String id = Build.ID;
        if (id.length() > 0) {
            buffer.append(" Build/");
            buffer.append(id);
        }
        return String.format(webUserAgent, buffer);
    }
}
