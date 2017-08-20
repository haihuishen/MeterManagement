package com.zh.metermanagement.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.shen.sweetdialog.SweetAlertDialog;
import com.zh.metermanagement.R;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.CopyFileUtils;
import com.zh.metermanagement.utils.FilesUtils;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.ResourceUtil;
import com.zh.metermanagement.utils.ShortCut;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 闪屏<p>
 *
 * Splash [splæʃ] vt.溅，泼；用...使液体飞溅	n.飞溅的水；污点；卖弄		vi.溅湿；溅开<p>
 *
 * 现在大部分APP都有Splash界面，下面列一下Splash页面的几个作用：<p>
 * 1、展示logo,提高公司形象<br>
 * 2、初始化数据 (拷贝数据到SD)<br>
 * 3、提高用户体验 <br>
 * 4、连接服务器是否有新的版本等。<br>
 *
 *     //implements UncaughtExceptionHandler
 *     //在onCreate()调用下面方法，才能捕获到线程中的异常
 *      Thread.setDefaultUncaughtExceptionHandler(this);
 */
public class SplashActivity extends Activity implements Thread.UncaughtExceptionHandler {


    private Handler handler = new Handler();
    private Runnable runnable;

    private TextView mTvCountDown;      // tv_count_down

    MyCountDownTimer myCountDownTimer;  // 倒计时器

    private final int REQUESTCODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(runnable = new Runnable(){                   // 发送个消息(runnable 可执行事件)到"消息队列中"，延时执行

            @Override
            public void run(){

                Intent intent = new Intent(SplashActivity.this, SelectorActivity1.class);        // 跳转到主页面
                startActivity(intent);
                finish();
            }
        }, 3000);

        //在此调用下面方法，才能捕获到线程中的异常
        //Thread.setDefaultUncaughtExceptionHandler(this);



        String sam = getDeviceNum();
        Log.i("shen", "getDeviceNum():"+sam);

        sam = sam.toLowerCase();
        if (!sam.contains("kt45")) {

            //if (!sam.contains("C70SC")) {
            Toast.makeText(this, "设备初始化失败！", Toast.LENGTH_SHORT).show();

            //如果之前创建了Runnable对象,那么就把这任务移除
            if(runnable!=null){
                handler.removeCallbacks(runnable);
            }
            //finish();
            MyApplication.exitApp();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkSelfPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);
            }
        }else {

            FilesUtils.createFile(this, Constant.srcPathDir);
            FilesUtils.createFile(this, Constant.excelPathDir);
            FilesUtils.createFile(this, Constant.IMPORT_METER_INFO_PATH);
            FilesUtils.createFile(this, Constant.IMPORT_PHONE_PATH);
            FilesUtils.createFile(this, Constant.CACHE_IMAGE_PATH);
            FilesUtils.createFile(this, Constant.IMAGE_PATH);
            FilesUtils.createFile(this, Constant.DIRECTIONSFORUSEIMAGE_PATH);
            FilesUtils.createFile(this, Constant.NoWorkOrder_PATH);

            initSrc(Constant.CACHE_IMAGE_PATH,"no_preview_picture.png");
            //initSrc(Constant.CACHE_IMAGE_PATH,"CacheImage.jpg");
        }

        if(!checkExcel()){
            //如果之前创建了Runnable对象,那么就把这任务移除
            if(runnable!=null){
                handler.removeCallbacks(runnable);
            }

            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("提示")
                    .setContentText("\'电表换装/导入数据源/换表导入文件/\'\n 无导入文件")
                    .setConfirmText("确认")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            //finish();
                            MyApplication.exitApp();
                            sweetAlertDialog.dismiss();
                        }
                    });


            dialog.setCancelable(false);
            dialog.show();

        }

        CopyFileUtils.copyFile(this.getDatabasePath(Constant.DB_NAME).getAbsolutePath(), Constant.IMAGE_PATH+Constant.DB_NAME);
        FilesUtils.broadCreateFile(this, new File(Constant.IMAGE_PATH+Constant.DB_NAME));


        // 在桌面生成图标
        ShortCut.createShortCut(this, ResourceUtil.getMipmapResIDByName(this, "app_icon"),
                ResourceUtil.getStringResIDByName(this, "app_name"));


