package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

public interface BossBarConfig {
    
    BossBar.Overlay DEFAULT_OVERLAY = BossBar.Overlay.PROGRESS;
    BossBar.Color DEFAULT_COLOR = BossBar.Color.YELLOW;

    String id();

    Requirement[] requirements();

    CarouselText[] carouselTexts();
    
    BossBar.Overlay overlay();
    
    BossBar.Color color();

    float progress();

    static BossBarConfig.Builder builder() {
        return new BossBarConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder requirement(Requirement[] requirements);

        Builder carouselText(CarouselText[] carouselTexts);
        
        Builder overlay(BossBar.Overlay overlay);
        
        Builder color(BossBar.Color color);

        Builder progress(float progress);

        BossBarConfig build();
    }
}
