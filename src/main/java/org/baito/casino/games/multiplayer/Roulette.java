package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nullable;
import java.util.HashMap;

public class Roulette extends MPCasinoGame {

    private HashMap<Member, RouletteBets> bets;

    public Roulette() {
        super("Roulette", 5, 10, 1);
    }

    @Override
    public MPCasinoGame newInstance() {
        return new Roulette();
    }

    @Override
    public void setPlayers(Member... players) {
        bets = new HashMap<>();
        for (Member i : players) {
            bets.put(i, new RouletteBets());
        }
    }

    @Override
    public void setup(MessageChannel channel) {

    }

    @Override
    public void turn(Member m, MessageChannel channel, String[] args) {

    }

    @Override
    public Member[] getPlayers() {
        return bets.keySet().toArray(new Member[0]);
    }

    @Nullable
    @Override
    public HashMap<Member, Double> getMultipliers() {
        return new HashMap<>();
    }

    @Nullable
    @Override
    public HashMap<Member, Double> getPotPercentage() {
        return null;
    }

    private class RouletteBets {

    }
}
