package org.baito.casino.games.singeplayer;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.casino.Deck;

public class Blackjack extends SPCasinoGame {
    private int dealerValue = 0;
    private int playerValue = 0;
    private Deck deck;

    private boolean cardOneAce = false;
    private boolean cardTwoAce = false;

    public Blackjack() {
        super("Blackjack", 0);
    }

    @Override
    public SPCasinoGame newInstance() {
        return new Blackjack();
    }

    @Override
    public void setup(MessageChannel channel) {
        deck = new Deck();

        Deck.Card cardOne = deck.draw();
        Deck.Card cardTwo = deck.draw();

        if (cardOne.value == Deck.CardValue.ACE) {
            if (dealerValue < 11) {
                dealerValue += 11;
            } else {
                dealerValue += 1;
            }
        } else {
            dealerValue += Math.min(10, cardOne.value.value);
        }

        if (cardTwo.value == Deck.CardValue.ACE) {
            if (dealerValue < 11) {
                dealerValue += 11;
            } else {
                dealerValue += 1;
            }
        } else {
            dealerValue += Math.min(10, cardTwo.value.value);
        }

        while (shouldHit(dealerValue, deck) && dealerValue < 21) {
            Deck.Card draw = deck.draw();
            if (draw.value == Deck.CardValue.ACE) {
                if (dealerValue < 11) {
                    dealerValue += 11;
                } else {
                    dealerValue += 1;
                }
            } else {
                dealerValue += Math.min(10, draw.value.value);
            }
        }

        if (dealerValue == 21) {
            channel.sendMessage("The dealer has hit 21! Dealer wins!").queue();
            multiplier = 0;
            endGame(channel);
            return;
        } else if (dealerValue > 21) {
            channel.sendMessage("The dealer hit past 21! You win!").queue();
            multiplier = 1.5;
            endGame(channel);
            return;
        } else {
            channel.sendMessage("The dealer has ended their turn!").queue();
        }

        cardOne = deck.draw();
        cardTwo = deck.draw();

        cardOneAce = cardOne.value == Deck.CardValue.ACE;
        cardTwoAce = cardTwo.value == Deck.CardValue.ACE;

        if (!cardOneAce) {
            playerValue += Math.min(10, cardOne.value.value);
        }
        if (!cardTwoAce) {
            playerValue += Math.min(10, cardTwo.value.value);
        }

        channel.sendMessage("Drew " + cardOne.value + " and " + cardTwo.value + ". Your total: " + playerValue).queue();

        if (cardOneAce || cardTwoAce) {
            if (!cardOneAce || !cardTwoAce) {
                channel.sendMessage("One of your cards is an ACE! Please choose whether to make it worth 1 or 11!").queue();
            } else {
                channel.sendMessage("Both of your cards are ACES! Please choose whether to make them worth 1 or 11!").queue();
            }
        }
    }

    @Override
    public void turn(MessageChannel channel, String[] args) {
        if (cardOneAce) {
            if (args[0].equalsIgnoreCase("11") || args[0].equalsIgnoreCase("1")) {
                channel.sendMessage("Ace is now worth " + args[0]).queue();
                playerValue += args[0].equalsIgnoreCase("11") ? 11 : 1;
                cardOneAce = false;
            } else {
                channel.sendMessage("Please specify how much you want ACE to be!").queue();
            }
            return;
        }
        if (cardTwoAce) {
            if (args[0].equalsIgnoreCase("11") || args[0].equalsIgnoreCase("1")) {
                channel.sendMessage("Ace is now worth " + args[0]).queue();
                playerValue += args[0].equalsIgnoreCase("11") ? 11 : 1;
                cardTwoAce = false;
            } else {
                channel.sendMessage("Please specify how much you want ACE to be!").queue();
            }
            return;
        }
        if (args[0].equalsIgnoreCase("hit")) {
            Deck.Card cardOne = deck.draw();

            cardOneAce = cardOne.value == Deck.CardValue.ACE;

            if (cardOneAce) {
                channel.sendMessage("You drew an ACE! Please choose whether to make them worth 1 or 11!").queue();
                return;
            } else {
                playerValue += Math.min(10, cardOne.value.value);
                channel.sendMessage("Drew " + cardOne.value + ". Your total: " + playerValue).queue();
            }

            if (playerValue == 21) {
                channel.sendMessage("You scored 21! You win!").queue();
                multiplier = 1.5;
                endGame(channel);
                return;
            } else if (playerValue > 21) {
                channel.sendMessage("You hit past 21! You lost!").queue();
                multiplier = 0;
                endGame(channel);
                return;
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            channel.sendMessage("You drew a total of **" + playerValue + "**. Dealer scores **" + dealerValue + "**.").queue();
            if (playerValue < dealerValue) {
                multiplier = Math.max(0.1, 1 - (dealerValue - playerValue)/10.0 - 0.3);
            } else if (playerValue > dealerValue) {
                multiplier = Math.min(1.5, 1 + (playerValue - dealerValue)/10.0);
            } else {
                multiplier = 1;
            }
            endGame(channel);
        } else {
            channel.sendMessage("Incorrect subcommand.").queue();
        }
    }

    private static boolean shouldHit(int dealerValue, Deck deck) {
        // The largest value that can be drawn is 10, so anything at 11 or below is always a hit.
        // Ace isn't acknowledged in this as Ace can be either 1 or 11, and the AI will already
        // know which one is better to pick
        if (dealerValue <= 11) {
            return true;
        }

        // Gets the highest number that is the best for the bot aka the value needed for 21.
        int bestLargestDraw = 21 - dealerValue;

        // The total amount of cards that won't cause a bust
        int positiveChance = 0;
        // The total amount of cards that WILL cause a bust
        int negativeChance = 0;

        // Loops through 1 to bestLargestDraw and counts the total cards in the deck equal to those values.
        // Adds it to positiveChance.
        for (int i = 1; i <= bestLargestDraw; i++) {
            Deck.CardValue value = Deck.CardValue.intToValue(i);
            positiveChance += deck.count(value);
        }

        // Loops through bestLargestDraw + 1 to 13 and counts the total cards in the deck equal to those values.
        // Adds it to negativeChance.
        for (int i = bestLargestDraw+1; i <= 13; i++) {
            Deck.CardValue value = Deck.CardValue.intToValue(i);
            negativeChance += deck.count(value);
        }

        // Rolls a random value between [0, 100}.
        // If the value is less than min(100, (positiveChance/negativeChance) x 70), then the bot will hit.
        // Graph for determining chance: https://www.desmos.com/calculator/hakeezqng6
        return Math.random() * 100 < Math.min(100, positiveChance/(double)negativeChance * 70);
    }
}
