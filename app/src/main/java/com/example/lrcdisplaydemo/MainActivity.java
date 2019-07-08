package com.example.lrcdisplaydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private LrcView mLrcView;
    private List<Integer> mTimeList;
    private List<String> mWordList;
    private MediaPlayer mPlayer;

    private Thread lrcThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPlayer != null && !mPlayer.isPlaying()) {
                    mPlayer.start();
                    // 注意，因为实现逻辑是让歌词高亮一段时间，而不是等待一段时间再高亮，所以先进行一次刷新
                    mLrcView.invalidate();
                    lrcThread.start();
                }
            }
        });

        mLrcView = findViewById(R.id.text);

        mPlayer = MediaPlayer.create(this, R.raw.testsong);
        LrcHandle lrcHandler = new LrcHandle();
        lrcHandler.loadLrcFile(getResources().openRawResource(R.raw.testlrc));
        mTimeList = lrcHandler.getTimeList();
        mWordList = lrcHandler.getWords();
        mLrcView.setDataList(mWordList);

        // 实现的逻辑是让下一句歌词出现时间减去当前歌词出现时间的差值作为高亮等待时间，
        // lrc中并没有歌曲结束的时间，要从MediaPlayer中获取，并添加到时间集合中
        mTimeList.add(mPlayer.getDuration());

        final Handler handler = new Handler();

        lrcThread = new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                while (true) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            mLrcView.invalidate();
                        }
                    });

                    try {
                        Thread.sleep(mTimeList.get(i + 1) - mTimeList.get(i));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    i++;
                    if (i == mTimeList.size() - 1) {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;
                        }
                        break;
                    }
                }
            }
        });
    }
}
