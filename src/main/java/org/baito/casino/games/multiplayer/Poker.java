package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.casino.Deck;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Poker extends MPCasinoGame {
    private HashMap<Member, PokerHand> hands;
    private Deck deck;
    private Community community;

    public Poker() {
        super("Poker", 0, 6, 2);
    }

    @Override
    public MPCasinoGame newInstance() {
        return new Poker();
    }

    @Override
    public void setPlayers(Member... players) {
        for (Member i : players) {
            hands.put(i, new PokerHand(bet));
        }
    }

    @Override
    public void setup(MessageChannel channel) {
        pot = 0;
        deck = new Deck();
        community = new Community();
        hands.entrySet().parallelStream().forEach(e -> {
            e.getValue().draw(deck);
            e.getKey().getUser().openPrivateChannel().complete().sendMessage(
                    new EmbedBuilder()
                            .setAuthor("Drew " + e.getValue().cardA + " and " + e.getValue().cardB)
                            .setColor(e.getKey().getRoles().get(0).getColor()).build()
            ).queue();
        });
        community.draw(deck);
    }

    @Override
    public void turn(Member m, MessageChannel channel, String[] args) {

    }

    @Override
    public Member[] getPlayers() {
        return hands.keySet().toArray(new Member[0]);
    }

    @Nullable
    public HashMap<Member, Integer> getWinnings() {
        HashMap<Member, Integer> winnings = new HashMap<>();
        for (Map.Entry<Member, PokerHand> i : hands.entrySet()) {
            winnings.put(i.getKey(), i.getValue().money);
        }
        return winnings;
    }

    private static class PokerHand {
        private int money = 0;
        private Deck.Card cardA;
        private Deck.Card cardB;

        public PokerHand(int init) {
            money = init;
        }

        public void draw(Deck d) {
            cardA = d.draw();
            cardB = d.draw();
        }
    }

    private static class Community {
        private Deck.Card[] cards;

        public Community() {
            cards = new Deck.Card[5];
        }

        public void draw(Deck d) {
            for (int i = 0; i < cards.length; i++) {
                cards[i] = d.draw();
            }
        }

        public PokerHand bestOf(PokerHand handOne, PokerHand handTwo) {
            return handOne;
        }
    }
}
