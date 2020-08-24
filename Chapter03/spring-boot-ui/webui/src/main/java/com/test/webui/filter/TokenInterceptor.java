package com.test.webui.filter;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSONObject;

/**
 * 拦截url中的access_token
 * @author Nob
 *
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    // private UserService userService;

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            String type = request.getHeader("X-Requested-With");
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获得该路径的方法
            Method method = handlerMethod.getMethod();
            //取得该方法上的Token注解
            Token annotation = method.getAnnotation(Token.class);
            //不为空则拦截
            boolean needSaveSession = false;
            if (annotation != null) {
                needSaveSession = annotation.AccessRequired();
            }
            if (annotation == null || needSaveSession == true) {
                boolean isToken = false;
                if("XMLHttpRequest".equals(type) || "toPageHome".equals(method.getName()) || "redirect_url".equals(method.getName())){
                    isToken = true;
                }else{
                    Object user_token =  request.getSession().getAttribute("user_token");
                    if(user_token != null){
                        String token = (String)user_token;
                        String url = ToolUtil.getAppConfig("getUser_url");
                        Map<String, String> query = new HashMap<String, String>();
                        long state = new Date().getTime();
                        query.put("ContextID", state+"");
                        query.put("Data", token);
                        String wxpost = doWxPost(url,query);
                        if(wxpost != null){
                            JSONObject obj = new JSONObject();
                            obj = JSONObject.parseObject(wxpost);
                            boolean success = obj.getBoolean("Success");
                            if(success){
                                isToken = true;
                            }
                        }
                    }
                }
                if(!isToken){
                    response.setContentType("text/html;charset=utf-8");
                    String redirect_url = ToolUtil.getAppConfig("redirect_url");
                    String login_url = ToolUtil.getAppConfig("login_url");
                    response.sendRedirect(login_url+redirect_url);
//        			response.sendRedirect(request.getContextPath() + "/main/login");
                    return false;
                }
                UserBean user = (UserBean) request.getSession().getAttribute("user");
                if(user==null){
                    response.setContentType("text/html;charset=utf-8");
                    String redirect_url = ToolUtil.getAppConfig("redirect_url");
                    String login_url = ToolUtil.getAppConfig("login_url");
                    response.sendRedirect(login_url+redirect_url);
//        			response.sendRedirect(request.getContextPath() + "/main/login");
                    return false;
                }
            }else if( "redirect_url".equals(method.getName())){
                //SSO 回调地址
                String token = request.getParameter("SECURE_GLOBAL_TOKEN");
                Object user_token =  request.getSession().getAttribute("user_token");
                if(user_token == null && StringUtils.isBlank(token)){
                    response.setContentType("text/html;charset=utf-8");
                    String redirect_url = ToolUtil.getAppConfig("redirect_url");
                    String login_url = ToolUtil.getAppConfig("login_url");
                    response.sendRedirect(login_url+redirect_url);
                    return false;
                }else if(user_token == null && StringUtils.isNotBlank(token)){
                    request.getSession().setAttribute("user_token",token);
                    response.sendRedirect(request.getContextPath() + "/user/redirect_url");
                    return false;
                }else if(user_token != null && StringUtils.isNotBlank(token)){
                    request.getSession().setAttribute("user_token",token);
                    response.sendRedirect(request.getContextPath() + "/user/sso_login");
                    return false;
                }else {
                    response.sendRedirect(request.getContextPath() + "/user/sso_login");
                    return false;
                }
            }else if( "sso_login".equals(method.getName())){
                Object user_token =  request.getSession().getAttribute("user_token");
                if(user_token != null){
                    String token = (String)user_token;
                    String url = ToolUtil.getAppConfig("getUser_url");
                    Map<String, String> query = new HashMap<String, String>();
                    long state = new Date().getTime();
                    query.put("ContextID", state+"");
                    query.put("Data", token);
                    String wxpost = doWxPost(url,query);
                    if(wxpost != null){
                        JSONObject obj = new JSONObject();
                        obj = JSONObject.parseObject(wxpost);
                        boolean success = obj.getBoolean("Success");
                        if(success){
                            return true;
                        }else{
                            response.setContentType("text/html;charset=utf-8");
                            String redirect_url = ToolUtil.getAppConfig("redirect_url");
                            String login_url = ToolUtil.getAppConfig("login_url");
                            response.sendRedirect(login_url+redirect_url);
                            return false;
                        }
                    }
                }
            }else{
                String wxuid = request.getParameter("wxuid");
                if(StringUtils.isNotBlank(wxuid)){
                    Jedis jedis = RedisLocalUtils.getJedis();
                    String token = jedis.get("token" + wxuid);
                    if(StringUtils.isBlank(token) || !"0".equals(token)){
                        jedis.close();
                        return false;
                    }else{
                        //清除redis token标识
                        jedis.del("token" + wxuid);
                        jedis.close();
                    }
                }
            }
        }
        return true;
    }

    private  String doWxPost(String url, Map<String, String> params) {
        String response = null;
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("ContentType",
                "application/x-www-form-urlencoded;charset=UTF-8");
        // 设置Http Post数据
        if (params != null) {
            NameValuePair[] data = new NameValuePair[params.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                data[i] = new NameValuePair(entry.getKey(), entry.getValue());
                i++;
            }
            method.setRequestBody(data);
        }
        try {
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = method.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer .append(str );
                }
                response = stringBuffer.toString();
            }
        } catch (IOException e) {
            return null;
        } finally {
            method.releaseConnection();
        }
        System.out.println("请求返回json:"+response);
        return response;
    }
}
