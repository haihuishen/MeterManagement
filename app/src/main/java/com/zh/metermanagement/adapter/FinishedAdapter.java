package com.zh.metermanagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.zh.metermanagement.R;
import com.zh.metermanagement.application.MyApplication;
import com.zh.metermanagement.bean.CollectorNumberBean;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.ImageFactory;
import com.zh.metermanagement.utils.LogUtils;
import com.zh.metermanagement.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2017/4/30.
 */
public class FinishedAdapter extends BaseAdapter {

    private ArrayList<MeterBean1> mBeanArrayList = new ArrayList<>();
    private Context mContext;

    FinishPhotoListener mFinishPhotoListener;

    private ArrayList<CollectorNumberBean> mCollectorNumberBean = new ArrayList<>();

    public FinishedAdapter(Context context, ArrayList<MeterBean1> meterBeanList,
                           ArrayList<CollectorNumberBean> collectorNumberBean,
                           FinishPhotoListener finishPhotoListener){

        mContext = context;
        mBeanArrayList = meterBeanList;
        mCollectorNumberBean = collectorNumberBean;
        mFinishPhotoListener = finishPhotoListener;
    }

    @Override
    public int getCount() {
        return mBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHold viewHold;
        if(convertView == null){                // 拿缓存
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.item_lv_finish,parent,false);      // listview中每一项的布局

            viewHold = new ViewHold(convertView);

            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }

        //viewHold.tvSequenceNumber.setText(mBeanArrayList.get(position).getSequenceNumber());
        viewHold.tvUserNumber.setText(mBeanArrayList.get(position).getUserNumber());
        viewHold.tvUserName.setText(mBeanArrayList.get(position).getUserName());
        viewHold.tvUserAddr.setText(mBeanArrayList.get(position).getUserAddr());
        viewHold.tvOldAssetNumbers.setText(mBeanArrayList.get(position).getOldAssetNumbers());

