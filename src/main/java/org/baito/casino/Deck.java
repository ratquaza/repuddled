package org.baito.casino;

import java.util.ArrayList;
import java.util.Arrays;

public class Deck {

    public ArrayList<Card> cards = new ArrayList<>();

    public Deck() {
        regen();
    }

    public boolean contains(CardValue v) {
        for (Card i : cards) {
            if (i.number == v) return true;
        }
        return false;
    }

    public boolean contains(CardSuite s) {
        for (Card i : cards) {
            if (i.suite == s) return true;
        }
        return false;
    }

    public boolean contains(CardSuite s, CardValue v) {
        for (Card i : cards) {
            if (i.number == v && i.suite == s) return true;
        }
        return false;
    }

    public boolean contains(Card c) {
        for (Card i : cards) {
            if (i.equals(c)) return true;
        }
        return false;
    }

    public int count(CardValue v) {
        int count = 0;
        for (Card i : cards) {
            if (i.number == v) count++;
        }
        return count;
    }

    public int count(CardSuite s) {
        int count = 0;
        for (Card i : cards) {
            if (i.suite == s) count++;
        }
        return count;
    }

    public int size() {
        return cards.size();
    }

    public void regen() {
        for (CardSuite s : CardSuite.values()) {
            for (CardValue v : CardValue.values()) {
                cards.add(new Card(s, v));
            }
        }
    }

    public Card draw() {
        int choice = (int) Math.floor(Math.random() * cards.size());
        Card card = cards.get(choice);
        cards.remove(choice);
        return card;
    }

    public void remove(CardSuite suite, CardValue value) {
        for (Card i : cards) {
            if (i.number == value && i.suite == suite) {
                cards.remove(i);
                return;
            }
        }
    }

    public void add(CardSuite suite, CardValue value) {
        cards.add(new Card(suite, value));
    }

    public void addAll(Card... cardsToAdd) {
        cards.addAll(Arrays.asList(cardsToAdd));
    }

    public void addAll(Deck d) {
        cards.addAll(d.cards);
    }

    public static class Card {
        public CardSuite suite;
        public CardValue number;

        public Card(CardSuite suite, CardValue value) {
            this.suite = suite;
            this.number = value;
        }

        public String toString() {
            return suite + " " + number;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Card)) return false;
            Card c = (Card) o;
            return c.suite == suite && c.number == number;
        }
    }

    public enum CardSuite {
        SPADES("\u2660"),
        CLUBS("\u2663"),
        DIAMONDS("\u2666"),
        HEARTS("\u2665");

        public String character;

        CardSuite(String s) {
            character = s;
        }
    }

    public enum CardValue {
        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13);

        public int value;

        CardValue(int value) {
            this.value = value;
        }

        public static CardValue intToValue(int i) {
            i = Math.max(1, Math.min(13, i));
            for (CardValue c : values()) {
                if (c.value == i) return c;
            }
            return ACE;
        }
    }
}
