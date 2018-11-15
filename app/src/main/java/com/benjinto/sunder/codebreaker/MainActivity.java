package com.benjinto.sunder.codebreaker;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ButtonList.OnFragmentInteractionListener, TextHighlight.TextRequest{

    public int fragCount = 0;
    public ArrayList<InfoKeeper> infoKeepers = new ArrayList<>();
    private Bundle lastInstance;
    public float[] lastPointer = new float[2];
    private int[] buttonListValues = {0,0,0};
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
                    lastPointer[0] = dragEvent.getX();
                    lastPointer[1] = dragEvent.getY();
                    buttonListValues[2] = 0;
                    return true;
                }
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_LOCATION){
                    int dragEventYOffset = (int) ((dragEvent.getY()-lastPointer[1])/50);
                    if(dragEventYOffset != buttonListValues[2]){
                        int makeOffset = buttonListValues[2] - dragEventYOffset;
                        buttonListValues[2] = dragEventYOffset;
                        onButtonClicked(buttonListValues[0],buttonListValues[1],makeOffset);
                    }
                    return true;
                }
                if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
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

        TextHighlight th = findViewById(R.id.textH);

        int bPeriod,bStart, bOffset;
        bPeriod = infoKeepers.get(fragId).buttonPeriod;
        bStart = infoKeepers.get(fragId).buttonStart;
        bOffset = infoKeepers.get(fragId).buttonOffset;
        switch (buttType) {
            case 0:
                bStart += value;
                while(bStart >= bPeriod){
                    bStart = -bPeriod;
                }
                while(bStart < 0){
                    bStart += bPeriod;
                }
                ((ButtonList)infoKeepers.get(fragId).fragment).al.get(buttType).setText(String.valueOf(bStart));
                break;
            case 1:
                bPeriod += value;
                if (bPeriod < 2){
                    bPeriod += (th.textIn.length()/2) - 1;
                }
                if (bPeriod > (th.textIn.length()/2)){
                    bPeriod += -(th.textIn.length()/2) + 1;
                }
                ((ButtonList)infoKeepers.get(fragId).fragment).al.get(buttType).setText(String.valueOf(bPeriod));
                break;
            case 2:
                bOffset += value;
                while (bOffset < 0){
                    bOffset += 28;
                }
                while (bOffset > 27){
                    bOffset -= 28;
                }
                ((ButtonList)infoKeepers.get(fragId).fragment).al.get(buttType).setText(String.valueOf(bOffset));
                break;
        }
        ((ButtonList)infoKeepers.get(fragId).fragment).ll.invalidate();
        int counti,countj;
        infoKeepers.get(fragId).buttonPeriod = bPeriod;
        infoKeepers.get(fragId).buttonStart = bStart;
        infoKeepers.get(fragId).buttonOffset = bOffset;

        ((TextHighlight)findViewById(R.id.textH)).higlightPos.set(fragId, null);
        infoKeepers.get(fragId).paint.setARGB(100,20,80,20);
        ((ButtonList)infoKeepers.get(fragId).fragment).ll.setBackgroundColor(Color.argb(100,20,20,20));
        int j=0,k=0;
        while(k < th.textDisplay.size()){
            j += th.textDisplay.get(k).length();
            k++;
        }

        int[] a = new int[(((j-bStart+bPeriod-1)/bPeriod))*2];
        int aCounter=0;
        countj = bStart;
        for(counti = bStart;counti<th.textIn.length();counti+=bPeriod){
            String first = th.textIn.substring(0,counti);
            String second = th.textIn.substring(counti+1,th.textIn.length());
            int thisChar = th.textIn.charAt(counti) + bOffset;
            while (thisChar > 'z')  thisChar -= 'z' - 'a';
            th.textIn = String.format("%s%c%s",first,thisChar,second);
        }
        for (counti = 0; counti < th.textDisplay.size(); counti++) {
            for (; countj < th.textDisplay.get(counti).length(); countj += bPeriod) {
                a[aCounter] = counti;
                a[aCounter+1] = countj;
                aCounter += 2;
                if (th.textDisplay.get(counti).charAt(countj) == ' ') {
                    infoKeepers.get(fragId).paint.setARGB(100, 100, 20, 20);
                    ((ButtonList)infoKeepers.get(fragId).fragment).ll.setBackgroundColor(Color.argb(100,80,20,20));
                }
            }
            countj -= th.textDisplay.get(counti).length();
            while (countj < 0) countj += bPeriod;
        }
        ((TextHighlight)findViewById(R.id.textH)).higlightPos.set(fragId,a);

        ((TextHighlight)findViewById(R.id.textH)).invalidate();

    }

    @Override
    public ArrayList<InfoKeeper> getInfoKeeper() {
        return infoKeepers;
    }
}
