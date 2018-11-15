package com.benjinto.sunder.codebreaker;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class TextHighlight extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...

    public ArrayList<String> textDisplay = new ArrayList<String>();
    public ArrayList<int[]> higlightPos = new ArrayList<int[]>();
    public ArrayList<int[]> charOffsetPos = new ArrayList<int[]>();
    public ArrayList<Color> colorPos = new ArrayList<>();
    public String textIn;
    public int[] asd = {0,1,3,5,7,8,9};
    public CharSequence text = "asdgh dabnd asd adwadwadn awdaw awda gvuz h guzguz sfsefgv fsf";
    public int benfordMax = 10000;
    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    public TextRequest mListener;


    public TextHighlight(Context context) {
        super(context);
        if (context instanceof TextRequest) {
            mListener = (TextRequest) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        init(null, 0);
    }

    public TextHighlight(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof TextRequest) {
            mListener = (TextRequest) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        init(attrs, 0);
    }

    public TextHighlight(Context context, AttributeSet attrs, int defStyle)  {
        super(context, attrs, defStyle);
        if (context instanceof TextRequest) {
            mListener = (TextRequest) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        init(attrs, defStyle);

    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TextHighlight, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.TextHighlight_exampleString);
        mExampleColor = a.getColor(
                R.styleable.TextHighlight_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.TextHighlight_exampleDimension,
                mExampleDimension);


        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);


        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();

        textDisplay.clear();
        textIn = makeText(160);
        ArrayList<int[]> whiteList = WhiteList(textIn);
        ArrayList<int[]> replaceList = new ArrayList<>();
        int i,j;
        int[] nn = new int[3];
        int[] collapsedList = new int[textIn.length()];
        for(i=0;i<textIn.length();i++){
            collapsedList[i] = 0;
        }
        for(i=0;i<5;i++){
            int rand = (int) (Math.random()*whiteList.size());
            nn[0] = whiteList.get(rand)[0];
            nn[1] = whiteList.get(rand)[1];
            nn[2] = (int) (Math.random() * 27) + 1;
            replaceList.add(nn);
            for(j=replaceList.get(i)[1];j<textIn.length();j+=replaceList.get(i)[0]){
                String first = textIn.substring(0,j);
                String second = textIn.substring(j+1,textIn.length());
                int thisChar = textIn.charAt(j) + replaceList.get(i)[2];
                while (thisChar > 'z')  thisChar -= 'z' - 'a';
                textIn = String.format("%s%c%s",first,thisChar,second);
                collapsedList[j] += replaceList.get(i)[2];
            }
        }


    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mTextWidth = mTextPaint.measureText(mExampleString);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int maxCharWidth = (int) ((getWidth()-getPaddingRight()-getPaddingLeft())/mTextPaint.measureText(" "));
        int b = textIn.length();
        int c = 0;
        textDisplay.clear();
        while(true){
            if(b > maxCharWidth) {
                textDisplay.add(textIn.substring(c,c+maxCharWidth));
            }else{
                textDisplay.add(textIn.substring(c,c+b));
                break;
            }
            b -= maxCharWidth;
            c += maxCharWidth;
        }
        int i,j;
        for(i = 0; i < textDisplay.size();i++) {
            canvas.drawText(textDisplay.get(i), paddingLeft, paddingTop + mExampleDimension * (i+1), mTextPaint);
        }

        float fontWidth = mTextPaint.measureText(" ");
        for(i = 0; i < higlightPos.size();i++) {
            for (j = 0; j < higlightPos.get(i).length; j += 2) {
                canvas.drawRect(paddingLeft + fontWidth * higlightPos.get(i)[j+1], paddingTop + mExampleDimension * (0.2f  + higlightPos.get(i)[j]),
                        paddingLeft + fontWidth * (higlightPos.get(i)[j+1] + 1),
                        paddingTop + mExampleDimension * (1.2f + higlightPos.get(i)[j]), mListener.getInfoKeeper().get(i).paint);
            }
        }
    }


    public interface TextRequest{
        ArrayList<InfoKeeper> getInfoKeeper();
    }
    public String makeText(int charMax) {
        AssetManager assetManager = getContext().getAssets();
        InputStream is;
        JSONObject jsonObject = null;
        int i,step=0;
        String textD = "";
        String temp= "";
        try {
            is = assetManager.open("wordsJSON.txt");
            jsonObject = new JSONObject(convertStreamToString(is));
            benfordMax = jsonObject.length();
            for(i=0;i < charMax; i+= step) {
                int num = -1;
                while (num < 0 || num > benfordMax) {
                    num = (int) (benford(Math.random())-1);
                }
                temp = jsonObject.getString(String.valueOf(num));
                step = temp.length();
                if(textD != "") {
                    textD = String.format("%s %s", textD, temp);
                }else{
                    textD = temp;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textD;
    }
    public ArrayList<int[]> WhiteList(String s){
        int i, j,k;
        int flag = 0;
        ArrayList<int[]> out = new ArrayList<>();
        for(i=2;i<s.length()/2;i++){
            for(j=0;j<i;j++){
                flag = 0;
                for(k=j;k<s.length();k+=i){
                    if(s.charAt(k)==' ') flag++;
                }
                if(flag == 0){
                    int[] a = {i,j};
                    out.add(a);
                }
            }
        }
        return out;
    }
    public double benford(double p){
        return  (Math.pow(benfordMax,1-p)-1);
    }
    public String convertStreamToString(InputStream is){
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext()?s.next():"";
    }
}
