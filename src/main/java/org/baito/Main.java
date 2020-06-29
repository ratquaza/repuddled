package org.baito;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.baito.API.command.CommandRegistry;
import org.baito.API.config.Config;
import org.baito.API.events.CommandEvent;
import org.baito.API.events.JoinLeaveEvent;
import org.baito.API.events.ResponseEvent;
import org.baito.commands.*;
import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.*;

public class Main {

    private static JDA bot;
    private static Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Australia/Sydney"));
    private static Config config = new Config("MAIN");

    private static HashMap<Guild, TextChannel> notificationChannels = new HashMap<>();

    public static final int PUDDLE_DAY_INTERVAL = 8;

    public static void main(String[] args) throws LoginException, InterruptedException {
        bot = JDABuilder.createDefault("NzAzNTczNTQwNDMxMDY5MjE1.XrMEXw.i5rArN5jC5GWxyIvyc_nUUCzE9U",
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.values())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build().awaitReady();
        bot.addEventListener(new JoinLeaveEvent(), new CommandEvent('.'), new ResponseEvent());

        load();

        // CIS - Console Input Thread
        new Input().start();

        // Registering Commands
        CommandRegistry.register(new CasinoCommand(), "cas", "casino");
        CommandRegistry.register(new EconomyCommand(), "econ", "economy", "e");
        CommandRegistry.register(new AdminCommand(), "admin");
        CommandRegistry.register(new MarketCommand(), "market", "m");
        CommandRegistry.register(new ShopCommand(), "shop", "s");

        TimerManager.init();
    }

    public static void setChannel(Guild g, @Nullable TextChannel c) {
        if (c == null) {
            if (g.getDefaultChannel() != null) {
                notificationChannels.put(g, g.getDefaultChannel());
            }
        } else {
            notificationChannels.put(g, c);
        }
    }

    public static TextChannel getNotificationChannel(Guild g) {
        return notificationChannels.get(g);
    }

    public static void notifyAll(Message m) {
        notificationChannels.values().parallelStream().forEach(t -> t.sendMessage(m).queue());
    }

    public static void notifyAll(MessageEmbed m) {
        notificationChannels.values().parallelStream().forEach(t -> t.sendMessage(m).queue());
    }

    public static void load() {
        JSONObject j = config.load();
        if (j.has("notificationChannels")) {
            JSONObject nc = j.getJSONObject("notificationChannels");
            ArrayList<Guild> cache = new ArrayList<>(bot.getGuilds());

            for (String i : nc.keySet()) {
                Guild g = cache.stream().filter(guild -> guild.getId().equalsIgnoreCase(i)).findFirst().orElse(null);
                if (g == null) continue;
                cache.remove(g);

                if (nc.getString(i).length() == 0) {
                    if (g.getDefaultChannel() != null) {
                        notificationChannels.put(g, g.getDefaultChannel());
                    }
                } else {
                    notificationChannels.put(g, g.getTextChannelById(nc.getString(i)));
                }
            }
        }

        if (j.has("calendar")) {
            JSONObject cal = j.getJSONObject("calendar");
            c.set(
                    cal.getInt("year"),
                    cal.getInt("month"),
                    cal.getInt("day"),
                    cal.getInt("hour"),
                    cal.getInt("minute"),
                    cal.getInt("second")
            );
        }

    }

    public static void save() {
        JSONObject j = config.load();
        JSONObject notifs = new JSONObject();
        for (Map.Entry<Guild, TextChannel> i : notificationChannels.entrySet()) {
            notifs.put(i.getKey().getId(), i.getValue().getId());
        }
        j.put("notificationChannels", notifs);
        JSONObject calendar = new JSONObject();
        calendar.put("year", c.get(Calendar.YEAR));
        calendar.put("month", c.get(Calendar.MONTH));
        calendar.put("day", c.get(Calendar.DAY_OF_MONTH));
        calendar.put("hour", c.get(Calendar.HOUR_OF_DAY));
        calendar.put("minute", c.get(Calendar.MINUTE));
        calendar.put("second", c.get(Calendar.SECOND));
        j.put("calendar", calendar);
        config.save(j);
    }

    public static JDA getBot() {
        return bot;
    }

    public static String gold() {
        return ":dvd:";
    }

    public static String maple() {
        return ":maple_leaf:";
    }

    public static String curr(boolean useMaple) {
        return useMaple ? maple() : gold();
    }

    public static Calendar getCalendar() {
        c.setTime(new Date());
        return c;
    }

    public static Calendar getCalendarNoUpdate() {
        return c;
    }
}
