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

package net.momirealms.customnameplates.common.util;

import java.util.Locale;

/**
 * Enum representing the supported system architectures.
 * This enum helps determine the architecture of the current system
 * and provides utility methods for architecture-related operations.
 * <p>
 * The possible architectures include:
 * - X64 (64-bit x86)
 * - X86 (32-bit x86)
 * - ARM64 (64-bit ARM)
 * - ARM32 (32-bit ARM)
 * - PPC64LE (64-bit PowerPC, Little Endian)
 * - RISCV64 (64-bit RISC-V)
 * <p>
 * It also provides a utility method to get the native path representation of the architecture
 * and a method to retrieve the current architecture of the system.
 */
public enum Architecture {

    /**
     * Represents a 64-bit x86 architecture.
     */
    X64(true),

    /**
     * Represents a 32-bit x86 architecture.
     */
    X86(false),

    /**
     * Represents a 64-bit ARM architecture.
     */
    ARM64(true),

    /**
     * Represents a 32-bit ARM architecture.
     */
    ARM32(false),

    /**
     * Represents a 64-bit PowerPC Little Endian architecture.
     * Only 'ppc64le' is supported in this case.
     */
    PPC64LE(true),

    /**
     * Represents a 64-bit RISC-V architecture.
     * Only 'riscv64' is supported in this case.
     */
    RISCV64(true);

    /**
     * The current architecture of the system, determined during class initialization.
     */
    static final Architecture current;

    /**
     * A boolean flag indicating if the architecture is 64-bit.
     */
    final boolean is64Bit;

    /**
     * Constructor to initialize the architecture with its 64-bit flag.
     *
     * @param is64Bit a boolean indicating if the architecture is 64-bit.
     */
    Architecture(boolean is64Bit) {
        this.is64Bit = is64Bit;
    }

    /**
     * Returns the native path representation of the architecture.
     * The name is returned in lowercase using English locale.
     *
     * @return the native path for the architecture (e.g., "x64", "arm64").
     */
    public String getNativePath() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Retrieves the current architecture of the system.
     *
     * @return the current system architecture as an {@link Architecture} enum.
     */
    public static Architecture get() {
        return current;
    }

    // Static block to determine the current system architecture based on system properties.
    static {
        String osArch = System.getProperty("os.arch");
        boolean is64Bit = osArch.contains("64") || osArch.startsWith("armv8");
        if (!osArch.startsWith("arm") && !osArch.startsWith("aarch")) {
            if (osArch.startsWith("ppc")) {
                if (!"ppc64le".equals(osArch)) {
                    throw new UnsupportedOperationException("Only PowerPC 64 LE is supported.");
                }
                current = PPC64LE;
            } else if (osArch.startsWith("riscv")) {
                if (!"riscv64".equals(osArch)) {
                    throw new UnsupportedOperationException("Only RISC-V 64 is supported.");
                }
                current = RISCV64;
            } else {
                current = is64Bit ? X64 : X86;
            }
        } else {
            current = is64Bit ? ARM64 : ARM32;
        }
    }
}
