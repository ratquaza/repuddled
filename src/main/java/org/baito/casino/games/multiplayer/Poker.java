package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.casino.Deck;

import javax.annotation.Nullable;
import java.util.*;

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
                            .setAuthor("Drew " + e.getValue().cardA.toStringUTF() + " and " + e.getValue().cardB.toStringUTF())
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

        public void clear() {
            cardA = null;
            cardB = null;
        }

        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof PokerHand)) return false;

            PokerHand other = (PokerHand) o;

            return cardA.suite == other.cardA.suite && cardA.value == other.cardA.value &&
                    cardB.suite == other.cardB.suite && cardB.value == other.cardB.value;
        }
    }

    private static class Community {
        private ArrayList<Deck.Card> cards;

        public Community() {
            cards = new ArrayList<>();
        }

        public void draw(Deck d) {
            for (int i = 0; i < 5; i++) {
                cards.add(d.draw());
            }
        }

        public void clear() {
            cards.clear();
        }

        public PokerHand bestOf(PokerHand handOne, PokerHand handTwo) {
            return getType(handOne).type.ordinal() > getType(handTwo).type.ordinal() ? handOne :
                    getType(handTwo).type.ordinal() > getType(handOne).type.ordinal() ? handTwo :
                            null;
        }

        public RankedHand getType(PokerHand hand) {
            if (hand.cardA == null || hand.cardB == null) return new RankedHand(HandType.EMPTY);

            ArrayList<Deck.Card> combined = new ArrayList<>(Arrays.asList(hand.cardA, hand.cardB));
            combined.addAll(cards);

            Collections.sort(combined, new Comparator<Deck.Card>() {
                @Override
                public int compare(Deck.Card o1, Deck.Card o2) {
                    return Integer.compare(o1.value.ordinal(), o2.value.ordinal());
                }
            });

            return new RankedHand(HandType.EMPTY);
        }
    }

    private enum HandType {
        EMPTY, HIGHCARD,
        PAIR, TWOPAIR,
        THREEKIND, STRAIGHT,
        FLUSH, FULLHOUSE,
        FOURKIND, STRAIGHTFLUSH,
        ROYALFLUSH;
    }

    private static class RankedHand {

        private final HandType type;
        private final ArrayList<Deck.Card> cards;

        public RankedHand(HandType type, Deck.Card... cards) {
            this.type = type;
            this.cards = new ArrayList<>(Arrays.asList(cards));
        }
    }
}
