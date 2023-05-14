package com.rogoshum.magickcore.proxy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;

import java.util.List;
import java.util.Timer;
import java.util.Vector;

public class TaskThread extends Thread{
    private int preTick;
    private volatile int tick;
    private Runnable additionTask = null;
    private Runnable additionCatch = null;
    private final List<Runnable> taskList = new Vector<>();

    public TaskThread(String name) {
        super(name);
    }

    public void addTask(Runnable run) {
        taskList.add(run);
    }
    public void setAdditionTask(Runnable runnable) {
        additionTask = runnable;
    }

    public void setAdditionCatch(Runnable runnable) {
        additionCatch = runnable;
    }

    public void tick() {
        int pre = tick;
        tick = 1 + pre;
    }

    public int getTick() {
        return tick;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            executeTask();
        }
    }

    public void executeTask() {
        if(tick > preTick) {
            preTick = tick;
            if(additionTask != null) {
                try {
                    additionTask.run();
                } catch (Exception e) {
                    MagickCore.LOGGER.info("Addition Task Crashed!");
                    interrupt();
                    e.printStackTrace();
                }
            }
            try {
                for (int i = 0; i < taskList.size(); ++i) {
                    Runnable run = taskList.get(i);
                    if(run != null) {
                        run.run();
                    }
                }
                taskList.clear();
            } catch (Exception e) {
                MagickCore.LOGGER.info(getName() + " Crashed!");
                interrupt();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        taskList.clear();
        additionTask = null;
        if(additionCatch != null) {
            additionCatch.run();
            additionCatch = null;
        }
    }
}
