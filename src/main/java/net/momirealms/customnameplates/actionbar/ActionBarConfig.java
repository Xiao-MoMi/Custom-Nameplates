package net.momirealms.customnameplates.actionbar;

import net.momirealms.customnameplates.requirements.Requirement;

import java.util.List;

public class ActionBarConfig {

    private final int rate;
    private final int interval;
    private final String[] text;
    private final List<Requirement> conditions;

    public ActionBarConfig(int rate, int interval, String[] text, List<Requirement> conditions) {
        this.rate = rate;
        this.text = text;
        this.conditions = conditions;
        this.interval = interval;
    }

    public int getRate() {
        return rate;
    }

    public String[] getText() {
        return text;
    }

    public List<Requirement> getConditions() {
        return conditions;
    }

    public int getInterval() {
        return interval;
    }
}
