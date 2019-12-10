package gapp.season.util.text;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalUtil {
    /**
     * 元转分
     */
    public static long changeY2F(double price) {
        return Math.round(price * 100);
    }

    /**
     * 分转元
     */
    public static String changeF2Y(long price) {
        return BigDecimal.valueOf(price).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    /**
     * 用户输入的金额转为分
     */
    public static long getFenByAmount(String amount) {
        int fen = 0;
        try {
            if (!TextUtils.isEmpty(amount)) {
                BigDecimal bd = new BigDecimal(amount);
                BigDecimal fenBd = bd.multiply(new BigDecimal(100));
                return fenBd.longValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fen;
    }

    /**
     * 格式化显示数字
     */
    public static String formatNum(double num, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);
    }

    /**
     * 格式化数字显示(四舍五入，保留digit位小数，不显示小数点后末尾的零)
     */
    public static String formatNum(double num, int digit) {
        StringBuilder pattern = new StringBuilder("0");
        if (digit > 0) {
            pattern = new StringBuilder("0.");
            for (int i = 0; i < digit; i++) {
                pattern.append("#");
            }
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(num);
    }

    /**
     * 将数据保留n位小数(四舍五入，截取末位小数，不去掉小数点后末尾的0)
     *
     * @param v     需要计算的数字
     * @param scale 小数点后保留几位
     */
    public static String formatNumIncludeZero(double v, int scale) {
        StringBuilder pattern = new StringBuilder(scale > 0 ? "#0." : "#0");
        for (int i = 0; i < scale; i++) {
            pattern.append("0");
        }
        DecimalFormat dFormat = new DecimalFormat(pattern.toString());
        return dFormat.format(v);
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v, int scale) {
        return roundToBD(v, scale).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理(不去掉小数点后末尾的0)
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static String roundStr(double v, int scale) {
        return roundToBD(v, scale).toPlainString();
    }

    private static BigDecimal roundToBD(double v, int scale) {
        if (scale < 0) scale = 0;
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将数据保留n位小数(截取末位小数)
     *
     * @param v     需要计算的数字
     * @param scale 小数点后保留几位
     */
    public static double floor(double v, int scale) {
        return floorToBD(v, scale).doubleValue();
    }

    /**
     * 将数据保留n位小数(截取末位小数，不去掉小数点后末尾的0)
     *
     * @param v     需要计算的数字
     * @param scale 小数点后保留几位
     */
    public static String floorStr(double v, int scale) {
        return floorToBD(v, scale).toPlainString();
    }

    private static BigDecimal floorToBD(double v, int scale) {
        if (scale < 0) scale = 0;
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_DOWN);
    }

    /**
     * 数值转成指定的精度(去除小数点后末尾的0)
     *
     * @param bigDecimal 数值
     * @param precision  精度
     */
    public static BigDecimal formatBigDecimal(BigDecimal bigDecimal, int precision) {
        try {
            if (bigDecimal != null && bigDecimal.doubleValue() != 0) {
                return bigDecimal.divide(new BigDecimal("1"), precision,
                        BigDecimal.ROUND_DOWN).stripTrailingZeros();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
/*
DecimalFormat用法介绍：
//例如输入  5211314
// 可以使用无参数/有参数构造(参数传字符串pattern，有参构造方法同使用applyPattern方法)
DecimalFormat df = new DecimalFormat();
// 例如"0.0"，"0.0¤"，"0.0%"，"0.##"，0表示必显示位数，#表示可显示位数，¤表示货币符号占位符，%表示显示数字的百分比形式
df.applyPattern("0.0#%");
// 设置国家货币符号，构造参数需添加‘¤’符号(和setXxxSuffix有冲突)，参考--> 5211314￥
df.setCurrency(Currency.getInstance("CNY"));
// 设置最多/最少保留几位(相当于0.0#).参考--> 5211314.0
df.setMaximumFractionDigits(2);
df.setMinimumFractionDigits(1);
// 设置分组大小(用逗号分隔).参考--> 5,211,314
df.setGroupingUsed(true);
df.setGroupingSize(3);
// 设置乘以的倍数.参考--> 521131400
df.setMultiplier(100);
// 设置正数前缀,参考--> @5211314
df.setPositivePrefix("@");
// 设置正数后缀 但是替换掉 已有字符 参考--> 5211314@
df.setPositiveSuffix("@");
// 设置负数前缀,只对负数有效   参考-->~-1
df.setNegativePrefix("~");
// 设置负数后缀 但是替换掉 已有字符  只对负数有效  参考--> -1~
df.setNegativeSuffix("~");
// 设置四舍五入的模式 详见 RoundingMode 类
df.setRoundingMode(RoundingMode.HALF_UP);
// 格式化数字
String text = df.format(5211314);
*/
