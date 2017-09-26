package com.example.billz.myapplication;

import android.util.Log;
import org.json.JSONObject;

/**
 * Created by billz on 2017/9/20.
 */
public class tool extends httpTool {

    //函数返回列表，显示篮球、足球等运动
    public static JSONObject getList() {
        JSONObject newObj = postForJsonObject("/sports_info",null);
        return newObj;
    }

    //函数返回具体的某一项运动的全部对象
    public static JSONObject getObject(JSONObject job) {
        JSONObject newObj = postForJsonObject("/object_info", job);
        return newObj;
    }
}