//        //内存地址
//        public static String root = Environment.getExternalStorageDirectory().getPath();
//        /** 获取SD可用容量 */
//    private static long getAvailableStorage() {
//
//        StatFs statFs = new StatFs(root);
//        long blockSize = statFs.getBlockSize();
//        long availableBlocks = statFs.getAvailableBlocks();
//        long availableSize = blockSize * availableBlocks;
//        // Formatter.formatFileSize(context, availableSize);
//        return availableSize;
//    }
//        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
//                && getAvailableStorage()>1000000) {
//
//            // Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
//
//            Log.i("shen","SD卡不可用");
//            return "请清理空间";
//        }

        initView();
        initListener();
        initData();

    }



    private void initView() {
        mTvCountDown = (TextView) findViewById(R.id.tv_count_down);

    }

    private void initListener(){

        mTvCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, SelectorActivity1.class);        // 跳转到主页面
                startActivity(intent);
                finish();
                //如果之前创建了Runnable对象,那么就把这任务移除
                if(runnable!=null){
                    handler.removeCallbacks(runnable);
                }
            }
        });


    }

    private void initData() {
        myCountDownTimer = new MyCountDownTimer(3000, 1000);
        myCountDownTimer.start();

        //mTvCountDown.setEnabled(false);         // 设置不可点击跳转！

        for(int i=1; i<10; i++) {
            initSrc(Constant.DIRECTIONSFORUSEIMAGE_PATH , i + ".png");
        }

    }





    /**
     * 继承 CountDownTimer 防范
     *
     * 重写 父类的方法 onTick() 、 onFinish()
     */
    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数<br>
         * 例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法<br>
         * 例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        public void onFinish() {
            mTvCountDown.setTextSize(10);
            mTvCountDown.setText("正在跳转");
        }
        public void onTick(long millisUntilFinished) {
            // mTvCountDown.setText("倒计时(" + millisUntilFinished / 1000 + ")");
            mTvCountDown.setTextSize(25);
            mTvCountDown.setText(millisUntilFinished / 1000 +"");
        }
    }



    /**
     * 检查excel是否存在
     */
    private boolean checkExcel() {
        if (!fileIsExists()) {
            //Toast.makeText(getApplicationContext(), "import.xls文件不存在", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 获取设备号
     * @return
     */
    public String getDeviceNum() {
        return android.os.Build.MODEL;
    }


    /**
     * 检查excel文件是否存在
     *
     * @return
     */
    public boolean fileIsExists() {

        boolean isExists = false;
        try {
            File directory = new File(Constant.IMPORT_METER_INFO_PATH);

            if(directory.exists()){
                File[] files = directory.listFiles();
                if(files.length == 0){

                }else {
                    isExists = true;
                }
            }

        } catch (Exception e) {
            LogUtils.i("e" + e.getMessage());
        }

        return isExists;
    }


    /**
     * 复制资源(assets目录下的文件)到指定路径
     * @param pathName      指定路径
     * @param imageName     图片文件名(文件名)
     */
    private void initSrc(String pathName, String imageName){

        File path = new File(pathName);
        if (!path.exists()) {                                      //  创建路径（文件夹）
            Log.d("TestFile", "Create the path:" + pathName);
            path.mkdir();

            try {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // 在电脑上马上可以看到创建的文件
                Uri uri = Uri.fromFile(path);
                intent.setData(uri);
                SplashActivity.this.sendBroadcast(intent);

                Log.i("shen", "广播成功！！！");
            }catch (Exception e){
                Log.i("shen", "广播失败：" + e.getMessage());
            }
        }

        File file = new File(pathName, imageName);

        InputStream stream = null;
        FileOutputStream fos = null;

        //2,输入流读取assets目录下的文件
        try {
            // getAssets()拿到"资产目录"的文件夹（工程目录下的assets目录）
            // ***打开"dbName名字的文件"    （拿到他的输入流）
            stream = getAssets().open(imageName);

            //3,将读取的内容写入到指定文件夹的文件中去
            // ***拿到"file文件"的"输出流"
            fos = new FileOutputStream(file);

            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){	// 將"输入流"（stream）读到"bs"
                fos.write(bs, 0, temp);				// 將"bs"写到"fos"（输出流）
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){	// "流"非等于"null",说明没有关闭
                try {
                    // 关闭流
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                //用户同意
                FilesUtils.createFile(this, Constant.srcPathDir);
                FilesUtils.createFile(this, Constant.excelPathDir);
                FilesUtils.createFile(this, Constant.IMPORT_METER_INFO_PATH);
                FilesUtils.createFile(this, Constant.IMPORT_PHONE_PATH);
                FilesUtils.createFile(this, Constant.CACHE_IMAGE_PATH);
                FilesUtils.createFile(this, Constant.IMAGE_PATH);
                FilesUtils.createFile(this, Constant.DIRECTIONSFORUSEIMAGE_PATH);

                initSrc(Constant.CACHE_IMAGE_PATH,"no_preview_picture.png");
            } else {
                //用户不同意

            }
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)                 // 触摸事件
    {

//        if(event.getAction()==MotionEvent.ACTION_UP)
//        {
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            if (runnable != null)                           // 如果这个(runnable 可执行事件)被new出来了.
//                handler.removeCallbacks(runnable);          // 就从"消息队列"删除(这个事件)
//        }

        return super.onTouchEvent(event);
    }

    /************************************************************************************/
    // 按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {                 // 如果点击的是"返回按钮"

            //如果之前创建了Runnable对象,那么就把这任务移除
            if(runnable!=null){
                handler.removeCallbacks(runnable);
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //在此处理异常， arg1即为捕获到的异常
        Log.i("shen","+++++++++++++++++++++++++++++++++++++++++++++  Exception  ++++++++++++++++++++++++++++++++++++++++++++++++");
        Log.i("shen","AAA:   " + ex);
        Log.i("shen","+++++++++++++++++++++++++++++++++++++++++++++  Exception  ++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
