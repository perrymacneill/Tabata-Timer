package com.perrymacneill.tabataintervaltimer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    public final String START_TAG = "start", TIMER_TAG = "timer";
    public final int WORK_INTERVAL = 20, REST_INTERVAL = 10;
    public final String REST = "Rest!", WORK = "Work!", PREPARE = "Prepare!", FINISH = "Finished!";

    int mMillisInFuture;
    int mCurrentSet;
    int mCountdownInterval;
    long mStopTimeInFuture;
    Thread mTimerThread;

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

        mCurrentSet = 0;
        mCountdownInterval = 1000;

        setTimer(REST_INTERVAL);
    }

    public void setTimer(int inputTime) {
        mMillisInFuture = inputTime * 1000;
        final int interval = inputTime;

        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;

        final TimerFragment fragment = (TimerFragment) getFragmentManager()
                .findFragmentByTag(TIMER_TAG);

        Log.d("log", String.valueOf(mCurrentSet));
        if (inputTime == REST_INTERVAL) {
            if (mCurrentSet != 0 && mCurrentSet != 8) {
                fragment.setText(REST, 0);
            } else if (mCurrentSet == 8) {
                fragment.setText(FINISH, 0);
                fragment.setTime(0);
                mCurrentSet = 0;
                mHandler.removeCallbacksAndMessages(null);
                return;
            } else {
                fragment.setText(PREPARE, 0);
            }
        } else {
            mCurrentSet++;
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
                if (msg.what == WORK_INTERVAL) {
                    setTimer(REST_INTERVAL);
                } else {
                    setTimer(WORK_INTERVAL);
                }
            } else if (millisLeft < mCountdownInterval) {
                // no tick, just delay until done
                sendMessageDelayed(obtainMessage(msg.what), millisLeft);
            } else {
                long lastTickStart = SystemClock.elapsedRealtime();
                // onTick(millisLeft);
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

                    fragment.setTime(secsLeft);
                    Log.d("log", String.valueOf(millisLeft));
                }

                // take into account user's onTick taking time to execute
                long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                // special case: user's onTick took more than interval to
                // complete, skip to next interval
                // while (delay < 0) delay += mCountdownInterval;

                sendMessageDelayed(obtainMessage(msg.what), delay);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mTimerThread.interrupt();
        mHandler.removeCallbacksAndMessages(null);
        mCurrentSet = 0;
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
}
