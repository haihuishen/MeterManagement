package com.zh.metermanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zh.metermanagement.R;
import com.zh.metermanagement.bean.AssetNumberBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/30.
 */
public class AssetsNumberMismatchesAdapter extends BaseAdapter {

    private List<AssetNumberBean> mBeanArrayList = new ArrayList<>();
    private Context mContext;

    public AssetsNumberMismatchesAdapter(Context context, List<AssetNumberBean> assetNumberBeanList){

        mContext = context;
        mBeanArrayList = assetNumberBeanList;
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

        ViewHold viewHold;
        if(convertView == null){                // 拿缓存
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.item_listview,parent,false);      // listview中每一项的布局

            viewHold = new ViewHold(convertView);

            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.tvAssetNumbers.setText(mBeanArrayList.get(position).getAssetNumbers());

        return convertView;
    }


    class ViewHold{

        public TextView tvAssetNumbers;


        public ViewHold(View view) {

            tvAssetNumbers = (TextView) view.findViewById(R.id.tv_lv_item);

        }


    }
}
