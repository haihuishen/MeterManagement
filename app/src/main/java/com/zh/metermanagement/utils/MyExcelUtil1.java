package com.zh.metermanagement.utils;

import android.util.Log;

import com.zh.metermanagement.bean.MeterBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Administrator on 2017/8/1.
 */

public class MyExcelUtil1 {



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



    public static void createExcel() {
        try {
            // 创建或打开Excel文件
            WritableWorkbook book = Workbook.createWorkbook(new File("mnt/sdcard/test.xls"));

            // 生成名为“第一页”的工作表,参数0表示这是第一页
            WritableSheet sheet1 = book.createSheet("第一页", 0);
            WritableSheet sheet2 = book.createSheet("第三页", 2);

            // 在Label对象的构造函数中,元格位置是第一列第一行(0,0)以及单元格内容为test
            Label label = new Label(0, 0, "test");

            // 将定义好的单元格添加到工作表中
            sheet1.addCell(label);

            /*
             * 生成一个保存数字的单元格.必须使用Number的完整包路径,否则有语法歧义
             */
            jxl.write.Number number = new jxl.write.Number(1, 0, 555.12541);
            sheet2.addCell(number);

            // 写入数据并关闭文件
            book.write();
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * jxl暂时不提供修改已经存在的数据表,这里通过一个小办法来达到这个目的,不适合大型数据更新! 这里是通过覆盖原文件来更新的.
     *
     * @param filePath
     */
    public static void updateExcel(String filePath) {
        try {
            Workbook rwb = Workbook.getWorkbook(new File(filePath));
            WritableWorkbook wwb = Workbook.createWorkbook(new File(
                    "d:/new.xls"), rwb);// copy
            WritableSheet ws = wwb.getSheet(0);
            WritableCell wc = ws.getWritableCell(0, 0);
            // 判断单元格的类型,做出相应的转换
            Label label = (Label) wc;
            label.setString("The value has been modified");
            wwb.write();
            wwb.close();
            rwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeExcel(String filePath) {
        try {
            // 创建工作薄
            WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath));
            // 创建工作表
            WritableSheet ws = wwb.createSheet("Sheet1", 0);
            // 添加标签文本
            // Random rnd = new Random((new Date()).getTime());
            // int forNumber = rnd.nextInt(100);
            // Label label = new Label(0, 0, "test");
            // for (int i = 0; i < 3; i++) {
            // ws.addCell(label);
            // ws.addCell(new jxl.write.Number(rnd.nextInt(50), rnd
            // .nextInt(50), rnd.nextInt(1000)));
            // }
            // 添加图片(注意此处jxl暂时只支持png格式的图片)
            // 0,1分别代表x,y 2,5代表宽和高占的单元格数
            ws.addImage(new WritableImage(5, 5, 2, 5, new File("mnt/sdcard/nb.png")));
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
