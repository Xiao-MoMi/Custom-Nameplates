# Event Type Condition Feature

## Overview

The `event-type` condition allows you to display text/nameplates/bubbles only when specific events occur. This is useful for showing temporary messages when players gain job experience, level up, or any other custom event from plugins like zJobs.

## How It Works

When an event occurs (like `JobExpGainEvent` from zJobs), the event is marked as "active" for the player. The condition checks if that event type is currently active, and if so, the text is displayed. After a configurable duration, the event is automatically marked as inactive.

## Configuration

### Basic Usage

Add the `event-type` requirement to any nameplate, bubble, actionbar, or bossbar configuration:

```yaml
requirements:
  event-type: "JobExpGainEvent"
```

### Complete Example - Actionbar

```yaml
# configs/actionbar.yml
default:
  text: "<#00FF00>+{exp} Job XP!"
  requirements:
    event-type: "JobExpGainEvent"
```

### Complete Example - Bubble

```yaml
# configs/bubble.yml
job_levelup:
  text: "<rainbow>LEVEL UP!</rainbow>"
  requirements:
    event-type: "JobLevelUpEvent"
  duration: 60  # Display for 3 seconds
```

## Setting Up Event Listeners

To use this feature with custom plugin events, you need to create an event listener. Here are examples:

### Example 1: zJobs Integration

```java
import fr.maxlego08.zjobs.api.event.JobExpGainEvent;
import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ZJobsListener implements Listener {

    private final CustomNameplates plugin;

    public ZJobsListener(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJobExpGain(JobExpGainEvent event) {
        Player player = event.getPlayer();
        AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) return;

        // Mark event as active
        cnPlayer.markEventActive("JobExpGainEvent");

        // Auto-remove after 40 ticks (2 seconds)
        Bukkit.getScheduler().runTaskLater(
            (org.bukkit.plugin.Plugin) plugin,
            () -> {
                AbstractCNPlayer p = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
                if (p != null) {
                    p.markEventInactive("JobExpGainEvent");
                }
            },
            40
        );
    }
}
```

### Example 2: Generic Event

```java
@EventHandler
public void onCustomEvent(YourCustomEvent event) {
    Player player = event.getPlayer();
    AbstractCNPlayer cnPlayer = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
    if (cnPlayer == null) return;

    // Mark event as active for 3 seconds
    cnPlayer.markEventActive("YourCustomEvent");

    Bukkit.getScheduler().runTaskLater(
        (org.bukkit.plugin.Plugin) plugin,
        () -> {
            AbstractCNPlayer p = (AbstractCNPlayer) plugin.getPlayer(player.getUniqueId());
            if (p != null) {
                p.markEventInactive("YourCustomEvent");
            }
        },
        60  // 3 seconds in ticks
    );
}
```

## API Methods

### CNPlayer Interface

- `void markEventActive(String eventType)` - Mark an event type as active for this player
- `void markEventInactive(String eventType)` - Mark an event type as inactive for this player
- `boolean isEventActive(String eventType)` - Check if an event type is currently active

## Multiple Events

You can track multiple different event types simultaneously:

```yaml
job_xp_gain:
  text: "<#00FF00>+XP!"
  requirements:
    event-type: "JobExpGainEvent"

job_level_up:
  text: "<rainbow>LEVEL UP!</rainbow>"
  requirements:
    event-type: "JobLevelUpEvent"

skill_unlock:
  text: "<gold>New Skill Unlocked!"
  requirements:
    event-type: "SkillUnlockEvent"
```

## Combining with Other Requirements

The event-type condition can be combined with other requirements:

```yaml
vip_job_xp:
  text: "<gold>VIP +{exp} XP!"
  requirements:
    event-type: "JobExpGainEvent"
    permission: "vip.rank"
    world:
      - "world"
      - "world_nether"
```

## Tips

1. **Duration**: Choose appropriate durations based on your needs:
   - Quick notifications: 20-40 ticks (1-2 seconds)
   - Important messages: 60-100 ticks (3-5 seconds)

2. **Event Names**: Use descriptive event type names that match your event class names for easier maintenance

3. **Performance**: The event tracking uses a HashSet, so checking active events is very fast

4. **Multiple Instances**: If the same event fires multiple times quickly, the display duration will extend (the last timer wins)

## Troubleshooting

**Text not showing:**
- Verify the event listener is properly registered in your plugin
- Check that the event type name matches exactly (case-sensitive)
- Ensure `markEventActive()` is being called when the event fires
- Verify the player is online when the event occurs

**Text stays too long:**
- Check the duration in your scheduler (in ticks: 20 ticks = 1 second)
- Ensure `markEventInactive()` is being called

**Events not clearing:**
- Make sure the scheduled task is running
- Verify the player hasn't logged out (null check in the delayed task)
