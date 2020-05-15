package org.baito.stonk;

import java.awt.*;

public class EchidnaMarket extends Market {
    public EchidnaMarket() {
        super("Echidna", 0, new Color(50, 45, 45), false, 1, 300);
        canBuy = true;
    }

    protected void newValues() {
        // Random height
        height = (Math.random() * (131)) + 20;
        // Random width, dependant on the height
        width = (Math.random() * (150 - height/3)) + height/3;
        // Random tilt
        tilt = (Math.floor(Math.random() * 11) - 5)/100;
        // Random offset
        offset = (Math.random() * 5) * 300;
        // Random length
        xLength = (Math.random() * 21) + 10;
        // Random max
        xMax = (int) (Math.random() * 10 + 10);
        // Resets x back to the lowest interval
        x = xLength;
    }

    protected void calcPrice() {
        price = (int) (Math.round(Math.min(maximum, Math.max(minimum,
                ((Math.sin((x + offset)/width) * height + height) + x * tilt) * ((100 + (Math.random() * 61 - 30))/100)
        ))));
    }

    @Override
    public void newStock() {

    }
}