        viewHold.tvOldAddr.setText(mBeanArrayList.get(position).getOldAddr());
        viewHold.tvOldElectricity.setText(mBeanArrayList.get(position).getOldElectricity());
        viewHold.tvNewAssetNumbersScan.setText(mBeanArrayList.get(position).getNewAssetNumbersScan());
        viewHold.tvNewAddr.setText(mBeanArrayList.get(position).getNewAddr());
        viewHold.tvNewElectricity.setText(mBeanArrayList.get(position).getNewElectricity());


        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);         // 水平


        String path = "";
        String collectorPath = "";
        if(mBeanArrayList.get(position).getRelaceOrAnd() !=null &&
                mBeanArrayList.get(position).getRelaceOrAnd().equals("0")) {
            viewHold.tvNewCollector.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mBeanArrayList.get(position).getPicPath())) {
                path = mBeanArrayList.get(position).getPicPath();

            }
            viewHold.rvPicCollector.setVisibility(View.GONE);
            viewHold.lLayoutPicCollector.setVisibility(View.GONE);

        }else {
            viewHold.lLayoutPicCollector.setVisibility(View.VISIBLE);
            viewHold.rvPicCollector.setVisibility(View.VISIBLE);
            viewHold.tvNewCollector.setVisibility(View.VISIBLE);
            viewHold.tvNewCollector.setText(mBeanArrayList.get(position).getCollectorAssetNumbersScan());

            if (!TextUtils.isEmpty(mBeanArrayList.get(position).getMeterPicPath())) {
                path = mBeanArrayList.get(position).getMeterPicPath();
            }

            for(CollectorNumberBean bean : mCollectorNumberBean){
                LogUtils.i("mBeanArrayList.get(position).getCollectorAssetNumbersScan()" +
                        mBeanArrayList.get(position).getCollectorAssetNumbersScan() +
                "\nbean.getCollectorNumbers()" + bean.getCollectorNumbers());
                if(mBeanArrayList.get(position).getCollectorAssetNumbersScan().equals(bean.getCollectorNumbers())){
                    collectorPath = bean.getCollectorPicPath();
                    if(!TextUtils.isEmpty(collectorPath)) {
                        viewHold.rvPicCollector.setLayoutManager(manager);
                        PicAdapter collectorPicAdapter = new PicAdapter(mContext, new ArrayList<String>(), new PicAdapter.PicListener() {
                            @Override
                            public void onDelete(int index, String path) {

                            }

                            @Override
                            public void onPreView(int index, String path, Info info) {

                                mFinishPhotoListener.onPreView(index, path, info);

                            }
                        });

                        viewHold.rvPicCollector.setAdapter(collectorPicAdapter);
                        collectorPicAdapter.setDeleteIcon(true);
                        collectorPicAdapter.setPathList(collectorPath);
                    }else {
                        viewHold.rvPicCollector.setVisibility(View.GONE);
                    }

                    break;
                }
            }
        }

        LinearLayoutManager manager1 = new LinearLayoutManager(mContext);
        manager1.setOrientation(LinearLayoutManager.HORIZONTAL);         // 水平
        viewHold.rvPic.setLayoutManager(manager1);

        PicAdapter picAdapter = new PicAdapter(mContext, new ArrayList<String>(), new PicAdapter.PicListener() {
            @Override
            public void onDelete(int index, String path) {

            }

            @Override
            public void onPreView(int index, String path, Info info) {

                mFinishPhotoListener.onPreView(index, path, info);

            }
        });

        viewHold.rvPic.setAdapter(picAdapter);
        picAdapter.setDeleteIcon(true);
        picAdapter.setPathList(path);




        return convertView;
    }

    public void setCollectorNumberBeanList(ArrayList<CollectorNumberBean> beanList){
        if(mCollectorNumberBean == null)
            mCollectorNumberBean = new ArrayList<>();

        mCollectorNumberBean.clear();
        mCollectorNumberBean.addAll(beanList);
        notifyDataSetChanged();
    }

    class ViewHold{

        ///** 序号 */
        //public TextView tvSequenceNumber;
        /** 用户编号 */
        public TextView tvUserNumber;
        /** 用户名称 */
        public TextView tvUserName;
        /** 用户地址 */
        public TextView tvUserAddr;
        /** 旧资产编码 */
        public TextView tvOldAssetNumbers;
        /** 旧表表地址 */
        public TextView tvOldAddr;
        /** 旧表止码 */
        public TextView tvOldElectricity;
        /** 新表资产编号 */
        public TextView tvNewAssetNumbersScan;
        /** 新表表地址 */
        public TextView tvNewAddr;
        /** 新表止码 */
        public TextView tvNewElectricity;
        /** 加装的采集器 */
        public TextView tvNewCollector;

//        /** 图片 */
//        public PhotoView pvItemImage;
        /** 图片列表 -- 电表的 */
        public RecyclerView rvPic;
        /** 图片列表 -- 采集器的 -- 包裹的布局 */
        public LinearLayout lLayoutPicCollector;
        /** 图片列表 -- 采集器的 */
        public RecyclerView rvPicCollector;


        public ViewHold(View view) {

            //tvSequenceNumber = (TextView) view.findViewById(R.id.tv_sequenceNumber);
            tvUserNumber = (TextView) view.findViewById(R.id.tv_userNumber);
            tvUserName = (TextView) view.findViewById(R.id.tv_userName);
            tvUserAddr = (TextView) view.findViewById(R.id.tv_userAddr);
            tvOldAssetNumbers = (TextView) view.findViewById(R.id.tv_oldAssetNumbers);
            tvOldAddr = (TextView) view.findViewById(R.id.tv_oldAddr);
            tvOldElectricity = (TextView) view.findViewById(R.id.tv_oldElectricity);
            tvNewAssetNumbersScan = (TextView) view.findViewById(R.id.tv_newAssetNumbersScan);
            tvNewAddr = (TextView) view.findViewById(R.id.tv_newAddr);
            tvNewElectricity = (TextView) view.findViewById(R.id.tv_newElectricity);
            tvNewCollector = (TextView) view.findViewById(R.id.tv_newCollector);
            //pvItemImage = (PhotoView) view.findViewById(R.id.pv_item_image);
            rvPic = (RecyclerView) view.findViewById(R.id.rv_pic);
            lLayoutPicCollector = (LinearLayout) view.findViewById(R.id.lLayout_newCollector);
            rvPicCollector = (RecyclerView) view.findViewById(R.id.rv_pic_newCollector);
        }


    }

    public interface FinishPhotoListener{
        void onPreView(int index, String path, Info info);
    }
}
