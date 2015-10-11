package com.hiddencity.games.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by arturskowronski on 11/10/15.
 */
public class HiddenBoldLabel extends TextView{
    public HiddenBoldLabel(Context context) {
        super(context);
        init(context);
    }

    public HiddenBoldLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HiddenBoldLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/sourcesans_pro.ttf");
        this.setTypeface(font);
        setTextColor(Color.WHITE);
    }
}