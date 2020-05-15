package org.baito.API.responses;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.HashMap;

public class ResponseSystem {

    private ResponseSystem() {}

    private static HashMap<Member, Response> queue = new HashMap<>();
    private static HashMap<Member, Object> data = new HashMap<>();

    public static void queue(Member member, Response response) {
        queue.put(member, response);
        data.put(member, null);
    }

    public static void queue(Member member, Response response, Object saveData) {
        queue.put(member, response);
        data.put(member, saveData);
    }

    public static boolean contains(Member member) {
        return queue.containsKey(member);
    }

    public static void process(Member member, MessageChannel channel, String message) {
        if (queue.containsKey(member)) {
            if (queue.get(member).run(member, channel, message, data.get(member)) == EndResponse.SUCCESS) {
                queue.remove(member);
                data.remove(member);
            }
        }
    }

    public static Object getData(Member m) {
        return data.getOrDefault(m, null);
    }

}
