package com.perrymacneill.tabataintervaltimer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    public static final String START_TAG = "start", TIMER_TAG = "timer";
    public static final int WORK_INTERVAL = 20;
    public static final int REST_INTERVAL = 10;
    private static final int MSG = 1;

    int mMillisInFuture;
    int mCountdownInterval = 1000;
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
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//
////        StartFragment fragment = new StartFragment();
//        fragmentTransaction.add(R.id.container, new StartFragment(), START_TAG);
//        fragmentTransaction.commit();
    }


    public void startTimer(View view) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new TimerFragment(), TIMER_TAG);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
        getFragmentManager().executePendingTransactions();

        setTimer(REST_INTERVAL);
    }

    public void setTimer(int time) {

        TimerFragment fragment = (TimerFragment) getFragmentManager()
                .findFragmentByTag(TIMER_TAG);

        mMillisInFuture= time*1000;
        final int interval = time;
        //runTimer(fragment, WORK_INTERVAL);
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;

        Runnable runnable = new Runnable() {
            public void run() {
                if(interval == WORK_INTERVAL) {
                    mHandler.sendMessage(mHandler.obtainMessage(WORK_INTERVAL));
                }
                else if(interval == REST_INTERVAL) {
                    mHandler.sendMessage(mHandler.obtainMessage(REST_INTERVAL));
                }
            }
        };

        mTimerThread = new Thread(runnable);
        mTimerThread.start();


        //mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        //mHandler.sendMessage(mHandler.obtainMessage(MSG));

//        for(int work = NUM_SETS; work > 0; work--) {
//            runTimer(fragment, 20000);
//            for(int rest = NUM_SETS - 1; rest> 0; rest--) {
//                runTimer(fragment, 10000);
//            }
//        }
    }

    public void runTimer(TimerFragment f, int time) {

        final TimerFragment fragment = f;

        CountDownTimer countDownTimer = new CountDownTimer(time, 800) {

            public void onTick(long millisUntilFinished) {
                if (fragment != null) {
                    //Log.d("log", "" + millisUntilFinished / 1000);
                    fragment.setTime(Math.round(millisUntilFinished * 0.001f));
                }
            }

            public void onFinish() {
                //startRest(fragment);
                runTimer(fragment, 10000);
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

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

            if (millisLeft <= 0) {
                //onFinish();
                switch(msg.what) {
                    case WORK_INTERVAL:
                        setTimer(REST_INTERVAL);
                        break;
                    case REST_INTERVAL:
                        setTimer(WORK_INTERVAL);
                        break;
                    default:
                        break;
                }
            } else if (millisLeft < mCountdownInterval) {
                // no tick, just delay until done
                sendMessageDelayed(obtainMessage(msg.what), millisLeft);
            } else {
                long lastTickStart = SystemClock.elapsedRealtime();
               // onTick(millisLeft);
                TimerFragment fragment = (TimerFragment) getFragmentManager()
                        .findFragmentByTag(TIMER_TAG);

                if(fragment != null) {
                    fragment.setTime(millisLeft / 1000);
                    Log.d("log", String.valueOf(millisLeft / 1000));
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
