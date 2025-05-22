package tech.jt_dev.moreprocessors;

import net.minecraft.core.Registry;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface PlatformHandler {

    PlatformHandler PLATFORM_HANDLER = load();

    private static PlatformHandler load() {
        return ServiceLoader.load(PlatformHandler.class)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + PlatformHandler.class.getName()));
    }

    <T> Supplier<T> register(Registry<? super T> registry, String name, Supplier<T> value);
}
