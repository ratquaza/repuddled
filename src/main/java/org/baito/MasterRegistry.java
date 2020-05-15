package org.baito;

import org.baito.API.registry.SerializableRegistry;
import org.baito.API.registry.SingularRegistry;
import org.baito.data.Account;
import org.baito.stonk.*;

import java.util.HashMap;

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

    static {
        serializableRegistries.put(Account.class, new SerializableRegistry<>("ACCOUNTS",
                (f, j) -> new Account().fromJson(f, j),
                Account::new));
        singularRegistries.put(Market.class, new SingularRegistry<String, Market>("MARKETS"));

        MasterRegistry.getSingularRegistry(Market.class).register(new EchidnaMarket(), new RubyMarket(), new SapphireMarket(), new EmeraldMarket(), new DragonMarket());

        MasterRegistry.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MasterRegistry.save();
            Main.save();
        }));
    }
}
