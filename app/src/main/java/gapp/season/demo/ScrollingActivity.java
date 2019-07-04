package gapp.season.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import gapp.season.util.app.ActivityHolder;
import gapp.season.util.log.LogUtil;
import gapp.season.util.sys.DeviceUtil;
import gapp.season.util.sys.ScreenUtil;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHolder.getInstance().addActivity(this);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        ActivityHolder.getInstance().removeActivity(this);
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
            LogUtil.d(String.format("型号：%s，版本：%s，AndroidId：%s，IMEI：%s，INSI：%s，",
                    DeviceUtil.getOsModel(), DeviceUtil.getOsVersion(), DeviceUtil.getAndroidId(getApplicationContext()),
                    DeviceUtil.getIMEI(getApplicationContext()), DeviceUtil.getIMSI(getApplicationContext())));
            int c = (int) (Math.random() * 2);
            int colorInt = c > 0 ? Color.MAGENTA : Color.CYAN;
            ScreenUtil.setStatusBarColor(ScrollingActivity.this, colorInt, true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
