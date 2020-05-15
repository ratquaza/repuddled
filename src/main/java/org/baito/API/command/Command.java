package org.baito.API.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface Command {

    void execute(Member executor, String[] arguments, MessageChannel channel, Message message);

}
