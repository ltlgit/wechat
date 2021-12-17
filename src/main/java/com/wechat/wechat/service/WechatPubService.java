package com.wechat.wechat.service;

import com.wechat.wechat.utils.WechatPubUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class WechatPubService {

    public String access(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String respMsg = null;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if(Objects.nonNull(echostr)&&WechatPubUtils.checkSign(signature,timestamp,nonce)){
            //公众号服务器配置修改返回
            return echostr;
        }

        /*Map<String, String> map = WechatPubUtils.parseXml(request);
        String encrypt = map.get("Encrypt");
        String toUserName = map.get("ToUserName");*/

        ServletInputStream in = request.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(in,"UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> decryptMap = WechatPubUtils.decrypt(signature, timestamp, nonce,sb.toString());

        String msgType = decryptMap.get("MsgType");
        String fromUserName = decryptMap.get("FromUserName");
        String toUserName = decryptMap.get("toUserName");
        if(WechatPubUtils.REQ_TEXT.endsWith(msgType)){
            log.info("文本消息");
            respMsg = "文本消息";
        }else if(WechatPubUtils.REQ_IMAGE.endsWith(msgType)){
            log.info("图片消息");
            respMsg = "图片消息";
        }

        String resultXml = "<xml>";
        resultXml += "<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>";
        resultXml += "<FromUserName><![CDATA["+toUserName+"]]></FromUserName>";
        resultXml += "<CreateTime><![CDATA["+new Date().getTime() +"]]></CreateTime>";
        resultXml += "<MsgType><![CDATA["+msgType+"]]></MsgType>";
        resultXml += "<Content><![CDATA["+respMsg+"]]></Content>";
        resultXml += "</xml>";
        return resultXml;
    }
}
