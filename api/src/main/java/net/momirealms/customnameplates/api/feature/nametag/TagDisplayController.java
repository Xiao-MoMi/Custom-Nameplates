package net.momirealms.customnameplates.api.feature.nametag;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.network.PassengerProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagDisplayController {

    private final CNPlayer owner;
    private final UnlimitedTagManager manager;
    private final TagDisplay[] tags;
    private double hatOffset;

    public TagDisplayController(UnlimitedTagManager manager, CNPlayer owner) {
        this.owner = owner;
        this.manager = manager;
        List<TagDisplay> senderList = new ArrayList<>();
        for (TagConfig config : manager.allConfigs()) {
            TagDisplay sender = new TagDisplay(owner, config, this);
            senderList.add(sender);
            this.owner.addFeature(sender);
        }
        this.tags = senderList.toArray(new TagDisplay[0]);
    }

    public double hatOffset() {
        return hatOffset;
    }

    public void setHatOffset(double hatOffset) {
        this.hatOffset = hatOffset;
    }

    public void onTick() {
        HashSet<CNPlayer> playersToUpdatePassengers = new HashSet<>();
        for (TagDisplay display : tags) {
            boolean canShow = display.checkOwnerConditions();
            // 能大众显示
            if (canShow) {
                // 当前大众显示
                if (display.isShown()) {
                    for (CNPlayer nearby : owner.nearbyPlayers()) {
                        // 如果已经展示了
                        if (display.isShown(nearby)) {
                            // 不满足条件就撤掉
                            if (!display.checkViewerConditions(nearby)) {
                                display.hide(nearby);
                            }
                        } else {
                            // 未展示，则检测条件，可以就上
                            if (display.checkViewerConditions(nearby)) {
                                display.show(nearby);
                                playersToUpdatePassengers.add(nearby);
                            }
                        }
                    }
                    // 更新一下文字顺序，放在后面是为了防止已经被hide的玩家多收一个包
                    display.tick();
                } else {
                    // 之前隐藏，现在开始大众显示
                    // 需要重置文字顺序
                    display.init();
                    // 更新一下文字顺序
                    display.tick();
                    display.show();
                    for (CNPlayer nearby : owner.nearbyPlayers()) {
                        if (display.checkViewerConditions(nearby)) {
                            display.show(nearby);
                            playersToUpdatePassengers.add(nearby);
                        }
                    }
                }
            } else {
                // 不能展示的情况
                // 如果已经展示了，就咔掉所有玩家
                if (display.isShown()) {
                    display.hide();
                }
            }
        }

        // Update passengers
        Set<Integer> realPassengers = owner.passengers();
        for (CNPlayer nearby : playersToUpdatePassengers) {
            updatePassengers(nearby, realPassengers, true);
        }
    }

    public void destroy() {
        for (TagDisplay tag : this.tags) {
            tag.hide();
            this.owner.removeFeature(tag);
        }
    }

    public void handlePlayerRemove(CNPlayer another) {
        for (TagDisplay display : this.tags) {
            if (display.isShown()) {
                if (display.isShown(another)) {
                    display.hide(another);
                }
            }
        }
    }

    public Runnable handlePlayerAdd(CNPlayer another) {
        boolean updatePassengers = false;
        for (TagDisplay display : this.tags) {
            if (display.isShown()) {
                if (!display.isShown(another)) {
                    if (display.checkViewerConditions(another)) {
                        display.show(another);
                        updatePassengers = true;
                    }
                }
            }
        }
        if (updatePassengers) {
            Set<Integer> realPassengers = owner.passengers();
            return () -> updatePassengers(another, realPassengers, true);
        }
        return null;
    }

    private Object updatePassengers(CNPlayer another, Set<Integer> realPassengers, boolean sendPacket) {
        Set<Integer> fakePassengers = another.getTrackedPassengerIds(owner);
        fakePassengers.addAll(realPassengers);
        int[] passengers = new int[fakePassengers.size()];
        int index = 0;
        for (int passenger : fakePassengers) {
            passengers[index++] = passenger;
        }
        Object packet = CustomNameplates.getInstance().getPlatform().setPassengersPacket(owner.entityID(), passengers);
        if (sendPacket) {
            CustomNameplates.getInstance().getPacketSender().sendPacket(another, packet);
        }
        return packet;
    }

    public void handleEntityDataChange(CNPlayer another, boolean isCrouching) {
        boolean updatePassengers = false;
        PassengerProperties properties = another.getTrackedProperties(owner);
        // should never be null
        if (properties == null) return;
        properties.setCrouching(isCrouching);
        ArrayList<Object> packets = new ArrayList<>();
        for (TagDisplay display : this.tags) {
            if (display.isShown()) {
                if (display.isShown(another)) {
                    packets.addAll(display.respawn(another, false));
                    updatePassengers = true;
                }
            }
        }
        if (updatePassengers) {
            Set<Integer> realPassengers = owner.passengers();
            packets.add(updatePassengers(another, realPassengers, false));
            CustomNameplates.getInstance().getPacketSender().sendPacket(another, packets);
        }
    }

    public void handleAttributeChange(CNPlayer another, double scale) {
        boolean updatePassengers = false;
        PassengerProperties properties = another.getTrackedProperties(owner);
        // should never be null
        if (properties == null) return;
        properties.setScale(scale);
        ArrayList<Object> packets = new ArrayList<>();
        for (TagDisplay display : this.tags) {
            if (display.isShown()) {
                if (display.isShown(another)) {
                    packets.addAll(display.respawn(another, false));
                    updatePassengers = true;
                }
            }
        }
        if (updatePassengers) {
            Set<Integer> realPassengers = owner.passengers();
            packets.add(updatePassengers(another, realPassengers, false));
            CustomNameplates.getInstance().getPacketSender().sendPacket(another, packets);
        }
    }
}
