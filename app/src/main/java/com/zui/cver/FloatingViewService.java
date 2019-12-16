package com.zui.cver;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class FloatingViewService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private TextView mFloatingTextView;
    //    private boolean showing = false;
    private WindowManager.LayoutParams params;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floatingwidget, null);

        //setting the layout parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.END; // put on the top of screen

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //adding click listener to close button and expanded view
        mFloatingTextView = mFloatingView.findViewById(R.id.floatingTView);//
//        mFloatingTextView.setOnClickListener(this);

//        //adding an touchlistener to make drag movement of the floating widget
//        mFloatingView.findViewById(R.id.floatingView).setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        //startActivity();
//                        return true;
//
//                    case MotionEvent.ACTION_UP:
////                        //when the drag is ended switching the state of the widget
////                        collapsedView.setVisibility(View.GONE);
////                        expandedView.setVisibility(View.VISIBLE);
//                        return true;
//
//                    case MotionEvent.ACTION_MOVE:
////                        //this code is helping the widget to move around the screen with fingers
////                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
////                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
////                        mWindowManager.updateViewLayout(mFloatingView, params);
//                        return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if ("show".equals(intent.getStringExtra("action"))) {
                Log.d("Cver:", "Floatingservice: onStartCommand : show");
                mWindowManager.addView(mFloatingView, params);
            } else if ("hide".equals(intent.getStringExtra("action"))) {
                //todo: how to get if the view is added?? or exception would occur
                Log.d("Cver:", "Floatingservice: onStartCommand : hide");
                mWindowManager.removeView(mFloatingView);
            } else {
                Log.d("Cver:", "Floatingservice: onStartCommand : update data");
                int avgCurrent = intent.getIntExtra("avgCurrent", 0);
                mFloatingTextView.setText(String.valueOf(avgCurrent));
                mWindowManager.updateViewLayout(mFloatingView, params);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.layoutExpanded:
//                //switching views
//                collapsedView.setVisibility(View.VISIBLE);
//                expandedView.setVisibility(View.GONE);
//                break;
//
//            case R.id.buttonClose:
//                //closing the widget
//                stopSelf();
//                break;
//        }
    }
}
