package net.momirealms.customnameplates.actionbar;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import net.momirealms.customnameplates.hook.PlaceholderManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarManager extends Function {

    private BukkitTask bukkitTask;

    private int timer;

    public ActionBarManager(String name) {
        super(name);
    }

    @Override
    public void load() {

        PlaceholderManager placeholderManager = CustomNameplates.instance.getPlaceholderManager();

        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer < ConfigManager.ActionbarConfig.rate){
                    timer++;
                }
                else {
                    Bukkit.getOnlinePlayers().forEach(player -> AdventureUtil.playerActionbar(player, ConfigManager.Main.placeholderAPI ? placeholderManager.parsePlaceholders(player, ConfigManager.ActionbarConfig.text) : ConfigManager.ActionbarConfig.text));
                    timer = 0;
                }
            }
        }.runTaskTimerAsynchronously(CustomNameplates.instance, 1, 1);
    }

    @Override
    public void unload() {
        this.bukkitTask.cancel();
    }
}
