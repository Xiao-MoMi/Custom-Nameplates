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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.DelegatingCaptionProvider;

/**
 * A custom implementation of a {@link CaptionProvider} for the Custom Nameplates plugin.
 * <p>
 * This class provides a caption provider that delegates to a constant provider which includes
 * predefined captions for argument parsing failure messages. It ensures that specific caption keys,
 * such as those related to parsing time, URL, and named text color arguments, are available for use.
 * </p>
 *
 * @param <C> the context type associated with the captions
 */
public final class CustomNameplatesCaptionProvider<C> extends DelegatingCaptionProvider<C> {

    private static final CaptionProvider<?> PROVIDER = CaptionProvider.constantProvider()
            .putCaption(CustomNameplatesCaptionKeys.ARGUMENT_PARSE_FAILURE_URL, "")
            .putCaption(CustomNameplatesCaptionKeys.ARGUMENT_PARSE_FAILURE_TIME, "")
            .putCaption(CustomNameplatesCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMEDTEXTCOLOR, "")
            .build();

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull CaptionProvider<C> delegate() {
        return (CaptionProvider<C>) PROVIDER;
    }
}
