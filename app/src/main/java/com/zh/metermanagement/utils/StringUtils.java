package com.zh.metermanagement.utils;

import android.text.TextUtils;

/**
 * Created by title on 2016/10/29.
 */
public class StringUtils {
    public static boolean isNotEmpty(String str) {
        //return str != null && !str.trim().equals("");
        return !TextUtils.isEmpty(str);
    }

    public static boolean isEmpty(String str) {
        //return str == null || str.trim().equals("");
        return TextUtils.isEmpty(str);
    }



    /**
     * 在"资源字符串"中，删除对应的"文件路径"
     *
     * @param parent        资源字符串
     * @param sub           要删除的"文件路径"
     * @return
     */
    public static String deleteSubStr(String parent, String sub) {
        if (StringUtils.isNotEmpty(parent) && StringUtils.isNotEmpty(sub)) {

            if (parent.contains(sub + ",")) {                   // 如果是最后一个        包含
                return parent.replace(sub + ",", "");

            } else if (parent.contains("," + sub)) {            // 如果不是最后一个      包含
                return parent.replace("," + sub, "");

            } else if (parent.equals(sub.trim())){              // 如果只有一个
                return parent.replace(sub, "");
            }

        }
        return parent;
    }
}
