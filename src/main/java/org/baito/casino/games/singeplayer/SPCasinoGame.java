package org.baito.casino.games.singeplayer;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.casino.Casino;

public abstract class SPCasinoGame {

    protected final String name;
    protected final int level;

    protected Member better;
    protected int bet;
    protected boolean useMaple;
    protected double multiplier = 1;

    public SPCasinoGame(String name, int minimum) {
        this.name = name.toUpperCase();
        this.level = minimum;
    }

    public abstract SPCasinoGame newInstance();

    public abstract void setup(MessageChannel channel);

    public abstract void turn(MessageChannel channel, String[] args);

    public final void setValues(Member m, int bet, boolean useMaple) {
        this.better = m;
        this.bet = bet;
        this.useMaple = useMaple;
    }

    public final String name() {
        return name;
    }

    public final void endGame(MessageChannel channel) {
        Casino.endSPGame(better, channel);
    }

    public final double getMultiplier() {
        return multiplier;
    }

    public final boolean useMaple() {
        return useMaple;
    }

    public final int getBet() {
        return bet;
    }

    public final int levelMinimum() {
        return level;
    }
}
