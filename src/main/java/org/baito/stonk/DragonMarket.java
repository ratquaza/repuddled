package org.baito.stonk;

import net.dv8tion.jda.api.entities.User;
import org.baito.MasterRegistry;
import org.baito.data.Account;
import org.baito.data.Flag;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Calendar;

public class DragonMarket extends Market {
    public DragonMarket() {
        super("Dragon Egg", 30, new Color(100, 0, 255), true,1, 50);
    }

    protected void newValues() {
        // Random height
        height = (Math.random() * (1001 - 500)) + 500;
        // Random width, dependant on the height
        width = (Math.random() * (height/3 - height/5)) + height/3;
        // Random tilt
        tilt = (-5 * Math.floor(Math.random() * 3 - 1))/10;
        // Random offset
        offset = (Math.random() * 5) * 300;
        // Random length
        xLength = (Math.random() * 51) + 200;
        // Random max
        xMax = (int) (Math.random() * 3) + 6;
        // Resets x back to the lowest interval
        x = xLength;
    }

    protected void calcPrice() {
        price = (int) (Math.round(Math.min(maximum, Math.max(minimum,
                ((Math.sin((x + offset)/width) * height + height) + x * tilt) * ((10 + (Math.random() * 11 - 5))/10)
        ))));
    }

    // 50 to 150, with intervals of 10
    @Override
    public void newStock() {
        stock = (int) (50 + (10 * Math.floor(Math.random() * 11)));
    }

    @Override
    public String getDescription() {
        return "A subscription based Market, that profits with Maples.";
    }

    // Every hour that is a multiple of 8: Selling and Buying Dragons
    // Otherwise, buying Dragons
    @Override
    public PurchadeMode purchadeMode(Calendar c) {
        return c.get(Calendar.HOUR_OF_DAY) % 8 == 0 ? PurchadeMode.BOTH : PurchadeMode.BUYING;
    }

    // If the day is Thursday or Friday, the Market is open
    // Also, if the player has the dragon pass or not.
    @Override
    public boolean isOpen(Calendar c, @Nullable User u) {
        boolean user = true;
        if (u != null) {
            Account ac = MasterRegistry.accountRegistry().get(u);
            user = ac.getFlag(Flag.DRAGON_SUBSCRIPTION);
        }
        return user && c.get(Calendar.DAY_OF_WEEK) >= Calendar.THURSDAY && c.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY;
    }
}
