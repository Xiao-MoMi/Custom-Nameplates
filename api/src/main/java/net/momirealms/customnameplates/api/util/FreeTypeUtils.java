package net.momirealms.customnameplates.api.util;

import net.momirealms.customnameplates.api.CustomNameplates;
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

    public static boolean checkError(int code, String description) {
        if (code != 0) {
            CustomNameplates.getInstance().getPluginLogger().warn("FreeType error: " + getErrorMessage(code) + " (" + description + ")");
            return true;
        } else {
            return false;
        }
    }

    private static String getErrorMessage(int code) {
        String string = FreeType.FT_Error_String(code);
        return string != null ? string : "Unrecognized error: 0x" + Integer.toHexString(code);
    }

    public static FT_Vector set(FT_Vector vec, float x, float y) {
        long l = Math.round(x * 64.0F);
        long m = Math.round(y * 64.0F);
        return vec.set(l, m);
    }

    public static float getX(FT_Vector vec) {
        return (float)vec.x() / 64.0F;
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