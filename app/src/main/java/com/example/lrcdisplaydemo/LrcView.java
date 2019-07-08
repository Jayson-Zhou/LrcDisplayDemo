package com.example.lrcdisplaydemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class LrcView extends TextView {

    private final String TAG = "LrcView";

    private List<String> mWordsList;
    private Paint mLoseFocusPaint;
    private Paint mOnFocusPaint;
    private float mX = 0;
    private float mMiddleY = 0;
    private float mY = 0;
    private float marginY = 10;
    private int mIndex = 0;
    private int loseFocusColor;
    private int focusColor;
    private float loseFocusTextSize;
    private float focusTextSize;

    public LrcView(Context context) {
        super(context);
        init(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LrcView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        Paint loseFocusPaint = mLoseFocusPaint;
        // Center代表drawText时x参数为文字的中心x坐标
        loseFocusPaint.setTextAlign(Paint.Align.CENTER);

        Paint focusPaint = mOnFocusPaint;
        focusPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(mWordsList.get(mIndex), mX, mMiddleY, focusPaint);

        // 设置歌词每次滑动的距离
        float DY = loseFocusPaint.getTextSize() + marginY * 2;

        float tempY = mMiddleY;
        for (int i = mIndex - 1; i >= 0; i--) {
            tempY -= DY;
            if (tempY < 0) {
                break;
            }
            canvas.drawText(mWordsList.get(i), mX, tempY, loseFocusPaint);
        }

        tempY = mMiddleY;
        for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
            tempY += DY;
            if (tempY > mY) {
                break;
            }
            canvas.drawText(mWordsList.get(i), mX, tempY, loseFocusPaint);
        }
        mIndex++;
    }

    /**
     * 在onLayout前会被调用，可以用来获取宽高
     *
     * @param w    现在的宽度
     * @param h    现在的高度
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mX = w * 0.5f;
        mY = h;
        mMiddleY = h * 0.3f;
    }

    private void init(Context context, AttributeSet attrs) {
        setFocusable(true);

        mWordsList = new ArrayList<>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
        loseFocusColor = a.getColor(R.styleable.LrcView_lose_focus_color, Color.BLACK);
        focusColor = a.getColor(R.styleable.LrcView_focus_color, Color.BLUE);
        loseFocusTextSize = a.getDimension(R.styleable.LrcView_lose_focus_size, 20);
        focusTextSize = a.getDimension(R.styleable.LrcView_focus_size, 30);

        mLoseFocusPaint = new Paint();
        // 抗锯齿
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(loseFocusTextSize);
        mLoseFocusPaint.setColor(loseFocusColor);

        mOnFocusPaint = new Paint();
        mOnFocusPaint.setAntiAlias(true);
        mOnFocusPaint.setColor(focusColor);
        mOnFocusPaint.setTextSize(focusTextSize);
        getPaint().setTextSize(loseFocusTextSize);
    }

    public void setDataList(List<String> words) {
        this.mWordsList = words;
    }
}
