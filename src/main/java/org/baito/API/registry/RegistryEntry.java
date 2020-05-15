package org.baito.API.registry;

public interface RegistryEntry<K> {
    K getKey();
    String fileName();
}
