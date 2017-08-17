package com.moon.samples.clockdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button startOrPauseBtn;

    private ClockView clockView;

    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startOrPauseBtn = (Button) findViewById(R.id.startOrPause);
        clockView = (ClockView) findViewById(R.id.clockView);
        timeTextView = (TextView) findViewById(R.id.timeView);

        startOrPauseBtn.setText("暂停");
        startOrPauseBtn.setBackground(null);
        startOrPauseBtn.setTextColor(Color.BLACK);

        // 开始或暂停
        startOrPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clockView.isPause){
                    clockView.start();
                    startOrPauseBtn.setText("暂停");
                    startOrPauseBtn.setBackground(null);
                    startOrPauseBtn.setTextColor(Color.BLACK);
                }else {
                    clockView.pause();
                    startOrPauseBtn.setText("开始");
                    startOrPauseBtn.setBackgroundColor(Color.RED);
                    startOrPauseBtn.setTextColor(Color.WHITE);
                }
            }
        });

        // 跟手机时间校准
        findViewById(R.id.adjust).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockView.correctTime(Calendar.getInstance());

            }
        });

      new CountDownTimer(Long.MAX_VALUE,1000){

          @Override
          public void onTick(long millisUntilFinished) {

              timeTextView.setText(DateFormat.format("HH:mm:ss",Calendar.getInstance()));
          }

          @Override
          public void onFinish() {
              // TODO: 17/8/12 我们活不到这个方法的回调

          }
      }.start();


    }
}
