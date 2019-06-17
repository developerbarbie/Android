package com.isaacson.josie.jisaacsonfinalproject;


import android.support.constraint.solver.widgets.Rectangle;

public class Ball {

    private float mX;
    private float mY;
    private final int mHeight;
    private final int mWidth;
    private final int mDraw;
    private Rectangle mBoundary;
    private boolean mIsDead;

    Ball(float x, float y, float height, float width, int draw, Rectangle boundary) {
        mX = x;
        mY = y;
        mHeight = (int) height;
        mWidth = (int) width;
        mDraw = draw;
        mBoundary = boundary;
        mIsDead = false;
    }

    public Rectangle getBoundary() {
        return mBoundary;
    }

    public void updateCoords(int x, int y) {
        mBoundary.setBounds(x, y, mWidth, mHeight);
    }

    public void kill() {
        mIsDead = true;
    }


}