package net.momirealms.customnameplates.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PapiPlate extends PlaceholderExpansion{

    private CustomNameplates plugin;

    public PapiPlate(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nameplates";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoMoMi";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        return null;
    }
}
