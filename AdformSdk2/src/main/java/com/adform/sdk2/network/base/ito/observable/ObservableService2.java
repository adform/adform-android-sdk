package com.adform.sdk2.network.base.ito.observable;

import android.os.Handler;
import android.util.Log;
import com.adform.sdk2.network.base.ito.network.NetworkTask;

import java.util.Observable;

/**
 * Created by mariusm on 06/05/14.
 */
public abstract class ObservableService2 extends Observable {

    /* repetitive request scheduler */
    private final Handler mScheduler;

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

    protected ObservableService2() {
        mScheduler = new Handler();
        mStatus = Status.STOPPED;
    }

    /**
     *  tag must be returned by inheriting classes!
     */
    public abstract String getTag();

    public void startService(){
        Log.d(getTag(), "start service");
        requestSequenceNumber = 0;
        mStatus = Status.RUNNING;
        onStartService();
    }

    protected abstract void onStartService();

    public void pauseService() {
        Log.d(getTag(),"pause service");
        //pause only running service
        if(mStatus == Status.RUNNING){
            mStatus = Status.PAUSED;
            // remove all requests from scheduler
            stopCurrentTask();
            onPauseService();
        } else {
            Log.d(getTag(),"service not running nothing to pause");

        }
    }

    protected abstract void onPauseService();

    /**
     * Resumes service. It only resumes if service was stopped before. Calls onResumeService() after resume
     */
    public void resumeService() {
        Log.d(getTag(),"resume service");
        if(mStatus == Status.PAUSED){
            Log.d(getTag(),"resuming...");
            mStatus = Status.RUNNING;
            onResumeService();
        } else {
            Log.d(getTag(),"ignoring resume, service not started yes");
        }
    }

    protected abstract void onResumeService();

    public void stopService(){
        Log.d(getTag(),"stop service");
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

    public void scheduleRequest(final NetworkTask<?> task, long millis) {
        Log.e(getTag(),"#"+requestSequenceNumber+ " scheduleRequest: " + task.getRequest().getUrl() + " in " + millis + "millis" );
        requestSequenceNumber++;
        mScheduler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // only when service is running
                if (mStatus == Status.RUNNING) {
                    mRunningTask = task;
                    mRunningTask.execute();
                }
            }
        }, millis);
    }

    public void scheduleRequestImmediate(final NetworkTask<?> task) {
        Log.e(getTag(),"#"+requestSequenceNumber+ " schedule request immediate: " + task.getRequest().getUrl());
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

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

}
