package net.momirealms.customnameplates.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {

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
        if (params.equals("prefix")){
            if (ScoreBoardManager.teams.get(player.getName()) != null){
                return ScoreBoardManager.teams.get(player.getName()).getPrefixText();
            }
        }
        if (params.equals("suffix")){
            if (ScoreBoardManager.teams.get(player.getName()) != null){
                return ScoreBoardManager.teams.get(player.getName()).getSuffixText();
            }
        }
        return null;
    }
}
