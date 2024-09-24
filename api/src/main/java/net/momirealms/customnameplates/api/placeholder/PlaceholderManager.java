package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PlaceholderManager extends Reloadable {

    void refreshPlaceholders();

    int getRefreshInterval(String id);

    <T extends Placeholder> T registerPlaceholder(T placeholder);

    SharedPlaceholder registerSharedPlaceholder(
            String id,
            int refreshInterval,
            Supplier<String> contextSupplier
    );

    PlayerPlaceholder registerPlayerPlaceholder(
            String id,
            int refreshInterval,
            Function<CNPlayer, String> function
    );

    RelationalPlaceholder registerRelationalPlaceholder(
            String id,
            int refreshInterval,
            BiFunction<CNPlayer, CNPlayer, String> function
    );

    Placeholder getPlaceholder(String id);

    void unregisterPlaceholder(String id);

    void unregisterPlaceholder(Placeholder placeholder);

    List<String> detectPlaceholders(String raw);
}
