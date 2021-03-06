package com.example.user.navigation_calendar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Http_AddGroupPost extends Service {

    String groupname=null;
    String group_picpath=null;
    String postUrl=null;
    String strResult=null;
    String token;

    public void Post(String groupName, String picture_path, String Url, String T) {
        groupname=groupName;
        group_picpath=picture_path;
        postUrl=Url;
        token = T;

        new Thread(new Runnable() {

            @Override
            public void run() {
                //建立HttpClient物件
                HttpClient httpClient = new DefaultHttpClient();
                //建立一個Post物件，並給予要連線的Url
                HttpPost httpPost = new HttpPost(postUrl);
                //send token to backend
                httpPost.setHeader("Authorization","Bearer "+token);

                //建立一個ArrayList且需是NameValuePair，此ArrayList是用來傳送給Http server端的訊息
                List params = new ArrayList();
                params.add(new BasicNameValuePair("name",groupname.toString()));
                params.add(new BasicNameValuePair("sticker",group_picpath.toString()));

                //發送Http Request，內容為params，且為UTF8格式
                UrlEncodedFormEntity ent = null;
                try {
                    ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                httpPost.setEntity(ent);
                //接收Http Server的回應
                HttpResponse httpResponse = null;
                try {
                    httpResponse = httpClient.execute(httpPost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //判斷Http Server是否回傳OK(200)
                if(httpResponse.getStatusLine().getStatusCode() == 200){

                    //將Post回傳的值轉為String，將轉回來的值轉為UTF8，否則若是中文會亂碼
                    try {
                        strResult = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    //設定Message的內容
                    msg.what = 12;
                    msg.obj=strResult;
                    //使用MainActivity的static handler來丟Message
                    AddGroup.handler.sendMessage(msg);

                }
                if(httpResponse.getStatusLine().getStatusCode() == 401){
                    //將Post回傳的值轉為String，將轉回來的值轉為UTF8，否則若是中文會亂碼
                    try {
                        strResult = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    //設定Message的內容
                    msg.what = 13;
                    msg.obj=strResult;
                    //使用MainActivity的static handler來丟Message
                    AddGroup.handler.sendMessage(msg);
                }

            }}).start();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
