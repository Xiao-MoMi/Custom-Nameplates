package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.CNPlayer;

import java.util.ArrayList;
import java.util.List;

public class BossBarDisplayController {

    private final CNPlayer owner;
    private final BossBarManager manager;
    private final BossBarSender[] senders;

    public BossBarDisplayController(BossBarManager manager, CNPlayer owner) {
        this.owner = owner;
        this.manager = manager;
        List<BossBarSender> senderList = new ArrayList<>();
        for (BossBarConfig config : manager.allConfigs()) {
            BossBarSender sender = new BossBarSender(owner, config);
            senderList.add(sender);
            this.owner.addFeature(sender);
        }
        this.senders = senderList.toArray(new BossBarSender[0]);
    }

    public void onTick() {
        int size = senders.length;
        int[] states = new int[size];
        int index = size;
        for (int i = 0; i < size; i++) {
            BossBarSender sender = senders[i];
            boolean canShow = sender.checkConditions();
            if (canShow) {
                if (!sender.isShown()) {
                    states[i] = 1;
                    sender.init();
                    sender.tick();
                    if (index == size) {
                        index = i;
                    }
                } else {
                    states[i] = 2;
                    if (i > index) {
                        sender.hide();
                    }
                    sender.tick();
                }
            } else {
                if (sender.isShown()) {
                    sender.hide();
                }
                states[i] = 0;
            }
        }
        if (index != size) {
            for (int i = index; i < size; i++) {
                if (states[i] != 0) {
                    senders[i].show();
                }
            }
        }
    }

    public void destroy() {
        for (BossBarSender sender : this.senders) {
            sender.hide();
            this.owner.removeFeature(sender);
        }
    }
}
