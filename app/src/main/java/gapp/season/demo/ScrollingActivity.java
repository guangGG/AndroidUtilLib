package gapp.season.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import gapp.season.demo.test.DecimalUtilTest;
import gapp.season.util.app.ActivityHolder;
import gapp.season.util.log.LogUtil;
import gapp.season.util.net.HttpSimpleUtil;
import gapp.season.util.sys.DeviceUtil;
import gapp.season.util.sys.ScreenUtil;
import gapp.season.util.tips.ToastUtil;
import gapp.season.util.view.ThemeUtil;

public class ScrollingActivity extends AppCompatActivity {
    private ActivityHolder.ForegroundStatusListener mForegroundStatusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.setTheme(this, R.style.AppTheme_NoActionBar);
        //ThemeUtil.setFullScreenTheme(this, 0, 0xffcccccc);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("key1", "value1");
                                HttpSimpleUtil.doPostAsync("https://www.baidu.com", headers, "a=b&c=d", new HttpSimpleUtil.StringCallBack("UTF-8") {
                                    @Override
                                    public void onRequestSuccess(String result) {
                                        LogUtil.i("doPostAsync success : " + result);
                                        ToastUtil.showShort("doPostAsync success");
                                    }

                                    @Override
                                    public void onRequestFailed(int errorCode) {
                                        LogUtil.w("doPostAsync error : " + errorCode);
                                        ToastUtil.showShort("doPostAsync error : " + errorCode);
                                    }
                                });
                            }
                        }).show();
                DecimalUtilTest.doTest();
            }
        });

        LogUtil.d("ActivityHolder activitys = " + ActivityHolder.getInstance().getActivityList());
        mForegroundStatusListener = new ActivityHolder.ForegroundStatusListener() {
            @Override
            public void onAppForegroundStatusChange(boolean isAppForeground) {
                ToastUtil.showShort("App change to " + (isAppForeground ? "Foreground" : "Background"));
            }
        };
        ActivityHolder.getInstance().registerForegroundStatusListener(mForegroundStatusListener);
    }

    @Override
    protected void onDestroy() {
        ActivityHolder.getInstance().unregisterForegroundStatusListener(mForegroundStatusListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LogUtil.d(String.format("型号：%s，版本：%s，AndroidId：%s，IMEI：%s，INSI：%s，wifi-ip:%s",
                    DeviceUtil.getOsModel(), DeviceUtil.getOsVersion(), DeviceUtil.getAndroidId(getApplicationContext()),
                    DeviceUtil.getIMEI(getApplicationContext()), DeviceUtil.getIMSI(getApplicationContext()),
                    DeviceUtil.getWifiIp(getApplicationContext())));
            int c = (int) (Math.random() * 2);
            int colorInt = c > 0 ? Color.MAGENTA : Color.CYAN;
            boolean darkText = c <= 0;
            ScreenUtil.setSysBarColor(ScrollingActivity.this, colorInt, darkText);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
