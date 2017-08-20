package com.zh.metermanagement.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;


//上面的代码
//
//        首先生成一个Intent实例（隐式Intent），然后向intent中放入一些值来配置
//
//        上面的就是放入了4个值来控制快捷图标的创建
//
//        1.是否允许重复创建
//
//        2.确定创建的图标的名称
//
//        3.确定创建的图标的图片
//
//        4.设置点击这个快捷图标就运行该APP
//
//        你可以在你的APP第一次运行时在第一个Activity中调用这个类的静态方法，就实现了快捷图标的创建
//
//        最后不要忘了添加权限
//              <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>

/**
 * 在桌面生成图标
 *
 */
public class ShortCut {
        /**
         * 在桌面生成图标
         *
         * @param act
         * @param iconResId             图片的id
         * @param appnameResId          app名字字符串id
         */
        public static void createShortCut(Activity act, int iconResId, int appnameResId) {
                Intent intent = new  Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                // 不允许重复创建
                intent.putExtra("duplicate", false);
                // 需要现实的名称
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,act.getString(appnameResId));
                // 快捷图片
                Parcelable icon = Intent.ShortcutIconResource.fromContext(act.getApplicationContext(), iconResId);
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
                // 点击快捷图片，运行程序
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(act.getApplicationContext(), act.getClass()));
                // 发送广播
                act.sendBroadcast(intent);

        }
}