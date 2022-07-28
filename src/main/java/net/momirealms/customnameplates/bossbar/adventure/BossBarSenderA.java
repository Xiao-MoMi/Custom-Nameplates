/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.bossbar.adventure;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarSenderA extends BukkitRunnable {

    private final Player player;
    private final Audience audience;
    private BossBar bossBar;
    private int timer;
    private final BossBarConfigA bossbarConfig;

    public BossBarSenderA(Player player, BossBarConfigA bossbarConfig){
        this.player = player;
        this.bossbarConfig = bossbarConfig;
        audience = CustomNameplates.adventure.player(player);
        this.timer = 0;
    }

    public void hideBossbar(){
        audience.hideBossBar(bossBar);
    }

    public void showBossbar(){
        if (ConfigManager.MainConfig.placeholderAPI){
            String s = PlaceholderAPI.setPlaceholders(player, bossbarConfig.getText());
            Component component = MiniMessage.miniMessage().deserialize(s);
            bossBar = BossBar.bossBar(component,1,bossbarConfig.getColor(),bossbarConfig.getOverlay());
            audience.showBossBar(bossBar);
        }else {
            Component component = MiniMessage.miniMessage().deserialize(bossbarConfig.getText());
            bossBar = BossBar.bossBar(component,1,bossbarConfig.getColor(),bossbarConfig.getOverlay());
            audience.showBossBar(bossBar);
        }
    }

    @Override
    public void run() {
        if (timer < bossbarConfig.getRate()){
            timer++;
        }else {
            if (ConfigManager.MainConfig.placeholderAPI){
                bossBar.name(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, bossbarConfig.getText())));
            }else {
                bossBar.name(MiniMessage.miniMessage().deserialize(bossbarConfig.getText()));
            }
            bossBar.color(bossbarConfig.getColor());
            bossBar.overlay(bossbarConfig.getOverlay());
            timer = 0;
        }
    }
}
