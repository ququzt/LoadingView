package com.lodingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import static java.lang.Math.PI;

/**
 * Created by Administrator on 2017/1/13.
 */
public class LoadingView extends View {
    private float mCenterX,mCenterY;     //中心点
    private float mHalfDiagonal;         //对角线一半长度
    private Paint ripplePaint,circlePaint;
    private float mEmptyRadio;                         //水波纹时空心圆的半径
    private int[] colors = getResources().getIntArray(R.array.colors);   //小圆的颜色
    private  float mBigCircleRadius = 100f;           //大圆的半径
    private  float mSmallCircleRadius = 10f;         //小圆半径
    private float angle;                            //旋转的角度
    private ValueAnimator valueAnimator;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w/2f;
        mCenterY = h/2f;
        mHalfDiagonal = (float) Math.sqrt( (w*w+h*h)/2 );
        ripplePaint = new Paint();
        circlePaint = new Paint();
    }

    //策略模式
    private LoadingStatus loadingStatus;
    private abstract class LoadingStatus{
        public abstract void onDraw(Canvas canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(loadingStatus == null){
            loadingStatus = new CircleStatus();
        }
        loadingStatus.onDraw(canvas);
    }

    //开启旋转动画的下个动画
    public void startNextAnimation(){
        if(valueAnimator != null && loadingStatus instanceof CircleStatus){
            CircleStatus circleStatus = (CircleStatus) loadingStatus;
            circleStatus.cancelAnimation();
            loadingStatus = new ExpandStatus();
        }
    }


    //圆的旋转
    private class CircleStatus extends LoadingStatus{

        public CircleStatus(){
            startCircleAnimation();
        }

        @Override
        public void onDraw(Canvas canvas) {
            drawCircle(canvas);
        }

        public void cancelAnimation(){
            valueAnimator.cancel();
        }
    }

    /*
       画小圆
       根据旋转的角度不停绘制
     */
    private void drawCircle(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        float rotationAngle = (float) (2* PI/colors.length);
        for (int i=0;i<colors.length;i++){
            circlePaint.setColor(colors[i]);
            float circleAngle = rotationAngle*i + angle;           //旋转的角度
            float circleX = (float) (Math.cos(circleAngle)*mBigCircleRadius + mCenterX);   //小圆的X轴点
            float circleY = (float) (Math.sin(circleAngle)*mBigCircleRadius + mCenterY);    //小圆的Y轴点
            canvas.drawCircle(circleX,circleY,mSmallCircleRadius,circlePaint);
        }
    }

    //小圆的旋转动画
    private void startCircleAnimation(){
        valueAnimator = ValueAnimator.ofFloat(0,(float) (2 * Math.PI));
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                angle = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    //圆的伸展
    private class ExpandStatus extends LoadingStatus{

        public ExpandStatus(){
            startExpandAnimation();
        }

        @Override
        public void onDraw(Canvas canvas) {
            drawCircle(canvas);
        }
    }

    //水波纹动画
    private class RippleStatus extends LoadingStatus{

        public RippleStatus(){
            startRippleAnimation();
        }

        @Override
        public void onDraw(Canvas canvas) {
            drawRipple(canvas);
        }

    }

    //伸展动画
    private void startExpandAnimation(){
        valueAnimator = ValueAnimator.ofFloat(0,mBigCircleRadius);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new OvershootInterpolator(40f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBigCircleRadius = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loadingStatus = new RippleStatus();
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.reverse();
    }

    //画水波纹
    private void drawRipple(Canvas canvas){
        ripplePaint.setColor(Color.WHITE);
        ripplePaint.setStyle(Paint.Style.STROKE);
        float stroke = mHalfDiagonal - mEmptyRadio;       //圆边的宽度
        ripplePaint.setStrokeWidth(stroke);
        float radio = stroke/2 + mEmptyRadio;             //圆的半径
        canvas.drawCircle(mCenterX,mCenterY,radio,ripplePaint);
    }

    //水波纹动画
    private void startRippleAnimation(){
        valueAnimator = ValueAnimator.ofFloat(0,mHalfDiagonal);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mEmptyRadio = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });

        valueAnimator.start();
    }
}
