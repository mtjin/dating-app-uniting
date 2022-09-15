package com.unilab.uniting.square;


import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;

public class SquareViewPager extends ViewPager {

    public SquareViewPager(final Context context) {
        super(context);
    }

    public SquareViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        width = Math.min(width, height);
        height = width;

        setMeasuredDimension(width, height);
    }

    @Override protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }
}