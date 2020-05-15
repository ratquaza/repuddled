package org.baito.stonk;

import java.awt.*;

public class RubyMarket extends Market {
    public RubyMarket() {
        super("Ruby", 10, new Color(255, 0, 0), false, 1, 2000);
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
        xLength = (Math.random() * 51) + 150;
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

    @Override
    public void newStock() {
        stock = (int) (300 + (10 * Math.floor(Math.random() * 11 - 5)));
    }
}
