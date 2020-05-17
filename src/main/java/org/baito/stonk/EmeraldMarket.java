package org.baito.stonk;

import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Calendar;

public class EmeraldMarket extends Market {
    public EmeraldMarket() {
        super("Emerald", 10, new Color(0, 200, 100), false, 500, 1000);
    }

    protected void newValues() {
        // Random height
        height = (Math.random() * (551)) + 200;
        // Random width, dependant on the height
        width = (Math.random() * (501 - height/3)) + height/3;
        // Random tilt
        tilt = (2 - Math.floor(Math.random() * 5))/10;
        // Random offset
        offset = (Math.random() * 5) * 300;
        // Random length
        xLength = 150;
        // Random max
        xMax = (int) (Math.random() * 11) + 5;
        // Resets x back to the lowest interval
        x = xLength;
    }

    protected void calcPrice() {
        price = (int) (Math.round(Math.min(maximum, Math.max(minimum,
                ((Math.sin((x + offset)/width) * height + height + (500 - width)) + x * tilt) * ((10 + (Math.random() * 5 - 2))/10)
        ))));
    }

    // Stock is dependant on the height and width
    @Override
    public void newStock() {
        stock = (int) (Math.floor(height/2/10)*10 + Math.floor((500 - width)/2/10)*10);
    }

    @Override
    public String getDescription() {
        return "A unique Market, where its profit is equal to its volatility and unpredictability.";
    }

    // Same as Echidnas
    @Override
    public PurchadeMode purchadeMode(Calendar c) {
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY ? PurchadeMode.SELLING : PurchadeMode.BUYING;
    }

    // If the day is a weekend, the Market is closed
    @Override
    public boolean isOpen(Calendar c, @Nullable User u) {
        return c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY;
    }
}
