package com.heaven.soulmate.fileuploader_ajax.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.fileuploader_ajax.Utils;
import com.heaven.soulmate.fileuploader_ajax.model.UploadResult;
import com.heaven.soulmate.fileuploader_ajax.model.UploadResultItem;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/9/8.
 */
@Controller
public class FileUploaderAjaxController {
    private static final Logger LOGGER = Logger.getLogger(FileUploaderAjaxController.class);

    @RequestMapping("/test")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return "hello world";
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public UploadResult upload(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {

        // Root Directory.
        Properties fileuploadProperties = Utils.readProperties("fileupload.properties");
        String uploadRootPath = fileuploadProperties.getProperty("root_dir");
        String baseUrl = fileuploadProperties.getProperty("base_url");
        LOGGER.info("uploadRootPath=" + uploadRootPath + " base_url=" + baseUrl);

        File uploadRootDir = new File(uploadRootPath);
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }

        UploadResult result = new UploadResult();
        result.errNo = 0;
        result.data = new LinkedList<UploadResultItem>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            // Client File Name
            String name = file.getOriginalFilename();
            LOGGER.info("Client File Name = " + name);

            if (name != null && name.length() > 0) {
                try {
                    byte[] bytes = file.getBytes();

                    // Create the file on server
                    File serverFile = new File(uploadRootDir.getAbsolutePath()
                            + File.separator + name);

                    // Stream to write data to file in server.
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(serverFile));
                    stream.write(bytes);
                    stream.close();
                    LOGGER.info("Write file: " + serverFile);

                    String md5 = Utils.md5FromFile(serverFile.getAbsolutePath());

                    UploadResultItem resultItem = new UploadResultItem();
                    resultItem.original_name = serverFile.getAbsolutePath();
                    resultItem.url = baseUrl + md5;

                    result.data.add(resultItem);

                } catch (Exception e) {
                    LOGGER.error("Error Write file: " + name);
                }
            }
        }

        return result;
    }
}
