package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.API.image.ImageOffset;
import org.baito.API.image.ImageUtils;
import org.baito.Main;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Roulette extends MPCasinoGame {

    private static BufferedImage BOARDIMAGE = ImageUtils.getImage("CASINO/ROULETTEGAME/ROULETTE.png");
    private static BufferedImage CHIPIMAGE = ImageUtils.getImage("CASINO/ROULETTEGAME/CHIP.png");

    private static int ONEX = 59;
    private static int ONEY = 92;
    private static int OFFSET = 37;

    private HashMap<Member, RouletteBets> bets;

    public Roulette() {
        super("Roulette", 5, 4, 1);
    }

    @Override
    public MPCasinoGame newInstance() {
        return new Roulette();
    }

    @Override
    public void setPlayers(Member... players) {
        bets = new HashMap<>();
        for (Member i : players) {
            bets.put(i, new RouletteBets(bet));
        }
    }

    @Override
    public void setup(MessageChannel channel) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("WELCOME TO ROULETTE! BUY IN IS " + Main.curr(useMaple) + bet).setColor(new Color(255, 180, 0));
        StringBuilder sb = new StringBuilder();
        for (Member i : bets.keySet()) {
            sb.append(i.getEffectiveName() + "\n");
        }
        eb.addField("CURRENT PLAYERS", sb.toString(), false);
        ImageUtils.embedImage(channel, BOARDIMAGE, eb, false, "ROULETTE", "png");
    }

    @Override
    public void turn(Member m, MessageChannel channel, String[] args) {
        RouletteBets betProfile = bets.get(m);

        if (args.length == 0) {
            BufferedImage board = ImageUtils.getImage("CASINO/ROULETTEGAME/ROULETTE.png");
            for (int i : betProfile.bets.keySet()) {
                if (i == 0) {
                    ImageUtils.draw(board, CHIPIMAGE, 20, ImageOffset.HALF, 56, ImageOffset.HALF);
                } else {
                    ImageUtils.draw(board, CHIPIMAGE, ONEX + (OFFSET * (row(i) - 1)), ImageOffset.HALF,
                            ONEY - (OFFSET * ((i-1)%3)), ImageOffset.HALF);
                }
            }
            EmbedBuilder eb = new EmbedBuilder().setTitle("CURRENT BOARD").setColor(new Color(255, 180, 0))
                    .addField("DATA", "You have " + Main.curr(useMaple) + betProfile.money + " left.", false);
            ImageUtils.embedImage(channel, board, eb, true, "BOARD", "png");
            return;
        }

        // Quit command
        if (args[0].equalsIgnoreCase("quit") && !betProfile.finishedBetting) {
            endGame(channel, false, m);
            bets.remove(m);
            return;
        }

        // Bet command
        if (args[0].equalsIgnoreCase("bet") && !betProfile.finishedBetting) {
            if (args.length < 3) {
                channel.sendMessage("Insufficient arguments.").queue();
                return;
            }

            // The amount betting
            int betAmount;

            try {
                betAmount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                channel.sendMessage("Invalid bet number.").queue();
                return;
            }

            if (betAmount <= 0) {
                channel.sendMessage("Invalid bet amount.").queue();
                return;
            }

            // The string for all the locations
            // First, copy the array from index 1 to the end.
            // Join it back with spaces, as some enums have spaces
            // Then split it by commas instead of spaces
            String[] values = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).split(",");

            if (values.length == 0) {
                channel.sendMessage("Insufficient arguments.").queue();
                return;
            }

            // If only one location is specified, meaning its either straight up, or anything past basket
            // Can not be split, street, corner, or six line as those require multiple values to be given
            if (values.length == 1) {
                // First, attempt to convert the string to the name of a number, if it IS a number
                // If not a number, parseToName() will just return the same string
                // Then, get the value as a bet location. getValue() will auto turn it to uppercase, and
                // replace spaces with underscores and remove them entirely, to attempt to match any enum
                BetLocation l = BetLocation.getValue(BetLocation.parseToName(values[0]));
                if (l == null) {
                    channel.sendMessage("No outside bet found.").queue();
                    return;
                }

                if (betAmount > betProfile.getMoney()) {
                    channel.sendMessage("Insufficient chips. You have " + (useMaple ? Main.maple() : Main.gold()) + betProfile.getMoney()).queue();
                    return;
                }

                // Place the total winning possible in the location
                // The maths here is simply: 35 divided by the total amount of numbers that this bet location covers floored
                // This is because with every bet (including splits, streets, etc), the total payout is always
                // 35 divided by the total amount of numbers covered, rounded down
                // For example: Six line bet covers 6 spots. 35/6 is ~= 5.83, rounded down is 5. Six line has
                // a payout of 5 on Wikipedia, and so does floor(35/6)
                // Another example: 1st dozen bet covers 12 spots. 35/12 is ~= 2.91, rounded down is 2.
                // 1st dozen has a payout of 2 on Wikipedia, and so does floor(35/12)
                betProfile.placeWinning(l, betAmount * (int) Math.floor(35 / (double) l.values.length));

                BufferedImage board = ImageUtils.getImage("CASINO/ROULETTEGAME/ROULETTE.png");
                for (int i : betProfile.bets.keySet()) {
                    if (i == 0) {
                        ImageUtils.draw(board, CHIPIMAGE, 20, ImageOffset.HALF, 56, ImageOffset.HALF);
                    } else {
                        ImageUtils.draw(board, CHIPIMAGE, ONEX + (OFFSET * (row(i) - 1)), ImageOffset.HALF,
                                ONEY - (OFFSET * ((i-1)%3)), ImageOffset.HALF);
                    }
                }

                betProfile.subtractMoney(betAmount);
                if (betProfile.getMoney() == 0) {
                    ImageUtils.embedImage(channel, board, new EmbedBuilder().setTitle("Successfully bet " + Main.curr(useMaple) + betAmount + " on **" + values[0] + "**. \n"
                            + "You have " + Main.curr(useMaple) + betProfile.money + " left, *autolocking in.*"), false, "BOARD", "png");
                    betProfile.finishedBetting = true;
                } else {
                    ImageUtils.embedImage(channel, board, new EmbedBuilder().setTitle("Successfully bet " + Main.curr(useMaple) + betAmount + " on **" + values[0] + "**. \n"
                            + "You have " + Main.curr(useMaple) + betProfile.money + " left."), false, "BOARD", "png");
                }

                // Split, street, corner or six line. Does the checking to ensure the numbers given are even
                // valid for the style they wish
            } else {
                if (values.length == 5 || values.length > 6) {
                    channel.sendMessage("No inside bet found.").queue();
                    return;
                }

                // Integer versions of locations given, to run formulas to ensure the bets are valid
                int[] t = new int[values.length];

                for (int i = 0; i < values.length; i++) {
                    try {
                        t[i] = Integer.parseInt(values[i]);
                    } catch (NumberFormatException e) {
                        channel.sendMessage("Invalid number.").queue();
                        return;
                    }
                    if (t[i] < 1 || t[i] > 36) {
                        channel.sendMessage("Out of range. Minimum is 1 and max is 36.").queue();
                        return;
                    }
                }

                // Sort the values out
                Arrays.sort(t);

                // Check to see if each bet is valid
                switch (t.length) {
                    // Ensure the numbers are adjacent, vertically or horizontally
                    case 2:
                        if (!(
                                (t[0] == t[1] - 1 && row(t[0]) == row(t[1])) ||
                                        (t[0] == t[1] - 3 && row(t[0]) < row(t[1]))
                        )) {
                            channel.sendMessage("Invalid split. Both numbers must be next to eachother.").queue();
                            return;
                        }
                        break;
                    // Ensure the numbers are horizontally adjacent aka straight
                    // Easy way to do this is to check if the starting number is in the first column,
                    // as all straights start in the first column
                    case 3:
                        if (!(t[0] == t[1] - 1 && t[1] == t[2] - 1 && Arrays.stream(BetLocation.FIRSTCOLUMN.values).anyMatch(i -> i == t[0]))) {
                            channel.sendMessage("Invalid straight, the first number must start in the first column.").queue();
                            return;
                        }
                        break;
                    case 6:
                        if (!(t[0] == t[1] - 1 && t[1] == t[2] - 1 && t[2] == t[3] - 1 && t[3] == t[4] - 1 && t[4] == t[5] - 1
                                && Arrays.stream(BetLocation.FIRSTCOLUMN.values).anyMatch(i -> i == t[0]))) {
                            channel.sendMessage("Invalid straight, the first number must start in the first column.").queue();
                            return;
                        }
                        break;
                    // Ensure the numbers are next to eachother (probs hardest one)
                    case 4:
                        if (!(t[0] == t[1] - 1 && t[1] == t[2] - 2 && t[2] == t[3] - 1 &&
                                row(t[0]) == row(t[1]) && row(t[2]) == row(t[3]) &&
                                row(t[0]) < row(t[2]))) {
                            channel.sendMessage("Invalid corner. All the numbers must share a single corner.").queue();
                            return;
                        }
                        break;
                }

                // The locations inwhich they want to bet, as this includes multiple bets
                BetLocation[] locations = new BetLocation[values.length];

                for (int i = 0; i < values.length; i++) {
                    // First,  convert the string to the name of a number, if it IS a number (it always is logically)
                    // Then, get the value as a bet location. getValue() will auto turn it to uppercase, and
                    // replace spaces with underscores and remove them entirely, to attempt to match any enum
                    locations[i] = BetLocation.getValue(BetLocation.parseToName(values[i]));
                }

                if (betAmount > betProfile.getMoney()) {
                    channel.sendMessage("Insufficient chips. You have " + (useMaple ? Main.maple() : Main.gold()) + betProfile.getMoney()).queue();
                    return;
                }

                betProfile.subtractMoney(betAmount);

                BufferedImage board = ImageUtils.getImage("CASINO/ROULETTEGAME/ROULETTE.png");
                for (int i : betProfile.bets.keySet()) {
                    if (i == 0) {
                        ImageUtils.draw(board, CHIPIMAGE, 20, ImageOffset.HALF, 56, ImageOffset.HALF);
                    } else {
                        ImageUtils.draw(board, CHIPIMAGE, ONEX + (OFFSET * (row(i) - 1)), ImageOffset.HALF,
                                ONEY - (OFFSET * ((i-1)%3)), ImageOffset.HALF);
                    }
                }

                if (betProfile.getMoney() == 0) {
                    ImageUtils.embedImage(channel, board, new EmbedBuilder().setTitle("Successfully bet " + Main.curr(useMaple) + betAmount + " on **"
                            + Arrays.toString(t).substring(1, Arrays.toString(t).length() - 1) + "**. \n"
                            + "You have " + Main.curr(useMaple) + betProfile.money + " left, *autolocking in.*"), false, "BOARD", "png");
                    betProfile.finishedBetting = true;
                } else {
                    ImageUtils.embedImage(channel, board, new EmbedBuilder().setTitle("Successfully bet " + Main.curr(useMaple) + betAmount + " on **"
                            + Arrays.toString(t).substring(1, Arrays.toString(t).length() - 1) + "**. \n"
                            + "You have " + Main.curr(useMaple) + betProfile.money + " left.*"), false, "BOARD", "png");
                }

                // Place the winnings
                for (BetLocation i : locations) {
                    betProfile.placeWinning(i, betAmount * (int) Math.floor(35 / (double) locations.length));
                }
            }
        }

        // Finish betting
        if (args[0].equalsIgnoreCase("finish") || betProfile.money == 0) {
            if (!betProfile.finishedBetting) {
                betProfile.finishedBetting = true;
                channel.sendMessage(new EmbedBuilder().setTitle("You have " + Main.curr(useMaple) + betProfile.money + " left. Now locked in.").build()).queue();
            }
            // Everyone has finished betting
            if (bets.values().parallelStream().allMatch(p -> p.finishedBetting)) {
                int rolled = (int) Math.floor(Math.random() * 37);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("NO MORE BETS! ROULETTE ROLLED A " + rolled);

                StringBuilder sb = new StringBuilder();
                ArrayList<Member> toLeave = new ArrayList<>();

                bets.entrySet().parallelStream().forEach(ent -> {
                    ent.getValue().money += ent.getValue().getWinning(rolled);
                    sb.append("**" + ent.getKey().getEffectiveName() + "** won " + Main.curr(useMaple) + ent.getValue().getWinning(rolled)
                            + " **|** " + Main.curr(useMaple) + ent.getValue().money + " total\n");

                    if (ent.getValue().money == 0) {
                        toLeave.add(ent.getKey());
                    }

                    ent.getValue().finishedBetting = false;
                    ent.getValue().bets.clear();
                });

                eb.addField("THE WINNINGS", sb.toString(), false);
                if (toLeave.size() > 0) {
                    StringBuilder leaving = new StringBuilder();
                    for (Member i : toLeave) {
                        leaving.append(i.getEffectiveName() + " \n");
                        endGame(channel, true, i);
                        bets.remove(m);
                    }
                    eb.addField("REMOVED PLAYERS", leaving.toString(), false);
                }

                channel.sendMessage(eb.build()).queue();
            }
        }
    }

    // Function for getting row number
    private int row(int number) {
        return (int) Math.ceil(number/3.0);
    }

    @Override
    public Member[] getPlayers() {
        return bets.keySet().toArray(new Member[0]);
    }

    @Nullable
    public HashMap<Member, Integer> getWinnings() {
        HashMap<Member, Integer> map = new HashMap<>();
        bets.entrySet().parallelStream().forEach(e -> map.put(e.getKey(), e.getValue().getMoney()));
        return map;
    }

    private static class RouletteBets {
        private int money = 0;
        private HashMap<Integer, Integer> bets = new HashMap<>();
        private boolean finishedBetting = false;

        public RouletteBets(int money) {
            this.money = money;
        }

        public int getMoney() {
            return money;
        }

        public void subtractMoney(int amount) {
            money -= amount;
            money = Math.max(money, 0);
        }

        public void placeWinning(BetLocation location, int amount) {
            for (Integer i : location.values) {
                bets.put(i, bets.getOrDefault(i, 0) + amount);
            }
        }

        public int getWinning(int location) {
            return bets.getOrDefault(location, 0);
        }
    }

    private enum BetLocation {
        ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4),
        FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9),
        TEN(10), ELEVEN(11), TWELVE(12), THIRTEEN(13), FOURTEEN(14),
        FIFTEEN(15), SIXTEEN(16), SEVENTEEN(17), EIGHTEEN(18), NINETEEN(19),
        TWENTY(20), TWENTYONE(21), TWENTYTWO(22), TWENTYTHREE(23), TWENTYFOUR(24),
        TWENTYFIVE(25), TWENTYSIX(26), TWENTYSEVEN(27), TWENTYEIGHT(28), TWENTYNINE(29),
        THIRTY(30), THIRTYONE(31), THIRTYTWO(32), THIRTYTHREE(33), THIRTYFOUR(34),
        THIRTYFIVE(35), THIRTYSIX(36),
        BASKET(0, 1, 2, 3),
        FIRSTCOLUMN(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34),
        SECONDCOLUMN(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35),
        THIRDCOLUMN(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36),
        FIRSTDOZEN(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
        SECONDDOZEN(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24),
        THIRDDOZEN(25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36),
        ODD(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35),
        EVEN(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36),
        RED(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36),
        BLACK(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35),
        FIRSTHALF(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18),
        SECONDHALF(19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36);

        public int[] values;

        static String[] integerNames = {
          "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
          "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
          "nineteen", "twenty", "twentyone", "twentytwo", "twentythree", "twentyfour", "twentyfive",
                "twentysix", "twentyseven", "twentyeight", "twentynine", "thirty", "thirtyone", "thirtytwo",
                "thirtythree", "thirtyfour", "thirtyfive", "thirtysix"
        };

        BetLocation(int... values) {
            this.values = values;
        }

        public static BetLocation getValue(String s) {
            try {
                return valueOf(s.toUpperCase().replace(" ", ""));
            } catch (IllegalArgumentException ef) {
                return null;
            }
        }

        public static String parseToName(String s) {
            try {
                int number = Integer.parseInt(s);
                return number >= 0 && number < integerNames.length ? integerNames[number] : s;
            } catch (NumberFormatException e) {
                return s;
            }
        }
    }
}
