package com.zh.metermanagement.activity;

import android.content.Context;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.zh.metermanagement.R;
import com.zh.metermanagement.activity.base.BaseActivity;
import com.zh.metermanagement.adapter.TabAdapter;
import com.zh.metermanagement.fragment.SearchByBSHFragment;
import com.zh.metermanagement.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    /** 标题 */
    TextView mTvTitle;
    /** 返回按钮 -- 按钮 */
    Button mBtnBack;
    /** 菜单按钮 -- 按钮 */
    Button mBtnMenu;


    private ViewPager mViewPager;
    private List<Fragment> mTabContents;
    private TabAdapter mTabAdapter;

    private TabLayout mTabLayout;

    private ArrayList<String> mTitleList;

    /** 三大标签中正在显示的Fragment */
    private Fragment mFragment;




    @Override
    public int getContentLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void initTitleListener(TextView tvTitle, Button btnBack, Button btnMenu) {
        mTvTitle = tvTitle;
        mBtnBack = btnBack;
        mBtnMenu = btnMenu;

        mBtnBack.setOnClickListener(this);
        // mBtnMenu.setOnClickListener(this);
    }

    @Override
    public void initTitleData(TextView tvTitle, Button btnBack, Button btnMenu) {
        mTvTitle.setVisibility(View.VISIBLE);
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnMenu.setVisibility(View.GONE);

        mTvTitle.setText("查询");
    }

    @Override
    public void initView() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

        mTabContents = new ArrayList<>();
        mTabContents.add(SearchByBSHFragment.newInstance());

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        mTitleList = new ArrayList<>();
        //mTitleList.add("户号");
        mTitleList.add("");

        mTabAdapter = new TabAdapter(getSupportFragmentManager(), mTabContents, mTitleList);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.setOffscreenPageLimit(1);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_back_left:

                finish();
                break;
        }
    }

}
