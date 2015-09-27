package com.floydjohn.pizzaplanet.data.time;

import com.badlogic.gdx.Gdx;
import com.floydjohn.pizzaplanet.utils.Percentage;

import java.util.TimerTask;

public class Timer implements Percentage {

    private float currentTime;
    private float endTime;
    private TimerTask task;
    private boolean isRunning = false;
    private float delay;

    public Timer() {
    }

    public void update() {
        if (!isRunning) return;
        currentTime += Gdx.graphics.getDeltaTime();
        if (currentTime > endTime) trigger();
    }

    public void start(TimerTask task) {
        this.task = task;
        currentTime = 0;
        endTime = delay;
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }

    private void trigger() {
        isRunning = false;
        task.run();
    }

    @Override
    public float getPercentage() {
        if (!isRunning) return 0;
        return currentTime / endTime;
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
