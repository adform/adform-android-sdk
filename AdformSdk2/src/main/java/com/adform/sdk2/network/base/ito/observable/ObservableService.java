package com.adform.sdk2.network.base.ito.observable;

import android.os.Handler;
import android.util.Log;
import com.adform.sdk2.network.base.ito.network.NetworkTask;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: andrius
 * Date: 8/2/13
 * Time: 4:53
 */
public abstract class ObservableService extends Observable {

    /* default refresh interval in seconds */
    protected final int DEFAULT_REFRESH_INTERVAL = 60;

    /* repetitive request scheduler */
    private final Handler mScheduler;

    /* for debugging purposes */
    private int requestSequenceNumber;

    /* currently running task */
    private NetworkTask<?> mRunningTask;

    public enum Status {
        STOPPED,
        RUNNING,
        PAUSED
    }

    private Status mStatus = Status.STOPPED;

    protected ObservableService() {
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
    public void resumeService() {
        Log.d(getTag(), "resume service");
        if(mStatus == Status.PAUSED){
            Log.d(getTag(), "resuming...");
            mStatus = Status.RUNNING;
            onResumeService();
        } else {
            Log.d(getTag(), "ignoring resume, service was not paused.");
        }
    }

    protected abstract void onResumeService();

    public void stopService(){
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

    public void scheduleRequest(final NetworkTask<?> task, int seconds) {
        Log.d(getTag(), "#" + requestSequenceNumber + " scheduleRequest: " + task.getRequest().getUrl() + " in " + seconds + "s");
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
        }, seconds * 1000);
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
}
