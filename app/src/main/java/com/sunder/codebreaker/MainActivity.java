package com.sunder.codebreaker;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ButtonList.OnFragmentInteractionListener, TextHighlight.TextRequest{

    public int fragCount = 0;
    public ArrayList<InfoKeeper> infoKeepers = new ArrayList<>();
    private Bundle lastInstance;
    public float[] lastPointer = new float[2];
    private int[] buttonListValues = {0,0,0};
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
     //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager  fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment f = ButtonList.newInstance("a",fragCount);
                ((TextHighlight)findViewById(R.id.textH)).higlightPos.add(null);
                infoKeepers.add(fragCount, new InfoKeeper(f,fragCount));
                ft.add(R.id.buttonLayout,f);
                ft.commit();

                fragCount++;

            }
        });
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.activityLayout).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
                    return true;
                }
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_STARTED){

                    buttonListValues[2] = 0;
                    return true;
                }
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_LOCATION){
                    if (lastPointer[0] < 0 || lastPointer[1] < 0) {
                        lastPointer[0] = dragEvent.getX();
                        lastPointer[1] = dragEvent.getY();
                    }
                    int dragEventYOffset = (int) (-(dragEvent.getY()-lastPointer[1])/50);
                    if(dragEventYOffset != buttonListValues[2]){
                        int makeOffset = buttonListValues[2] - dragEventYOffset;
                        buttonListValues[2] = dragEventYOffset;
                        onButtonClicked(buttonListValues[0],buttonListValues[1],makeOffset);
                    }
                    /*
                    lastPointer[0] = dragEvent.getX();
                    lastPointer[1] = dragEvent.getY();
                    */
                    return true;
                }
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    lastPointer[0] = -1;
                    lastPointer[1] = -1;
                    return true;
                }
                return false;
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public void onInstantiated(int fragCount){
        onButtonClicked(fragCount,0,0);
    }
    @Override
    public View getViewHere(int id) {
        return findViewById(id);
    }

    @Override
    public void setButtonListButton(int fragIndex, int buttonType) {
        buttonListValues[0] = fragIndex;
        buttonListValues[1] = buttonType;
    }

    @Override
    public void onButtonClicked(int fragId,int buttType, int value){
        if(infoKeepers.get(fragId).fragment == null) return;
        TextHighlight th = findViewById(R.id.textH);
        if(th.higlightPos == null) return;

        int bPeriod,bStart, bOffset;
        bPeriod = infoKeepers.get(fragId).buttonPeriod;
        bStart = infoKeepers.get(fragId).buttonStart;
        bOffset = infoKeepers.get(fragId).buttonOffset;
        int counti, countj;
        int j = 0, k = 0;
        int[] a;
        while (k < th.textDisplay.size()) {
            j += th.textDisplay.get(k).length();
            k++;
        }
        switch (buttType) {
            case 0:
                bStart += value;
                break;
            case 1:
                bPeriod += value;
                break;
            case 2:
                bOffset += value;
                break;
        }
        while (bPeriod < 2){
            bPeriod += (th.textIn.length()/2) - 1;
        }
        while (bPeriod > (th.textIn.length()/2)){
            bPeriod -= (th.textIn.length()/2) - 1;
        }
        while(bStart >= bPeriod){
            bStart = -bPeriod;
        }
        while(bStart < 0){
            bStart += bPeriod;
        }
        while (bOffset < 0){
            bOffset += 'z' - 'a' + 1;
        }
        while (bOffset > 'z' - 'a'){
            bOffset -= 'z' - 'a' + 1;
        }
        ((ButtonList)infoKeepers.get(fragId).fragment).al.get(0).setText(String.valueOf(bStart));
        ((ButtonList)infoKeepers.get(fragId).fragment).al.get(1).setText(String.valueOf(bPeriod));
        ((ButtonList)infoKeepers.get(fragId).fragment).al.get(2).setText(String.valueOf(bOffset));
        ((ButtonList) infoKeepers.get(fragId).fragment).ll.invalidate();
        ((ButtonList) infoKeepers.get(fragId).fragment).ll.setBackgroundColor(Color.argb(100, 20, 20, 20));

        infoKeepers.get(fragId).buttonPeriod = bPeriod;
        infoKeepers.get(fragId).buttonStart = bStart;
        infoKeepers.get(fragId).buttonOffset = bOffset;
        infoKeepers.get(fragId).doOffsetFlag = true;

        ((TextHighlight) findViewById(R.id.textH)).higlightPos.set(fragId, null);
        infoKeepers.get(fragId).paint.setARGB(100, 20, 80, 20);
        a = new int[(((j-bStart+bPeriod-1)/bPeriod))*2];
        countj = bStart;
        int aCounter = 0;
        infoKeepers.get(fragId).doOffsetFlag = true;
        for (counti = 0; counti < th.textDisplay.size(); counti++) {
            for (; countj < th.textDisplay.get(counti).length(); countj += bPeriod) {
                a[aCounter] = counti;
                a[aCounter + 1] = countj;
                aCounter += 2;
                if (th.textDisplay.get(counti).charAt(countj) == ' ') {
                    infoKeepers.get(fragId).doOffsetFlag = false;
                    infoKeepers.get(fragId).paint.setARGB(100, 100, 20, 20);
                    ((ButtonList) infoKeepers.get(fragId).fragment).ll.setBackgroundColor(Color.argb(100, 80, 20, 20));
                }
            }
            countj -= th.textDisplay.get(counti).length();
            while (countj < 0) countj += bPeriod;
        }

        textIn_obfu2(th);

        ((TextHighlight)findViewById(R.id.textH)).higlightPos.set(fragId,a);
        th.updateWordsUnderscore();

        ((TextHighlight)findViewById(R.id.textH)).invalidate();

    }
    public void textIn_obfu2(TextHighlight th){
        int counti, countj;
        String first,second;
        int thisChar;
        th.textIn_new = th.textIn_obfuscator(th.textIn);
        for (int i = 0; i < infoKeepers.size(); i++) {
            if(infoKeepers.get(i).fragment==null) continue;
            if(infoKeepers.get(i).doOffsetFlag) {
                for (counti = infoKeepers.get(i).buttonStart; counti < th.textIn_new.length(); counti += infoKeepers.get(i).buttonPeriod) {
                    first = th.textIn_new.substring(0, counti);
                    second = th.textIn_new.substring(counti + 1, th.textIn_new.length());
                    thisChar = th.textIn_new.charAt(counti) + infoKeepers.get(i).buttonOffset;
                    while (thisChar > 'z') thisChar -= 'z' - 'a' + 1;
                    th.textIn_new = String.format("%s%c%s", first, thisChar, second);
                }
            }
        }
    }
    @Override
    public ArrayList<InfoKeeper> getInfoKeeper() {
        return infoKeepers;
    }

    @Override
    public void destroyButtonList(int fragId){
        if(infoKeepers.get(fragId).fragment == null) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(infoKeepers.get(fragId).fragment);
        ft.commit();
        infoKeepers.get(fragId).fragment = null;
        int[] a = {};
        ((TextHighlight)findViewById(R.id.textH)).higlightPos.set(fragId,a);
        textIn_obfu2((TextHighlight)findViewById(R.id.textH));
        ((TextHighlight)findViewById(R.id.textH)).updateWordsUnderscore();
        ((TextHighlight)findViewById(R.id.textH)).invalidate();

    }
    @Override
    public void recreateMain(){

        TextHighlight th = findViewById(R.id.textH);

        for(int i=0;i < infoKeepers.size();i++){
            destroyButtonList(i);
        }

        th.initializeText();
        th.updateWordsUnderscore();

        fragCount = 0;
    }
}
