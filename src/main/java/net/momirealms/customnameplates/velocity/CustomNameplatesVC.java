package net.momirealms.customnameplates.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
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
import net.william278.velocitab.Velocitab;
import net.william278.velocitab.api.VelocitabAPI;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Optional;

@Plugin(id = "customnameplates", name = "CustomNameplates", version = "2.2", authors = {"XiaoMoMi"},
        dependencies = {
            @Dependency(id = "velocitab")
        }
)
public class CustomNameplatesVC {

    private static CustomNameplatesVC instance;
    private final ProxyServer server;
    private final Logger logger;
    private Velocitab tab;

    @Inject
    public CustomNameplatesVC(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;
    }

    public static CustomNameplatesVC getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(MinecraftChannelIdentifier.from("customnameplates:cnp"));
        Optional<PluginContainer> optContainer = server.getPluginManager().getPlugin("velocitab");
        optContainer.ifPresent(pluginContainer -> tab = (Velocitab) pluginContainer.getInstance().get());
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        server.getChannelRegistrar().unregister(MinecraftChannelIdentifier.from("customnameplates:cnp"));
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().getId().equals("customnameplates:cnp")) {
            return;
        }
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());
        String playerName = dataInput.readUTF();
        Optional<Player> optPlayer = server.getPlayer(playerName);
        if (optPlayer.isEmpty()) return;
        var player = VelocitabAPI.getInstance().getUser(optPlayer.get());
        player.ifPresent(presentPlayer -> presentPlayer.getTeamName(tab).thenAccept(team -> {
            server.getScheduler().buildTask(this, () -> {
                ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
                byteArrayDataOutput.writeUTF(playerName);
                byteArrayDataOutput.writeUTF(team);
                optPlayer.get().getCurrentServer().ifPresent(it -> it.sendPluginMessage(MinecraftChannelIdentifier.from("customnameplates:cnp"), byteArrayDataOutput.toByteArray()));
            }).delay(Duration.ofSeconds(1)).schedule();
        }));
    }
}
