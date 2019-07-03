package com.example.lrcdisplaydemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class LrcView extends TextView {

    private List<String> mWordsList = new ArrayList<>();
    private Paint mLoseFocusPaint;
    private Paint mOnFocusPaint;
    private float mX = 0;
    private float mMiddleY = 0;
    private float mY = 0;
    private static final int DY = 50;
    private int mIndex = 0;

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LrcView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        Paint loseFocusPaint = mLoseFocusPaint;
        // Center代表drawText时x参数为文字的中心x坐标
        loseFocusPaint.setTextAlign(Paint.Align.CENTER);

        Paint focusPaint = mOnFocusPaint;
        focusPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(mWordsList.get(mIndex), mX, mMiddleY, focusPaint);

        int alphaValue = 25;
        float tempY = mMiddleY;
        for (int i = mIndex - 1; i >= 0; i--) {
            tempY -= DY;
            if (tempY < 0) {
                break;
            }
            loseFocusPaint.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
            canvas.drawText(mWordsList.get(i), mX, tempY, loseFocusPaint);
            alphaValue += 25;
        }
        alphaValue = 25;
        tempY = mMiddleY;
        for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
            tempY += DY;
            if (tempY > mY) {
                break;
            }
            loseFocusPaint.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
            canvas.drawText(mWordsList.get(i), mX, tempY, loseFocusPaint);
            alphaValue += 25;
        }
        mIndex++;
    }

    /**
     * 在onLayout前会被调用，可以用来获取宽高
     * @param w 现在的宽度
     * @param h 现在的高度
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mX = w * 0.5f;
        mY = h;
        mMiddleY = h * 0.1f;
    }

    private void init() {
        setFocusable(true);

        LrcHandle lrcHandler = new LrcHandle();
        lrcHandler.loadLrcFile(getContext().getResources().openRawResource(R.raw.testlrc));
        mWordsList = lrcHandler.getWords();

        mLoseFocusPaint = new Paint();
        // 抗锯齿
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(50);
        mLoseFocusPaint.setColor(Color.WHITE);

        mOnFocusPaint = new Paint();
        mOnFocusPaint.setAntiAlias(true);
        mOnFocusPaint.setColor(Color.YELLOW);
        mOnFocusPaint.setTextSize(60);
    }
}
