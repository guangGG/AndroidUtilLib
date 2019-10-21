package gapp.season.util.view;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import gapp.season.util.sys.ScreenUtil;

/**
 * 手势控制工具类
 */
public class GestureDetectorUtil {
    /**
     * 获取View上下左右滑动的手势监听器
     */
    public static GestureDetector flingInstance(final View view, final FlingCallBack callBack) {
        if (view == null || callBack == null) return null;
        final int flingMinDistance = ScreenUtil.dpToPx(20); //px
        final int flingMinVelocity = ScreenUtil.dpToPx(30); // px/s
        return new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true; //返回true时才会继续触发onScroll/onFling事件
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return callBack.onClick(view, false);
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                callBack.onClick(view, true);
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 触发条件 ：X轴的坐标位移大于m个像素，且X轴移动速度大于n个像素/秒，Y轴的坐标位移*1.5小于X轴的坐标位移
                boolean inLeft = ((e1.getX() + e2.getX()) < view.getWidth());
                boolean inTop = ((e1.getY() + e2.getY()) < view.getHeight());
                float offsetX = Math.abs(e1.getX() - e2.getX());
                float offsetY = Math.abs(e1.getY() - e2.getY());
                if (offsetY * 1.5 < offsetX) {
                    if (Math.abs(velocityX) > flingMinVelocity) {
                        if (e1.getX() - e2.getX() > flingMinDistance) {
                            // 向左侧滑动
                            return callBack.onFling(view, inLeft, inTop, false, -1);
                        } else if (e2.getX() - e1.getX() > flingMinDistance) {
                            // 向右侧滑动
                            return callBack.onFling(view, inLeft, inTop, false, 1);
                        }
                    }
                } else if (offsetY > offsetX * 1.5) {
                    if (Math.abs(velocityY) > flingMinVelocity) {
                        if (e1.getY() - e2.getY() > flingMinDistance) {
                            // 向上侧滑动
                            return callBack.onFling(view, inLeft, inTop, true, -1);
                        } else if (e2.getY() - e1.getY() > flingMinDistance) {
                            // 向下侧滑动
                            return callBack.onFling(view, inLeft, inTop, true, 1);
                        }
                    }
                }
                return false;
            }
        });
    }

    public interface FlingCallBack {
        /**
         * 点击或长按回调
         */
        boolean onClick(View view, boolean longPress);

        /**
         * 手势滑动回调
         *
         * @param view          操作的View
         * @param inLeft        标记在View左/右侧做的滑动动作
         * @param inTop         标记在View上/下侧做的滑动动作
         * @param flingVertical 标记纵向/横向滑动
         * @param direction     滑动方向：手势向左侧/上侧滑动为-1，手势向右侧/下侧滑动为1
         */
        boolean onFling(View view, boolean inLeft, boolean inTop, boolean flingVertical, int direction);
    }
}
