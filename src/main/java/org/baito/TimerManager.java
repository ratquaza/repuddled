package org.baito;

import org.baito.API.Duration;

import java.util.*;

public final class TimerManager {
    private static Timer timer = new Timer();
    private static HashMap<String, TimerTask> tasks = new HashMap<>();

    public static void register(String name, TimerTask task, long delay, long repeat) {
        if (tasks.containsKey(name)) {
            tasks.get(name).cancel();
        }
        tasks.put(name, task);
        timer.schedule(task, delay, repeat);
    }

    public static boolean exists(String s) {
        return tasks.containsKey(s);
    }

    public static TimerTask getTimer(String s) {
        return tasks.getOrDefault(s, null);
    }

    public static void init() {
        Calendar currentTime = Main.getCalendar();
        Duration duration = new Duration().setSeconds(3600 - currentTime.get(Calendar.MINUTE) * 60 - currentTime.get(Calendar.SECOND));

        register("HOURLY", new TimerTask() {
            @Override
            public void run() {
                Calendar prev = (Calendar) Main.getCalendar().clone();
                prev.set(Calendar.HOUR_OF_DAY, prev.get(Calendar.HOUR_OF_DAY) - 1);
                Calendar curr = (Calendar) Main.getCalendar().clone();
                Events.onHourly(curr);
            }
        }, duration.toMilliseconds(), new Duration().setHours(1).toMilliseconds());
    }

}
