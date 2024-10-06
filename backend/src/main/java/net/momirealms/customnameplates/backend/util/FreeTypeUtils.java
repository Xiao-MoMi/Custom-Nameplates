/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.backend.util;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;

public class FreeTypeUtils {

    public static final Object LOCK = new Object();
    private static long freeType = 0L;

    public FreeTypeUtils() {
    }

    public static long initialize() {
        synchronized(LOCK) {
            if (freeType == 0L) {
                MemoryStack memoryStack = MemoryStack.stackPush();
                try {
                    PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                    checkFatalError(FreeType.FT_Init_FreeType(pointerBuffer), "Initializing FreeType library");
                    freeType = pointerBuffer.get();
                } catch (Throwable t1) {
                    try {
                        memoryStack.close();
                    } catch (Throwable t2) {
                        t1.addSuppressed(t2);
                    }
                    throw t1;
                }
                memoryStack.close();
            }
            return freeType;
        }
    }

    public static void checkFatalError(int code, String description) {
        if (code != 0) {
            String var10002 = getErrorMessage(code);
            throw new IllegalStateException("FreeType error: " + var10002 + " (" + description + ")");
        }
    }

    private static String getErrorMessage(int code) {
        String string = FreeType.FT_Error_String(code);
        return string != null ? string : "Unrecognized error: 0x" + Integer.toHexString(code);
    }

    public static FT_Vector setShift(FT_Vector vec, float x, float y) {
        long xShift = Math.round(x * 64.0F);
        long yShift = Math.round(y * 64.0F);
        return vec.set(xShift, yShift);
    }

    public static float getAdvance(FT_Vector vec) {
        return (float) vec.x() / 64.0F;
    }

    public static void release() {
        synchronized(LOCK) {
            if (freeType != 0L) {
                FreeType.FT_Done_Library(freeType);
                freeType = 0L;
            }
        }
    }

    public static ByteBuffer readResource(InputStream inputStream) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
        if (readableByteChannel instanceof SeekableByteChannel seekableByteChannel) {
            return readResource(readableByteChannel, (int)seekableByteChannel.size() + 1);
        } else {
            return readResource(readableByteChannel, 8192);
        }
    }

    private static ByteBuffer readResource(ReadableByteChannel channel, int bufSize) throws IOException {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(bufSize);
        try {
            while(channel.read(byteBuffer) != -1) {
                if (!byteBuffer.hasRemaining()) {
                    byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
                }
            }
            return byteBuffer;
        } catch (IOException e) {
            MemoryUtil.memFree(byteBuffer);
            throw e;
        }
    }
}