package com.zh.metermanagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.zh.metermanagement.R;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.bean.MeterBean1;
import com.zh.metermanagement.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/30.
 */
public class MeterContentAdapter1 extends BaseAdapter {

    private ArrayList<MeterBean1> mMeterBeanList = new ArrayList<>();
    private Context mContext;
    private MeterContentListener mMeterContentListener;

    public MeterContentAdapter1(Context context, ArrayList<MeterBean1> mMeterBeanList, MeterContentListener meterContentListener){

        mContext = context;
        mMeterBeanList = mMeterBeanList;
        mMeterContentListener = meterContentListener;
    }

    @Override
    public int getCount() {
        return mMeterBeanList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHold viewHold;
        if(convertView == null){                // 拿缓存
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.item_lv_meter_content,parent,false);      // listview中每一项的布局

            viewHold = new ViewHold(convertView);

            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }

        Bitmap bitmap;

        viewHold.tvAssetNumbers.setText(mMeterBeanList.get(position).getOldAssetNumbers());
        final String path = mMeterBeanList.get(position).getMeterPicPath();
        if(StringUtils.isNotEmpty(path)){
            File file = new File(path);
            if(file.exists()){
                bitmap =  createThumbnail(path);
                if(bitmap != null){
                    viewHold.rLayoutPhoto.setVisibility(View.VISIBLE);
                    viewHold.ibCameraItem.setVisibility(View.GONE);
                    viewHold.pvPicItem.setImageBitmap(bitmap);
                }else {
                    viewHold.rLayoutPhoto.setVisibility(View.GONE);
                    viewHold.ibCameraItem.setVisibility(View.VISIBLE);
                    //mMeterContentListener.onDeletePic(position, path);
                }
            }else {
                viewHold.rLayoutPhoto.setVisibility(View.GONE);
                viewHold.ibCameraItem.setVisibility(View.VISIBLE);
                //mMeterContentListener.onDeletePic(position, path);
            }
        }else{
            viewHold.rLayoutPhoto.setVisibility(View.GONE);
            viewHold.ibCameraItem.setVisibility(View.VISIBLE);
        }

        viewHold.ibCameraItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeterContentListener.onCamera(position);
            }
        });
        viewHold.pvPicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info info = viewHold.pvPicItem.getInfo();
                mMeterContentListener.onPreView(position, path, info);

            }
        });
        viewHold.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeterContentListener.onDeletePic(position, path);
            }
        });

        viewHold.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMeterContentListener.onDeleteMeter(position, path);
            }
        });


        return convertView;
    }


    class ViewHold{

        public TextView tvAssetNumbers;
        /** 照相机 */
        public ImageButton ibCameraItem;
        /** 照片布局 */
        public RelativeLayout rLayoutPhoto;
        /** 照片 */
        public PhotoView pvPicItem;
        /** 删除图片 */
        public ImageView ivDeleteItem;
        /** 删除表 */
        public Button btnDel;



        public ViewHold(View view) {

            tvAssetNumbers = (TextView) view.findViewById(R.id.tv_assetNumbers);

            ibCameraItem = (ImageButton) view.findViewById(R.id.ib_camera_item);
            rLayoutPhoto = (RelativeLayout) view.findViewById(R.id.rlayout_photo);
            pvPicItem = (PhotoView) view.findViewById(R.id.pv_pic_item);
            ivDeleteItem = (ImageView) view.findViewById(R.id.iv_pic_item_delete);

            btnDel = (Button) view.findViewById(R.id.btn_del);


        }
    }



    /**
     * 添加一个项
     *
     * @param meterBean
     */
    public void addItem(MeterBean1 meterBean) {
        if (mMeterBeanList == null) {
            mMeterBeanList = new ArrayList<>();
        }

        mMeterBeanList.add(meterBean);
        notifyDataSetChanged();
    }

    /**
     * 设置全部item
     */
    public void setItemList(ArrayList<MeterBean1> meterBeanList){
        if (mMeterBeanList == null) {
            mMeterBeanList = new ArrayList<>();
        }

        mMeterBeanList.clear();
        mMeterBeanList.addAll(meterBeanList);
        notifyDataSetChanged();
    }


    /**
     * 清空item列表
     */
    public void clearPathList(){
        if (mMeterBeanList == null) {
            mMeterBeanList = new ArrayList<>();
        }

        mMeterBeanList.clear();
        notifyDataSetChanged();
    }

    /**
     * 生成"图片/视频" -- 略缩图  -- 其他文件返回一个"null"Bitmap
     *
     * @param path      "图片/视频" 路径
     * @return
     */
    private Bitmap createThumbnail(String path) {
        Bitmap bitmap = null;
        if(StringUtils.isNotEmpty(path)) {
            if (path.indexOf(".jpg") > 0) {

                Bitmap bm = BitmapFactory.decodeFile(path);
                bitmap = ThumbnailUtils.extractThumbnail(bm, 128, 128);     // "图片"生成略缩图
            }
        }

        return bitmap;
    }



    /**
     * 监听接口 -- 删除
     */
    public interface MeterContentListener {
        /**
         * 删除图片
         * @param position
         * @param path
         */
        void onDeletePic(int position, String path);


        /**
         * 预览资源
         * @param position
         * @param path
         */
        void onPreView(int position, String path, Info info);

        /**
         * 拍照
         * @param position

         */
        void onCamera(int position);

        /**
         * 删除表
         * @param position
         * @param path
         */
        void onDeleteMeter(int position, String path);


    }
}
