package com.blackondev.my.gallery;


import android.content.Context;
import android.util.AttributeSet;

public class GridImage extends android.support.v7.widget.AppCompatImageView {


    public GridImage(Context context) {
        super(context);
    }

    public GridImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }
}
