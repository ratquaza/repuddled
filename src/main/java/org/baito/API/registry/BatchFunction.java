package org.baito.API.registry;

public interface BatchFunction<V> {

    void run(V value);

}
