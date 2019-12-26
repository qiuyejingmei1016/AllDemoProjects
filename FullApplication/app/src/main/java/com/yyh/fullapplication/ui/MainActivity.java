package com.yyh.fullapplication.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yyh.fullapplication.ApiClient;
import com.yyh.fullapplication.GroupInfo;
import com.yyh.fullapplication.HttpUtil;
import com.yyh.fullapplication.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTimeView;
    private WeakHandler mWeakHandler;
    private Timer mTimer;
    private static final int TIME_UPDATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimeView = (TextView) findViewById(R.id.time_view);
        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);

        mWeakHandler = new WeakHandler(this, mTimeView);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mWeakHandler != null) {
                    mWeakHandler.sendEmptyMessage(TIME_UPDATA);
                }
            }
        }, 0, 1000);
    }

    /**
     * 弱引用Handler
     */
    private static class WeakHandler extends Handler {
        private final WeakReference<MainActivity> mActivty;
        private int time;
        private TextView timeView;

        public WeakHandler(MainActivity activity, TextView timeView) {
            this.mActivty = new WeakReference<>(activity);
            this.timeView = timeView;
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivty.get();
            super.handleMessage(msg);
            if (activity == null || activity.isFinishing()) {
                return;
            }
            //执行业务逻辑
            if (msg.what == TIME_UPDATA) {
                time++;
                if (timeView != null) {
                    timeView.setText(timeFormat(time));
                }
            }
        }
    }

    private static String timeFormat(int Seconds) {

        String strtime = new String();
        int hour = Seconds / (60 * 60);
        int min = (Seconds / 60) % 60;
        int s = Seconds % 60;
//			String hourStr = (hour >= 10) ? "" + hour : "0" + hour;
        String minStr = (min >= 10) ? "" + min : "0" + min;
        String seondStr = (s >= 10) ? "" + s : "0" + s;
//			strtime = hourStr + "时" + minStr + "分" + seondStr+"秒";
        strtime = minStr + ":" + seondStr;
        return strtime;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt1) {
            ApiClient.requestSmsCode("17666536986", new HttpUtil.ResultCallback() {
                @Override
                public void onSuccess(Object response) {
                    if (response != null) {
                        Log.e("=====", response.toString());
                    }
                    Toast.makeText(MainActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (id == R.id.bt2) {
            ApiClient.updateTradeInfo("123", "yyh", "456", new HttpUtil.ResultCallback() {
                @Override
                public void onSuccess(Object response) {

                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        } else if (id == R.id.bt3) {
            ApiClient.getClassData(new GroupInfo("test", "123", "789"), new HttpUtil.ResultCallback() {
                @Override
                public void onSuccess(Object response) {

                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacksAndMessages(null);
        }
    }
}
