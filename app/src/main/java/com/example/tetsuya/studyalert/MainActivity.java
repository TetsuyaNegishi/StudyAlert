package com.example.tetsuya.studyalert;

import android.graphics.Color;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer mainTimer;					//タイマー用
    private MainTimerTask mainTimerTask;		//タイマタスククラス
    private TextView countText;					//テキストビュー
    private int count = 0;						//カウント
    private Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    // サウンド
    private AudioAttributes audioAttributes;
    private SoundPool soundPool;
    private int sound;

    // センサーマネージャー
    private SensorManager manager;

    // センサーイベントリスナー
    private SensorEventListener listener;

    // テキストビュー
    TextView textView = null;

    private float xMax = 0.0f;
    private float yMax = 0.0f;
    private float zMax = 0.0f;

    // 加速度
    private float fAccell[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //タイマーインスタンス生成
        this.mainTimer = new Timer();
        //タスククラスインスタンス生成
        this.mainTimerTask = new MainTimerTask();
        //タイマースケジュール設定＆開始
        this.mainTimer.schedule(mainTimerTask, 0, 1000);
        //テキストビュー
        this.countText = (TextView)findViewById(R.id.count);

        // text
        textView = (TextView)findViewById(R.id.textView);

        // センサーマネージャーを取得
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                xMax = 0.0f;
                yMax = 0.0f;
                zMax = 0.0f;
            }
        });

        // センサーのイベントリスナーを登録
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch( sensorEvent.sensor.getType()) {
                    // 加速度
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        fAccell = sensorEvent.values.clone();

                        for(float accell:fAccell) {
                            if(accell > 0.3f){
                                count = 0;
                                countText.setTextColor(Color.BLACK);
                                countText.setText(String.valueOf(count));
                            }
                        }

//                        if(xMax < fAccell[0]){
//                            xMax = fAccell[0];
//                        }
//                        if(yMax < fAccell[1]){
//                            yMax = fAccell[1];
//                        }
//                        if(zMax < fAccell[2]){
//                            zMax = fAccell[2];
//                        }

                        String str = "";
                        str += "加速度センサー（重力あり）\n"
                                + "X:" + Math.abs(fAccell[0]) + "\n"
                                + "Y:" + Math.abs(fAccell[1]) + "\n"
                                + "Z:" + Math.abs(fAccell[2]) + "\n";
                        str += "\n\n";
                        str += "加速度センサー（重力除去）\n"
                                + "X:" + Math.abs(xMax) + "\n"
                                + "Y:" + Math.abs(yMax) + "\n"
                                + "Z:" + Math.abs(zMax) + "\n";
                        textView.setText(str);

                        break;
                }

            }

            //
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();

        // リスナー設定：加速度
        manager.registerListener(
                listener,
                manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // リスナー解除
//        manager.unregisterListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "destroy");
        mainTimer.cancel();

        this.finish();
        this.moveTaskToBack(true);
    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述します
            mHandler.post( new Runnable() {
                public void run() {

                    //実行間隔分を加算処理
                    count += 1;
                    //画面にカウントを表示
                    countText.setText(String.valueOf(count));

                    if(count > 30) {
                        countText.setTextColor(Color.RED);
                    } else if (count > 20) {
                        countText.setTextColor(Color.BLUE);
                    } else if (count > 10) {
                        soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1);
                        countText.setTextColor(Color.YELLOW);
                    }

                }
            });
        }
    }
}