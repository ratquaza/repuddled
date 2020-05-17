package org.baito;

import net.dv8tion.jda.api.entities.User;
import org.baito.API.registry.SerializableRegistry;
import org.baito.API.registry.SingularRegistry;
import org.baito.data.Account;
import org.baito.stonk.*;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class MasterRegistry {
    private static HashMap<Class, SerializableRegistry> serializableRegistries = new HashMap<>();
    private static HashMap<Class, SingularRegistry> singularRegistries = new HashMap<>();

    public static SerializableRegistry getSerializableRegistry(Class c) {
        return serializableRegistries.getOrDefault(c, null);
    }

    public static SingularRegistry getSingularRegistry(Class c) {
        return singularRegistries.getOrDefault(c, null);
    }

    public static void load() {
        for (SerializableRegistry i : serializableRegistries.values()) {
            i.load();
        }
        for (SingularRegistry i : singularRegistries.values()) {
            i.load();
        }
    }

    public static void save() {
        for (SerializableRegistry i : serializableRegistries.values()) {
            i.save();
        }
        for (SingularRegistry i : singularRegistries.values()) {
            i.save();
        }
    }

    public static SerializableRegistry<User, Account> accountRegistry() {
        return serializableRegistries.get(Account.class);
    }

    public static SingularRegistry<String, Market> marketRegistry() {
        return singularRegistries.get(Market.class);
    }

    static {
        serializableRegistries.put(Account.class, new SerializableRegistry<>("ACCOUNTS",
                (f, j) -> new Account().fromJson(f, j),
                Account::new));
        singularRegistries.put(Market.class, new SingularRegistry<String, Market>("MARKETS"));

        marketRegistry().register(new EchidnaMarket(), new RubyMarket(), new SapphireMarket(), new EmeraldMarket(), new DragonMarket());

        MasterRegistry.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MasterRegistry.save();
            Main.save();
        }));
    }
}
