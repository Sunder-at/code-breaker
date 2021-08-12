package com.sunder.codebreaker;

import android.graphics.Paint;
import android.support.v4.app.Fragment;


public class InfoKeeper {

    public Fragment fragment;
    public int fragementCount;
    public int buttonStart=0;
    public int buttonPeriod=2;
    public int buttonOffset=0;
    public boolean doOffsetFlag = true;
    public Paint paint = new Paint();
    public Paint backgroundPaint = new Paint();

    public InfoKeeper(Fragment fragment, int fragCount){
        this.fragment = fragment;
        this.fragementCount = fragCount;
        this.paint.setARGB(100,20,80,20);
        this.backgroundPaint.setARGB(100,20,20,20);
    }

}
