package com.zh.metermanagement.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shen on 11/26 0026.
 */
public class FilesUtils {

    private FilesUtils()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 不存在就创建"文件夹"<p>
     * 拿到后使用： files.exists() 再判断一次，比如文件创建失败
     *
     * @param context
     * @param path      文件夹路径
     */
    public static File createFile(Context context, String path){

        Log.i("shen", "Environment:" + Environment.getExternalStorageDirectory().getPath());
        File files = new File(path);
        Log.i("shen", "开始创建文件夹:"+path);
        if (!files.exists()) {                              // 不存在，创建
            try {
                //按照指定的路径创建文件夹
                if(files.mkdirs()) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // 在电脑上马上可以看到创建的文件
                        Uri uri = Uri.fromFile(files);
                        intent.setData(uri);
                        context.sendBroadcast(intent);

                        Log.i("shen", "广播成功！！！");
                    } catch (Exception e) {
                        Log.i("shen", "广播失败：" + e.getMessage());
                    }
                }else {
                    Log.i("shen","创建文件夹失败:"+path);
                }

            } catch (Exception e) {
                Log.i("shen","创建文件夹失败:"+path +"--"+ e.getMessage());
            }
        }

        return files;           // 拿到后使用： files.exists() 再判断一次，比如文件创建失败
    }


    /**
     * 将 InputStream 写入到指定的文件
     * @param is        InputStream
     * @param path  将 InputStream 写入到指定的文件 如：f:/fqf.txt
     * @return boolean
     */
    public static File InputStream2File(InputStream is, String path) throws IOException {
        int byteSum = 0;
        int byteRead = 0;
        FileOutputStream fs = null;

        try {
            File file = new File(path);
            if (is != null) {                                           // InputStream流不为空
                fs = new FileOutputStream(file);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteRead = is.read(buffer)) != -1) {
                    byteSum += byteRead;                                    //字节数 文件大小
                    System.out.println(byteSum);
                    fs.write(buffer, 0, byteRead);
                }
            }

            return file;
        }  catch (Exception e) {
            System.out.println("将InputStream写入到指定的文件，操作出错");
            e.printStackTrace();
            return null;
        } finally {
            is.close();
            fs.close();
        }
    }


    /**
     * 复制资源(assets目录下的文件)到指定路径
     * @param path          指定路径
     * @param imageName     图片文件名(文件名)
     */
    public static void assets2File(Context context, String path, String imageName){
        File files = FilesUtils.createFile(context, path);     // 创建"文件夹"
        File file = new File(files, imageName);
        if(file.exists()){
            file.delete();
        }
        file = new File(files, imageName);

        InputStream stream = null;
        FileOutputStream fos = null;

        //2,输入流读取assets目录下的文件
        try {
            // getAssets()拿到"资产目录"的文件夹（工程目录下的assets目录）
            // ***打开"dbName名字的文件"    （拿到他的输入流）
            stream = context.getAssets().open(imageName);

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

}
