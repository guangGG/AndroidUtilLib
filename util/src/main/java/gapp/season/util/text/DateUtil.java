package gapp.season.util.text;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final String PATTERN_DATE_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化显示日期，常用pattern："yyyy-MM-dd HH:mm:ss"
     */
    public static String getDateStr(long timestamp, String pattern) {
        return getDateStr(new Date(timestamp), pattern);
    }

    /**
     * 格式化显示日期，常用pattern："yyyy-MM-dd HH:mm:ss"
     */
    public static String getDateStr(Date date, String pattern) {
        if (TextUtils.isEmpty(pattern)) pattern = PATTERN_DATE_DEFAULT;
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return df.format(date);
    }
}
