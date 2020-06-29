package org.baito.stonk;

import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Calendar;

public class SapphireMarket extends Market {
    public SapphireMarket() {
        super("Sapphire", 10, new Color(10, 100, 255), false, 500, 1000, 1024);
    }

    protected void newValues() {
        // Random height
        height = (Math.random() * (251)) + 20;
        // Random width, dependant on the height
        width = (Math.random() * (height*2 - height*0.6)) + height*0.6;
        // Random tilt
        tilt = (1 - Math.floor(Math.random() * 3))/10;
        // Random offset
        offset = (Math.random() * 5) * 300;
        // Random length
        xLength = 150;
        // Random max
        xMax = (int) (Math.random() * 13) + 8;
        // Resets x back to the lowest interval
        x = xLength;
    }

    protected void calcPrice() {
        price = (int) (Math.round(Math.min(maximum, Math.max(minimum,
                ((Math.sin((x + offset)/width) * height + height + minimum) + x * tilt) * ((10 + (Math.random() * 5 - 2))/10)
        ))));
    }

    // Always 300
    @Override
    public void newStock() {
        stock = 300;
    }

    @Override
    public String getDescription() {
        return "A consistent and trustworthy Market.";
    }

    // Monday: Selling Sapphires
    // Tuesday - Thursday: No options
    // Firday: Buying Sapphires
    @Override
    public PurchadeMode purchadeMode(Calendar c) {
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? PurchadeMode.BUY :
                c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ? PurchadeMode.SELL : PurchadeMode.NEITHER;
    }

    // If the day is a weekend, the Market is closed
    @Override
    public boolean isOpen(Calendar c, @Nullable User u) {
        return c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY;
    }
}
