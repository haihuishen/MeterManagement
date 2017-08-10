package com.zh.metermanagement.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gbh on 16/12/3.
 */

public class TimeUtils {

    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        return simpleDateFormat.format(new Date());
    }

    public static String getCurrentTimeyyyyMMdd() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date());
    }


    public static String getCurrentyyyyMMdd() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }
    public static String getCurrentTimeyyyyMM() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        return simpleDateFormat.format(new Date());
    }


    public static String getCurrentTimeRq() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        return simpleDateFormat.format(new Date());
    }


    public static boolean compareTime(String time1, String time2, String dateFormat){
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//创建日期转换对象HH:mm:ss为时分秒，年月日为yyyy-MM-dd
        try {
            Date dt1 = df.parse(time1);//将字符串转换为date类型
            Date dt2 = df.parse(time2);
            if(dt1.getTime()>= dt2.getTime())//比较时间大小,dt1小于dt2
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

}
