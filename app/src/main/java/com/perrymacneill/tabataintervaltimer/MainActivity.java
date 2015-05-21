package com.perrymacneill.tabataintervaltimer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    public static final String START_TAG = "start", TIMER_TAG = "timer";
    public static final String PREFS_NAME = "UserPrefs";
    public static final int NUM_SETS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StartFragment(), START_TAG)
                    .commit();

            getFragmentManager().executePendingTransactions();

        }
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//
////        StartFragment fragment = new StartFragment();
//        fragmentTransaction.add(R.id.container, new StartFragment(), START_TAG);
//        fragmentTransaction.commit();

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean silent = settings.getBoolean("silentMode", false);
        //setSilent(silent);

    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        //editor.putBoolean("silentMode", mSilentMode);

        editor.commit();
    }


    public void startTimer(View view) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new TimerFragment(), TIMER_TAG);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        getFragmentManager().executePendingTransactions();

        setTimer();
    }

    public void setTimer() {

        TimerFragment fragment = (TimerFragment) getFragmentManager()
                .findFragmentByTag(TIMER_TAG);

        runTimer(fragment, 20000);

//        for(int work = NUM_SETS; work > 0; work--) {
//            runTimer(fragment, 20000);
//            for(int rest = NUM_SETS - 1; rest> 0; rest--) {
//                runTimer(fragment, 10000);
//            }
//        }
    }

    public void runTimer(TimerFragment f, int time) {

        final TimerFragment fragment = f;

        CountDownTimer countDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                if (fragment != null) {
                    Log.d("log", "" + millisUntilFinished / 1000);
                    fragment.setTime(millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                startRest(fragment);
            }
        }.start();
    }

    public void startRest(TimerFragment f) {

        final TimerFragment fragment = f;

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (fragment != null) {
                    fragment.setTime(millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                runTimer(fragment, 20000);
            }
        }.start();
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

    /**
     * A placeholder fragment containing a simple view.
     */
}
