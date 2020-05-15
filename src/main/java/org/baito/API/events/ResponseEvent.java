package org.baito.API.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.baito.API.responses.ResponseSystem;
import org.baito.Main;

public class ResponseEvent extends ListenerAdapter {

    private JDA bot;

    public ResponseEvent() {
        this.bot = Main.getBot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (ResponseSystem.contains(event.getMember())) {
            ResponseSystem.process(event.getMember(), event.getChannel(), event.getMessage().getContentRaw());
        }
    }

}
