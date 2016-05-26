package com.example.tetsuya.studyalert;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by tetsuyanegishi on 2016/05/26.
 */
public class StudyState {
    private ImageView imageView;
    private Handler mHandler = new Handler();
    private State state;

    public StudyState(ImageView imageView) {
        this.imageView = imageView;
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 1000);
    }

    abstract class State {
        public abstract State nextState();
        public abstract int getImageId();
        public State detectStudy() {
            return new OkState();
        }
    }

    private class NormalState extends State {
        public State nextState() {
            return new AngerState();
        }
        public int getImageId() {
            return R.drawable.s_woman1;
        }
    }

    private class AngerState extends State {
        public State nextState() {
            return this;
        }
        public int getImageId() {
            return R.drawable.s_woman3;
        }
    }

    private class OkState extends State {
        public State nextState() {
            return this;
        }
        public int getImageId() {
            return R.drawable.s_woman4;
        }
    }
}
