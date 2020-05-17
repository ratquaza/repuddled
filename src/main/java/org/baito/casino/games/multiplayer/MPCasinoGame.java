package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.casino.Casino;

import net.dv8tion.jda.api.entities.Member;

public abstract class MPCasinoGame{

    // Game name
    public final String name;
    // Minimum level a player must be
    public final int levelMinimum;
    // Maximum players
    public final int maxPLayers;
    // Minimum players
    public final int minPlayers;

    // Minimum bet players need
    protected int bet;
    // The pot, all the money bet
    protected int pot;
    // If using maple
    protected boolean useMaple;

    public MPCasinoGame(String name, int levelMinimum, int maxPlayers, int minPlayers) {
        this.name = name;
        this.levelMinimum = levelMinimum;
        this.maxPLayers = Math.max(maxPlayers, 2);
        this.minPlayers = Math.max(2, Math.min(maxPlayers, minPlayers));
    }

    public abstract MPCasinoGame newInstance();

    public abstract void setPlayers(Member... players);

    public abstract void setup(MessageChannel channel);

    public abstract void turn(Member m, MessageChannel channel, String[] args);

    public abstract Member[] getPlayers();

    public abstract Member getCurrentTurn();

    public final void setValues(int bet, boolean useMaple, Member... players) {
        this.bet = bet;
        this.useMaple = useMaple;
        this.pot = players.length * bet;
        setPlayers(players);
    }

    public final String name() {
        return name.toUpperCase();
    }

    public final void endGame(MessageChannel channel) {
        Casino.endMPGame(getPlayers());
    }

    public final boolean useMaple() {
        return useMaple;
    }

    public final int getBet() {
        return bet;
    }

    public final int levelMinimum() {
        return levelMinimum;
    }
}
