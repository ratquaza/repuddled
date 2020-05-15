package org.baito.API.responses;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface Response {
    EndResponse run(Member member, MessageChannel channel, String message, Object dat);
}
