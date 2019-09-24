package gapp.season.util.text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalUtil {
    /**
     * 元转分，确保price保留两位有效数字
     */
    public static long changeY2F(double price) {
        return Math.round(price * 100);
    }

    /**
     * 分转元，转换为bigDecimal再toString
     */
    public static String changeF2Y(long price) {
        return BigDecimal.valueOf(price).divide(new BigDecimal(100), RoundingMode.HALF_UP).toString();
    }

    /**
     * 格式化显示数字
     */
    public static String formatNum(double num, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);
    }

    /**
     * 格式化数字显示
     */
    public static String formatNum(double num, int digit) {
        String pattern = "0";
        if (digit > 0) {
            pattern = "0.";
            for (int i = 0; i < digit; i++) {
                pattern += "#";
            }
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(num);
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
