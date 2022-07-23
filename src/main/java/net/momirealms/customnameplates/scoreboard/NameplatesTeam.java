package net.momirealms.customnameplates.scoreboard;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.font.FontCache;
import net.momirealms.customnameplates.hook.ParsePapi;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Optional;

public class NameplatesTeam {

    private final CustomNameplates plugin;
    private final Player player;
    private final Team team;
    private Component prefix;
    private Component suffix;
    private ChatColor color;

    public void hideNameplate() {
        this.team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }
    public void showNameplate() {
        this.team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    }
    public Component getPrefix() {
        return this.prefix;
    }
    public Component getSuffix() {
        return this.suffix;
    }
    public ChatColor getColor() {
        return this.color;
    }

    public NameplatesTeam(CustomNameplates plugin, Player player) {
        this.color = ChatColor.WHITE;
        this.plugin = plugin;
        this.player = player;
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String name = player.getName();
        this.team = Optional.ofNullable(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name)).orElseGet(() -> scoreboard.registerNewTeam(name));
        team.addEntry(player.getName());
    }

    public void updateNameplates() {
        Optional<PlayerData> playerData = Optional.ofNullable(this.plugin.getDataManager().getOrCreate(this.player.getUniqueId()));
        String nameplate;
        if (playerData.isPresent()){
            nameplate = playerData.get().getEquippedNameplate();
        }else {
            nameplate = "none";
        }
        //如果是空铭牌直接飞机票送走
        if (nameplate.equals("none")) {
            if (plugin.getHookManager().hasPlaceholderAPI()) {
                this.prefix = Component.text(ParsePapi.parsePlaceholders(this.player, ConfigManager.MainConfig.player_prefix));
                this.suffix = Component.text(ParsePapi.parsePlaceholders(this.player, ConfigManager.MainConfig.player_suffix));
            }else {
                this.prefix = Component.text(ConfigManager.MainConfig.player_prefix);
                this.suffix = Component.text(ConfigManager.MainConfig.player_suffix);
            }
            this.color = ChatColor.WHITE;
            this.team.setPrefix("");
            return;
        }
        //根据铭牌名获取FontCache
        FontCache fontCache = this.plugin.getResourceManager().getNameplateInfo(nameplate);
        if (fontCache == null){
            this.prefix = Component.text("");
            this.suffix = Component.text("");
            this.color = ChatColor.WHITE;
            this.team.setPrefix("");
            DataManager.cache.get(player.getUniqueId()).equipNameplate("none");
            return;
        }
        NameplateUtil nameplateUtil = new NameplateUtil(fontCache);
        String name = this.player.getName();
        String playerPrefix;
        String playerSuffix;
        //有Papi才解析
        if (plugin.getHookManager().hasPlaceholderAPI()) {
            if (!ConfigManager.MainConfig.hidePrefix){
                playerPrefix = ParsePapi.parsePlaceholders(this.player, ConfigManager.MainConfig.player_prefix);
            }else {
                playerPrefix = "";
            }
            if (!ConfigManager.MainConfig.hideSuffix){
                playerSuffix = ParsePapi.parsePlaceholders(this.player, ConfigManager.MainConfig.player_suffix);
            }else {
                playerSuffix = "";
            }
        }else {
            if (!ConfigManager.MainConfig.hidePrefix){
                playerPrefix = ConfigManager.MainConfig.player_prefix;
            }else {
                playerPrefix = "";
            }
            if (!ConfigManager.MainConfig.hideSuffix){
                playerSuffix = ConfigManager.MainConfig.player_suffix;
            }else {
                playerSuffix = "";
            }
        }
        //最终prefix:  偏移 + 铭牌左 + 偏移 + 铭牌中 + 偏移 + 铭牌右 + 偏移 + 前缀
        //最终suffix:  偏移 + 后缀
        this.prefix = Component.text(nameplateUtil.makeCustomNameplate(playerPrefix, name, playerSuffix)).color(TextColor.color(255, 255, 255)).font(ConfigManager.MainConfig.key).append(Component.text(playerPrefix).font(Key.key("default")));
        this.suffix = Component.text(playerSuffix).append(Component.text(nameplateUtil.getSuffixLength(playerPrefix + name + playerSuffix)).font(ConfigManager.MainConfig.key));
        this.color = nameplateUtil.getColor();
        this.team.setPrefix("");
    }
}