package com.adform.sdk2.network.base.ito.observable;

import android.os.Handler;
import android.util.Log;
import com.adform.sdk2.Constants;
import com.adform.sdk2.network.base.ito.network.NetworkTask;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 8/2/13
 * Time: 4:53
 */
@Deprecated // Instead use ObservableService2
public abstract class ObservableService extends Observable {

    private int mTimerTimeout = Constants.REFRESH_SECONDS; // Timer when task should execute
    private int mTimePassed; // Counted time

    /* repetitive request scheduler */
    private Handler mScheduler;

    /* for debugging purposes */
    private int requestSequenceNumber;

    /* currently running task */
    private NetworkTask<?> mRunningTask;

    public enum Status {
        STOPPED(0),
        RUNNING(1),
        PAUSED(2);
        private int value;

        private Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Status parseType(int status) {
            switch (status) {
                case 0: return STOPPED;
                case 1: return RUNNING;
                case 2: return PAUSED;
                default: return STOPPED;
            }
        }

    }

    private Status mStatus = Status.STOPPED;

    protected ObservableService() {
        mScheduler = new Handler();
        mStatus = Status.STOPPED;
    }

    /**
     *  tag must be returned by inheriting classes!
     */
    public String getTag() {
        return null;
    }

    public void startService(){
        mTimePassed = 0;
        mTimerTimeout = Constants.REFRESH_SECONDS;
        Log.d(getTag(), "start service");
        requestSequenceNumber = 0;
        mStatus = Status.RUNNING;
        onStartService();
    }

    protected abstract void onStartService();

    public void pauseService() {
        Log.d(getTag(), "pause service");
        //pause only running service
        if(mStatus == Status.RUNNING){
            mStatus = Status.PAUSED;
            // remove all requests from scheduler
            stopCurrentTask();
            onPauseService();
        } else {
            Log.d(getTag(), "service not running nothing to pause");
        }
    }

    protected abstract void onPauseService();

    /**
     * Resumes service. It only resumes if service was stopped before. Calls onResumeService() after resume
     */
    public void resumeService(int timePassed) {
        mTimePassed = timePassed;
        Log.d(getTag(), "resume service");
        if(mStatus == Status.PAUSED || mStatus == Status.RUNNING){
            Log.d(getTag(), "resuming...");
            mStatus = Status.RUNNING;
            onResumeService();
        } else {
            Log.d(getTag(), "ignoring resume, service was not paused.");
        }
    }

    protected abstract void onResumeService();

    public void stopService(){
        mTimePassed = 0;
        mTimerTimeout = Constants.REFRESH_SECONDS;
        Log.d(getTag(), "stop service");
        mStatus = Status.STOPPED;
        stopCurrentTask();

        onStopService();
    }

    protected abstract void onStopService();

    /**
     * does not stops service, be careful using this, don't forget to schedule new request
     */
    protected void stopCurrentTask() {
        mScheduler.removeCallbacksAndMessages(null);
        if(mRunningTask != null){
            mRunningTask.cancel(true);
            mRunningTask = null;
        }
    }

    private Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTimePassed < mTimerTimeout) {
                Log.e(getTag(), "Task execution in "+(mTimerTimeout- mTimePassed));
                timerTick();
            } else {
                Log.e(getTag(), "Executing task...");
                executeTask();
            }
        }
    };

    private void timerTick() {
        if (mStatus == Status.RUNNING) {
            mScheduler.postDelayed(tickRunnable, 1000);
            mTimePassed++;
        }
    }

    private void executeTask() {
        if (mStatus == Status.RUNNING) {
            try {
                mRunningTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void scheduleNewRequest(final NetworkTask<?> task, int seconds) {
        mTimePassed = 0;
        mTimerTimeout = seconds;
        Log.d(getTag(), "#" + requestSequenceNumber + " scheduleNewRequest: " + task.getRequest().getUrl() + " in " + seconds + "s");
        requestSequenceNumber++;
        mRunningTask = task;
        timerTick();
    }

    public void resumeScheduledRequest(final NetworkTask<?> task) {
        Log.d(getTag(), "#" + requestSequenceNumber + " resuming scheduled request: " + task.getRequest().getUrl());
        requestSequenceNumber++;
        mRunningTask = task;
        timerTick();
    }

    public void scheduleRequestImmediate(final NetworkTask<?> task) {
        Log.d(getTag(), "#" + requestSequenceNumber + " schedule request immediate: " + task.getRequest().getUrl());
        stopCurrentTask();
        requestSequenceNumber++;

        if (mStatus == Status.RUNNING) {
            mRunningTask = task;
            mRunningTask.execute();
        }
    }

    void setRunningTask(NetworkTask mRunningTask) {
        this.mRunningTask = mRunningTask;
    }

    public void triggerObservers(Object data) {
        setChanged();
        if(data!= null){
            notifyObservers(data);
        } else {
            notifyObservers();
        }
    }

    public int getTimePassed() {
        return mTimePassed;
    }

    public void setTimePassed(int timePassed) {
        this.mTimePassed = timePassed;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public int getTimerTimeout() {
        return mTimerTimeout;
    }

    public void setTimerTimeout(int timerTimeout) {
        this.mTimerTimeout = timerTimeout;
    }
}
