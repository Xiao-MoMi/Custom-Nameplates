/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.common.locale;

import net.kyori.adventure.internal.Internals;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.util.TriState;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class MiniMessageTranslationRegistryImpl implements Examinable, MiniMessageTranslationRegistry {
    private final Key name;
    private final Map<String, Translation> translations = new ConcurrentHashMap<>();
    private Locale defaultLocale = Locale.US;
    private final MiniMessage miniMessage;

    MiniMessageTranslationRegistryImpl(final Key name, final MiniMessage miniMessage) {
        this.name = name;
        this.miniMessage = miniMessage;
    }

    @Override
    public void register(final @NotNull String key, final @NotNull Locale locale, final @NotNull String format) {
        this.translations.computeIfAbsent(key, Translation::new).register(locale, format);
    }

    @Override
    public void unregister(final @NotNull String key) {
        this.translations.remove(key);
    }

    @Override
    public boolean contains(final @NotNull String key) {
        return this.translations.containsKey(key);
    }

    @Override
    public @NotNull Key name() {
        return name;
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        // No need to implement this method
        return null;
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        Translation translation = translations.get(component.key());
        if (translation == null) {
            return null;
        }
        String miniMessageString = translation.translate(locale);
        if (miniMessageString == null) {
            return null;
        }
        if (miniMessageString.isEmpty()) {
            return Component.empty();
        }
        final Component resultingComponent;
        if (component.arguments().isEmpty()) {
            resultingComponent = this.miniMessage.deserialize(miniMessageString);
        } else {
            resultingComponent = this.miniMessage.deserialize(miniMessageString, new ArgumentTag(component.arguments()));
        }
        if (component.children().isEmpty()) {
            return resultingComponent;
        } else {
            return resultingComponent.children(component.children());
        }
    }

    @Override
    public String miniMessageTranslation(@NotNull String key, @NotNull Locale locale) {
        Translation translation = translations.get(key);
        if (translation == null) {
            return null;
        }
        return translation.translate(locale);
    }

    @Override
    public @NotNull TriState hasAnyTranslations() {
        if (!this.translations.isEmpty()) {
            return TriState.TRUE;
        }
        return TriState.FALSE;
    }

    @Override
    public void defaultLocale(final @NotNull Locale defaultLocale) {
        this.defaultLocale = requireNonNull(defaultLocale, "defaultLocale");
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("translations", this.translations));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof MiniMessageTranslationRegistryImpl that)) return false;
        return this.name.equals(that.name)
                && this.translations.equals(that.translations)
                && this.defaultLocale.equals(that.defaultLocale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.translations, this.defaultLocale);
    }

    @Override
    public String toString() {
        return Internals.toString(this);
    }

    public static class ArgumentTag implements TagResolver {
        private static final String NAME = "argument";
        private static final String NAME_1 = "arg";

        private final List<? extends ComponentLike> argumentComponents;

        public ArgumentTag(final @NotNull List<? extends ComponentLike> argumentComponents) {
            this.argumentComponents = Objects.requireNonNull(argumentComponents, "argumentComponents");
        }

        @Override
        public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue arguments, final @NotNull Context ctx) throws ParsingException {
            final int index = arguments.popOr("No argument number provided").asInt().orElseThrow(() -> ctx.newException("Invalid argument number", arguments));

            if (index < 0 || index >= argumentComponents.size()) {
                throw ctx.newException("Invalid argument number", arguments);
            }

            return Tag.inserting(argumentComponents.get(index));
        }

        @Override
        public boolean has(final @NotNull String name) {
            return name.equals(NAME) || name.equals(NAME_1);
        }
    }

    final class Translation implements Examinable {
        private final String key;
        private final Map<Locale, String> formats;

        Translation(final @NotNull String key) {
            this.key = requireNonNull(key, "translation key");
            this.formats = new ConcurrentHashMap<>();
        }

        void register(final @NotNull Locale locale, final @NotNull String format) {
            if (this.formats.putIfAbsent(requireNonNull(locale, "locale"), requireNonNull(format, "message format")) != null) {
                throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", this.key, locale));
            }
        }

        @Nullable String translate(final @NotNull Locale locale) {
            String format = this.formats.get(requireNonNull(locale, "locale"));
            if (format == null) {
                format = this.formats.get(new Locale(locale.getLanguage())); // try without country
                if (format == null) {
                    format = this.formats.get(MiniMessageTranslationRegistryImpl.this.defaultLocale); // try local default locale
                }
            }
            return format;
        }

        @Override
        public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
            return Stream.of(
                    ExaminableProperty.of("key", this.key),
                    ExaminableProperty.of("formats", this.formats)
            );
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) return true;
            if (!(other instanceof Translation that)) return false;
            return this.key.equals(that.key) &&
                    this.formats.equals(that.formats);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.key, this.formats);
        }

        @Override
        public String toString() {
            return Internals.toString(this);
        }
    }
}
