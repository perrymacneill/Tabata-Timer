package com.perrymacneill.tabataintervaltimer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    //tags for fragments
    private final String START_TAG = "start", TIMER_TAG = "timer";

    //constants for intervals
    private final int WORK_INTERVAL = 20, REST_INTERVAL = 10;

    //constants for what strings to display
    private final String REST = "Rest!", WORK = "Work!", PREPARE = "Prepare!", FINISH = "Finished!";

    //used for timer
    private int mMillisInFuture, mCurrentSet, mCountdownInterval;
    private long mStopTimeInFuture;
    private Thread mTimerThread;

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
    }


    public void startTimer(View view) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new TimerFragment(), TIMER_TAG);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        getFragmentManager().executePendingTransactions();

        //initialize variables for the timer
        mCurrentSet = 0;
        mCountdownInterval = 1000;

        //begin prep interval
        setTimer(REST_INTERVAL);
    }

    public void setTimer(int inputTime) {

        //input time in millis
        mMillisInFuture = inputTime * 1000;
        final int interval = inputTime;

        //total interval time relative to current time
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;

        final TimerFragment fragment = (TimerFragment) getFragmentManager()
                .findFragmentByTag(TIMER_TAG);

        if (inputTime == REST_INTERVAL) {
            if (mCurrentSet != 0 && mCurrentSet != 8) {
                //this is a rest interval
                fragment.setText(REST, 0);

            } else if (mCurrentSet == 8) {
                //workout is finished
                fragment.setText(FINISH, 0);
                fragment.setTime(0);
                mCurrentSet = 0;

                //removes callbacks and return
                mHandler.removeCallbacksAndMessages(null);
                return;

            } else {
                //the set is 0, so this is a prep interval
                fragment.setText(PREPARE, 0);
            }

        } else {
            //this is a work interval, so increase the current set
            mCurrentSet++;

            //set text and increase progress bar
            fragment.setText(WORK, mCurrentSet);
            fragment.setProgressBar(mCurrentSet);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                if (interval == WORK_INTERVAL) {
                    mHandler.sendMessage(mHandler.obtainMessage(WORK_INTERVAL));
                } else if (interval == REST_INTERVAL) {
                    mHandler.sendMessage(mHandler.obtainMessage(REST_INTERVAL));
                }
            }
        };

        mTimerThread = new Thread(runnable);
        mTimerThread.start();
    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

            if (millisLeft <= 0) {
                //interval is finished, start next interval based on current
                if (msg.what == WORK_INTERVAL) {
                    setTimer(REST_INTERVAL);
                } else {
                    setTimer(WORK_INTERVAL);
                }
            } else if (millisLeft < mCountdownInterval) {
                //interval not reached, delay by time left
                sendMessageDelayed(obtainMessage(msg.what), millisLeft);
            } else {
                long lastTickStart = SystemClock.elapsedRealtime();
                TimerFragment fragment = (TimerFragment) getFragmentManager()
                        .findFragmentByTag(TIMER_TAG);

                if (fragment != null) {
                    int secsLeft = (int) millisLeft / 1000;

                    //this is do deal with the chance of hitting the exact full interval
                    if (secsLeft == 20) {
                        secsLeft = 19;
                    }
                    if (secsLeft == 10) {
                        secsLeft = 9;
                    }

                    //update fragment with time left in interval
                    fragment.setTime(secsLeft);
                }
                //take into account time to execute, start next tick
                long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
                sendMessageDelayed(obtainMessage(msg.what), delay);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //going back is treated as a reset, remove callbacks and interrupt thread
        mTimerThread.interrupt();
        mHandler.removeCallbacksAndMessages(null);
        mCurrentSet = 0;
    }

    //TODO implement options
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
}
