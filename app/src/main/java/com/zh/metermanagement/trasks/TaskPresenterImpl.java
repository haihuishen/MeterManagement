package com.zh.metermanagement.trasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.db.biz.TableEx;
import com.zh.metermanagement.db.helper.DataManagement;
import com.zh.metermanagement.utils.ExcelUtil;
import com.zh.metermanagement.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by gbh on 16/12/7.
 */

public class TaskPresenterImpl implements TaskPresenter {


    private Context mContext;
    private TableEx mTableEx;

    public TaskPresenterImpl(Context context){
        mContext = context;
        mTableEx = new TableEx(MyApplication.getContext());
    }


    @Override
    public void loginData(String id, String pwd) {

    }

    @Override
    public void queryData(String plateNumber, Observer observer) {

    }

    @Override
    public void queryData(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, Observer observer) {

    }

    @Override
    public void addPlateNumber() {

    }

    @Override
    public void importExcelToDb(Observer observer) {



        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, Long>() {
                    @Override
                    public Long apply(@io.reactivex.annotations.NonNull String s)
                            throws Exception {

                        DataManagement.deleteTable(mContext, Constant.DB_NAME, Constant.TABLE_METERINFO);
                        DataManagement.deleteTable(mContext, Constant.DB_NAME, Constant.TABLE_ASSETNUMBERS);

                        List<MeterBean> beanList = ExcelUtil.readExcel(Constant.excelPathDir + Constant.importExcel);
                        Log.i("shen", "beanList.size():"+beanList.size());

                        long i = 0;

                        if(beanList == null || beanList.size() == 0)
                            return i;


                        for(MeterBean bean : beanList) {
                            ContentValues values = new ContentValues();
                            values.put(Constant.TABLE_METERINFO_STR_sequenceNumber, bean.getSequenceNumber());
                            values.put(Constant.TABLE_METERINFO_STR_userNumber, bean.getUserNumber());
                            values.put(Constant.TABLE_METERINFO_STR_userName, bean.getUserName());
                            values.put(Constant.TABLE_METERINFO_STR_userAddr, bean.getUserAddr());
                            values.put(Constant.TABLE_METERINFO_STR_oldAssetNumbers, bean.getOldAssetNumbers());
                            values.put(Constant.TABLE_METERINFO_STR_oldElectricity, bean.getOldElectricity());
                            values.put(Constant.TABLE_METERINFO_STR_newAssetNumbersScan, bean.getNewAssetNumbersScan());
                            values.put(Constant.TABLE_METERINFO_STR_newElectricity, bean.getNewElectricity());
                            values.put(Constant.TABLE_METERINFO_STR_collectorAssetNumbersScan, bean.getCollectorAssetNumbersScan());
                            values.put(Constant.TABLE_METERINFO_STR_area, bean.getArea());
                            values.put(Constant.TABLE_METERINFO_STR_isFinish, bean.isFinish());

                            i = mTableEx.Add(Constant.TABLE_METERINFO, values);  // 返回值是  -1  失败
                            //Log.i("shen", "i 返回值：" + i);
                            //Log.i("shen", "bean.toString()：" + bean.toString());
                        }

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);


    }

    @Override
    public List<MeterBean> readDbToBean(Observer observer) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, List<MeterBean>>() {
                    @Override
                    public List<MeterBean> apply(@io.reactivex.annotations.NonNull String s) throws Exception {

                        Cursor cursor = mTableEx.Query(Constant.TABLE_METERINFO,
                                null,
                                null,
                                null,
                                null, null, null);

                        List<MeterBean> beanList = new ArrayList<MeterBean>();
                        if(cursor.getCount() != 0) {
                            if(cursor.moveToFirst()) {
                                do{
                                    MeterBean meterBean = new MeterBean();

                                    try {


                                        meterBean.setSequenceNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_sequenceNumber)));
                                        meterBean.setUserNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_userNumber)));
                                        meterBean.setUserName(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_userName)));
                                        meterBean.setUserAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_userAddr)));
                                        meterBean.setArea(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_area)));
                                        meterBean.setOldAssetNumbers(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_oldAssetNumbers)));
                                        meterBean.setOldAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_oldAssetNumbersScan)));
                                        meterBean.setOldAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_oldAddr)));
                                        meterBean.setOldAddrAndAsset(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_oldAddrAndAsset)).equals("1"));
                                        meterBean.setOldElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_oldElectricity)));

                                        meterBean.setNewAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_newAddr)));
                                        meterBean.setNewAddrAndAsset(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_newAddrAndAsset)).equals("1"));
                                        meterBean.setNewAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_newAssetNumbersScan)));
                                        meterBean.setNewElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_newElectricity)));
                                        meterBean.setCollectorAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_collectorAssetNumbersScan)));
                                        meterBean.setTime(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_time)));
                                        meterBean.setPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_picPath)));

                                        meterBean.setFinish(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO_STR_isFinish)).equals("1"));

                                    }catch (Exception e){
                                        // LogUtils.i("e.getMessage():" + e.getMessage());  // 因为有些空值
                                    }

                                    Log.i("shen", "nei:" + meterBean.toString());
                                    beanList.add(meterBean);

                                }while (cursor.moveToNext());
                            }
                        }

                        Log.i("shen", "beanList.size():"+beanList.size());

                        return beanList;

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        return null;
    }


    @Override
    public void saveData(Observer observer, final ContentValues values, final String whereClause, final String[] whereArgs) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, Long>() {
                    @Override
                    public Long apply(@io.reactivex.annotations.NonNull String s) throws Exception {

                        long i = mTableEx.Update(Constant.TABLE_METERINFO, values, whereClause, whereArgs);

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    @Override
    public void saveNewCollector(Observer observer, final String NewCollector, String[] meters) {
        Observable.fromArray(meters)
                .observeOn(Schedulers.io())
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(@NonNull String s) throws Exception {

                        ContentValues values = new ContentValues();
                        values.put("collectorAssetNumbersScan", NewCollector);
                        long i = mTableEx.Update(Constant.TABLE_METERINFO, values, "oldAssetNumbers=?", new String[]{s});

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    @Override
    public void statisticsData(Observer observer) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, List<AssetNumberBean>>() {
                    @Override
                    public List<AssetNumberBean> apply(@io.reactivex.annotations.NonNull String s) throws Exception {

                        Cursor cursor = mTableEx.Query(Constant.TABLE_ASSETNUMBERS,
                                null,
                                null,
                                null,
                                null, null, null);

                        List<AssetNumberBean> beanList = new ArrayList<AssetNumberBean>();
                        if(cursor.getCount() != 0) {
                            if(cursor.moveToFirst()) {
                                do{
                                    AssetNumberBean assetNumberBean = new AssetNumberBean();

                                    try {


                                        assetNumberBean.setAssetNumbers(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_ASSETNUMBERS_STR_assetNumbers)));

                                    }catch (Exception e){
                                        LogUtils.i("e.getMessage():" + e.getMessage());
                                    }

                                    LogUtils.i("nei:" + assetNumberBean.toString());
                                    beanList.add(assetNumberBean);

                                }while (cursor.moveToNext());
                            }
                        }

                        Log.i("shen", "beanList.size():"+beanList.size());

                        return beanList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void addMismatchingAssetNumbers(Observer observer, final ContentValues values) {
        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, Long>() {
                    @Override
                    public Long apply(@io.reactivex.annotations.NonNull String s) throws Exception {

                        Long i = mTableEx.Add(Constant.TABLE_ASSETNUMBERS, values);

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void generateReports(Observer observer, final Context context, final List<MeterBean> meterBeanList, final List<AssetNumberBean> assetNumberBeenList) {
        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@io.reactivex.annotations.NonNull String s) throws Exception {

                        String str = ExcelUtil.writeExcel(context, meterBeanList, assetNumberBeenList);

                        return str;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
