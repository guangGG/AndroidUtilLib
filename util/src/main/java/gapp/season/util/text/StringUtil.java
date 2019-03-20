package gapp.season.util.text;

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 按照指定的编码表比较两个字符串的顺序
     *
     * @param charsetName 编码：GBK、UTF-8、Unicode
     * @return 正数表示str1>str2,负数表示str1<str2,0表示str1=str2
     * @throws UnsupportedEncodingException
     */
    public static int compare(String str1, String str2, String charsetName) throws UnsupportedEncodingException {
        byte[] b1 = str1.getBytes(charsetName);
        byte[] b2 = str2.getBytes(charsetName);
        int l1 = b1.length;
        int l2 = b2.length;
        int l = Math.min(l1, l2);
        int k = 0;
        while (k < l) {
            byte bt1 = b1[k];
            byte bt2 = b2[k];
            if (bt1 != bt2) {
                // 把byte中的负数转成正数(中文在英文后面)
                int i1 = bt1 >= 0 ? bt1 : bt1 + 256;
                int i2 = bt2 >= 0 ? bt2 : bt2 + 256;
                return i1 - i2;
            }
            k++;
        }
        return l1 - l2;
    }

    /**
     * 计算字符串中的汉字数
     */
    public static int countChineseWord(String string) {
        char[] str = string.toCharArray();
        int count = 0;
        for (int i = 0; i < str.length; i++) {
            if (str[i] > 19967 && str[i] < 40870) {
                count++;
            } else if (str[i] == 12295) {
                // 字符"〇"
                count++;
            }
        }
        return count;
    }

    /**
     * 字符串操作：将一个字符串中除\n以外的空格符（编码小于等于32的符号）全去除
     */
    public static String removeNeedlessBlank(String string) {
        char[] str = string.toCharArray();
        List<Character> list = new LinkedList<Character>();
        for (int i = 0; i < str.length; i++) {
            if (str[i] == 10 || str[i] > ' ') {
                list.add(str[i]);
            }
        }
        char[] newstr = new char[list.size()];
        for (int i = 0; i < newstr.length; i++) {
            newstr[i] = list.get(i);
        }
        return new String(newstr);
    }

    /**
     * xml文件中须转义的字符：<br>
     * &(逻辑与) &amp; <(小于) &lt; >(大于) &gt; "(双引号) &quot; '(单引号) &apos;
     */
    public static String escapeXmlCharacter(String string) {
        string = string.replaceAll("&", "&amp;");
        string = string.replaceAll("<", "&lt;");
        string = string.replaceAll(">", "&gt;");
        string = string.replaceAll("\"", "&quot;");
        string = string.replaceAll("\'", "&apos;");
        return string;
    }

    /**
     * 生成格式化显示文本
     *
     * @param color    颜色值
     * @param listener 点击事件
     */
    public static SpannableString toSpannableString(String string, int start, int end, final int color, final View.OnClickListener listener) {
        SpannableString ss = new SpannableString(string);
        ss.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(color); // 颜色
            }

            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    listener.onClick(widget);
                }
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 将字符串转为可点击匹配内容的SpannableString
     *
     * @param string   文本内容
     * @param pattern  正则表达式规则，为null时默认为适配网址链接
     * @param listener 点击高亮显示的文本的回调
     */
    @SuppressLint("NewApi")
    public static SpannableString toClickableSpannableString(String string, Pattern pattern, final View.OnClickListener listener) {
        if (pattern == null) {
            pattern = Patterns.WEB_URL;
        }
        Matcher m = pattern.matcher(string);
        SpannableString ss = new SpannableString(string);
        while (m.find()) {
            final String str = m.group();
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    widget.setTag(str);
                    listener.onClick(widget);
                }
            }, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /**
     * 金额大写转换
     *
     * @param number 金额
     * @param mode   模式：1:小写(十百千);默认:大写(拾佰仟)
     */
    public static String toCNBigWrite(long number, int mode) {
        String[] CN_BIG_WRITE;
        String[] CN_UNIT;
        if (mode == 1) {
            CN_BIG_WRITE = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
            CN_UNIT = new String[]{"", "十", "百", "千", "万", "亿", "兆", "京", "垓", "杼", "穰", "沟", "涧", "正", "载", "极"};
        } else {
            CN_BIG_WRITE = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
            CN_UNIT = new String[]{"", "拾", "佰", "仟", "万", "亿", "兆", "京", "垓", "杼", "穰", "沟", "涧", "正", "载", "极"};
        }
        String bigWrite = "";
        String numStr = String.valueOf(number);
        int numberLength = numStr.length();
        // 4个数字变化一个单位
        int bitLength = (numberLength / 4) + (numberLength % 4 == 0 ? 0 : 1);
        // 最左边的位数
        int leftLength = numberLength - (bitLength - 1) * 4;
        // 大写字母
        for (int i = 0; i < bitLength; i++) {
            // 最左边数字
            String leftValue = numStr.substring(0, leftLength);
            // 取每个数字变化成大写，并加上单位(千 佰 十)
            for (int j = 0; j < leftValue.length(); j++) {
                // 数字
                String value = leftValue.substring(j, j + 1);
                // 大写
                String BW = CN_BIG_WRITE[Integer.valueOf(value)];
                // 单位
                String UT = CN_UNIT[leftValue.length() - (j + 1)];
                // 如果数字不是0
                if (!"零".equals(BW)) {
                    bigWrite += BW;
                    bigWrite += UT;
                } else {
                    // 如果数字是0，并且结尾不是0和单位十,则加上零
                    if (!bigWrite.endsWith("零") && !bigWrite.endsWith("十")) {
                        bigWrite += BW;
                    }
                }
            }
            // 单位 万 亿 兆
            String UT = bitLength - i + 2 > 3 ? CN_UNIT[bitLength - i + 2] : "";
            if (bigWrite.endsWith("零")) {// 如果以0结尾，则要去掉0
                bigWrite = bigWrite.substring(0, bigWrite.length() - 1);
            }
            bigWrite += UT;
            numStr = numStr.substring(leftLength);
            leftLength = 4;
        }
        if ("".equals(bigWrite)) {
            bigWrite += "零";
        }
        // 判断开始文本的“一十”改为“十”(如:一十三万 改为 十三万)
        if (bigWrite.startsWith(CN_BIG_WRITE[1] + CN_UNIT[1])) {
            bigWrite = bigWrite.replaceFirst(CN_BIG_WRITE[1], "");
        }
        return bigWrite;
    }

    /**
     * 替换字符串中的指定字符串
     */
    public static String replaceStr(String text, String str, String newStr) {
        if (text == null || str == null || newStr == null) {
            return text;
        }
        int l1 = text.length();
        int l2 = str.length();
        if (l1 < l2) {
            return text;
        } else {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (; i < l1 - l2 + 1; i++) {
                String subStr = text.substring(i, i + l2);
                if (str.equals(subStr)) {
                    sb.append(newStr);
                    i = i + l2 - 1;
                } else {
                    sb.append(text.charAt(i));
                }
            }
            for (; i < l1; i++) {
                sb.append(text.charAt(i));
            }
            return sb.toString();
        }
    }

    /**
     * 替换字符串中的指定字符
     */
    public static String replaceStr(String text, char c, String newStr) {
        if (text == null || newStr == null) {
            return text;
        }
        char[] array = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (c == array[i]) {
                sb.append(newStr);
            } else {
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 格式化毫秒为时长字符串
     */
    public static String getDurationString(long duration) {
        duration = duration / 1000;
        StringBuilder sb = new StringBuilder();
        long s;
        String f, m;
        s = duration / 3600;
        if (s > 0) {
            sb.append(s).append(":");
        }
        f = String.valueOf((duration % 3600) / 60);
        if (f.length() == 1) {
            sb.append("0");
        }
        sb.append(f).append(":");
        m = String.valueOf((duration % 3600) % 60);
        if (m.length() == 1) {
            sb.append("0");
        }
        sb.append(m);
        return sb.toString();
    }
}
