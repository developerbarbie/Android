package com.isaacson.josie.jisaacsonfinalproject;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.preference.PreferenceManager;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Observable;


public class Game extends View implements TimeAnimator.TimeListener {

    static final float mOverallWidth = 2090f;
    static final float mOverallHeight = 1000f;
    static final float asRatHeight = 1f;
    static final float asRatWidth = 1f;

    private TimeAnimator mTimer;
    private GameStats mGameStats;

    private float mOriginX;
    private float mOriginY;
    private float mGameBoardWidth;
    private float mGameBoardHeight;

    private int INITIAL_SCORE = 0;
    private int INITIAL_LEVEL = 1;

    private int mBoardTop;
    private int mBoardBottom;
    private int mBoardLeft;
    private int mBoardRight;

    private boolean mGameStarted;
    private boolean mGamePaused;

    Paint mPaint;

    //BRICKS
    static final float mPadding = 10f;
    static final float mBrickHeight = 20f;
    static final float mBrickWidth = 200f;
    private int mCurBricks;
    private Brick[][] mBricks;
    private int TOTAL_BRICK_ROW;
    private int TOTAL_BRICK_COL;
    private int INITIAL_BRICKS;
    private int INITIAL_HITS;
    private int mBrickTotalHits;
    private boolean bricksArrayFilled;


    //BALL
    static final float mBallWidth = 20f;
    static final float mBallHeight = 40f;
    private Bitmap mBallBitmap;
    private boolean ballSet;
    private int mTotalBalls = 1;
    private Ball mBall;
    private float mBalldX;
    private float mBalldY;
    private int INITIAL_BALLS;
    private int mBallsLeft;
    private float mBallSpeed = 2f;


    //PADDLE
    static final float mPaddleHeight = 20f;
    static final float mPaddleWidth = 400f;
    private boolean paddleSet;
    private Brick mPaddle;
    private String mPaddleDirection;
    private float mPaddleDx = 10f;
    private final String PADDLE_STOP = "stop";
    private final String PADDLE_LEFT = "left";
    private final String PADDLE_RIGHT = "right";

    //PREFS
    int[] mInitialPrefStats;

    Rect temp;

    Bitmap mWallBitmap;

    //what you will use if you Create your view programmatically
    public Game(Context context){
        super(context, null);
        init();

    }

    //will be called when the view is inflated from an XML file
    public Game(Context context, AttributeSet attrs){
        super(context, attrs, 0);
        init();
    }

    //will be called when the view is inflated from an XML file with a specific base style from some theme.
    public Game(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init();
    }

