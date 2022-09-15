package com.unilab.uniting.Square;

import android.content.Context;
import android.util.AttributeSet;


public class StudentCardImageView extends androidx.appcompat.widget.AppCompatImageView {

    public StudentCardImageView(Context context) {
        super(context);
    }

    public StudentCardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StudentCardImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        double width = MeasureSpec.getSize(widthMeasureSpec);
        double height = MeasureSpec.getSize(heightMeasureSpec);

        height = 0.625 * width;

        int intOfHight = (int) height;
        int intOfWidth = (int) width;

        setMeasuredDimension(intOfWidth, intOfHight);
    }

}