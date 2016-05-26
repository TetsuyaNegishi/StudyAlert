package com.example.tetsuya.studyalert;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    // タイマーカウント用
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private int count = 0;
    private Handler mHandler = new Handler();

    private StudyState studyState;

    // サウンド
    private AudioAttributes audioAttributes;
    private SoundPool soundPool;
    private int sound;

    // センサー用
    private SensorManager manager;
    private SensorEventListener listener;

    // 加速度
    private float fAccell[] = new float[3];
    private final float ACCELL_MAX = 0.3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studyState = new StudyState((ImageView)findViewById(R.id.imageView));

        // アプリ起動中画面をスリープさせない
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 音声読み込み
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(2)
                .build();
        sound = soundPool.load(this, R.raw.tes, 1);

        //タイマーカウント設定
        this.mainTimer = new Timer();
        this.mainTimerTask = new MainTimerTask();
        this.mainTimer.schedule(mainTimerTask, 0, 1000);

        // センサーマネージャーを取得
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // センサーのイベントリスナーを登録
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch( sensorEvent.sensor.getType()) {
                    // 加速度
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        fAccell = sensorEvent.values.clone();
                        for(float accell:fAccell) {
                            if(accell > ACCELL_MAX){
                                count = 0;
                                studyState.detectStudy();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        // リスナー設定：加速度
        manager.registerListener(
                listener,
                manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterListener(listener);
        mainTimer.cancel();
        this.finish();
        this.moveTaskToBack(true);
    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            // タイマーカウント
            mHandler.post( new Runnable() {
                public void run() {
                    count += 1;
                    if (count == 10) {
                        studyState.nextState();
//                        soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1);
                    }
                    if (count == 20) {

                    }
                    if(count == 30) {
                    }
                    Log.d("count", String.valueOf(count));
                }
            });
        }
    }
}