package com.example.tetsuya.studyalert;

import android.os.Handler;
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

    abstract class State {
        public abstract State nextState();
        public abstract int getImageId();
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
            return new AngerState();
        }
        public int getImageId() {
            return R.drawable.s_woman3;
        }
    }
}
