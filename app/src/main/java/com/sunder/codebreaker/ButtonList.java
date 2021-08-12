package com.sunder.codebreaker;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ButtonList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ButtonList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "fragIndex";
    private View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private int fragIndex;
    private OnFragmentInteractionListener mListener;

    public ArrayList<Button> al = new ArrayList<Button>();
    public LinearLayout ll;
    public int b1Text = 0,b2Text = 2,b3Text = 0;
    public ButtonList() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param fragIndex Parameter 2.
     * @return A new instance of fragment ButtonList.
     */
    // TODO: Rename and change types and number of parameters
    public static ButtonList newInstance(String param1, int fragIndex) {
        ButtonList fragment = new ButtonList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, fragIndex);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.onInstantiated(fragIndex);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            fragIndex = getArguments().getInt(ARG_PARAM2);
        }
        ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        // View a = inflater.inflate(fragment_button_list,container,false);
        Button b1 = new Button(getActivity());
        Button b2 = new Button(getActivity());
        Button b3 = new Button(getActivity());
        Button b4 = new Button(getActivity());
        b1.setText(String.valueOf(b1Text));
        b2.setText(String.valueOf(b2Text));
        b3.setText(String.valueOf(b3Text));
        b4.setText("X");

        b1.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ClipData clipData = new ClipData(ClipData.newPlainText("", ""));
                mListener.setButtonListButton(fragIndex,0);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
                view.startDrag(clipData, new View.DragShadowBuilder(), null, 0);
                return true;
            }

        });
        b2.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ClipData clipData = new ClipData(ClipData.newPlainText("", ""));
                mListener.setButtonListButton(fragIndex,1);
                view.startDrag(clipData, new View.DragShadowBuilder(), null, 0);
                return true;
            }
        });
        b3.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ClipData clipData = new ClipData(ClipData.newPlainText("", ""));
                mListener.setButtonListButton(fragIndex,2);
                view.startDrag(clipData, new View.DragShadowBuilder(), null, 0);
                return true;
            }
        });
        b4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mListener.destroyButtonList(fragIndex);

                return true;
            }
        });
        ll.setBackgroundColor(Color.argb(100,20,20,20));
        al.add(b1);
        al.add(b2);
        al.add(b3);
        al.add(b4);

        ll.addView(b1);
        ll.addView(b2);
        ll.addView(b3);
        ll.addView(b4);

        ll.setWeightSum(2.0f);

        return ll;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onButtonClicked(int fragId, int buttType, int value);
        void onInstantiated(int fragCount);
        View getViewHere(int id);
        void setButtonListButton(int fragIndex, int buttonType);
        void destroyButtonList(int fragId);
    }
}
