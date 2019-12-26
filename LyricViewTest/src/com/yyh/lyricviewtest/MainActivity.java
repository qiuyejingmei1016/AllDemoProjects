package com.yyh.lyricviewtest;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity implements Animator.AnimatorListener {

    private LyricTextView lyricTextView;

    private ObjectAnimator mAnimator;
    private int mTime = 0;

    private TextView mStartView;
    private TextView mEndView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();

        if (hasWord()) {
            String content = getWord();
            lyricTextView.setText(content);

            String word = getWord();
            mEndView.setText(word);

            mTime = content.length() * 300;
            Log.e("=====time", mTime + "");
            mAnimator = ObjectAnimator.ofFloat(lyricTextView, "progress", 0f, 1f);
            mAnimator.setDuration(mTime);
            mAnimator.addListener(MainActivity.this);
            mAnimator.start();
        }
    }

    private void initView() {
        lyricTextView = (LyricTextView) findViewById(R.id.lyric);
        mStartView = (TextView) findViewById(R.id.start_view);
        mEndView = (TextView) findViewById(R.id.end_view);
    }

    private Iterator<String> mContentIterator;
    private List<String> mContentList = new ArrayList<>();

    private void initData() {
        mContentList.add("我本次购买A理财产品");
        mContentList.add("的资金为自有资金");
        mContentList.add("我已阅读过该产品的");
        mContentList.add("推介材料和相关合同");
        mContentList.add("清楚了解风险和收益情况");
        mContentList.add("对本产品不承诺保本和");
        mContentList.add("最低收益表示知晓和认可");
        mContentList.add("");
        mContentIterator = mContentList.iterator();
    }

    private boolean hasWord() {
        return mContentIterator.hasNext();
    }

    private String getWord() {
        return mContentIterator.next();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Log.e("===onAnimationEnd", "Adapter  onAnimationEnd");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAnimator.cancel();
                if (hasWord()) {
                    mStartView.setText(lyricTextView.getText());

                    String content = mEndView.getText().toString();
                    lyricTextView.setText(content);

                    String word = getWord();
                    mEndView.setText(word);

                    mTime = content.length() * 300;
                    Log.e("=====time", mTime + "");
                    lyricTextView.setText(content);
                    mAnimator = ObjectAnimator.ofFloat(lyricTextView, "progress", 0f, 1f);
                    mAnimator.setDuration(content.length() * 300);
                    mAnimator.addListener(MainActivity.this);
                    mAnimator.start();
                }
            }
        });
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
