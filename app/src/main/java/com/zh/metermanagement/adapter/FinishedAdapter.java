package com.zh.metermanagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.zh.metermanagement.R;
import com.zh.metermanagement.bean.MeterBean;
import com.zh.metermanagement.config.Constant;
import com.zh.metermanagement.utils.ImageFactory;

import java.io.File;
import java.util.ArrayList;

import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2017/4/30.
 */
public class FinishedAdapter extends BaseAdapter {

    private ArrayList<MeterBean> mBeanArrayList = new ArrayList<>();
    private Context mContext;

    View mParent;
    View mBg;
    PhotoView mPhotoView;
    Info mInfo;

    AlphaAnimation mIn;
    AlphaAnimation mOut;

    PhotoViewInfo mPhotoViewInfo;

    public FinishedAdapter(Context context, ArrayList<MeterBean> meterBeanList,
                           View parent,
                           View bg,
                           PhotoView photoView,
                           Info info,
                           AlphaAnimation in,
                           AlphaAnimation out,
                           PhotoViewInfo photoViewInfo){

        mContext = context;
        mBeanArrayList = meterBeanList;

        mParent = parent;
        mBg = bg;
        mPhotoView = photoView;
        mInfo = info;
        mIn = in;
        mOut = out;
        mPhotoViewInfo = photoViewInfo;
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

        viewHold.tvSequenceNumber.setText(mBeanArrayList.get(position).getSequenceNumber());
        viewHold.tvUserNumber.setText(mBeanArrayList.get(position).getUserNumber());
        viewHold.tvUserName.setText(mBeanArrayList.get(position).getUserName());
        viewHold.tvUserAddr.setText(mBeanArrayList.get(position).getUserAddr());
        viewHold.tvOldAssetNumbers.setText(mBeanArrayList.get(position).getOldAssetNumbers());

        viewHold.tvOldAddr.setText(mBeanArrayList.get(position).getOldAddr());
        viewHold.tvOldElectricity.setText(mBeanArrayList.get(position).getOldElectricity());
        viewHold.tvNewAssetNumbersScan.setText(mBeanArrayList.get(position).getNewAssetNumbersScan());
        viewHold.tvNewAddr.setText(mBeanArrayList.get(position).getNewAddr());
        viewHold.tvNewElectricity.setText(mBeanArrayList.get(position).getNewElectricity());

        File file = new File(Constant.IMAGE_PATH + mBeanArrayList.get(position).getPicPath());
        final Bitmap bitmap;
        if(file.exists())
            viewHold.pvItemImage.setImageBitmap(bitmap = ImageFactory.getBitmap(Constant.IMAGE_PATH + mBeanArrayList.get(position).getPicPath()));
        else
            viewHold.pvItemImage.setImageBitmap(bitmap = ImageFactory.getBitmap(Constant.CACHE_IMAGE_PATH + "no_preview_picture.png"));



        viewHold.pvItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInfo =  viewHold.pvItemImage.getInfo();
                mPhotoViewInfo.whenOnClickSetPhotoViewInfo(mInfo);
                mPhotoView.setImageBitmap(bitmap);
                mBg.startAnimation(mIn);
                mBg.setVisibility(VISIBLE);
                mParent.setVisibility(VISIBLE);
                mPhotoView.animaFrom(mInfo);


            }
        });
        return convertView;
    }


    class ViewHold{

        /** 序号 */
        public TextView tvSequenceNumber;
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
        /** 图片 */
        public PhotoView pvItemImage;


        public ViewHold(View view) {

            tvSequenceNumber = (TextView) view.findViewById(R.id.tv_sequenceNumber);
            tvUserNumber = (TextView) view.findViewById(R.id.tv_userNumber);
            tvUserName = (TextView) view.findViewById(R.id.tv_userName);
            tvUserAddr = (TextView) view.findViewById(R.id.tv_userAddr);
            tvOldAssetNumbers = (TextView) view.findViewById(R.id.tv_oldAssetNumbers);
            tvOldAddr = (TextView) view.findViewById(R.id.tv_oldAddr);
            tvOldElectricity = (TextView) view.findViewById(R.id.tv_oldElectricity);
            tvNewAssetNumbersScan = (TextView) view.findViewById(R.id.tv_newAssetNumbersScan);
            tvNewAddr = (TextView) view.findViewById(R.id.tv_newAddr);
            tvNewElectricity = (TextView) view.findViewById(R.id.tv_newElectricity);
            pvItemImage = (PhotoView) view.findViewById(R.id.pv_item_image);
        }


    }

    public interface PhotoViewInfo{
        void whenOnClickSetPhotoViewInfo(Info info);
    }
}
