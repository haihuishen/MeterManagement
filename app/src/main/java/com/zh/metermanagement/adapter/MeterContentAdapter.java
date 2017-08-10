package com.zh.metermanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zh.metermanagement.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/30.
 */
public class MeterContentAdapter extends BaseAdapter {

    private ArrayList<String> mStrList = new ArrayList<>();
    private Context mContext;
    private AdapterDel mAdapterDel;

    public MeterContentAdapter(Context context, ArrayList<String> strList, AdapterDel adapterDel){

        mContext = context;
        mStrList = strList;
        mAdapterDel = adapterDel;
    }

    @Override
    public int getCount() {
        return mStrList.size();
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

        ViewHold viewHold;
        if(convertView == null){                // 拿缓存
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.item_lv_meter,parent,false);      // listview中每一项的布局

            viewHold = new ViewHold(convertView);

            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }

        viewHold.tvAssetNumbers.setText(mStrList.get(position));
        viewHold.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapterDel.del(position);
            }
        });


        return convertView;
    }


    class ViewHold{

        public TextView tvAssetNumbers;
        public Button btnDel;



        public ViewHold(View view) {

            tvAssetNumbers = (TextView) view.findViewById(R.id.tv_assetNumbers);
            btnDel = (Button) view.findViewById(R.id.btn_del);


        }
    }


    public interface AdapterDel{

        void del(int position);

    }
}
