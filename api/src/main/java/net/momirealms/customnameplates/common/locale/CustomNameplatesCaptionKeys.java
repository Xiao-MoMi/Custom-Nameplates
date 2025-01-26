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

import org.incendo.cloud.caption.Caption;

/**
 * A utility class that defines keys for various captions used in the Custom Nameplates plugin.
 * <p>
 * This class provides static final constants for common caption keys that are used for
 * error messages, argument parsing failures, and other localized messages in the plugin.
 * These keys can be used with a captioning system to retrieve appropriate translations
 * based on the user's locale.
 * </p>
 */
public final class CustomNameplatesCaptionKeys {
    /**
     * Caption key for a failure when parsing a time argument.
     * This key is used for error messages related to time arguments that failed to parse.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_TIME = Caption.of("argument.parse.failure.time");
    /**
     * Caption key for a failure when parsing a URL argument.
     * This key is used for error messages related to URL arguments that failed to parse.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_URL = Caption.of("argument.parse.failure.url");
    /**
     * Caption key for a failure when parsing a named text color argument.
     * This key is used for error messages related to named text color arguments that failed to parse.
     */
    public static final Caption ARGUMENT_PARSE_FAILURE_NAMEDTEXTCOLOR = Caption.of("argument.parse.failure.namedtextcolor");
}
