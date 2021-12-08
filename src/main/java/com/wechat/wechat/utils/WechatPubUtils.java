package com.wechat.wechat.utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
