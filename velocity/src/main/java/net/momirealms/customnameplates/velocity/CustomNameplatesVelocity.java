package net.momirealms.customnameplates.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.momirealms.customnameplates.common.message.MessageType;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;

@Plugin(
        id = "customnameplates",
        name = "CustomNameplates",
        version = "2.3",
        authors = {"XiaoMoMi"},
        dependencies = {@Dependency(id = "velocitab", optional = true)}
)
public class CustomNameplatesVelocity {

        private static final String CHANNEL = "customnameplates:cnp";
        private static CustomNameplatesVelocity instance;
        private final ProxyServer server;
        private final Logger logger;
        private VelocityTeamManager teamManager;

        @Inject
        public CustomNameplatesVelocity(ProxyServer server, Logger logger) {
                this.server = server;
                this.logger = logger;
        }

        @Subscribe
        public void onInit(ProxyInitializeEvent event) {
                instance = this;
                server.getChannelRegistrar().register(MinecraftChannelIdentifier.from(CHANNEL));
                Optional<PluginContainer> optContainer = server.getPluginManager().getPlugin("velocitab");
                if (optContainer.isEmpty()) {
                        logger.warn("You don't have to install CustomNameplates on Velocity if you don't use Velocitab");
                        server.shutdown();
                } else {
                        teamManager = new VelocitabManager(optContainer.get());
                }
        }

        @Subscribe
        public void onShutdown(ProxyShutdownEvent event) {
                server.getChannelRegistrar().unregister(MinecraftChannelIdentifier.from(CHANNEL));
        }

        @Subscribe
        @SuppressWarnings("UnstableApiUsage")
        public void onPluginMessage(PluginMessageEvent event) {
                if (!event.getIdentifier().getId().equals(CHANNEL)) {
                        return;
                }
                if (teamManager == null) {
                        this.logger.warn("No team manager available");
                        return;
                }
                ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());
                byte length = dataInput.readByte();
                if (length != 7) {
                        return;
                }
                String type = dataInput.readUTF();
                if (!type.equals(MessageType.UPDATE)) {
                        return;
                }
                Optional<Player> optionalOwner = server.getPlayer(dataInput.readUTF());
                if (optionalOwner.isEmpty()) return;
                Player owner = optionalOwner.get();

                String team = teamManager.getTeamName(owner);

                Optional<Player> optionalViewer = server.getPlayer(dataInput.readUTF());
                if (optionalViewer.isEmpty()) return;
                Player viewer = optionalViewer.get();

                String prefixJson = dataInput.readUTF();
                String suffixJson = dataInput.readUTF();

                TeamColor teamColor = TeamColor.valueOf(dataInput.readUTF());
                TeamTagVisibility visibility = TeamTagVisibility.valueOf(dataInput.readUTF());

                if (teamManager instanceof VelocitabManager) {
                        server.getScheduler().buildTask(this, () -> {
                                if (!owner.isActive() || !viewer.isActive()) {
                                        return;
                                }
                                String teamNameRetry = teamManager.getTeamName(owner);
                                if (teamNameRetry != null)
                                        teamManager.sendTeamUpdatePacket(viewer, teamNameRetry, teamColor, visibility, prefixJson, suffixJson);
                                else
                                        logger.warn("Failed to get player " + owner.getUsername() + "'s team name.");
                        }).delay(Duration.ofMillis(500)).schedule();
                } else {
                        teamManager.sendTeamUpdatePacket(viewer, team, teamColor, visibility, prefixJson, suffixJson);
                }
        }

        public static CustomNameplatesVelocity get() {
                return instance;
        }

        public static CustomNameplatesVelocity getPlugin() {
                return instance;
        }
}
