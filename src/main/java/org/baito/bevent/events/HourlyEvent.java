package org.baito.bevent.events;

import java.util.Calendar;

public class HourlyEvent extends BEvent {

    private Calendar c;

    public HourlyEvent(Calendar time) {
        this.c = time;
    }

    public Calendar getTime() {
        return c;
    }


}
