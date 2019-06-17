package com.isaacson.josie.jisaacsonfinalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends AppCompatActivity implements Observer{
    private Game mGame;
    private Game.GameStats mGameStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View mainView = findViewById(R.id.mainLayout);
        mGame = mainView.findViewById(R.id.gameMain);
        if(mGame.mInitialPrefStats == null){
            mGame.setInitialPrefs(new int[]{5,2,3});
        }
        mGameStats = mGame.getGameStatsRef();
        mGameStats.addObserver(this);

        TextView stats = findViewById(R.id.scoreTextView);
        String statsToWrite = "Score = 0 Level = 1 Balls = " + mGameStats.getBalls() + " Bricks = " + mGameStats.getBricks();
        stats.setText(statsToWrite);

        Button leftBut = findViewById(R.id.leftButton);
        leftBut.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mGame.onLeftClick();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    mGame.onPaddleStop();
                    return true;
                }
                return false;
            }
        });


        Button rightBut = findViewById(R.id.rightButton);
        rightBut.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mGame.onRightClick();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    mGame.onPaddleStop();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        mGame.pause();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Toast.makeText(this,
                    "Final Project, Winter 2019, Josie Isaacson",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }


        if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void update(Observable o, Object arg) {
        if(mGame.isGameRunning()){
            TextView stats = findViewById(R.id.scoreTextView);
            int score = mGameStats.getScore();
            int level = mGameStats.getLevel();
            int balls = mGameStats.getBalls();
            int bricks = mGameStats.getBricks();
            String statsToWrite = "Score = " + score + " Level = " + level + " Balls = " + balls + " Bricks = " + bricks;
            stats.setText(statsToWrite);
        }else{
            //game over
            Toast.makeText(this,
                    "Game Over",
                    Toast.LENGTH_SHORT)
                    .show();
            View parentView = findViewById(R.id.mainLayout);
            Snackbar.make(parentView, "Restart Game? " ,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.confirm_button, new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                           restart();
                        }
                    })
                    .show();

        }


    }

    public void restart(){
        this.recreate();
    }

    public void onGameScreenClick(View v){
        mGame.onGameScreenClick();
    }

    @Override
    public void onResume(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //bricks
        String[] bricksString = prefs.getString("pref_bricks", "5").split(" ");
        int bricks = Integer.parseInt(bricksString[bricksString.length - 1]);
        //hits
        String[] hitsString = prefs.getString("pref_hits", "2").split(" ");
        int hits = Integer.parseInt(hitsString[hitsString.length - 1]);
        //balls
        String[] ballsString = prefs.getString("pref_balls", "3").split(" ");
        int balls = Integer.parseInt(ballsString[ballsString.length - 1]);

        mGame.setInitialPrefs(new int[]{bricks, hits, balls});

        super.onResume();
    }
}

