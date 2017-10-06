package cn.kingsleychung.sportsexpert;

/**
 * Created by billz on 2017/9/19.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class httpTool {
    protected static final MediaType JSON = MediaType.parse("application/json, charset=utf-8");
    protected static final String TARGET_URL = "http://139.199.59.246:8000/sports";
    //protected static final String TARGET_URL = "http://localhost:8000/sports";
    //设置超时时间
    protected static final int CONNECT_TIMEOUT = 3*1000;
    protected static final int READ_TIMEOUT = 10*1000;
    protected static final int WRITE_TIMEOUT = 10*1000;

    protected static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();

    //函数部分
    protected static Response postObject(String url, JSONObject jobj) {
        RequestBody body = RequestBody.create(JSON, "{}");
        if (jobj != null) {
            body = RequestBody.create(JSON, jobj.toString());
        }
        Request req = new Request.Builder().url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response res = null;
        try {
            res = okHttpClient.newCall(req).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }




    protected static JSONObject postForJsonObject(String extra_url, JSONObject jObj) {
        JSONObject obj = new JSONObject();
        try {
            Response res = postObject(TARGET_URL + extra_url, jObj);
            if (res != null) {
                if (res.isSuccessful()) {
                    try {
                        String data = res.body().string();
                        obj = new JSONObject(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    obj.put("state", false);
                }
            } else {
                obj.put("state", false);
                obj.put("result", "接不上服务器呢");
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return obj;
    }
}







//写法

        /*try {
            JSONArray jArray = obj.getJSONArray("result");
            JSONObject jObject = jArray.getJSONObject(0);
            Log.i("得到的数据：", jObject.getString("sname"));
        } catch (JSONException e) {

        } */

