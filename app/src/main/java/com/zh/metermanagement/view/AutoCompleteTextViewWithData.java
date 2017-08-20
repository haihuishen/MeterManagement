package com.zh.metermanagement.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 带提示编辑<p>
 *
 * 记录"以往"输入的"字符串"，再次输入的时候会提示相同的！
 *
 * 需要 gson-xxx.jar
 */
public class AutoCompleteTextViewWithData extends AutoCompleteTextView {

    private static final String DB_NAME = "AutoCompleteTextViewWithData";
    private static final String DATA_Name = "JsonAutoData";

    /** 存放"用户"，添加的时候，一般是插入到最前面 */
    private List<String> dataArr = new ArrayList<>();
    private Context context;

    /** 键："用户"  值："密码" */
    private HashMap<String, String> dataMap = new HashMap<>();


    public AutoCompleteTextViewWithData(Context context) {
        super(context);
        this.context = context;
    }

    public AutoCompleteTextViewWithData(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }



    public void removeRecord(String account) {
        dataMap.remove(account);
        dataArr.remove(account);
        saveData();
    }

    /**
     * 将"用户" 添加到 List <p>
     * "用户"和"密码" 添加到  Map <p>
     * 最后存放到 sp
     *
     * @param account   用户
     * @param pwd       密码
     */
    public void addNewRecord(String account, String pwd) {
        dataArr.add(0, account);
        dataMap.put(account, pwd);
        saveData();
    }

    /**
     * 将 Map 的"键""值" 作为 JsonArray的属性弄成一个 JsonObject <p>
     * 将 JsonObject 一个个 添加到"JsonArray" 中， <br>
     * 将JsonArray.toString() 存放在 SP 中
     */
    private void saveData() {
        SharedPreferences sp = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        JsonArray arr = new JsonArray();
        for (String key : dataMap.keySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("account", key);
            object.addProperty("pwd", dataMap.get(key));
            arr.add(object);
        }
        sp.edit().putString(DATA_Name, arr.toString()).commit();
    }



    public void initData() {

        SharedPreferences sp = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        String jsonArrData = sp.getString(DATA_Name, "");

        JsonParser parser = new JsonParser();               // new Json 解析器
        JsonElement element = parser.parse(jsonArrData);    // 先将字符串解析成 "JsonElement"

        if (element != null && element.isJsonArray()) {     // 如果 "JsonElement" 是 "JsonArray"

            JsonArray arr = element.getAsJsonArray();
            Iterator<JsonElement> it = arr.iterator();      // 迭代器

            dataMap.clear();
            dataArr.clear();

            while (it.hasNext()) {
                JsonObject jsonObject = it.next().getAsJsonObject();// 得到"JsonObject"
                if (jsonObject != null) {
                    JsonElement acE = jsonObject.get("account");    // 的到"JsonObject"属性
                    JsonElement pwE = jsonObject.get("pwd");
                    String account = null;
                    String pwd = null;

                    if (acE != null && pwE != null) {               // 根据"属性"获取"值"
                        account = acE.getAsString();
                        pwd = pwE.getAsString();

                        dataMap.put(account, pwd);
                        dataArr.add(account);
                    }
                }
            }


            // 将的到的用户数组，作为 "自动提示控件" 的"数据源"
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, dataArr);
            setAdapter(adapter);
        }
    }
}
