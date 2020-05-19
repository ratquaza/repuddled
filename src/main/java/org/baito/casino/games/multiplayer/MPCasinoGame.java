package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.casino.Casino;

import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nullable;
import java.util.HashMap;

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

    // For registering
    public MPCasinoGame(String name, int levelMinimum, int maxPlayers, int minPlayers) {
        this.name = name;
        this.levelMinimum = levelMinimum;
        this.maxPLayers = Math.max(maxPlayers, 2);
        this.minPlayers = Math.max(1, Math.min(maxPlayers, minPlayers));
    }

    // New, no arg instnace
    public abstract MPCasinoGame newInstance();

    // Set the players playing
    public abstract void setPlayers(Member... players);

    // Initial setup
    public abstract void setup(MessageChannel channel);

    // Parse a "turn" or input from a player. Checking if its a certain players turn
    // must be done here, as the system will not check
    public abstract void turn(Member m, MessageChannel channel, String[] args);

    // Get all the players playing
    public abstract Member[] getPlayers();

    // Auto-pay system
    // Not all functions need to be overwritten, just the one wanted for paying

    // Multiplier function, multiplies the BET by the VALUE GIVEN for the winning amount
    @Nullable
    public HashMap<Member, Double> getMultipliers() {
        return null;
    }

    // Pot percentage function, the winning amount is the PERCENTAGE of the TOTAL POT
    @Nullable
    public HashMap<Member, Double> getPotPercentage() {
        return null;
    }

    // Static winning function, where the value is the full winning
    @Nullable
    public HashMap<Member, Integer> getWinnings() {
        return null;
    }

    // Final functions, can't be overridden

    // Sets protected values, and calls the setPlayers() function
    // Setting players isn't automatically done as some games will wish to store the players
    // differently. Making both an automatic way to set players into an array, along with
    // the subclass setting them is redundant and wastes space.
    public final void setValues(int bet, boolean useMaple, Member... players) {
        this.bet = bet;
        this.useMaple = useMaple;
        this.pot = players.length * bet;
        setPlayers(players);
    }

    // Returns the name of the game
    public final String name() {
        return name.toUpperCase();
    }

    // Ends the game
    public final void endGame(MessageChannel channel, boolean silent) {
        Casino.endMPGame(channel, silent, getPlayers());
    }
    public final void endGame(MessageChannel channel, boolean silent, Member... members) {
        Casino.endMPGame(channel, silent, members);
    }

    // Whether the game is using Maple or Gold
    public final boolean useMaple() {
        return useMaple;
    }

    // Returns the bet every player put in
    public final int getBet() {
        return bet;
    }

    // Returns the pot, which is the total of all the bets
    public final int getPot() {
        return pot;
    }

    // Returns the minimum level required
    public final int levelMinimum() {
        return levelMinimum;
    }
}
