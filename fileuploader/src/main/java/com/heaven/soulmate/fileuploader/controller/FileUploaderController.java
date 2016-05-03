package com.heaven.soulmate.fileuploader.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.util.LinkedList;

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
}
