package org.baito.API;

public final class Duration {

    private int days = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;

    public static final int MILLISECOND = 1000;

    public Duration() {}

    public Duration setSeconds(int s) {
        if (s < 0) return this;
        if (s > 59) {
            int m = s/60;
            s -= (60 * m);
            setMinutes(m);
        }
        this.seconds = s;
        return this;
    }

    public Duration setMinutes(int m) {
        if (m < 0) return this;
        if (m > 59) {
            int h = m/60;
            m -= (h * 60);
            setHours(h);
        }
        this.minutes = m;
        return this;
    }

    public Duration setHours(int h) {
        if (h < 0) return this;
        if (h > 23) {
            int d = h/24;
            h -= (d * 24);
            setDays(d);
        }
        this.hours = h;
        return this;
    }

    public Duration setDays(int d) {
        if (d < 0) return this;
        this.days = d;
        return this;
    }

    public long toMilliseconds() {
        return (seconds * MILLISECOND) + (minutes * (MILLISECOND * 60)) + (hours * (MILLISECOND * 60 * 60)) + (days * (MILLISECOND * 60 * 60 * 24));
    }

    public long getMinutes() { return minutes; }

    public long getHours() {
        return hours;
    }

    public long getSeconds() { return seconds; }

    public long detDays() { return days; }
}
