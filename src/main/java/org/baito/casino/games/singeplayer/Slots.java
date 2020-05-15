package org.baito.casino.games.singeplayer;

import javafx.util.Pair;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.WeightedArray;

import java.time.Duration;

public class Slots extends SPCasinoGame {

    public Slots() {
        super("Slots", 0);
    }

    @Override
    public SPCasinoGame newInstance() {
        return new Slots();
    }

    private static WeightedArray<Pair<String, Double>> emojis = new WeightedArray<>();

    static {
        emojis.add(new Pair<>(":strawberry:", 1.5), 40);
        emojis.add(new Pair<>(":lemon:", 2.0), 20);
        emojis.add(new Pair<>(":grapes:", 2.5), 10);
        emojis.add(new Pair<>(":tangerine:", 5.0), 5);
        emojis.add(new Pair<>(":seven:", 10.0), 3);
    }

    @Override
    public void setup(MessageChannel channel) {
        String empty = ":green_square:";

        Pair<String, Double> roll1 = emojis.roll();
        Pair<String, Double> roll2 = emojis.roll();
        Pair<String, Double> roll3 = emojis.roll();

        if (roll1.equals(roll2) && roll1.equals(roll3)) {
            multiplier = roll1.getValue();
        } else {
            if (roll1.equals(roll2) && !roll1.equals(roll3)) { // 1 and 2 are equal
                multiplier = roll1.getValue()/10;
            } else if (roll1.equals(roll3) && !roll1.equals(roll2)) { // 1 and 3 are equal
                multiplier = roll1.getValue()/10;
            } else if (roll2.equals(roll3) && !roll2.equals(roll1)) { // 2 and 3 are equal
                multiplier = roll2.getValue()/10;
            } else { // No equal
                multiplier = 0;
            }
        }

        channel.sendMessage(empty + " " + empty + " " + empty)
                .delay(Duration.ofMillis(10))
                .flatMap(m -> m.editMessage(roll1.getKey() + " " + empty + " " + empty))
                .delay(Duration.ofMillis(10))
                .flatMap(m -> m.editMessage(roll1.getKey() + " " + roll2.getKey() + " " + empty))
                .delay(Duration.ofMillis(10))
                .flatMap(m -> m.editMessage(roll1.getKey() + " " + roll2.getKey() + " " + roll3.getKey()))
                .complete();

        endGame(channel);
    }

    @Override
    public void turn(MessageChannel channel, String[] args) {
        return;
    }
}
