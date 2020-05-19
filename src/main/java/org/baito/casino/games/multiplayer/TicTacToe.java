package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.baito.Main;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class TicTacToe extends MPCasinoGame {

    private Member x;
    private Member o;

    private boolean xTurn;

    private HashMap<Member, Double> percentages;

    private Value[][] map = new Value[3][3];

    public TicTacToe() {
        super("TicTacToe", 0, 2, 2);
    }

    private String board() {
        StringBuilder sb = new StringBuilder();
        sb.append(":black_small_square: :regional_indicator_a: :regional_indicator_b: :regional_indicator_c:\n");
        for (int col = 0; col < map.length; col++) {
            sb.append(col == 0 ? ":one:" : col == 1 ? ":two:" : ":three:").append(" ");
            for (int row = 0; row < map[col].length; row++) {
                sb.append(map[col][row].s + " ");
            }
            if (col < 2) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public MPCasinoGame newInstance() {
        return new TicTacToe();
    }

    @Override
    public void setPlayers(Member... players) {
        x = players[0];
        o = players[1];
    }

    @Override
    public void setup(MessageChannel channel) {
        percentages = new HashMap<>();
        xTurn = Math.floor(Math.random() * 2) == 0;
        for (int i = 0; i < map.length; i++) {
            Arrays.fill(map[i], Value.BLANK);
        }

        channel.sendMessage(new EmbedBuilder()
                .setTitle(getCurrentTurn().getEffectiveName().toUpperCase() + "'S TURN")
                .setDescription(board())
                .setColor(xTurn ? Color.RED : Color.BLUE).build()).queue();
    }

    @Override
    public void turn(Member m, MessageChannel channel, String[] args) {
        if (args.length < 1) {
            return;
        }

        if ((xTurn && m.equals(o)) || (!xTurn && m.equals(x))) {
            return;
        }

        int row;
        int col;
        String cord = args[0].toUpperCase();

        if (cord.contains("A") || cord.contains("B") || cord.contains("C")) {
            col = cord.contains("A") ? 0 : cord.contains("B") ? 1 : 2;
            if (cord.contains("1") || cord.contains("2") || cord.contains("3")) {
                row = cord.contains("1") ? 0 : cord.contains("2") ? 1 : 2;
                if (map[row][col] == Value.BLANK) {
                    map[row][col] = xTurn ? Value.X : Value.O;
                } else {
                    channel.sendMessage("That spot is already taken.").queue();
                    channel.sendMessage(new EmbedBuilder()
                            .setTitle(getCurrentTurn().getEffectiveName().toUpperCase() + "'S TURN")
                            .setDescription(board())
                            .setColor(xTurn ? Color.RED : Color.BLUE).build()).queue();
                    return;
                }
            } else {
                return;
            }
        } else {
            return;
        }
        if (checkWin()) {
            channel.sendMessage(new EmbedBuilder()
                    .setTitle(getCurrentTurn().getEffectiveName().toUpperCase() + " HAS WON!")
                    .setDescription(board())
                    .setColor(xTurn ? Color.RED : Color.BLUE).build()).queue();
            percentages.put(xTurn ? x : o, 1.0);
            percentages.put(xTurn ? o : x, 0.0);
            endGame(channel, false);
        } else {
            if (checkEmpty()) {
                xTurn = !xTurn;
                channel.sendMessage(new EmbedBuilder()
                        .setTitle(getCurrentTurn().getEffectiveName().toUpperCase() + "'S TURN")
                        .setDescription(board())
                        .setColor(xTurn ? Color.RED : Color.BLUE).build()).queue();
            } else {
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("A TIE!")
                        .setDescription(board())
                        .setColor(Color.WHITE).build()).queue();
                percentages.put(x, 0.5);
                percentages.put(o, 0.5);
                endGame(channel, false);
            }
        }
    }

    private boolean checkWin() {
        for (int col = 0; col < map.length; col++) {
            if (map[col][0] == map[col][1] && map[col][0] == map[col][2] && map[col][0] != Value.BLANK) {
                return true;
            }
        }
        for (int row = 0; row < map.length; row++) {
            if (map[0][row] == map[1][row] && map[0][row] == map[2][row] && map[0][row] != Value.BLANK) {
                return true;
            }
        }
        if (map[0][0] == map[1][1] && map[0][0] == map[2][2] && map[0][0] != Value.BLANK) {
            return true;
        }
        if (map[0][2] == map[1][1] && map[0][2] == map[2][0] && map[0][2] != Value.BLANK) {
            return true;
        }
        return false;
    }

    private boolean checkEmpty() {
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 3; row++) {
                if (map[col][row] == Value.BLANK) return true;
            }
        }
        return false;
    }

    private Member getCurrentTurn() {
        return xTurn ? x : o;
    }

    @Nullable
    @Override
    public HashMap<Member, Double> getPotPercentage() {
        return percentages;
    }

    @Override
    public Member[] getPlayers() {
        return new Member[]{ x, o };
    }

    protected enum Value {
        BLANK(":white_square_button:"),
        X(":x:"),
        O(":o:");

        protected String s;

        Value(String s) {
            this.s = s;
        }

        public static Value random() {
            int rand = (int) Math.floor(Math.random() * 3);
            return rand == 0 ? BLANK : rand == 1 ? X : O;
        }
    }
}
