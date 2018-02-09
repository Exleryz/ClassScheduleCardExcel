package com.yezhou.classschedulecardexcel;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.TableLayout;

/**
 * Created by Administrator on 2018/1/14.
 */

public class CourceDetail extends TableLayout {

    public CourceDetail(Context context)
    {
        super(context);
    }

    /**
     * 重写View必备构造器
     * @param context
     * @param attrs
     */
    public CourceDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        ListView view = new ListView(context);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed,l, t, r, b);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
