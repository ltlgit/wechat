package com.wechat.wechat.controller;

import com.wechat.wechat.service.WechatPubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/wechatPub")
@Slf4j
public class WechatPubController {

    @Autowired
    private WechatPubService wechatPubService;

    @RequestMapping("/test")
    public String test(){
        return "ok";
    }

    @RequestMapping("/access")
    public String access(HttpServletRequest request, HttpServletResponse response){
        log.info("公众号信息接送start");
        try {
            wechatPubService.access(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
