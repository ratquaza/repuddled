package org.baito.bevent;

import org.baito.API.TimerManager;
import org.baito.MasterRegistry;
import org.baito.bevent.events.HourlyEvent;
import org.baito.bevent.events.LevelUpEvent;
import org.baito.data.Account;
import org.baito.data.Modify;

import java.util.Calendar;

public class BEventManager {

    public static void onLevelUp(LevelUpEvent e) {
        Account account = (Account) MasterRegistry.getSerializableRegistry(Account.class).get(e.getUser());
        account.modifyLevel(Modify.ADD, 1);
        account.modifyXP(Modify.SUBTRACT, account.getLevel() * 75);

        account.modifyGold(Modify.ADD, account.getLevel() * 200);

        if (account.getLevel() % 5 == 0) {
            account.modifyMaple(Modify.ADD, 1);
        }
    }

    public static void onHourly(HourlyEvent e) {
        if (e.getTime().get(Calendar.HOUR_OF_DAY) % TimerManager.PUDDLE_DAY_INTERVAL == 0) {

        }
    }
}