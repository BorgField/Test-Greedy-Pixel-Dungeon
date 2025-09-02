package com.watabou.utils;

@FunctionalInterface
public interface QuietCallable<V> {

    V call();

}
