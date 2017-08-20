package com.zh.metermanagement.trasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.bean.CollectorNumberBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.bean.MeterPhoneBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.db.biz.TableEx;
import com.zh.metermanagement.db.helper.DataManagement;
import com.zh.metermanagement.utils.ExcelUtil1;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.POIExcelUtil;
import com.zh.metermanagement.utils.StringUtils;
import com.zh.metermanagement.view.linkagerecyclerview.ShenBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by gbh on 16/12/7.
 */

public class TaskPresenterImpl1 implements TaskPresenter1 {


    private Context mContext;
    private TableEx mTableEx;

    public TaskPresenterImpl1(Context context){
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
    public void importMeterInfoExcelToDb(Observer observer, File[] files) {

        //LogUtils.i("files.length:" + files.length);
        String[] importExcels = new String[files.length];
        for(int i=0; i<files.length; i++){
            //LogUtils.i("files[i].getAbsolutePath():" + files[i].getAbsolutePath());
            importExcels[i] = files[i].getAbsolutePath();

        }

        DataManagement.deleteTable(mContext, Constant.DB_NAME, Constant.TABLE_METERINFO1);
        DataManagement.deleteTable(mContext, Constant.DB_NAME, Constant.TABLE_ASSETNUMBER);
        DataManagement.deleteTable(mContext, Constant.DB_NAME, Constant.TABLE_COLLECTORNUMBER);

        Observable.fromArray(importExcels)
                .observeOn(Schedulers.io())
                .map(new Function<String, Long>() {
                    @Override
                    public Long apply(@NonNull String importExcel)
                            throws Exception {

                        List<MeterBean1> beanList = POIExcelUtil.readMeterInfoExcel(importExcel);
                        //Log.i("shen", "beanList.size():"+beanList.size());

                        long i = 0;

                        if(beanList == null || beanList.size() == 0)
                            return i;

                        for(MeterBean1 bean : beanList) {
                            ContentValues values = new ContentValues();
                            values.put(Constant.TABLE_METERINFO1_STR_userNumber, bean.getUserNumber());
                            values.put(Constant.TABLE_METERINFO1_STR_userName, bean.getUserName());
                            values.put(Constant.TABLE_METERINFO1_STR_userAddr, bean.getUserAddr());
                            values.put(Constant.TABLE_METERINFO1_STR_oldAssetNumbers, bean.getOldAssetNumbers());
                            values.put(Constant.TABLE_METERINFO1_STR_measurementPointNumber, bean.getMeasurementPointNumber());
                            values.put(Constant.TABLE_METERINFO1_STR_powerSupplyBureau, bean.getPowerSupplyBureau());
                            values.put(Constant.TABLE_METERINFO1_STR_theMeteringSection, bean.getTheMeteringSection());
                            values.put(Constant.TABLE_METERINFO1_STR_courts, bean.getCourts());
                            values.put(Constant.TABLE_METERINFO1_STR_measuringPointAddress, bean.getMeasuringPointAddress());
                            values.put(Constant.TABLE_METERINFO1_STR_isFinish, bean.isFinish());

                            i = mTableEx.Add(Constant.TABLE_METERINFO1, values);  // 返回值是  -1  失败
                            //Log.i("shen", "i 返回值：" + i);
                            //Log.i("shen", "bean.toString()：" + bean.toString());
                        }

                        return (long) beanList.size();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void importMeterPhoneExcelToDb(Observer observer, File[] files) {

        //LogUtils.i("files.length:" + files.length);
        String[] importExcels = new String[files.length];
        for(int i=0; i<files.length; i++){
            //LogUtils.i("files[i].getAbsolutePath():" + files[i].getAbsolutePath());
            importExcels[i] = files[i].getAbsolutePath();

        }

        Observable.fromArray(importExcels)
                .observeOn(Schedulers.io())
                .map(new Function<String, Long>() {
                    @Override
                    public Long apply(@NonNull String importExcel)
                            throws Exception {

                        List<MeterPhoneBean> beanList = POIExcelUtil.readMeterPhoneExcel(importExcel);
                        //Log.i("shen", "beanList.size():"+beanList.size());

                        long i = 0;

                        if(beanList == null || beanList.size() == 0)
                            return i;

                        for(MeterPhoneBean bean : beanList) {

                            ContentValues values = new ContentValues();
                            values.put("userPhone", bean.getUserPhone());

                            i = mTableEx.Update(Constant.TABLE_METERINFO1, values,
                                    "userNumber=?",
                                    new String[]{bean.getUserNumber()});


                            //Log.i("shen", "i 返回值：" + i);
                            //Log.i("shen", "bean.toString()：" + bean.toString());
                        }

                        return (long) beanList.size();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    /**
     *
     * @param observer
     * @param meteringSection  为"" 不筛选
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MeterBean1> readDbToBean(Observer observer, final String meteringSection) {


        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, List<MeterBean1>>() {
                    @Override
                    public List<MeterBean1> apply(@NonNull String s) throws Exception {

                        Cursor cursor;
                        if(StringUtils.isNotEmpty(meteringSection)) {
                            cursor  = mTableEx.Query(Constant.TABLE_METERINFO1,
                                    null,
                                    "theMeteringSection=?",
                                    new String[]{meteringSection},
                                    null, null, null);
                        }else {
                            cursor  = mTableEx.Query(Constant.TABLE_METERINFO1,
                                    null,
                                    null,
                                    null,
                                    null, null, null);
                        }

                        List<MeterBean1> beanList = new ArrayList<MeterBean1>();
                        if(cursor.getCount() != 0) {
                            if(cursor.moveToFirst()) {
                                do{
                                    MeterBean1 meterBean = new MeterBean1();

                                    try {

                                        meterBean.setUserNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userNumber)));
                                        meterBean.setUserName(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userName)));
                                        meterBean.setUserAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userAddr)));
                                        meterBean.setUserPhone(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userPhone)));
                                        meterBean.setMeasurementPointNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_measurementPointNumber)));
                                        meterBean.setPowerSupplyBureau(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_powerSupplyBureau)));
                                        meterBean.setTheMeteringSection(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_theMeteringSection)));
                                        meterBean.setCourts(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_courts)));
                                        meterBean.setMeasuringPointAddress(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_measuringPointAddress)));

                                        meterBean.setOldAssetNumbers(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAssetNumbers)));
                                        meterBean.setOldAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAddr)));
                                        //------------------------
                                        String oldAddrAndAsset = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAddrAndAsset));
                                        if(StringUtils.isNotEmpty(oldAddrAndAsset) ) {
                                            meterBean.setOldAddrAndAsset(oldAddrAndAsset.equals("1"));
                                        }else {
                                            meterBean.setOldAddrAndAsset(false);
                                        }
                                        //------------------------
                                        meterBean.setOldElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldElectricity)));

                                        meterBean.setNewAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAddr)));
                                        //------------------------
                                        String newAddrAndAsset = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAddrAndAsset));
                                        if(StringUtils.isNotEmpty(newAddrAndAsset) ) {
                                            meterBean.setNewAddrAndAsset(newAddrAndAsset.equals("1"));
                                        }else {
                                            meterBean.setNewAddrAndAsset(false);
                                        }
                                        //------------------------
                                        meterBean.setNewAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAssetNumbersScan)));
                                        meterBean.setNewElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newElectricity)));
                                        meterBean.setCollectorAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_collectorAssetNumbersScan)));
                                        meterBean.setTime(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_time)));
                                        meterBean.setPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_picPath)));
                                        meterBean.setMeterPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_meterPicPath)));
                                        meterBean.setMeterContentPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_meterContentPicPath)));
                                        meterBean.setRelaceOrAnd(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_relaceOrAnd)));
                                        //------------------------
                                        String isFinish = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_isFinish));
                                        if(StringUtils.isNotEmpty(isFinish) ) {
                                            meterBean.setFinish(isFinish.equals("1"));
                                        }else {
                                            meterBean.setFinish(false);
                                        }
                                        //------------------------

                                    }catch (Exception e){
                                        LogUtils.i("e.getMessage()22222:" + e.getMessage());  // 因为有些空值
                                    }

                                    //Log.i("shen", "nei:" + meterBean.toString());
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
                    public Long apply(@NonNull String s) throws Exception {

                        long i = mTableEx.Update(Constant.TABLE_METERINFO1, values, whereClause, whereArgs);

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
                    public List<AssetNumberBean> apply(@NonNull String s) throws Exception {

                        Cursor cursor = mTableEx.Query(Constant.TABLE_ASSETNUMBER,
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
                                                cursor.getColumnIndex(Constant.TABLE_ASSETNUMBER_STR_assetNumbers)));

                                    }catch (Exception e){
                                        LogUtils.i("e.getMessage():" + e.getMessage());
                                    }

                                    //LogUtils.i("nei:" + assetNumberBean.toString());
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
                    public Long apply(@NonNull String s) throws Exception {

                        Long i = mTableEx.Add(Constant.TABLE_ASSETNUMBER, values);

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void generateReports(Observer observer, final Context context,
                                final List<MeterBean1> meterBeanList,
                                final List<AssetNumberBean> assetNumberBeenList,
                                final String excelPath1,
                                final String excelPath2,
                                final String excelPath3) {
        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull String s) throws Exception {

                        //String str = ExcelUtil.writeExcel1(context, meterBeanList, assetNumberBeenList);

                        POIExcelUtil.writeExcel1(context, meterBeanList, assetNumberBeenList, excelPath1);
                        POIExcelUtil.writeExcel2(context, meterBeanList,  excelPath2);
                        POIExcelUtil.writeExcel3(context, meterBeanList,  excelPath3);

                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    @Override
    public void getMeteringSection(Observer observer) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, List<ShenBean>>() {
                    @Override
                    public List<ShenBean> apply(@NonNull String s) throws Exception {

                        /**
                         * 查询<p>
                         *
                         * 必须在Cursor使用之后，才可以关闭数据库连接。 例如：Cursor.moveToNext()执行的时候，才会去查询数据库中是否有数据。<p>
                         *
                         * Cursor cursor = database.query("person"(这个在某段代码填上了：表名),
                         *                                  new String[]{"name,age,phone"}, "name=?",
                         *                                  new String[]{"shen"}, null, null, null);
                         *
                         * @param columns           返回列(要查询的字段)
                         * @param selection         查询条件(过滤字段) 例如：id=?，?为通配符
                         * @param selectionArgs     条件集合(过滤字段的值) 例如： new String[]{"1"}
                         * @param groupBy           分组
                         * @param having
                         * @param orderBy           排序
                         *
                         */
                        Cursor cursor = mTableEx.Query(Constant.TABLE_METERINFO1,
                                new String[]{"powerSupplyBureau","courts","theMeteringSection"},
                                null,
                                null,
                                "theMeteringSection", //"theMeteringSection,courts,powerSupplyBureau",
                                null, "powerSupplyBureau");

                        List<ShenBean> beanList = new ArrayList<ShenBean>();
                        String powerSupplyBureau = "";
                        boolean isStart = true;
                        ShenBean shenBean = null;
                        int count = cursor.getCount();
                        LogUtils.i("ount:" + count);
                        int j = 0;
                        if(count != 0) {
                            if(cursor.moveToFirst()) {
                                do{
                                    j++;
                                    try {
                                        String supplyBureau = cursor.getString(0);
                                        if(!TextUtils.isEmpty(supplyBureau) && isStart){
                                            isStart = false;
                                            shenBean = new ShenBean();
                                            shenBean.setPowerSupplyBureau(cursor.getString(0));
                                            ArrayList<ShenBean.SupplyBureau> supplyBureaus = new ArrayList<ShenBean.SupplyBureau>();
                                            ShenBean.SupplyBureau bureau = new ShenBean.SupplyBureau();
                                            bureau.setCourts(cursor.getString(1));
                                            bureau.setTheMeteringSection(cursor.getString(2));
                                            supplyBureaus.add(bureau);
                                            shenBean.setSupplyBureaus(supplyBureaus);
                                            powerSupplyBureau = supplyBureau;

                                        }else if (!TextUtils.isEmpty(supplyBureau) && supplyBureau.equals(powerSupplyBureau ) && !isStart){
                                            ArrayList<ShenBean.SupplyBureau> supplyBureaus = shenBean.getSupplyBureaus();
                                            ShenBean.SupplyBureau bureau = new ShenBean.SupplyBureau();
                                            bureau.setCourts(cursor.getString(1));
                                            bureau.setTheMeteringSection(cursor.getString(2));
                                            supplyBureaus.add(bureau);
                                            shenBean.setSupplyBureaus(supplyBureaus);

                                        }else if(!TextUtils.isEmpty(supplyBureau) && !supplyBureau.equals(powerSupplyBureau) && !isStart){
                                            beanList.add(shenBean);
                                            shenBean = new ShenBean();
                                            shenBean.setPowerSupplyBureau(cursor.getString(0));
                                            ArrayList<ShenBean.SupplyBureau> supplyBureaus = new ArrayList<ShenBean.SupplyBureau>();
                                            ShenBean.SupplyBureau bureau = new ShenBean.SupplyBureau();
                                            bureau.setCourts(cursor.getString(1));
                                            bureau.setTheMeteringSection(cursor.getString(2));
                                            supplyBureaus.add(bureau);
                                            shenBean.setSupplyBureaus(supplyBureaus);
                                        }

                                        powerSupplyBureau = supplyBureau;
                                    }catch (Exception e){
                                        LogUtils.i("e.getMessage():" + e.getMessage());
                                    }

                                    if(j == count){
                                        beanList.add(shenBean);
                                    }
                                }while (cursor.moveToNext());
                            }
                        }

                        for (ShenBean shenBean1 : beanList){
                            //LogUtils.i("shenBean1.toString():" + shenBean1.toString());
                        }

                        Log.i("shen", "beanList.size():"+beanList.size());

                        return beanList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void getCollector(Observer observer, final String meteringSection, final String collector) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, CollectorNumberBean>() {
                    @Override
                    public CollectorNumberBean apply(@NonNull String s) throws Exception {

                        /**
                         * 查询<p>
                         *
                         * 必须在Cursor使用之后，才可以关闭数据库连接。 例如：Cursor.moveToNext()执行的时候，才会去查询数据库中是否有数据。<p>
                         *
                         * Cursor cursor = database.query("person"(这个在某段代码填上了：表名),
                         *                                  new String[]{"name,age,phone"}, "name=?",
                         *                                  new String[]{"shen"}, null, null, null);
                         *
                         * @param columns           返回列(要查询的字段)
                         * @param selection         查询条件(过滤字段) 例如：id=?，?为通配符
                         * @param selectionArgs     条件集合(过滤字段的值) 例如： new String[]{"1"}
                         * @param groupBy           分组
                         * @param having
                         * @param orderBy           排序
                         *
                         */
                        Cursor cursor = mTableEx.Query(Constant.TABLE_COLLECTORNUMBER,
                                null,
                                "theMeteringSection=? and collectorNumbers=?",
                                new String[]{meteringSection, collector},
                                null, null, null);


                        CollectorNumberBean bean = new CollectorNumberBean();
                        if(cursor != null) {
                            int count = cursor.getCount();

                            if (count != 0) {
                                if (cursor.moveToFirst()) {
                                    bean.setCollectorNumbers(cursor.getString(
                                            cursor.getColumnIndex(Constant.TABLE_COLLECTORNUMBER_STR_collectorNumbers)));
                                    bean.setTheMeteringSection(cursor.getString(
                                            cursor.getColumnIndex(Constant.TABLE_COLLECTORNUMBER_STR_theMeteringSection)));
                                    bean.setCollectorPicPath(cursor.getString(
                                            cursor.getColumnIndex(Constant.TABLE_COLLECTORNUMBER_STR_collectorPicPath)));
                                }
                            }
                        }
                        return bean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void getCollectorList(Observer observer, final String meteringSection) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, ArrayList<CollectorNumberBean>>() {
                    @Override
                    public ArrayList<CollectorNumberBean> apply(@NonNull String s) throws Exception {

                        /**
                         * 查询<p>
                         *
                         * 必须在Cursor使用之后，才可以关闭数据库连接。 例如：Cursor.moveToNext()执行的时候，才会去查询数据库中是否有数据。<p>
                         *
                         * Cursor cursor = database.query("person"(这个在某段代码填上了：表名),
                         *                                  new String[]{"name,age,phone"}, "name=?",
                         *                                  new String[]{"shen"}, null, null, null);
                         *
                         * @param columns           返回列(要查询的字段)
                         * @param selection         查询条件(过滤字段) 例如：id=?，?为通配符
                         * @param selectionArgs     条件集合(过滤字段的值) 例如： new String[]{"1"}
                         * @param groupBy           分组
                         * @param having
                         * @param orderBy           排序
                         *
                         */
                        Cursor cursor = mTableEx.Query(Constant.TABLE_COLLECTORNUMBER,
                                null,
                                "theMeteringSection=?",
                                new String[]{meteringSection},
                                null, null, null);


                        ArrayList<CollectorNumberBean> beanList = new ArrayList<CollectorNumberBean>();
                        if(cursor != null) {
                            int count = cursor.getCount();

                            if (count != 0) {
                                if (cursor.moveToFirst()) {
                                    do{
                                        CollectorNumberBean bean = new CollectorNumberBean();
                                        bean.setCollectorNumbers(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_COLLECTORNUMBER_STR_collectorNumbers)));
                                        bean.setTheMeteringSection(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_COLLECTORNUMBER_STR_theMeteringSection)));
                                        bean.setCollectorPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_COLLECTORNUMBER_STR_collectorPicPath)));
                                        beanList.add(bean);

                                    }while (cursor.moveToNext());
                                }
                            }
                        }
                        return beanList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public List<MeterBean1> readDbToBeanForCollector(Observer observer, final String meteringSection, final String collectorNumber) {
        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, List<MeterBean1>>() {
                    @Override
                    public List<MeterBean1> apply(@NonNull String s) throws Exception {

                        Cursor cursor;
                        cursor  = mTableEx.Query(Constant.TABLE_METERINFO1,
                                null,
                                "theMeteringSection=? and collectorAssetNumbersScan=?",
                                new String[]{meteringSection, collectorNumber},
                                null, null, null);

                        List<MeterBean1> beanList = new ArrayList<MeterBean1>();
                        if(cursor.getCount() != 0) {
                            if(cursor.moveToFirst()) {
                                do{
                                    MeterBean1 meterBean = new MeterBean1();

                                    try {

                                        meterBean.setUserNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userNumber)));
                                        meterBean.setUserName(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userName)));
                                        meterBean.setUserAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userAddr)));
                                        meterBean.setUserPhone(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userPhone)));
                                        meterBean.setMeasurementPointNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_measurementPointNumber)));
                                        meterBean.setPowerSupplyBureau(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_powerSupplyBureau)));
                                        meterBean.setTheMeteringSection(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_theMeteringSection)));
                                        meterBean.setCourts(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_courts)));
                                        meterBean.setMeasuringPointAddress(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_measuringPointAddress)));

                                        meterBean.setOldAssetNumbers(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAssetNumbers)));
                                        meterBean.setOldAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAddr)));
                                        //------------------------
                                        String oldAddrAndAsset = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAddrAndAsset));
                                        if(StringUtils.isNotEmpty(oldAddrAndAsset) ) {
                                            meterBean.setOldAddrAndAsset(oldAddrAndAsset.equals("1"));
                                        }else {
                                            meterBean.setOldAddrAndAsset(false);
                                        }
                                        //------------------------
                                        meterBean.setOldElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldElectricity)));
                                        meterBean.setNewAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAddr)));
                                        //------------------------
                                        String newAddrAndAsset = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAddrAndAsset));
                                        if(StringUtils.isNotEmpty(newAddrAndAsset) ) {
                                            meterBean.setNewAddrAndAsset(newAddrAndAsset.equals("1"));
                                        }else {
                                            meterBean.setNewAddrAndAsset(false);
                                        }
                                        //------------------------
                                        meterBean.setNewAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAssetNumbersScan)));
                                        meterBean.setNewElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newElectricity)));
                                        meterBean.setCollectorAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_collectorAssetNumbersScan)));
                                        meterBean.setTime(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_time)));
                                        meterBean.setPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_picPath)));
                                        meterBean.setMeterPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_meterPicPath)));
                                        meterBean.setMeterContentPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_meterContentPicPath)));
                                        meterBean.setRelaceOrAnd(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_relaceOrAnd)));
                                        //------------------------
                                        String isFinish = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_isFinish));
                                        if(StringUtils.isNotEmpty(isFinish) ) {
                                            meterBean.setFinish(isFinish.equals("1"));
                                        }else {
                                            meterBean.setFinish(false);
                                        }
                                        //------------------------
                                    }catch (Exception e){
                                        LogUtils.i("readDbToBeanForCollector -- e.getMessage()3:" + e.getMessage());  // 因为有些空值
                                    }

                                    //Log.i("shen", "nei:" + meterBean.toString());
                                    beanList.add(meterBean);

                                }while (cursor.moveToNext());
                            }
                        }

                        Log.i("shen", "readDbToBeanForCollector -- beanList.size():"+beanList.size());

                        return beanList;

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        return null;
    }

    @Override
    public void addCollectorToMeterInfo(Observer observer, final String meteringSection, final String collectorNumber, List<MeterBean1> meterBean1List) {


        MeterBean1[] meterBean1arr = new MeterBean1[meterBean1List.size()];
        for(int i=0; i< meterBean1List.size(); i++){
            meterBean1arr[i] = meterBean1List.get(i);
        }

        Observable.fromArray(meterBean1arr)
                .observeOn(Schedulers.io())
                .map(new Function<MeterBean1, Long>() {

                    @Override
                    public Long apply(@NonNull MeterBean1 meterBean1) throws Exception {

                        ContentValues values = new ContentValues();
                        values.put("collectorAssetNumbersScan", collectorNumber);
                        values.put("relaceOrAnd", "1");         // 0:"换表"； 1："新装采集器" -- 要先判断是否抄完
                        values.put("meterPicPath", meterBean1.getMeterPicPath());
                        values.put("isFinish", true);

                        long i = mTableEx.Update(Constant.TABLE_METERINFO1, values,
                                "theMeteringSection=? and oldAssetNumbers=?",
                                new String[]{meteringSection, meterBean1.getOldAssetNumbers()});

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }



    @Override
    public void addCollectorToCollectorTable(Observer observer, final String meteringSection, final CollectorNumberBean collectorNumberBean) {

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, Long>() {

                    @Override
                    public Long apply(@NonNull String s) throws Exception {

                        LogUtils.i("CollectorNumberBean:" + collectorNumberBean.toString());

                        ContentValues values = new ContentValues();
                        values.put("collectorNumbers", collectorNumberBean.getCollectorNumbers());
                        values.put("theMeteringSection", meteringSection);         // 0:"换表"； 1："新装采集器" -- 要先判断是否抄完
                        values.put("collectorPicPath", collectorNumberBean.getCollectorPicPath());


                        Cursor cursor =mTableEx.Query(Constant.TABLE_COLLECTORNUMBER,
                                null,
                                "collectorNumbers=?",
                                new String[]{collectorNumberBean.getCollectorNumbers()},
                                null,
                                null,
                                null);

                        long i = 0;
                        if(cursor != null) {
                            int count = cursor.getCount();

                            LogUtils.i("count:" + count);

                            if (count > 0) {
                                i = mTableEx.Update(Constant.TABLE_COLLECTORNUMBER, values,
                                        "collectorNumbers=?",
                                        new String[]{collectorNumberBean.getCollectorNumbers()});
                            } else {
                                i = mTableEx.Add(Constant.TABLE_COLLECTORNUMBER, values);
                            }



                        } else {
                            i = mTableEx.Add(Constant.TABLE_COLLECTORNUMBER, values);
                        }

                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public List<MeterBean1> searchMeterInfo(Observer observer, final String meteringSection, HashMap<String, String> conditionMap) {

        int size = conditionMap.keySet().size();
        if(size <=0 ){
            return null;
        }

        String selection = null;
        final String[] selectionArgs = new String[size];

        int i = 0;
        for(String set : conditionMap.keySet()){
            if(i == size-1){
                if(set.equals("userNumber") || set.equals("userName")) {
                    selectionArgs[i] = "%" + conditionMap.get(set) + "%";
                    selection = set + " like ?";

                }else {

                    selection = set + "=?";
                    selectionArgs[i] = conditionMap.get(set);
                }
            }else {

                if(set.equals("userNumber") || set.equals("userName")) {
                    selectionArgs[i] = "%" + conditionMap.get(set) + "%";
                    selection = set + " like ? and ";

                }else {
                    selection = set + "=? and ";
                    selectionArgs[i] = conditionMap.get(set);
                }
            }

            i++;
        }

        LogUtils.i(selection);

        final String finalSelection = selection;
        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Function<String, List<MeterBean1>>() {
                    @Override
                    public List<MeterBean1> apply(@NonNull String s) throws Exception {

                        Cursor cursor;
                        cursor  = mTableEx.Query(Constant.TABLE_METERINFO1,
                                null,
                                finalSelection,
                                selectionArgs,
                                null, null, null);

                        List<MeterBean1> beanList = new ArrayList<MeterBean1>();
                        if(cursor != null && cursor.getCount() != 0) {
                            if(cursor.moveToFirst()) {
                                do{
                                    MeterBean1 meterBean = new MeterBean1();

                                    try {

                                        meterBean.setUserNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userNumber)));
                                        meterBean.setUserName(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userName)));
                                        meterBean.setUserAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userAddr)));
                                        meterBean.setUserPhone(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_userPhone)));
                                        meterBean.setMeasurementPointNumber(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_measurementPointNumber)));
                                        meterBean.setPowerSupplyBureau(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_powerSupplyBureau)));
                                        meterBean.setTheMeteringSection(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_theMeteringSection)));
                                        meterBean.setCourts(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_courts)));
                                        meterBean.setMeasuringPointAddress(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_measuringPointAddress)));

                                        meterBean.setOldAssetNumbers(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAssetNumbers)));
                                        meterBean.setOldAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAddr)));
                                        //------------------------
                                        String oldAddrAndAsset = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldAddrAndAsset));
                                        if(StringUtils.isNotEmpty(oldAddrAndAsset) ) {
                                            meterBean.setOldAddrAndAsset(oldAddrAndAsset.equals("1"));
                                        }else {
                                            meterBean.setOldAddrAndAsset(false);
                                        }
                                        //------------------------
                                        meterBean.setOldElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_oldElectricity)));
                                        meterBean.setNewAddr(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAddr)));
                                        //------------------------
                                        String newAddrAndAsset = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAddrAndAsset));
                                        if(StringUtils.isNotEmpty(newAddrAndAsset) ) {
                                            meterBean.setNewAddrAndAsset(newAddrAndAsset.equals("1"));
                                        }else {
                                            meterBean.setNewAddrAndAsset(false);
                                        }
                                        //------------------------
                                        meterBean.setNewAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newAssetNumbersScan)));
                                        meterBean.setNewElectricity(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_newElectricity)));
                                        meterBean.setCollectorAssetNumbersScan(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_collectorAssetNumbersScan)));
                                        meterBean.setTime(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_time)));
                                        meterBean.setPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_picPath)));
                                        meterBean.setMeterPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_meterPicPath)));
                                        meterBean.setMeterContentPicPath(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_meterContentPicPath)));
                                        meterBean.setRelaceOrAnd(cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_relaceOrAnd)));
                                        //------------------------
                                        String isFinish = cursor.getString(
                                                cursor.getColumnIndex(Constant.TABLE_METERINFO1_STR_isFinish));
                                        if(StringUtils.isNotEmpty(isFinish) ) {
                                            meterBean.setFinish(isFinish.equals("1"));
                                        }else {
                                            meterBean.setFinish(false);
                                        }
                                        //------------------------
                                    }catch (Exception e){
                                        LogUtils.i("readDbToBeanForCollector -- e.getMessage()3:" + e.getMessage());  // 因为有些空值
                                    }

                                    //Log.i("shen", "nei:" + meterBean.toString());
                                    beanList.add(meterBean);

                                }while (cursor.moveToNext());
                            }
                        }

                        Log.i("shen", "readDbToBeanForCollector -- beanList.size():"+beanList.size());

                        return beanList;

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);


        return null;
    }
}
