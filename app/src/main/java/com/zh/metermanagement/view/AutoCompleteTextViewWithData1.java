package com.zh.metermanagement.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 带提示编辑<p>
 *
 * 记录"以往"输入的"字符串"，再次输入的时候会提示相同的！
 *
 * 需要 gson-xxx.jar
 */
public class AutoCompleteTextViewWithData1 extends AutoCompleteTextView {

    private static final String DB_NAME = "AutoCompleteTextViewWithData1";
    private static final String DATA_Name = "JsonAutoData";
    private String mDataName = "";

    /** 邮件收件人 */
    public static final String DATA_Name_RECIPIENTS = "recipients";
    /** 邮件标题/主题 */
    public static final String DATA_Name_SUBJECT = "subject";
    /** 邮件内容 */
    public static final String DATA_Name_TEXT = "text";


    /** 存放"数据"，添加的时候，一般是插入到最前面 */
    private List<String> dataArr = new ArrayList<>();
    private Context context;

    public AutoCompleteTextViewWithData1(Context context) {
        super(context);
        this.context = context;
    }

    public AutoCompleteTextViewWithData1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 去掉重复值
     * @param list
     * @return
     */
    public static List removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element)){
                newList.add(element);
            }
        }
        return newList;
    }


    public void removeRecord(String account, String dataName) {
        dataArr.remove(account);
        saveData(dataName);
    }

    /**
     * 将"数据" 添加到 List <p>
     * 最后存放到 sp
     *
     * @param data   数据
     */
    public void addNewRecord(String dataName, String data) {
        if(!TextUtils.isEmpty(dataName)) {
            dataArr.add(0, data);
            saveData(dataName);
        }
    }

    /**
     * 将 Map 的"键""值" 作为 JsonArray的属性弄成一个 JsonObject <p>
     * 将 JsonObject 一个个 添加到"JsonArray" 中， <br>
     * 将JsonArray.toString() 存放在 SP 中
     */
    private void saveData(String dataName) {
        SharedPreferences sp = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        JsonArray arr = new JsonArray();
        dataArr = removeDuplicateWithOrder(dataArr);
        for (String data : dataArr) {
            JsonObject object = new JsonObject();
            object.addProperty("data", data);
            arr.add(object);
        }
        sp.edit().putString(dataName, arr.toString()).commit();
    }



    public void initData(String dataName) {

        if(!TextUtils.isEmpty(dataName)) {
            SharedPreferences sp = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
            String jsonArrData = sp.getString(dataName, "");

            JsonParser parser = new JsonParser();               // new Json 解析器
            JsonElement element = parser.parse(jsonArrData);    // 先将字符串解析成 "JsonElement"

            if (element != null && element.isJsonArray()) {     // 如果 "JsonElement" 是 "JsonArray"

                JsonArray arr = element.getAsJsonArray();
                Iterator<JsonElement> it = arr.iterator();      // 迭代器

                dataArr.clear();

                while (it.hasNext()) {
                    JsonObject jsonObject = it.next().getAsJsonObject();// 得到"JsonObject"
                    if (jsonObject != null) {
                        JsonElement acE = jsonObject.get("data");    // 的到"JsonObject"属性
                        String account = null;

                        if (acE != null) {               // 根据"属性"获取"值"
                            account = acE.getAsString();
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
}
