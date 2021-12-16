package com.wechat.wechat.service;

import com.wechat.wechat.utils.WechatPubUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
        if(WechatPubUtils.checkSign(signature,timestamp,nonce)){
            respMsg = echostr;
        }

        Map<String, String> map = WechatPubUtils.parseXml(request);
        String encrypt = map.get("Encrypt");
        String toUserName = map.get("ToUserName");


        String decrypt = WechatPubUtils.decrypt(signature, timestamp, nonce, encrypt);

        String msgType = map.get("MsgType");

        if(WechatPubUtils.REQ_TEXT.endsWith(msgType)){
            log.info("文本消息");
            respMsg = "文本消息";
        }else if(WechatPubUtils.REQ_IMAGE.endsWith(msgType)){
            log.info("图片消息");
            respMsg = "图片消息";
        }
        return respMsg;
    }
}
