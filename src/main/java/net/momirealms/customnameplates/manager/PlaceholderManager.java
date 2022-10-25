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

package net.momirealms.customnameplates.manager;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.hook.NameplatePlaceholders;
import net.momirealms.customnameplates.hook.OffsetPlaceholders;
import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.StaticText;
import net.momirealms.customnameplates.objects.background.BackGroundText;
import net.momirealms.customnameplates.objects.nameplates.NameplateText;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManager extends Function {

    private final NameplatePlaceholders nameplatePlaceholders;
    private final OffsetPlaceholders offsetPlaceholders;

    private final HashMap<String, BackGroundText> papiBG;
    private final HashMap<String, NameplateText> papiNP;
    private final HashMap<String, StaticText> papiST;

    public PlaceholderManager() {
        this.papiBG = new HashMap<>();
        this.papiNP = new HashMap<>();
        this.papiST = new HashMap<>();
        this.nameplatePlaceholders = new NameplatePlaceholders(this);
        this.offsetPlaceholders = new OffsetPlaceholders();
    }

    @Override
    public void load() {
        loadPapi();
        this.nameplatePlaceholders.register();
        this.offsetPlaceholders.register();
    }

    @Override
    public void unload() {
        this.nameplatePlaceholders.unregister();
        this.offsetPlaceholders.unregister();
    }

    public String parsePlaceholders(Player player, String papi) {
        if (papi == null || papi.equals("")) return "";
        return PlaceholderAPI.setPlaceholders(player, papi);
    }

    private final Pattern placeholderPattern = Pattern.compile("%([^%]*)%");

    public List<String> detectPlaceholders(String text){
        if (text == null || !text.contains("%")) return Collections.emptyList();
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = placeholderPattern.matcher(text);
        while (matcher.find()) placeholders.add(matcher.group());
        return placeholders;
    }

    public void loadPapi() {
        papiBG.clear();
        papiNP.clear();
        papiST.clear();
        YamlConfiguration papiInfo = ConfigUtil.getConfig("custom-papi.yml");
        papiInfo.getConfigurationSection("papi").getKeys(false).forEach(key -> {
            if (papiInfo.contains("papi." + key + ".background"))
                papiBG.put(key, new BackGroundText(papiInfo.getString("papi." + key + ".text"), papiInfo.getString("papi." + key + ".background")));
            if (papiInfo.contains("papi." + key + ".nameplate"))
                papiNP.put(key, new NameplateText(papiInfo.getString("papi." + key + ".text"), papiInfo.getString("papi." + key + ".nameplate")));
            if (papiInfo.contains("papi." + key + ".static"))
                papiST.put(key, new StaticText(papiInfo.getString("papi." + key + ".text"), papiInfo.getInt("papi." + key + ".static")));
        });
    }

    public HashMap<String, BackGroundText> getPapiBG() {
        return papiBG;
    }

    public HashMap<String, NameplateText> getPapiNP() {
        return papiNP;
    }

    public HashMap<String, StaticText> getPapiST() {
        return papiST;
    }
}
