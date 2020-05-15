package org.baito.API.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandRegistry {

    private static HashMap<String, Command> commands = new HashMap<>();

    public static void register(Command executor, String... command) {
        for (String i : command) {
            if (!commands.containsKey(i)) {
                commands.put(i, executor);
            }
        }
    }

    public static Command getCommand(String s) {
        return commands.getOrDefault(s, null);
    }

    public static void attempt(String command, Member executor, String[] args, MessageChannel channel, Message full) {
        if (commands.containsKey(command)) {
            commands.get(command).execute(executor, args, channel, full);
        }
    }

    public static ArrayList<Member> searchForMember(Guild server, String name) {
        ArrayList<Member> list = new ArrayList<>();
        server.getMembers().forEach((m) -> {
            if (!m.isFake() && !m.getUser().isBot() &&
                    (m.getEffectiveName().contains(name) || m.getEffectiveName().toUpperCase().equalsIgnoreCase(name.toUpperCase()) ||
                            m.getUser().getName().contains(name) || m.getUser().getName().toUpperCase().equalsIgnoreCase(name.toUpperCase()))
            ) {
                list.add(m);
            }
        });
        return list;
    }

}
