package gapp.season.util.sys;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘工具类
 */
public class KeyBoardUtil {
    public static void openKeybord(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void closeKeybord(EditText editText, Context context) {
        if (editText == null) {
            return;
        }
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void closeKeybord(Activity activity) {
        if (activity == null) {
            return;
        }
        if (activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                try {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 模拟系统按键(发送KeyEvent，同时发送down和up两种KeyEvent，可以在Activity的dispatchKeyEvent中处理)
     *
     * @param keyCode KeyEvent.KEYCODE_...
     */
    public static void sendKeyEvent(final int keyCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 相当于命令：“adb shell input keyevent KeyCode”
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}