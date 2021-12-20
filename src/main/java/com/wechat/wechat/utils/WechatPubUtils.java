package com.wechat.wechat.utils;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WechatPubUtils {

    public final static String REQ_TEXT = "text";
    public final static String REQ_IMAGE = "image";
    public final static String REQ_VOICE = "voice";
    public final static String REQ_VIDEO = "video";
    public final static String REQ_LINK = "link";
    public final static String REQ_LOCATION = "location";
    public final static String REQ_SHORTVIDEO = "shortvideo";
    public final static String REQ_EVENT = "event";

    public final static String RESP_TEXT = "text";
    public final static String RESP_IMAGE = "image";
    public final static String RESP_VOICE = "voice";
    public final static String RESP_MUSIC = "music";
    public final static String RESP_NEWS = "news";
    public final static String RESP_VIDEO = "video";
    public final static String RESP_SUBSCRIBE = "subscribe";
    public final static String RESP_UNSUBSCRIBE = "unsubscribe";
    public final static String RESP_SCAN = "scan";
    public final static String RESP_LOCATION = "location";
    public final static String RESP_CLICK = "CLICK";
    public final static String RESP_VIEW = "VIEW";
    public final static String RESP_TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";

    public final static String token = "ltlce";
    public final static String encodingAesKey = "8iLKKH6zk1XeZsKxdGOvNjhYsydtFH0ySO1iS0ZleRe";
    public final static String appId = "wx994fe56baeeb0361";

    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception{
        Map<String,String> map = new HashMap<>();
        InputStream inputStream = request.getInputStream();
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.elements();

        if(elementList.size()==0){
            map.put(rootElement.getName(),rootElement.getText());
        }else{
            for(Element e:elementList){
                map.put(e.getName(),e.getText());
            }
        }
        inputStream.close();
        return map;
    }

    public static boolean checkSign(String signature,String timestamp,String nonce){
        String[] strArray = { token, timestamp, nonce };
        Arrays.sort(strArray);
        StringBuilder sbuilder = new StringBuilder();
        for (String str : strArray) {
            sbuilder.append(str);
        }
        String sign = SHA1(sbuilder.toString());
        if(signature.equals(sign)){
            log.warn("签名校验通过。");
            return true;
        }else{
            log.warn("签名校验失败。");
        }
        return false;
    }


    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 消息解密
     * */
    public static Map<String, String> decrypt(String msgSignature,String timestamp,String nonce,String fromXML) throws Exception{
        /*String token = "ltlce";
        String encodingAesKey = "8iLKKH6zk1XeZsKxdGOvNjhYsydtFH0ySO1iS0ZleRe";
        String appId = "wx994fe56baeeb0361";*/
        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);

        /*String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
        String fromXML = String.format(format,encrypt);*/

        String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
        log.info("解密后明文: " + result2);

        //将解密后的消息转为xml
        Map<String, String> map = new HashMap<>();
        Document doc = DocumentHelper.parseText(result2);
        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        return map;
/*//    demo
        // 第三方回复公众平台
        //

        // 需要加密的明文
        String encodingAesKey = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFG";
        String token = "pamtest";
        String timestamp = "1409304348";
        String nonce = "xxxxxx";
        String appId = "wxb11529c136998cb6";
        String replyMsg = " 中文<xml><ToUserName><![CDATA[oia2TjjewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>";

        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        String mingwen = pc.encryptMsg(replyMsg, timestamp, nonce);
        System.out.println("加密后: " + mingwen);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(mingwen);
        InputSource is = new InputSource(sr);
        org.w3c.dom.Document document = db.parse(is);

        org.w3c.dom.Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

        String encrypt = nodelist1.item(0).getTextContent();
        String msgSignature = nodelist2.item(0).getTextContent();

        String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
        String fromXML = String.format(format, encrypt);

        //
        // 公众平台发送消息给第三方，第三方处理
        //

        // 第三方收到公众号平台发送的消息
        String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
        System.out.println("解密后明文: " + result2);

        //pc.verifyUrl(null, null, null, null);*/

    }

    /**
     * 消息加密
     * */
    public static String encryptMsg(String replyMsg,String timestamp,String nonce) throws Exception{
        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        String mingwen = pc.encryptMsg(replyMsg, timestamp, nonce);
        return mingwen;
    }
}
