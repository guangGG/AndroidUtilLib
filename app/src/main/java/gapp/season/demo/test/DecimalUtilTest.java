package gapp.season.demo.test;

import java.math.BigDecimal;

import gapp.season.util.log.LogUtil;
import gapp.season.util.text.DecimalUtil;

public class DecimalUtilTest {
    private static final String TAG = "DecimalUtilTest";

    public static void doTest() {
        long f1 = DecimalUtil.changeY2F(2.1356);
        LogUtil.d(TAG, "changeY2F 2.1356 " + f1);
        String y1 = DecimalUtil.changeF2Y(5670);
        LogUtil.d(TAG, "changeF2Y 5670 " + y1);
        LogUtil.d(TAG, "getFenByAmount 1.365 " + DecimalUtil.getFenByAmount("1.365"));
        LogUtil.d(TAG, "getFenByAmount 1. " + DecimalUtil.getFenByAmount("1."));
        LogUtil.d(TAG, "getFenByAmount 1.3 " + DecimalUtil.getFenByAmount("1.3"));
        LogUtil.d(TAG, "getFenByAmount 0. " + DecimalUtil.getFenByAmount("0."));
        LogUtil.d(TAG, "formatNum 2.1356 0.0% " + DecimalUtil.formatNum(2.1356, "0.0%"));
        LogUtil.d(TAG, "formatNum 2.1356 -1 " + DecimalUtil.formatNum(2.1356, -1));
        LogUtil.d(TAG, "formatNum 2.1356 0 " + DecimalUtil.formatNum(2.1356, 0));
        LogUtil.d(TAG, "formatNum 2.1356 3 " + DecimalUtil.formatNum(2.1356, 3));
        LogUtil.d(TAG, "formatNumIncludeZero 2.1 3 " + DecimalUtil.formatNumIncludeZero(2.1, 3));
        LogUtil.d(TAG, "add 2.1356 0.0000001 " + DecimalUtil.add(2.1356, 0.0000001));
        LogUtil.d(TAG, "sub 2.1356 0.0000001 " + DecimalUtil.sub(2.1356, 0.0000001));
        LogUtil.d(TAG, "mul 2.1356 0.0000001 " + DecimalUtil.mul(2.1356, 0.0000001));
        LogUtil.d(TAG, "div 2.1356 6.3333 5 " + DecimalUtil.div(2.1356, 6.3333, 5));
        LogUtil.d(TAG, "div 2.1356 6.3333 7 " + DecimalUtil.div(2.1356, 6.3333, 7));
        LogUtil.d(TAG, "round 2.13555 3 " + DecimalUtil.round(2.13555, 3));
        LogUtil.d(TAG, "roundStr 2.13555 3 " + DecimalUtil.roundStr(2.13555, 3));
        LogUtil.d(TAG, "floor 2.13555 3 " + DecimalUtil.floor(2.13555, 3));
        LogUtil.d(TAG, "floorStr 2.13555 3 " + DecimalUtil.floorStr(2.13555, 3));
        LogUtil.d(TAG, "floorStr 2.130000 3 " + DecimalUtil.floorStr(2.130000, 3));
        LogUtil.d(TAG, "formatBigDecimal 2.130000 5 " + DecimalUtil.formatBigDecimal(BigDecimal.valueOf(2.130000), 5));
    }
}
