package com.wufanguitar.breakpad;


public class BreakpadDumper {
    static {
        System.loadLibrary("breakpad-core");
    }

    public static void initBreakpad(String path) {
        initBreakpadNative(path);
    }

    private static native void initBreakpadNative(String path);
}
