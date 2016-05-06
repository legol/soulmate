package com.heaven.soulmate.fileuploader_ajax.controller;

import org.apache.log4j.Logger;

/**
 * Created by ChenJie3 on 2016/5/6.
 */
public class FileVault {
    private static final Logger LOGGER = Logger.getLogger(FileVault.class);
    private static FileVault instance = null;

    public static FileVault sharedInstance() {
        if(instance == null) {
            instance = new FileVault();
        }
        return instance;
    }

}