    public void init(){

        mPaint = new Paint();
        TOTAL_BRICK_ROW = 10;
        TOTAL_BRICK_COL = 10;

        //------------------------------------------------------------

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //bricks
        String[] bricksString = prefs.getString("pref_bricks", "5").split(" ");
        int bricks = Integer.parseInt(bricksString[bricksString.length - 1]);
        //hits
        String[] hitsString = prefs.getString("pref_hits", "2").split(" ");
        int hits = Integer.parseInt(hitsString[hitsString.length - 1]);
        //balls
        String[] ballsString = prefs.getString("pref_balls", "3").split(" ");
        Log.v("Balls String", ballsString[ballsString.length - 1]);
        int balls = Integer.parseInt(ballsString[ballsString.length - 1]);
        Log.v("Balls", String.valueOf(balls));

        setInitialPrefs(new int[]{bricks, hits, balls});

        //------------------------------------------------------------

        if(mInitialPrefStats != null){
            INITIAL_BRICKS = mInitialPrefStats[0];
            INITIAL_HITS = mInitialPrefStats[1];
            INITIAL_BALLS = mInitialPrefStats[2];
        }


        mGameStats = new GameStats(INITIAL_SCORE, INITIAL_LEVEL, INITIAL_BALLS, INITIAL_BRICKS);


        mBallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.purpleball);
        mWallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rainbowwall2);

        //set initial ball velocity
        mBalldX = 4f;
        mBalldY = 4f;

        mTotalBalls = INITIAL_BALLS;
        mBallsLeft = mTotalBalls;
        mCurBricks = INITIAL_BRICKS;
        mBrickTotalHits = INITIAL_HITS;

        mInitialPrefStats = null;

        bricksArrayFilled = false;
        paddleSet = false;
        ballSet = false;

        mTimer = new TimeAnimator() ;
        mTimer.setTimeListener(this) ;
        mTimer.start();

    }

    @Override
    public void onDraw(Canvas canvas){
        float width = getWidth();
        float height = getHeight();

        //scale
        width = width/mOverallWidth;
        height = height/mOverallHeight;
        canvas.scale(width, height);

        temp = canvas.getClipBounds();
        canvas.drawBitmap(mWallBitmap, null, temp, null);
        mBoardTop = temp.top;
        mBoardBottom = mBoardTop + 600;
        mBoardLeft = temp.left;
        mBoardRight = temp.right;
        mGameBoardHeight = temp.height();
        mGameBoardWidth = temp.width();

        if(mInitialPrefStats == null){
            mInitialPrefStats = new int[]{5, 2, 3};
        }

        INITIAL_BRICKS = mInitialPrefStats[0];
        INITIAL_HITS = mInitialPrefStats[1];
        INITIAL_BALLS = mInitialPrefStats[2];

        if(!bricksArrayFilled){
            mBricks = fillBricksArray();
            mGameStarted = true;
            mGamePaused = false;
        }

        //draw bricks
        drawBricks(canvas);

        //draw paddle
        if(!paddleSet){
            createPaddle();
            paddleSet = true;
        }
        drawPaddle(canvas);

        //draw ball
        if(!ballSet){
            createBall();
            ballSet = true;
        }
        drawBall(canvas);



    }

    private void createPaddle(){
        Rectangle paddleBounds = new Rectangle();
        paddleBounds.setBounds(mBoardLeft + 500, mBoardTop + 500, (int)mPaddleWidth, (int)mPaddleHeight);
        mPaddle = new Brick(paddleBounds, Color.BLACK, mPaddleWidth, mPaddleHeight);
        mPaddleDirection = PADDLE_STOP;
    }

    private void createBall(){
        Rectangle ballBoundary = new Rectangle();
        ballBoundary.setBounds(mBoardLeft + 600, mBoardTop + 400, (int)mBallWidth, (int)mBallHeight);
        mBall = new Ball(mBoardLeft + 900, mBoardTop + 200, mBallWidth, mBallHeight, R.drawable.rainbowball, ballBoundary);
    }

    private Brick[][] fillBricksArray(){
        Brick[][] bricks = new Brick[TOTAL_BRICK_ROW][TOTAL_BRICK_COL];
        float x = mBoardLeft;
        float y = mBoardTop;
        int totalBricks = INITIAL_BRICKS;
        int rowCount = 0;
        int colCount = 0;

        for(int row = 0; row < TOTAL_BRICK_ROW && totalBricks > 0; row++){
            rowCount++;
            for(int col = 0; col < TOTAL_BRICK_COL && totalBricks > 0; col++){

                Rectangle brickBoundary = new Rectangle();
                brickBoundary.setBounds((int)x, (int)y, (int)mBrickWidth, (int)mBrickHeight);
                Brick brick = new Brick(brickBoundary, mBrickWidth, mBrickHeight, mBrickTotalHits);
                bricks[row][col] = brick;
                totalBricks --;
                colCount++;
                x = x + mBrickWidth + mPadding;

            }
            y = y + mBrickHeight + mPadding;
            x = mBoardLeft;
        }
        bricksArrayFilled = true;
        if(rowCount <= 10){
            TOTAL_BRICK_ROW = rowCount;
        }else{
            TOTAL_BRICK_ROW = 10;
        }
        if(colCount <= 10){
            TOTAL_BRICK_COL = colCount;
        }else{
            TOTAL_BRICK_COL = 10;
        }

        return bricks;
    }

    private void drawBricks(Canvas canvas){
        for(int row = 0; row < TOTAL_BRICK_ROW ; row++){
            for(int col = 0; col < TOTAL_BRICK_COL ; col++){
                if(mBricks[row][col] != null) {
                    if(!mBricks[row][col].isDead()){
                        Brick brick = mBricks[row][col];
                        Rectangle brickBoundary = brick.getBoundary();
                        int left = brickBoundary.x;
                        int right = brickBoundary.x + brickBoundary.width;
                        int top = brickBoundary.y;
                        int bottom = brickBoundary.y + brickBoundary.height;
                        Rect brickDrawn = new Rect(left, top, right, bottom);
                        mPaint.setColor(brick.getColor());
                        canvas.drawRect(brickDrawn, mPaint);
                    }
                }
            }
        }
    }

    private void drawPaddle(Canvas canvas){
        updatePaddlePosition(mPaddleDirection);
        Brick paddle = mPaddle;
        Rectangle paddleBounds = paddle.getBoundary();
        int left = paddleBounds.x;
        int right = paddleBounds.x + paddleBounds.width;
        int top = paddleBounds.y;
        int bottom = paddleBounds.y + paddleBounds.height;
        Rect paddleDrawn = new Rect(left, top, right, bottom);
        mPaint.setColor(paddle.getColor());
        canvas.drawRect(paddleDrawn, mPaint);
    }

    private void drawBall(Canvas canvas){
            if(mBallsLeft > 0){
                Ball ball = mBall;
                Rectangle ballBounds = ball.getBoundary();
                int left = ballBounds.x;
                int right = ballBounds.x + ballBounds.width;
                int top = ballBounds.y;
                int bottom = ballBounds.y + ballBounds.height;
                Rect ballDrawn = new Rect(left, top, right, bottom);
                canvas.drawBitmap(mBallBitmap, null, ballDrawn, mPaint);
            }

    }


    public void onLeftClick(){
        mPaddleDirection = PADDLE_LEFT;
    }

    public void onRightClick(){
        mPaddleDirection = PADDLE_RIGHT;
    }

    public void onPaddleStop(){
        mPaddleDirection = PADDLE_STOP;
    }

    private void updateBallPosition(){
        if(ballSet){
                float x = mBall.getBoundary().x;
                float y = mBall.getBoundary().y;
                Rectangle ballBounds = mBall.getBoundary();
                int left = ballBounds.x;
                int right = ballBounds.x + ballBounds.width;
                int top = ballBounds.y;
                int bottom = ballBounds.y + ballBounds.height;
                Rect ballRect = new Rect(left, top, right, bottom);

                Rectangle paddleBounds = mPaddle.getBoundary();
                left = paddleBounds.x;
                right = paddleBounds.x + paddleBounds.width;
                top = paddleBounds.y;
                bottom = paddleBounds.y + paddleBounds.height;
                Rect paddleRect = new Rect(left, top, right, bottom);

                Rect topWall = new Rect(mBoardLeft, mBoardTop - 10, mBoardRight, mBoardTop);
                Rect leftWall = new Rect(mBoardLeft - 10, mBoardTop, mBoardLeft, mBoardBottom);
                Rect bottomWall = new Rect(mBoardLeft, mBoardBottom, mBoardRight, mBoardBottom + 10);
                Rect rightWall = new Rect(mBoardRight, mBoardTop, mBoardRight + 10, mBoardBottom);

                if(ballRect.intersect(paddleRect) ||
                        ballRect.intersect(topWall) ||
                        ballRect.intersect(leftWall) ||
                        ballRect.intersect(rightWall) ||
                        intersectBricks(ballRect)) {

                    playBounce();

                    if(ballRect.intersect(rightWall) || ballRect.intersect(leftWall)){
                        mBalldX = mBalldX * -1;
                    }else if(ballRect.intersect(paddleRect)) {

                        if(ballRect.centerX() < paddleRect.centerX()){
                            float xHat = (ballRect.centerX() - (paddleRect.centerX() + (float)(paddleBounds.width)/2))/(float)(paddleBounds.width)/2;
                            float theta = xHat * 67.5f;
                            double absV = Math.abs(Math.sqrt(Math.pow(mBalldX, 2) + Math.pow(mBalldY, 2)));
                            mBalldX = (float) (Math.sin(theta) * absV);
                            mBalldY = (float) (Math.cos(theta) * absV);

                        }else {
                            float xHat = (ballRect.centerX() - (paddleRect.centerX() + (float)(paddleBounds.width)/2))/(float)(paddleBounds.width)/2;
                            float theta = xHat * 67.5f;
                            double absV = Math.abs(Math.sqrt(Math.pow(mBalldX, 2) + Math.pow(mBalldY, 2)));
                            mBalldX = (float) (Math.sin(theta) * absV);
                            mBalldY = (float) (Math.cos(theta) * absV);
                        }
                    }else{
                        mBalldY = mBalldY * -1;
                    }


                }else if(ballRect.intersect(bottomWall)){
                    playBallLost();
                    mBall.kill();
                    mBallsLeft--;
                    mGameStats.setBalls(mBallsLeft);
                    if(mBallsLeft == 0){
                        restartGame();
                    }else{
                        restartLevel();
                    }
                }

                mBall.updateCoords((int)(x + mBalldX), (int)(y + mBalldY));
            }



    }

    private boolean intersectBricks(Rect rect){
        for(int i = 0; i < TOTAL_BRICK_ROW; i++){
            for(int j = 0; j < TOTAL_BRICK_COL; j++){
                if(mBricks[i][j] != null){
                    if(!mBricks[i][j].isDead()){
                        Brick brick = mBricks[i][j];
                        Rectangle brickBoundary = brick.getBoundary();
                        int left = brickBoundary.x;
                        int right = brickBoundary.x + brickBoundary.width;
                        int top = brickBoundary.y;
                        int bottom = brickBoundary.y + brickBoundary.height;
                        Rect brickRect = new Rect(left, top, right, bottom);
                        if(brickRect.intersect(rect)){

                            mBricks[i][j].hit();

                            if(brick.hitsLeft() == 1){
                                mBricks[i][j].changeColor();
                                return true;
                            }else if(brick.hitsLeft() == 0){
                                mBricks[i][j].kill();
                                mCurBricks--;
                                mGameStats.setScore(mGameStats.getScore() + 1);
                                mGameStats.setBricks(mCurBricks);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void updatePaddlePosition(String paddleDirection){
        if(paddleDirection.compareTo(PADDLE_LEFT) == 0 && mPaddle.getBoundary().x > mBoardLeft - 250){
            mPaddle.updateCoords((int)(mPaddle.getBoundary().x - mPaddleDx), mPaddle.getBoundary().y);
        }else if(paddleDirection.compareTo(PADDLE_RIGHT) == 0 && mPaddle.getBoundary().x < mBoardRight - 150){
            mPaddle.updateCoords((int)(mPaddle.getBoundary().x + mPaddleDx), mPaddle.getBoundary().y);
        }else{ //paddle stop

        }
    }


    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){
        mOriginX = (float)w/2 + mPadding;
        mOriginY = (float)h/2 + mPadding;
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //get scale factor
        float heightRatio = (height / asRatHeight);
        float widthRatio = (width / asRatWidth);
        float scaleFactor = Math.min(widthRatio, heightRatio);

        int desiredWidth = (int)(asRatWidth * scaleFactor);
        int desiredHeight = (int)(asRatHeight * scaleFactor);

        setMeasuredDimension(desiredWidth, desiredHeight);

    }



    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        if(mGameStarted){
            if(!checkLevelOver()){
                updateBallPosition();
                invalidate();
            }else{
                nextLevel();
            }

        }

    }

    private boolean checkLevelOver(){
        int totalBricks = INITIAL_BRICKS;
        for(int i = 0; i < TOTAL_BRICK_ROW && totalBricks > 0; i++){
            for(int j = 0; j < TOTAL_BRICK_COL && totalBricks > 0; j++){
                if(!mBricks[i][j].isDead()){ //if brick alive then not all dead
                    return false;
                }
                totalBricks --;
            }
        }
        return true; //level over
    }

    private void restartGame(){
        pause();
        Toast.makeText(getContext(),
                "Game Over",
                Toast.LENGTH_SHORT)
                .show();
        mGameStarted = false;
        mGameStats.notifyGameOverOrNextLevel();

    }

    private void restartLevel(){
        pause();
        Toast.makeText(getContext(),
                "New ball",
                Toast.LENGTH_SHORT)
                .show();

        ballSet = false;
        paddleSet = false;
    }

    private void nextLevel(){
        pause();
        Toast.makeText(getContext(),
                "Next Level",
                Toast.LENGTH_SHORT)
                .show();
        mGameStats.setLevel(mGameStats.getLevel() + 1);
        mGameStats.setBricks(INITIAL_BRICKS);
        bricksArrayFilled = false;
        paddleSet = false;
        ballSet = false;
        mBallSpeed = (float) (mBallSpeed * 1.33);
        invalidate();
    }

    public void onGameScreenClick(){
        if(mGamePaused){
            resume();
        }else{
            pause();
        }
    }

    public void playBounce(){
        ToneGenerator tg = new
                ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100) ;
        tg.startTone(ToneGenerator.TONE_PROP_BEEP) ;
        tg.release() ;

    }

    public void playBallLost(){
        ToneGenerator tg = new
                ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100) ;
        tg.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_S_X4, 1000) ;
        tg.release() ;

    }

    public void pause(){
        mTimer.pause();
        mGamePaused = true;
    }

    public void resume(){
        mTimer.resume();
        mGamePaused = false;
    }

    public void setInitialPrefs(int[] prefs){
        mInitialPrefStats = prefs;
    }


    public GameStats getGameStatsRef(){
        return mGameStats;
    }

    public boolean isGameRunning(){
        return mGameStarted;
    }

    class GameStats extends Observable {
        private int mCurScore;
        private int mCurLevel;
        private int mCurBalls;
        private int mCurBricks;

        GameStats(int score, int level, int balls, int bricks){
            mCurScore = score;
            mCurLevel = level;
            mCurBalls = balls;
            mCurBricks = bricks;
        }

        void setScore(int score){
            mCurScore = score;
            super.setChanged();
            super.notifyObservers();
        }

        void setLevel(int level){
            mCurLevel = level;
            super.setChanged();
            super.notifyObservers();
        }

        void setBalls(int balls){
            mCurBalls = balls;
            super.setChanged();
            super.notifyObservers();
        }

        void setBricks(int bricks){
            mCurBricks = bricks;
            super.setChanged();
            super.notifyObservers();
        }

        int getScore(){
            return mCurScore;
        }

        int getLevel(){
            return mCurLevel;
        }

        int getBalls(){
            return mCurBalls;
        }

        int getBricks(){
            return mCurBricks;
        }

        void notifyGameOverOrNextLevel(){
            super.setChanged();
            super.notifyObservers();
        }


    }

}
