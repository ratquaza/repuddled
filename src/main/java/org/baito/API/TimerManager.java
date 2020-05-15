package org.baito.API;

import org.baito.Main;
import org.baito.bevent.BEventManager;
import org.baito.bevent.events.HourlyEvent;

import java.text.SimpleDateFormat;
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

    public static final int PUDDLE_DAY_INTERVAL = 8;

    static {
        Calendar currentTime = Main.getCalendar();
        Duration duration = new Duration().setSeconds(3600 - currentTime.get(Calendar.MINUTE) * 60 - currentTime.get(Calendar.SECOND));

        register("HOURLY", new TimerTask() {
            @Override
            public void run() {
                BEventManager.onHourly(new HourlyEvent(Main.getCalendar()));
            }
        }, duration.toMilliseconds(), new Duration().setHours(1).toMilliseconds());
    }
}
