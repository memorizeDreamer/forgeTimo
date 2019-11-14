package com.tmall.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsInterface {
    //账号
    private static String account = "JSM42969";
    //密码
    private static String password = "9qkwrxuu";
    //通讯Key/校验码
    private static String veryCode = "ghmuikhbvi8u";
    //模板编号
    private static String tplId = "JSM42969-0001";
    //apiUrl
    private static String http_url  = "http://112.74.76.186:8030";
    /*
     * 默认字符编码集
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    public static String sendTplSms(String mobile,String content){
        String sendTplSmsUrl = http_url + "/service/httpService/httpInterface.do?method=sendUtf8Msg";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username", account);
        params.put("password", password);
        params.put("veryCode", veryCode);
        params.put("mobile", mobile);
        params.put("content", "@1@="+content); //变量值，以英文逗号隔开
        params.put("msgtype", "2");     //2-模板短信
        params.put("tempid", tplId);    //模板编号
        params.put("code", "utf-8");
        String result = sendHttpPost(sendTplSmsUrl, params);
        return result;
    }

    /*
     * @param apiUrl 接口请求地址
     * @param paramsMap 请求参数集合
     * @return xml字符串，格式请参考文档说明
     * String
     */
    private static String sendHttpPost(String apiUrl, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(apiUrl);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, CHARSET_UTF8));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }
}

