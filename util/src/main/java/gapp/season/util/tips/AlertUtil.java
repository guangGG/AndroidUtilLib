package gapp.season.util.tips;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import gapp.season.util.task.OnTaskDone;

public class AlertUtil {
    public static int POSITIVE_BUTTON = 1;
    public static int NEUTRAL_BUTTON = 2;
    public static int NEGATIVE_BUTTON = 3;

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

    public static void showMsg(Context context, String title, final String msg, String positiveButton, String neutralButton,
                               String negativeButton, boolean cancelable, final OnTaskDone<DialogInterface> listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) listener.onTaskDone(POSITIVE_BUTTON, msg, dialog);
                    }
                })
                .setNeutralButton(neutralButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) listener.onTaskDone(NEUTRAL_BUTTON, msg, dialog);
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) listener.onTaskDone(NEGATIVE_BUTTON, msg, dialog);
                    }
                })
                .setCancelable(cancelable)
                .show();
    }

    public static void alert(Context context, String title, String msg, String btn, boolean cancelable) {
        showMsg(context, title, msg, btn, null, null, cancelable, null);
    }

    public static void confirm(Context context, String title, String msg, String positiveButton,
                               String negativeButton, boolean cancelable, OnTaskDone<DialogInterface> listener) {
        showMsg(context, title, msg, positiveButton, null, negativeButton, cancelable, listener);
    }

    public static void list(Context context, String title, CharSequence[] items,
                            DialogInterface.OnClickListener listener, boolean cancelable) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, listener)
                .setCancelable(cancelable)
                .show();
    }
}
