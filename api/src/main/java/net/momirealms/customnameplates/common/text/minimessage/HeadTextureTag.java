package net.momirealms.customnameplates.common.text.minimessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class HeadTextureTag implements TagResolver {
    public static final HeadTextureTag INSTANCE = new HeadTextureTag();

    private HeadTextureTag() {
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        if (!has(name)) return null;

        String texture = arguments.popOr("No texture hash provided").lowerValue();
        if (!isTextureHash(texture)) {
            throw ctx.newException("Invalid texture hash", arguments);
        }
        if (arguments.hasNext()) {
            throw ctx.newException("Too many arguments present", arguments);
        }
        String profile = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/" + texture + "\"}}}";
        String encoded = Base64.getEncoder().encodeToString(profile.getBytes(StandardCharsets.UTF_8));
        return Tag.selfClosingInserting(Component.object(ObjectContents.playerHead()
                .profileProperty(PlayerHeadObjectContents.property("textures", encoded))
                .build()));
    }

    @Override
    public boolean has(@NotNull String name) {
        return name.equals("head_texture");
    }

    private static boolean isTextureHash(String texture) {
        int length = texture.length();
        if (length == 0 || length > 64) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char c = texture.charAt(i);
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
                return false;
            }
        }
        return true;
    }
}
