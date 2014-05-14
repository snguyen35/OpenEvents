package csc.mobility.openevents.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;
import csc.mobility.openevents.R;

public class CustomPagerTabStrip extends PagerTabStrip{

	public CustomPagerTabStrip(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomPagerTabStrip);
        setTabIndicatorColor(a.getColor(
                R.styleable.CustomPagerTabStrip_indicatorColor, Color.WHITE));
        a.recycle();
    }

}
