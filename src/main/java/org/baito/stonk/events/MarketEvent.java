package org.baito.stonk.events;

import org.baito.stonk.Market;

public abstract class MarketEvent {

    public abstract void update(Market m);
    public abstract String text();

}
