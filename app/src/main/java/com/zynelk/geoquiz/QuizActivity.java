package com.zynelk.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String KEY_ANSWER = "KEY_ANSWER";
    private static final String KEY_CHEATS = "KEY_CHEATS";
    private boolean mIsCheater;
    private int mNumberOfCheats = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;
    private TextView mCheats_remaining;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int score = 0;
    private int cheats_allowed = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);


        mCheats_remaining = findViewById(R.id.cheats_remaining_textView);
        mCheats_remaining.setText("Cheat tokens: " + cheats_allowed);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_ANSWER, false);
            mNumberOfCheats = savedInstanceState.getInt(KEY_CHEATS, 0);
        }


        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();

            }
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                disableButton(true, mTrueButton);
                disableButton(true, mFalseButton);

            }
        });
        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                disableButton(true, mFalseButton);
                disableButton(true, mTrueButton);

            }
        });
        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cheats_allowed--;
                mCheats_remaining.setText("Cheat tokens: " + cheats_allowed);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

                Log.d(TAG, "onClick: mNumberOfCheats" + mNumberOfCheats);

                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, mNumberOfCheats);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);

            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableButton(false, mFalseButton);
                disableButton(false, mTrueButton);

                if (mCurrentIndex < mQuestionBank.length - 1) {
                    mCurrentIndex = (mCurrentIndex + 1);
                    mIsCheater = false;
//                    mNumberOfCheats = 0;

                    updateQuestion();
                }
            }
        });
        updateQuestion();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mNumberOfCheats = CheatActivity.numberOfCheats(data);
            Log.d(TAG, " onActivityResult mNumberOfCheats " + mNumberOfCheats);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        Log.d(TAG, " onResume mNumberOfCheats " + mNumberOfCheats);


        if (mNumberOfCheats >= 3) {
            mCheatButton.setEnabled(false);
            cheats_allowed = 0;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_ANSWER, mIsCheater);
        savedInstanceState.putInt(KEY_CHEATS, mNumberOfCheats);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTRue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater && mNumberOfCheats >= 3) {
            messageResId = R.string.judgment_toast;

        } else {
            if (userPressedTrue == answerIsTRue) {
                messageResId = R.string.correct_toast;
                score++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        if (mCurrentIndex >= mQuestionBank.length - 1) {
            mNextButton.setEnabled(false);
            double finalScore = (double) score / (double) mQuestionBank.length * 100;
            Toast.makeText(QuizActivity.this, finalScore + "%", Toast.LENGTH_LONG).show();
        }


    }

    private void disableButton(Boolean enabledButton, Button button) {
//        button.setEnabled(!enabledButton);
        if (enabledButton) {
            button.setEnabled(false);
        } else {
            button.setEnabled(true);
        }
    }
}