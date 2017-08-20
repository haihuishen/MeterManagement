package com.zh.metermanagement.fragment.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.zh.metermanagement.R;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/18.
 */

public abstract class BaseFragment extends Fragment {


    //---------------------------------------------------

    public Bitmap mBitmap;

    public View mLlayoutParent;
    public View mIvBg;
    /** 放大后存放图片的控件*/
    public PhotoView mPvBgImg;
    public Info mInfo;

    public AlphaAnimation in;
    public AlphaAnimation out;

    //----------------------------------------------------

    /** 将 布局填充成 "控件"， 将控件加载到"界面"中 */
    public abstract int getContentLayout();

    public abstract void initView(View view);
    public abstract void initListener();
    public abstract void initData();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);

        initView(view);
        initListener();
        initData();


        //------------------------------------------------------------

        view.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(backlistener);
        return view;
    }


    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作

                    if(mLlayoutParent.getVisibility() == View.VISIBLE && mIvBg.getVisibility() == View.VISIBLE){   // 缩小、隐藏那个预览布局
                        mIvBg.startAnimation(out);
                        //setTitleIsShow(View.VISIBLE);
                        mPvBgImg.animaTo(mInfo, new Runnable() {
                            @Override
                            public void run() {
                                mLlayoutParent.setVisibility(View.GONE);

                            }
                        });
                        return true;
                    }
                }
            }
            return false;
        }
    };

}
