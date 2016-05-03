package com.heaven.soulmate.fileuploader.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heaven.soulmate.fileuploader.Utils;
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
public class FileUploaderController {
    private static final Logger LOGGER = Logger.getLogger(FileUploaderController.class);

    @RequestMapping("/test")
    @ResponseBody
    public Object test(HttpServletRequest request, HttpServletResponse response) {
        return "hello world";
    }

    // Upload Multi File.
    @RequestMapping(value = "/uploadMultiFile", method = RequestMethod.GET)
    public String uploadMultiFileHandler() {
        // the place where all jsp file resides is "/WEB-INF/pages". This is defined in applicationContext.xml
        // Forward to "/WEB-INF/pages/uploadMultiFile.jsp".
        return "uploadMultiFile";
    }

    // uploadMultiFile.jsp submit to.
    @RequestMapping(value = "/doUpload", method = RequestMethod.POST)
    public String uploadFileHandler(HttpServletRequest request, Model model,
                                    @RequestParam("file") MultipartFile[] files) {

        // Root Directory.
        Properties fileuploadProperties = Utils.readProperties("fileupload.properties");
        String uploadRootPath = fileuploadProperties.getProperty("root_dir");
        LOGGER.info("uploadRootPath=" + uploadRootPath);

        File uploadRootDir = new File(uploadRootPath);
        //
        // Create directory if it not exists.
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        //
        List<File> uploadedFiles = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            // Client File Name
            String name = file.getOriginalFilename();
            System.out.println("Client File Name = " + name);

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
                    //
                    uploadedFiles.add(serverFile);
                    System.out.println("Write file: " + serverFile);
                } catch (Exception e) {
                    System.out.println("Error Write file: " + name);
                }
            }
        }

        // model will be accessed by JSTL. see uploadResult.jsp
        model.addAttribute("uploadedFiles", uploadedFiles);

        // Forward to "/WEB-INF/pages/uploadMultiFile.jsp".
        return "uploadResult";
    }
}
