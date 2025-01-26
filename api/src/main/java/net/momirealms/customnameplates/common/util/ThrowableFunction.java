package net.momirealms.customnameplates.common.util;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T, R> extends Function<T, R> {

    @Override
    default R apply(T t) {
        try {
            return applyWithException(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    R applyWithException(T t) throws Exception;
}