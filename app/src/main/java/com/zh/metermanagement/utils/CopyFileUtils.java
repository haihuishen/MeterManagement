package com.zh.metermanagement.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by shen on 12/6 0006.
 */
public class CopyFileUtils {

//    如果有同名文件，自动覆盖。不存在时自动建立。
//    FileOutputStream的默认构造方法是直接覆盖掉原来的文件，
//    而FileOutputStream(File file, boolean append) 的构造方法
//    如果后面的append为true的时候就是追加到尾部而不是直接覆盖了。

    /**
    * 复制单个文件
    * @param oldPath String 原文件路径 如：c:/fqf.txt
    * @param newPath String 复制后路径 如：f:/fqf.txt
    * @return boolean
    */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {                  // 源文件存在时
                InputStream inStream = new FileInputStream(oldPath);      //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;            //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }  catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {


        try {
            (new File(newPath)).mkdirs();       //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }


                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){           //如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }  catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }
}
