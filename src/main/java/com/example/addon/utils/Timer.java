package com.example.addon.utils;

//import com.example.addon.modules.ProximaScaffold; /// Господи, чашка, что это за хрень?
import net.minecraft.util.math.MathHelper;

public class Timer {
    private long time;

    public Timer() {
        time = System.currentTimeMillis();
    }

    public static float[] Field2558 = new float[20];

    public static float Method2190() {
        float f;
        float f2;
        float f3;
        try {
            f3 = Field2558[Field2558.length - 1];
            f2 = 0.0f;
            f = 20.0f;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 20.0f;
        }
        return MathHelper.clamp((float) f3, (float) f2, (float) f);
    }

    public boolean hasPassed(double ms) {
        return System.currentTimeMillis() - time >= ms;
    }

    public void reset() {
        time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    //private static ProximaScaffold currentModule;
    private static int priority;
    private static float timerSpeed;
    private static boolean active = false;
    private boolean tpsSync = false;
	/*
    public static void updateTimer(ProximaScaffold module, int prio, float speed) {
        if (module == currentModule) {
            priority = prio;
            timerSpeed = speed;
            active = true;
        } else if (priority > prio || !active) {
            currentModule = module;
            priority = prio;
            timerSpeed = speed;
            active = true;
        }
    }

    public static void resetTimer(ProximaScaffold module) {
        if (currentModule == module) {
            active = false;
        }
    }
	*/
	
}
