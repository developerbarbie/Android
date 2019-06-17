package com.isaacson.josie.jisaacsonfinalproject;

import android.graphics.Color;
import android.support.constraint.solver.widgets.Rectangle;

public class Brick {
    private Rectangle mBoundary;
    private int mColor;
    private final int mWidth;
    private final int mHeight;
    private boolean mIsDead;
    private int mTotalHits;

    Brick(Rectangle boundary,float width, float height, int totalHits) {
        mBoundary = boundary;
        mColor = Color.DKGRAY;
        mWidth = (int)width;
        mHeight = (int)height;
        mIsDead = false;
        mTotalHits = totalHits;
    }

    Brick(Rectangle boundary, int color, float width, float height) { //paddle
        mBoundary = boundary;
        mColor = color;
        mWidth = (int)width;
        mHeight = (int)height;
        mIsDead = false;
    }

    public Rectangle getBoundary() {
        return mBoundary;
    }

    public void updateCoords(int x, int y){
        mBoundary.setBounds(x, y, mWidth, mHeight);
    }

    public int getColor() {
        return mColor;
    }

    public void changeColor(){
        mColor = Color.LTGRAY;
    }

    public void kill(){
        mIsDead = true;
    }

    public boolean isDead(){
        return mIsDead;
    }

    public void hit(){
        mTotalHits --;
    }
    public int hitsLeft(){
        return mTotalHits;
    }
}
