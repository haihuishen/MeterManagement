package com.zh.metermanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zh.metermanagement.R;
import com.zh.metermanagement.bean.MeterBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/30.
 */
public class UnFinishedAdapter extends BaseAdapter {

    private ArrayList<MeterBean> mBeanArrayList = new ArrayList<>();
    private Context mContext;

    public UnFinishedAdapter(Context context, ArrayList<MeterBean> meterBeanList){

        mContext = context;
        mBeanArrayList = meterBeanList;
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
            convertView = inflater.inflate(R.layout.item_lv_unfinish,parent,false);      // listview中每一项的布局

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

        return convertView;
    }


    class ViewHold{

        public TextView tvSequenceNumber;
        public TextView tvUserNumber;
        public TextView tvUserName;
        public TextView tvUserAddr;
        public TextView tvOldAssetNumbers;


        public ViewHold(View view) {

            tvSequenceNumber = (TextView) view.findViewById(R.id.tv_sequenceNumber);
            tvUserNumber = (TextView) view.findViewById(R.id.tv_userNumber);
            tvUserName = (TextView) view.findViewById(R.id.tv_userName);
            tvUserAddr = (TextView) view.findViewById(R.id.tv_userAddr);
            tvOldAssetNumbers = (TextView) view.findViewById(R.id.tv_oldAssetNumbers);

        }


    }
}
