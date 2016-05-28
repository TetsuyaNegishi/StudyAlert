package com.example.tetsuya.studyalert;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by tetsuyanegishi on 2016/05/26.
 */
public class StudyState {
    private ImageView imageView;
    private Button stopButton;
    private Handler mHandler = new Handler();
    private State state;

    public StudyState(MainActivity mainActivity) {
        this.imageView = (ImageView)mainActivity.findViewById(R.id.imageView);
        this.stopButton = (Button)mainActivity.findViewById(R.id.stopButton);
        this.stopButton.setVisibility(View.INVISIBLE);
        reset();
    }
    void reset() {
        state = new NormalState();
        imageView.setImageResource(state.getImageId());
    }
    void nextState() {
        state = state.nextState();
        imageView.setImageResource(state.getImageId());
    }
    void detectStudy() {
        state = state.detectStudy();
        imageView.setImageResource(state.getImageId());
    }

    abstract class State {
        public abstract State nextState();
        public abstract int getImageId();
        public State detectStudy() {
            Log.d("log", "state_detectStudy");
            return new OkState();
        }
    }

    private class NormalState extends State {
        public State nextState() {
            return new HatenaState();
        }
        public int getImageId() {
            return R.drawable.s_woman1;
        }
    }

    private class HatenaState extends State {
        public State nextState() {
            return new AngerState();
        }
        public int getImageId() {
            return R.drawable.s_woman2;
        }
    }

    private class AngerState extends State {
        public AngerState() {
            stopButton.setVisibility(View.VISIBLE);
        }
        public State nextState() {
            return this;
        }
        public int getImageId() {
            return R.drawable.s_woman3;
        }
        @Override
        public State detectStudy() {
            return this;
        }
    }

    private class OkState extends State {
        public OkState() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            }, 1000);
        }
        public State nextState() {
            return this;
        }
        public int getImageId() {
            return R.drawable.s_woman4;
        }
    }
}
