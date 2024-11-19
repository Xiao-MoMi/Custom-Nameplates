package net.momirealms.customnameplates.sponge.scheduler;

import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import org.spongepowered.api.scheduler.ScheduledTask;

public class SpongeTask implements SchedulerTask {

    private final ScheduledTask task;

    public SpongeTask(final ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }

    @Override
    public boolean cancelled() {
        return this.task.isCancelled();
    }
}
