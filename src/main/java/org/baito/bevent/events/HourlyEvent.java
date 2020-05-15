package org.baito.bevent.events;

import org.baito.Main;
import java.util.Calendar;

public class HourlyEvent extends BEvent {

    private static Calendar previous = Main.getCalendarNoUpdate();
    private static Calendar c = Main.getCalendar();

    public HourlyEvent(Calendar time) {
        previous = c;
        c = time;
    }

    public Calendar getTime() {
        return c;
    }

    public Calendar getPrevious() {
        return previous;
    }

}
