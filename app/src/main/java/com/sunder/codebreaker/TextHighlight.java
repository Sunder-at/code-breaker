package com.sunder.codebreaker;

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
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class TextHighlight extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...

    public ArrayList<String> textDisplay = new ArrayList<String>();
    public ArrayList<int[]> higlightPos = new ArrayList<int[]>();
    public ArrayList<int[]> whiteList = new ArrayList<>();
    public ArrayList<int[]> replaceList = new ArrayList<>();
    public String textIn;
    public String textIn_new;
    public float rngModifier = 0.5f;
    public ArrayList<String> words = new ArrayList<>();
    public boolean[] wordsUnderscore;
    public int[] collapsedList;
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
    public void initializeText(){
        textDisplay.clear();
        replaceList.clear();
        higlightPos.clear();
        words = makeText(120);
        wordsUnderscore = new boolean[words.size()];
        textIn = words.get(0);
        for(int eg=1;eg<words.size();eg++){
            textIn = String.format("%s %s",textIn,words.get(eg));
        }
        whiteList = WhiteList(textIn);

        int i;
        /*
        for(i = 0;i < whiteList.size();i++){
            System.out.printf("%d %d\t",whiteList.get(i)[0],whiteList.get(i)[1]);
        }
        */
        collapsedList = new int[textIn.length()];
        for(i=0;i<textIn.length();i++){
            collapsedList[i] = 0;
        }
        int rand;
        Random random = new Random();
        for(i=0;i<3;i++){
            int[] nn = new int[3];
            rand = (int) (random.nextDouble()*whiteList.size()*rngModifier);
            nn[0] = whiteList.get(rand)[0];
            nn[1] = whiteList.get(rand)[1];
            nn[2] = (int) (random.nextDouble() * 27) + 1;
            replaceList.add(nn);
        }
        /*
        for(i = 0;i < replaceList.size();i++){
            System.out.printf("%d %d\n",replaceList.get(i)[0],replaceList.get(i)[1]);
        }
        */
        textIn_new = textIn_obfuscator(textIn);

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
        initializeText();
        updateWordsUnderscore();


    }
    public void updateWordsUnderscore(){
        int count = 0;
        int flag = 0;
        String temp;
        for (int i = 0;i < wordsUnderscore.length;i++){
            temp = textIn_new.substring(count,count+words.get(i).length());
            if (temp.compareTo(words.get(i))==0){
                wordsUnderscore[i] = false;
            }else{
                wordsUnderscore[i] = true;
                flag++;
            }
            count += words.get(i).length() + 1;
        }
        if(flag==0){
            mListener.recreateMain();
        }
    }
    public String textIn_obfuscator(String textIn){
        int i,j;
        String textIn_new = textIn;
        collapsedList = new int[textIn.length()];
        for(i=0;i<textIn.length();i++){
            collapsedList[i] = 0;
        }
        for(i=0;i<replaceList.size();i++){
            for(j=replaceList.get(i)[1];j<textIn_new.length();j+=replaceList.get(i)[0]){
                String first = textIn_new.substring(0,j);
                String second = textIn_new.substring(j+1,textIn_new.length());
                int thisChar = textIn_new.charAt(j) + replaceList.get(i)[2];
                while (thisChar > 'z')  thisChar -= 'z' - 'a' + 1;
                textIn_new = String.format("%s%c%s",first,thisChar,second);
                collapsedList[j] += replaceList.get(i)[2];
            }
        }
        return textIn_new;
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
        int i,j;
        int b = textIn_new.length();
        int c = 0;
        textDisplay.clear();
        while(true){
            if(b > maxCharWidth) {
                textDisplay.add(textIn_new.substring(c,c+maxCharWidth));
            }else{
                textDisplay.add(textIn_new.substring(c,c+b));
                break;
            }
            b -= maxCharWidth;
            c += maxCharWidth;
        }
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
        i = 0;
        c = 0;
        int rec = 0;
        Paint p = new Paint();
        p.setColor(Color.RED);
        while(i<(words.size())) {
            if ((rec + words.get(i).length() + 1) > maxCharWidth) {
                drawLine(canvas, i, paddingLeft, paddingTop, fontWidth, rec,maxCharWidth, c);
                rec += words.get(i).length();
                c++;
                rec -= maxCharWidth;
                drawLine(canvas, i, paddingLeft, paddingTop, fontWidth, 0,rec, c);
                rec++;
            }else {
                drawLine(canvas, i, paddingLeft, paddingTop, fontWidth, rec,rec + words.get(i).length(), c);
                rec += words.get(i).length() + 1;
            }

            i++;
        }
    }

    private void drawLine(Canvas canvas, int i, int paddingLeft, int paddingTop, float fontWidth, int pStart, int pEnd, int c){
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(1.0f);

        if(wordsUnderscore[i]) {
            canvas.drawLine(paddingLeft + fontWidth * pStart,
                    paddingTop + mExampleDimension * (1.2f + c),
                    paddingLeft + fontWidth * pEnd,
                    paddingTop + mExampleDimension * (1.2f + c),
                    p);
        }
    }
    public interface TextRequest{
        ArrayList<InfoKeeper> getInfoKeeper();
        void recreateMain();
    }
    public ArrayList<String> makeText(int charMax) {
        AssetManager assetManager = getContext().getAssets();
        InputStream is;
        JSONObject jsonObject = null;
        int i,step=0;
        ArrayList<String> textD = new ArrayList<>();
        String temp= "";
        try {
            is = assetManager.open("wordsJSON.txt");
            jsonObject = new JSONObject(convertStreamToString(is));
            benfordMax = jsonObject.length();
            Random random = new Random();
            for(i=0;i < charMax; i+= step) {
                int num;
                do {
                    num = -1;
                    while (num < 0 || num > benfordMax) {
                        num = (int) (benford(random.nextDouble()) - 1);
                    }
                    temp = jsonObject.getString(String.valueOf(num));
                }while(temp.length()<=3);
                step = temp.length();
                textD.add(temp);
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
        for(i=2;i<s.length()/3;i++){
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
