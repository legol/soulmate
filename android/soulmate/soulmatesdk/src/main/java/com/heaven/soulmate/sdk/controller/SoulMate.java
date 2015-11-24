package com.heaven.soulmate.sdk.controller;

/**
 * Created by ChenJie3 on 2015/11/24.
 */
public class SoulMate {
    private static SoulMate ourInstance = new SoulMate();

    public static SoulMate getInstance() {
        return ourInstance;
    }

    private SoulMate() {
    }
}
