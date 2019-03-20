package gapp.season.util.text;

import android.text.TextUtils;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Checker {
    private Checker() {
    }

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(File file) {
        return file == null || !file.exists() || file.length() <= 0;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(SparseArray array) {
        return array == null || array.size() <= 0;
    }

    public static boolean isEmpty(Date date) {
        return date == null || date.getTime() == 0;
    }


    public static boolean isEmpty(JSONObject json) {
        return json == null || json.length() == 0;
    }

    public static boolean isEmpty(JSONArray json) {
        return json == null || json.length() == 0;
    }


    public static boolean isExistedFile(File file) {
        return file != null && file.exists();
    }


    public static boolean isZero(Long longNum) {
        return longNum == null || longNum == 0;
    }

    public static boolean isZero(Integer integer) {
        return integer == null || integer == 0;
    }

    public static boolean isZero(Double doubleNum) {
        return doubleNum == null || doubleNum == 0;
    }

    public static boolean isPositive(Integer integer) {
        return integer != 0 && integer > 0;
    }


    public static int parseInt(String s, int defaultNum) {
        int result = defaultNum;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static double parseDouble(String s, double defaultNum) {
        double result = defaultNum;
        try {
            result = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static double parseDouble(String s) {
        return parseDouble(s, 0);
    }


    public static boolean isNumStr(String str) {
        // return str!=null&&str.matches("^\\d+$");
        if (!TextUtils.isEmpty(str)) {
            for (char c : str.toCharArray()) {
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static String removeBlank(String str) {
        if (!TextUtils.isEmpty(str)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c > ' ') {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return str;
    }

    public static <E> boolean isContains(E[] array, E element) {
        if (!isEmpty(array)) {
            for (E e : array) {
                if (e == element) {
                    return true;
                }
                if (e != null && e.equals(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <E> boolean isContains(List<E> list, E element) {
        if (!isEmpty(list)) {
            return list.contains(element);
        }
        return false;
    }

    public static <E> boolean isContains(Set<E> set, E element) {
        if (!isEmpty(set)) {
            return set.contains(element);
        }
        return false;
    }

    public static <K, V> boolean isContainsKey(Map<K, V> map, K key) {
        if (!isEmpty(map)) {
            return isContains(map.keySet(), key);
        }
        return false;
    }

    public static <K, V> boolean isContainsValue(Map<K, V> map, V value) {
        if (!isEmpty(map)) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (entry != null) {
                    V v = entry.getValue();
                    if (v == value) {
                        return true;
                    }
                    if (v != null && v.equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
