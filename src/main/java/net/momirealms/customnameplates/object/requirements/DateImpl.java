package net.momirealms.customnameplates.object.requirements;

import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashSet;

public record DateImpl(HashSet<String> dates) implements Requirement {

    @Override
    public boolean isConditionMet(Player player) {
        Calendar calendar = Calendar.getInstance();
        String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
        return dates.contains(current);
    }
}