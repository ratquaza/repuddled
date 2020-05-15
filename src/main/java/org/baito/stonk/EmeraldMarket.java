package org.baito.stonk;

import java.awt.*;

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
                ((Math.sin((x + offset)/width) * height + height + width) + x * tilt) * ((10 + (Math.random() * 5 - 2))/10)
        ))));
    }

    @Override
    public void newStock() {

    }
}
