package net.momirealms.customnameplates.paper.mechanic.misc;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.manager.RequirementManager;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.requirement.Requirement;
import org.bukkit.entity.Player;

public class DisplayController {

    private final Player owner;
    private final int checkFrequency;
    private final Requirement[] requirements;
    private boolean isShown;
    private int checkTimer;
    private int refreshTimer;
    private String latestValue;
    private int timeLeft;
    private int index;
    private final TimeLimitText[] texts;

    public DisplayController(
            Player player,
            int checkFrequency,
            Requirement[] requirements,
            TimeLimitText[] texts
    ) {
        this.owner = player;
        this.checkFrequency = checkFrequency;
        this.checkTimer = 0;
        this.refreshTimer = 0;
        this.requirements = requirements;
        this.texts = texts;
    }

    public NextStage stateCheck(Condition condition) {
        this.checkTimer++;
        if (this.checkTimer % checkFrequency != 0) {
            return NextStage.KEEP;
        }
        boolean canShow = RequirementManager.isRequirementMet(condition, requirements);
        if (canShow) {
            if (this.isShown) {
                return NextStage.KEEP;
            } else {
                this.isShown = true;
                return NextStage.UPDATE;
            }
        } else {
            if (!this.isShown) {
                return NextStage.KEEP;
            } else {
                this.isShown = false;
                return NextStage.UPDATE;
            }
        }
    }

    public boolean isShown() {
        return isShown;
    }

    public boolean updateText(Condition condition) {
        timeLeft--;
        if (timeLeft <= 0) {
            do {
                index++;
                if (index >= texts.length) {
                    index = 0;
                }
            } while (!RequirementManager.isRequirementMet(condition, texts[index].getRequirements()));

            timeLeft = texts[index].getDuration();
            refreshTimer = 0;
            return updateText(texts[index].getText());
        }

        if (texts[index].getRefreshFrequency() <= 0) {
            return false;
        }

        refreshTimer++;
        if (refreshTimer >= texts[index].getRefreshFrequency()) {
            refreshTimer = 0;
            return updateText(texts[index].getText());
        } else {
            return false;
        }
    }

    private boolean updateText(String text) {
        var newText = PlaceholderAPI.setPlaceholders(owner, text);
        if (newText.equals(latestValue)) {
            return false;
        }
        latestValue = newText;
        return true;
    }

    public void initialize() {
        index = 0;
        checkTimer = 0;
        refreshTimer = 0;
        timeLeft = texts[0].getDuration();
        latestValue = PlaceholderAPI.setPlaceholders(owner, texts[0].getText());
    }

    public String getLatestContent() {
        return latestValue;
    }

    public enum NextStage {
        KEEP,
        UPDATE
    }
}
