package org.baito.bevent.events;

import net.dv8tion.jda.api.entities.User;

public class LevelUpEvent extends BEvent {

    private User user;
    private int prevLevel;
    private int newLevel;

    private boolean isCancelled = false;

    public LevelUpEvent(User user, int prevLevel, int newLevel) {
        this.user = user;
        this.prevLevel = prevLevel;
        this.newLevel = newLevel;
    }

    public User getUser() {
        return user;
    }

    public int previousLevel() {
        return prevLevel;
    }

    public int newLevel() {
        return newLevel;
    }

    public void setCancelled(boolean c) {
        isCancelled = c;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
