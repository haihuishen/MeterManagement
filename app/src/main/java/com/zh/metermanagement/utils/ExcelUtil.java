package com.zh.metermanagement.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    //内存地址
    public static String root = Environment.getExternalStorageDirectory().getPath();

    /** 获取SD可用容量 */
    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }


    public static List<MeterBean> readExcel(String path) {

        List<MeterBean> beanList = new ArrayList<MeterBean>();

        try {

            /**
             * 后续考虑问题,比如Excel里面的图片以及其他数据类型的读取
             **/
            InputStream is = new FileInputStream(path);

            Workbook book = Workbook.getWorkbook(new File(path));
            book.getNumberOfSheets();

            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int rows = sheet.getRows();
            int cols = sheet.getColumns();

            Log.i("shen","当前工作表的名字:" + sheet.getName());
            Log.i("shen","总行数:" + rows);
            Log.i("shen","总列数:" + cols);

            String area = sheet.getCell(0, 0).getContents();

            for (int i = 2; i < rows; ++i) {



                // getCell(Col,Row)获得单元格的值
                MeterBean bean = new MeterBean();
                bean.setSequenceNumber(sheet.getCell(0, i).getContents());      // 序号
                bean.setUserNumber(sheet.getCell(1, i).getContents());          //用户编号
                bean.setUserName(sheet.getCell(2, i).getContents());          //用户名称
                bean.setUserAddr(sheet.getCell(3, i).getContents());          //用户地址
                bean.setOldAssetNumbers(sheet.getCell(4, i).getContents());          //旧表资产编号(导入的)
                bean.setOldAssetNumbersScan(sheet.getCell(5, i).getContents());          //旧表资产编号(需扫描)
                bean.setOldElectricity(sheet.getCell(6, i).getContents());          //旧电能表止码-电量(需扫描)
                bean.setNewAssetNumbersScan(sheet.getCell(7, i).getContents());          //新表资产编号(需扫描)
                bean.setNewElectricity(sheet.getCell(8, i).getContents());          //新电能表止码-电量(需扫描)
                bean.setCollectorAssetNumbersScan(sheet.getCell(9, i).getContents());          //采集器资产编号(需扫描)

                bean.setArea(area);             // 区域
                bean.setFinish(false);          // 是否完成(扫描完)

                beanList.add(bean);
            }

            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return beanList;
    }



    public static String writeExcel(Context context, List<MeterBean> meterBeanList, List<AssetNumberBean> assetNumberBeenList) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && getAvailableStorage()>1000000) {

            // Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();

            Log.i("shen","SD卡不可用");
            return "请清理空间";
        }

        String[] meterTitle = { "序号", "用户编号", "用户名称", "用户地址", "旧表资产编号",
                "旧表资产编号", "旧表表地址", "旧电能表止码", "新电能表资产编号",
                "新表表地址", "新电能表止码", "II型采集器资产编号"};
        String[] assetNumberTitle = { "无匹配的资产编号"};

        String pathName = Constant.excelPathDir;
        String fileName = Constant.exportExcel;

        File path = new File(pathName);
        File file = new File(pathName + fileName);
        if (!path.exists()) {                                      //  创建路径（文件夹）
            Log.i("shen", "Create the path:" + pathName);
            path.mkdir();
            broadCreateFile(context, path);
        }

        if (!file.exists()) {                                       //  创建文件
            Log.i("shen", "Create the file:" + fileName);
            file.createNewFile();
            broadCreateFile(context, file);
        }

        Log.i("shen", "file.getAbsolutePath():" + file.getAbsolutePath());

        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);

        // 添加第一个工作表并设置第一个Sheet的名字
        // WritableSheet sheet1 = wwb.createSheet("电表换装关系表", 0);
        WritableSheet sheet1 = wwb.createSheet("电表换装关系表", 0);


        //分别给2,3,4列设置不同的宽度;
         sheet1.setColumnView(0, 30);
        sheet1.setColumnView(1, 30);
        sheet1.setColumnView(2, 30);
        sheet1.setColumnView(3, 30);
        sheet1.setColumnView(4, 30);
        sheet1.setColumnView(5, 30);
        sheet1.setColumnView(6, 30);
        sheet1.setColumnView(7, 30);
        sheet1.setColumnView(8, 30);
        sheet1.setColumnView(9, 30);
        sheet1.setColumnView(10, 30);
        sheet1.setColumnView(11, 30);


        Label label1;
        for (int i = 0; i < meterTitle.length; i++) {
            Log.i("shen","meterTitle[i]:" + meterTitle[i]);

            //sheet1.setColumnView(i, (meterTitle[i]).length());

            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label1 = new Label(i, 0, meterTitle[i], getHeader());

            sheet1.addCell(label1);

//            sheet1.setColumnView(i,
//                    meterTitle[i].length());  // 将定义好的单元格添加到工作表中
//
        }

        for (int i = 0; i < meterBeanList.size(); i++) {
            MeterBean bean = meterBeanList.get(i);

            //Log.i("shen", "bean.toString():" + bean.toString());

            try {
                Label sequenceNumber = new Label(0, i + 1, bean.getSequenceNumber());
                Label userNumber = new Label(1, i + 1, bean.getUserNumber());
                Label userName = new Label(2, i + 1, bean.getUserName());
                Label userAddr = new Label(3, i + 1, bean.getUserAddr());
                Label oldAssetNumbers = new Label(4, i + 1, bean.getOldAssetNumbers());
                Label oldAssetNumbersScan = new Label(5, i + 1, bean.getOldAssetNumbersScan());
                Label oldAddr = new Label(6, i + 1, bean.getOldAddr());
                Label oldElectricity = new Label(7, i + 1, bean.getOldElectricity());
                Label newAssetNumbersScan = new Label(8, i + 1, bean.getNewAssetNumbersScan());
                Label newAddr = new Label(9, i + 1, bean.getNewAddr());
                Label newElectricity = new Label(10, i + 1, bean.getNewElectricity());
                Label collectorAssetNumbersScan = new Label(11, i + 1, bean.getCollectorAssetNumbersScan());

//                if(!TextUtils.isEmpty(bean.getPicPath())){
//                    File fileImage=new File(Constant.IMAGE_PATH + bean.getPicPath());
//                    if(fileImage.exists()){
//                        WritableImage image=new WritableImage(12, i + 1,1,1,fileImage);//从A1开始 跨2行3个单元格
//                        sheet1.addImage(image);//ws是Sheet
//                    }
//                }



                sheet1.addCell(sequenceNumber);
                sheet1.addCell(userNumber);
                sheet1.addCell(userName);
                sheet1.addCell(userAddr);
                sheet1.addCell(oldAssetNumbers);
                sheet1.addCell(oldAssetNumbersScan);
                sheet1.addCell(oldAddr);
                sheet1.addCell(oldElectricity);
                sheet1.addCell(newAssetNumbersScan);
                sheet1.addCell(newAddr);
                sheet1.addCell(newElectricity);
                sheet1.addCell(collectorAssetNumbersScan);

            } catch (Exception e) {
                Log.i("shen", "e.getMessage():" + e.getMessage());
            }
        }

        // 添加第一个工作表并设置第一个Sheet的名字
        WritableSheet sheet2 = wwb.createSheet("无匹配的资产编号", 1);
        Label label2;
        for (int i = 0; i < assetNumberTitle.length; i++) {

            Log.i("shen","assetNumberTitle[i]:" + assetNumberTitle[i]);

            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label2 = new Label(i, 0, assetNumberTitle[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet2.addCell(label2);
        }

        for (int i = 0; i < assetNumberBeenList.size(); i++) {
            AssetNumberBean bean = assetNumberBeenList.get(i);

            Log.i("shen", "bean.toString():" + bean.toString());

            try {
                Label assetNumbers = new Label(0, i + 1, bean.getAssetNumbers());

                sheet2.addCell(assetNumbers);

            } catch (Exception e) {
                Log.i("shen", "e.getMessage():" + e.getMessage());
            }
        }

    Log.i("shen", "写入成功1");

    // 写入数据
    wwb.write();
    // 关闭文件
    wwb.close();

    os.close();


        broadCreateFile(context, file);
        return "已生成Excel文件";
    }


    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 16, WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);

        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }



    public static void broadCreateFile(Context context, File file){

        try {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // 在电脑上马上可以看到创建的文件
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);

            Log.i("shen", "广播成功！！！-- " + file.getAbsolutePath());
        }catch (Exception e){
            Log.i("shen", "广播失败：" + e.getMessage());
        }
    }



}