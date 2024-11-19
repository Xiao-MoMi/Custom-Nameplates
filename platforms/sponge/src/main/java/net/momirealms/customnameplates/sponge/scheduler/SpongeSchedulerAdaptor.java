package net.momirealms.customnameplates.sponge.scheduler;

import net.momirealms.customnameplates.common.plugin.scheduler.AbstractJavaScheduler;
import net.momirealms.customnameplates.common.plugin.scheduler.RegionExecutor;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import net.momirealms.customnameplates.sponge.SpongeCustomNameplates;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

public class SpongeSchedulerAdaptor extends AbstractJavaScheduler<Void> {

    private final SpongeExecutor executor;

    public SpongeSchedulerAdaptor(SpongeCustomNameplates plugin) {
        super(plugin);
        this.executor = new SpongeExecutor(plugin);
    }

    @Override
    public RegionExecutor<Void> sync() {
        return executor;
    }

    public static class SpongeExecutor implements RegionExecutor<Void> {

        private final SpongeCustomNameplates plugin;

        public SpongeExecutor(SpongeCustomNameplates plugin) {
            this.plugin = plugin;
        }

        @Override
        public void run(Runnable r, Void l) {
            this.plugin.game().server().scheduler().submit(Task.builder()
                    .plugin(plugin.pluginContainer())
                    .execute(r)
                    .build());
        }

        @Override
        public SchedulerTask runLater(Runnable r, long delayTicks, Void l) {
            return new SpongeTask(
                    this.plugin.game().server().scheduler().submit(Task.builder()
                            .plugin(plugin.pluginContainer())
                            .execute(r)
                            .delay(Ticks.of(delayTicks))
                            .build())
            );
        }

        @Override
        public SchedulerTask runRepeating(Runnable r, long delayTicks, long period, Void l) {
            return new SpongeTask(
                    this.plugin.game().server().scheduler().submit(Task.builder()
                            .plugin(plugin.pluginContainer())
                            .execute(r)
                            .delay(Ticks.of(delayTicks))
                            .interval(Ticks.of(period))
                            .build())
            );
        }
    }
}
