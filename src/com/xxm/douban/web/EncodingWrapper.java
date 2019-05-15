package com.xxm.douban.web;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 包装类
 */
public class EncodingWrapper extends HttpServletRequestWrapper {
    private String ENCODING;
    public EncodingWrapper(HttpServletRequest request, String ENCODING) {
        super(request);
        this.ENCODING = ENCODING;
    }
    
    @Override
    public String getParameter(String name) {
        String value = getRequest().getParameter(name);
        if(value != null) {
            try {
                byte[] b = value.getBytes("ISO-8859-1");
                value = new String(b, ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);   
            }
        }
        return value;
    }
    
    

}
