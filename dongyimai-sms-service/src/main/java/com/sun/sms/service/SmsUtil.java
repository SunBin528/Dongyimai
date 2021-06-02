package com.sun.sms.service;

import com.sun.sms.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class SmsUtil {

    private String host = "http://dingxinyx.market.alicloudapi.com";
    private String path = "/dx/marketSendSms";
    private String method = "POST";
    private String appcode = "fede06271c82450aafeab218b104cd7c";
    private String tpl_id = "TP18041310";


    public HttpResponse sendMessage(String mobile,String param) throws Exception {

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        querys.put("param", "code"+param);
        querys.put("tpl_id", tpl_id);
        Map<String, String> bodys = new HashMap<String, String>();

        HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);

        return response;
    }
}
