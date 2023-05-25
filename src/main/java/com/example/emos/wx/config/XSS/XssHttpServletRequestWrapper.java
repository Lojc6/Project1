package com.example.emos.wx.config.XSS;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        //定义一个构造器否则子类就无法接收传入的请求对象
    }

    @Override
    public String getParameter(String name) {
        String value= super.getParameter(name);//原始数据

        if (!StrUtil.hasEmpty(value)){       //判断是否为空
           value =HtmlUtil.filter(value);//不空 ,转义
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values= super.getParameterValues(name);

        if (values!=null){
            for (int i=0;i< values.length;i++){
                String value=values[i];
                if (!StrUtil.hasEmpty(value)){       //判断是否为空
                    value =HtmlUtil.filter(value);//不空  转义
                }
                values[i]=value;
            }
        }
        return values;
    }



    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters= super.getParameterMap();
        LinkedHashMap<String, String[]> map=new LinkedHashMap();//直接hashmap插入无序,Linkedhashmap有序

        if (parameters!=null){
            for (String key:parameters.keySet()){//←获取Map里面的key
                  String[] values=parameters.get(key);
                  for (int i=0;i< values.length;i++){
                      String value=values[i];
                      if (!StrUtil.hasEmpty(value)){
                          value=HtmlUtil.filter(value);
                      }
                      values[i]=value;
                  }
                  map.put(key,values);
            }
        }

        return map;
    }



    @Override
    public String getHeader(String name) {
        String value= super.getHeader(name);

        if (!StrUtil.hasEmpty(value)){
            value=HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        InputStream in= super.getInputStream();
        InputStreamReader reader=new InputStreamReader(in,"UTF-8");
        BufferedReader buffer=new BufferedReader(reader);

        StringBuffer body=new StringBuffer();
        String line= buffer.readLine();
        while (line!=null){
            body.append(line);
            line= buffer.readLine();
        }
        buffer.close();
        reader.close();
        in.close();

        //数据转换
        Map<String,Object> map = JSONUtil.parseObj(body.toString());
        Map<String,Object> result=new LinkedHashMap<>();
        for (String key:map.keySet()){
            Object val=map.get(key);
            if (val instanceof String){
                if (!StrUtil.hasEmpty(val.toString())){
                    result.put(key,HtmlUtil.filter(val.toString()));
                }
            }
            else{
                result.put(key,val);
            }
        }
        String json=JSONUtil.toJsonStr(result);
        ByteArrayInputStream bain=new ByteArrayInputStream(json.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bain.read();
            }
        };
    }
}













