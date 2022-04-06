package com.zynelk.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    public static final String TAG = "CheatActivity";

    private static final String EXTRA_ANSWER_IS_TRUE = "com.zynelk.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.zynelk.geoquiz.answer_shown";
    private static final String EXTRA_CHEATS = "com.zynelk.geoquiz.extra_cheats";
    private static final String ANSWER_SHOWN = "answer";
    private static final String CHEATS = "cheats";
    private boolean mAnswerIsTrue;
    private int mNumberOfCheats = 0;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView apiLevelTextView;


    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int mNumberOfCheats) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEATS, mNumberOfCheats);
        return intent;
    }
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
    public static int numberOfCheats(Intent result) {
        return result.getIntExtra(EXTRA_CHEATS, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        apiLevelTextView = findViewById(R.id.ApiLevelTextView);

        if (savedInstanceState != null) {
            mAnswerIsTrue = savedInstanceState.getBoolean(ANSWER_SHOWN, false);
            mNumberOfCheats = savedInstanceState.getInt(CHEATS);
            setAnswerShownResult(true);
        }

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mNumberOfCheats = getIntent().getIntExtra(EXTRA_CHEATS, 0);
        Log.d(TAG, "onCreate: mNumberOfCheats " + mNumberOfCheats);

        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                apiLevelTextView.setText("API Level " + Build.VERSION.RELEASE);

                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                mNumberOfCheats ++;
                setAnswerShownResult(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }

            }
        });
    }
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_CHEATS, mNumberOfCheats);
        setResult(RESULT_OK, data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(ANSWER_SHOWN, mAnswerIsTrue);
        savedInstanceState.putInt(CHEATS, mNumberOfCheats);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}