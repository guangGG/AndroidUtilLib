package gapp.season.util.tips;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public class AlertUtil {
    public static void showMsg(Context context, @StringRes int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageId);
        builder.show();
    }

    public static void showMsg(Context context, @Nullable CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.show();
    }

    public static void showMsg(Context context, @StringRes int title, @StringRes int message, @StringRes int btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(btn, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showMsg(Context context, CharSequence title, CharSequence message, CharSequence btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(btn, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
