package org.baito.API.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.baito.API.command.CommandRegistry;
import org.baito.Main;

import java.util.Arrays;

public class CommandEvent extends ListenerAdapter {

    private char commandPrefix;
    private JDA bot;

    public CommandEvent(char prefix) {
        this.commandPrefix = prefix;
        this.bot = Main.getBot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild() && event.getMessage().getContentRaw().length() > 0 && !event.getAuthor().getId().equalsIgnoreCase(bot.getSelfUser().getId())) {
            if (event.getMessage().getContentRaw().charAt(0) == commandPrefix) {
                String[] fullArgs = event.getMessage().getContentRaw().split(" ");
                String command = fullArgs[0].substring(1);
                String[] args = fullArgs.length == 1 ? new String[0] : Arrays.copyOfRange(fullArgs, 1, fullArgs.length);
                CommandRegistry.attempt(command, event.getMember(), args, event.getChannel(), event.getMessage());
            }
        }
    }

}
