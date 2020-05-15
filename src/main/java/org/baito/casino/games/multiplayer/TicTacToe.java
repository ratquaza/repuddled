package org.baito.casino.games.multiplayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.util.Arrays;

public class TicTacToe extends MPCasinoGame {

    private Member x;
    private Member o;

    private boolean xTurn;

    private Value[][] map = new Value[3][3];

    public TicTacToe() {
        super("Tic Tac Toe", 0, 2, 2);
    }

    private String board() {
        StringBuilder sb = new StringBuilder();
        sb.append(":black_small_square: :regional_indicator_a: :regional_indicator_b: :regional_indicator_c:\n");
        for (int row = 0; row < map.length; row++) {
            sb.append(row == 0 ? ":one:" : row == 1 ? ":two:" : ":three:").append(" ");
            for (int col = 0; col < map[row].length; col++) {
                sb.append(map[row][col].s + " ");
            }
            if (row < 2) {
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

        xTurn = !xTurn;
    }

    private boolean checkWin() {
        boolean win = false;
        for (int row = 0; row < map.length && !win; row++) {
            for (int col = 0; col < map[row].length  && !win; col++) {
                if (map[row][col] != Value.BLANK) {

                }
            }
        }
        return win;
    }

    @Override
    public Member getCurrentTurn() {
        return xTurn ? x : o;
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
