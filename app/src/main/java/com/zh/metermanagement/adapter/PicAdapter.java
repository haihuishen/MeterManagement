package com.zh.metermanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zh.metermanagement.R;
import com.zh.metermanagement.bean.AssetNumberBean;
import com.zh.metermanagement.utils.ImageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/30.
 */
public class PicAdapter extends BaseAdapter {

    private List<String> mPathList = new ArrayList<>();
    private Context mContext;

    public PicAdapter(Context context, List<String> pathList){

        mContext = context;
        mPathList = pathList;
    }

    @Override
    public int getCount() {
        return mPathList.size();
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

        ViewHold viewHold;
        if(convertView == null){                // 拿缓存
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.item_lv_pic,parent,false);      // listview中每一项的布局

            viewHold = new ViewHold(convertView);

            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }

        viewHold.ivPic.setImageBitmap(ImageFactory.getBitmap(mPathList.get(position)));

        return convertView;
    }


    class ViewHold{

        public ImageView ivPic;


        public ViewHold(View view) {

            ivPic = (ImageView) view.findViewById(R.id.iv_lv_time);

        }


    }
}
