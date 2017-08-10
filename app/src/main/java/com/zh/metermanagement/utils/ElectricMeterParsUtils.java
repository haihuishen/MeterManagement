package com.zh.metermanagement.utils;

import android.serialport.Tools;
import android.util.Log;

/**
 * 电表协议解析 ———— 97、07
 *
 */
public class ElectricMeterParsUtils {

    /**
     * 根据"数据标识"截取数据
     *
     * @param bytes
     * @return
     */
    public static String[] getMsg(byte[] bytes) {

        /** 地址*/
        byte[] mByteDz = new byte[6];
        /**  得到的地址 -- 已经将 bytes转码成 字符串(16进制数字)*/
        String mDz = "";

        /** 控制码*/
        byte[] mBytesDataID;
        /** 得到的控制码 -- 已经将 bytes转码成 字符串(16进制数字)*/
        String mDataID = "";
        /** 接收数据帧*/
        String buf = "";

        try {
            byte[] recvBuffer = bytes;
            int x = 0;
            int i = 0;

            // 2014/7/17判断接收帧中 第一个 68 的位置
            for (i = 0; i < recvBuffer.length; i++) {

                //region 定位"0x68"
                if (recvBuffer[i] == (byte) 0x68) {				// (前面的FE去掉)0x68
                    x = i;										// 拿到"0x68"在"byte[]"中的位置

                    //region 判断控制码
                    // --68 + 6字节地址 + 68 + 1字节控制码-------
                    if ((x + 8) < recvBuffer.length) {

                        // 判断控制码
                        // 97协议为 0x81
                        // 07协议为 0x91
                        if (recvBuffer[x + 8] == (byte) 0x81 || recvBuffer[x + 8] == (byte) 0x91) {
                            Log.i("info", "接收到了 电表数据！");
                            Log.i("info", recvBuffer.toString());

                            // 数据域长度：有数据时长度为 2+4；无数据时长度为：2
                            // 97协议： 2 + 4 = 6
                            // 07协议： 4 + 4 = 8

                            // --68 + 6字节地址 + 68 + 1字节控制码 + 1字节数据域长度--------
                            if ((x + 9) < recvBuffer.length) {
                                int dataL = recvBuffer[x + 9];		// 数据域长度

                                // --68 + 6字节地址 + 68 + 1字节控制码 + 1字节数据域长度 + n字节数据域--------
                                if (x + 11 + dataL < recvBuffer.length) {

                                    // --68H + 6字节地址 + 68H + 1字节控制码 + 1字节数据域长度 + n字节数据域 + 1字节校验 + 16H--------
                                    if (recvBuffer[x + 11 + dataL] != (byte) 0x16) {
                                        Log.i("info", "---5----错误：没有接收到数据帧！");
                                        continue;
                                    }

                                    // 数据标识的长度  -- 97:2字节   07:4字节
                                    int iData = 0;
                                    if (recvBuffer[x + 8] == (byte) 0x81) {
                                        iData = 2;
                                    } else if (recvBuffer[x + 8] == (byte) 0x91) {
                                        iData = 4;
                                    } else {
                                        Log.i("info", "---6----错误：没有接收到数据帧！");
                                        continue;
                                    }

                                    // 保存"数据域"中对电表数据 -- "数据域" = "数据标识" + "对应的数据"
                                    byte[] dClearData = new byte[dataL - iData];
                                    mBytesDataID = new byte[iData];
                                    Log.i("info", "对比数组长度 == " + dClearData.length);

                                    // 数据解析
                                    int mm = 0;

                                    for (mm = x + 1, i = 0; mm <= x + 6; mm++, i++){
                                        mByteDz[i] = recvBuffer[mm];
                                    }
                                    for (int j = mByteDz.length - 1; j >= 0 ; j--){
                                        mDz  += Tools.byteToString(mByteDz[j]);
                                    }


                                    for (mm = x + 10, i = 0; mm <= x + 9 + iData; mm++, i++){
                                        mBytesDataID[i] = (byte) (recvBuffer[mm] - 0x33);
                                    }
                                    for (int j = mBytesDataID.length - 1; j >= 0 ; j--){
                                        mDataID  += Tools.byteToString(mBytesDataID[j]);
                                    }


                                    // 数据域中的数据(电表返回数据会将其 + 0x33)
                                    for (mm = x + 10 + iData, i = 0; mm <= x + 9 + dataL; mm++, i++) {
                                        dClearData[i] = (byte) (recvBuffer[mm] - 0x33);
                                    }

                                    for (int j = dClearData.length - 1; j >= 0; j--) {
                                        // 将字节以16进制的形式弄成字符串 -- 然后接在一起
                                        buf += Tools.byteToString(dClearData[j]);
                                    }

                                    // 使用StringBuilder包装下,方便插入！
                                    StringBuilder  sbBuf = new StringBuilder (buf);

                                    if(mDataID.length() == 4){              // 97
                                        switch(Integer.valueOf(mDataID, 16)) {
                                            
                                            case 0x9010:sbBuf = sbBuf.insert(6,".");break;//"正向有功电度"
                                            case 0x9110:sbBuf = sbBuf.insert(6,".");break;//"正向无功电度"
                                            case 0x9020:sbBuf = sbBuf.insert(6,".");break;//"反向有功电度"
                                            case 0x9120:sbBuf = sbBuf.insert(6,".");break;//"反向无功电度"
                                            case 0xB611:sbBuf = sbBuf.delete(0,1)  ;break;//"A相电压
                                            case 0xB612:sbBuf = sbBuf.delete(0,1)  ;break;//"B相电压
                                            case 0xB613:sbBuf = sbBuf.delete(0,1)  ;break;//"C相电压
                                            case 0xB621:sbBuf = sbBuf.insert(2,".");break;//"A相电流
                                            case 0xB622:sbBuf = sbBuf.insert(2,".");break;//"B相电流
                                            case 0xB623:sbBuf = sbBuf.insert(2,".");break;//"C相电流
                                            case 0xB630:sbBuf = sbBuf.insert(2,".");break;//"有功功率"
                                            case 0xB631:sbBuf = sbBuf.insert(2,".");break;//"A相有功功率
                                            case 0xB632:sbBuf = sbBuf.insert(2,".");break;//"B相有功功率
                                            case 0xB633:sbBuf = sbBuf.insert(2,".");break;//"C相有功功率
                                            case 0xB640:sbBuf = sbBuf.insert(2,".");break;//"无功功率"
                                            case 0xB641:sbBuf = sbBuf.insert(2,".");break;//"A相无功功率
                                            case 0xB642:sbBuf = sbBuf.insert(2,".");break;//"B相无功功率
                                            case 0xB643:sbBuf = sbBuf.insert(2,".");break;//"C相无功功率
                                            case 0xB650:sbBuf = sbBuf.insert(1,".");break;//"总功率因数
                                            case 0xB651:sbBuf = sbBuf.insert(1,".");break;//"A相功率因数
                                            case 0xB652:sbBuf = sbBuf.insert(1,".");break;//"B相功率因数
                                            case 0xB653:sbBuf = sbBuf.insert(1,".");break;//"C相功率因数
                                            default:sbBuf = new StringBuilder ("暂不支持此项标识");break;
                                        }
                                        
                                        buf = sbBuf.toString();
                                        return new String[]{mDz, mDataID , buf};

                                    } else if(mDataID.length() == 8){        // 07

                                        switch(Integer.valueOf(mDataID, 16)) {
                                            case 0x00010000:sbBuf = sbBuf.insert(6,".");break;//正向有功电度
                                            case 0x00020000:sbBuf = sbBuf.insert(6,".");break;//反向有功电度
                                            case 0x02010100:sbBuf = sbBuf.insert(3,".");break;//A相电压
                                            case 0x02010200:sbBuf = sbBuf.insert(3,".");break;//B相电压
                                            case 0x02010300:sbBuf = sbBuf.insert(3,".");break;//C相电压
                                            case 0x02020100:sbBuf = sbBuf.insert(3,".");break;//A相电流
                                            case 0x02020200:sbBuf = sbBuf.insert(3,".");break;//B相电流
                                            case 0x02020300:sbBuf = sbBuf.insert(3,".");break;//C相电流
                                            case 0x02030000:sbBuf = sbBuf.insert(2,".");break;//有功功率
                                            case 0x02030100:sbBuf = sbBuf.insert(2,".");break;//A相有功功率
                                            case 0x02030200:sbBuf = sbBuf.insert(2,".");break;//B相有功功率
                                            case 0x02030300:sbBuf = sbBuf.insert(2,".");break;//C相有功功率
                                            case 0x02040000:sbBuf = sbBuf.insert(2,".");break;//无功功率
                                            case 0x02040100:sbBuf = sbBuf.insert(2,".");break;//A相无功功率
                                            case 0x02040200:sbBuf = sbBuf.insert(2,".");break;//B相无功功率
                                            case 0x02040300:sbBuf = sbBuf.insert(2,".");break;//C相无功功率
                                            case 0x02060000:sbBuf = sbBuf.insert(1,".");break;//总功率因数
                                            case 0x02060100:sbBuf = sbBuf.insert(1,".");break;//A相功率因数
                                            case 0x02060200:sbBuf = sbBuf.insert(1,".");break;//B相功率因数
                                            case 0x02060300:sbBuf = sbBuf.insert(1,".");break;//C相功率因数
                                            case 0x02800002:sbBuf = sbBuf.insert(2,".");break;//频率
                                            default:sbBuf = new StringBuilder ("暂不支持此项标识");break;
                                        }

                                        buf = sbBuf.toString();
                                        return new String[]{mDz, mDataID, buf};
                                    }

                                    Log.i("info", "buf ---- " + buf);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        // TODO 自动生成的 catch 块
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                continue;
                            }
                        } else {
                            Log.i("info", "---3----错误：没有接收到数据帧！");
                            continue;
                        }
                    } else {
                        continue;
                    }
                    //endregion 判断控制码
                }
                //endregion 定位"0x68"
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return new String[]{mDz, mDataID, buf.toString()};
    }

}
