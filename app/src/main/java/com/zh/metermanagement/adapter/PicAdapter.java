package com.zh.metermanagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.zh.metermanagement.R;
import com.zh.metermanagement.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加"附件"界面的 -- "图片"、"视频"、"文档" -- 对应的"适配器"
 *
 *
 *  RecyclerView 再生控件 -- 相当于 自定义ListView
 */
public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {


    /**
     * 监听接口 -- 删除
     */
    public interface PicListener {
        /**
         * 删除资源
         * @param index
         * @param path
         */
        void onDelete(int index, String path);


        /**
         * 预览资源
         * @param index
         * @param path
         */
        void onPreView(int index, String path, Info info);
    }


    /** 资源文件"路径" -- 列表 */
    private List<String> pathList = new ArrayList<>();
    private Context context;

    /** "删除"监听接口 -- 子类实现 */
    private PicListener listener;


    private boolean isDeleteIconhide = false;
    /**
     * 构造函数
     *
     * @param context
     * @param pathList          资源文件"路径" -- 列表
     * @param l                 "删除"监听接口 -- 子类实现
     */
    public PicAdapter(Context context, List<String> pathList, PicListener l) {
        this.pathList = pathList;
        this.context = context;
        this.listener = l;
    }

    @Override
    public int getItemCount() {
        return pathList != null ? pathList.size() : 0;
    }


    // 初始化"控件"
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_item, null);
        ViewHolder holder = new ViewHolder(view);
        holder.initView(view);

        return holder;
    }


    // 为控件 -- 绑定数据 -- 绑定事件
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String path = pathList.get(position);

        final Bitmap thumbNail = createThumbnail(path);          // 获取"图片/视频"的 略缩图

        if (thumbNail != null) {                           // 有"略缩图"
            holder.pv.setImageBitmap(thumbNail);
        } else {                                           // 没有"略缩图"
            holder.pv.setImageResource(R.mipmap.no_preview_picture);
        }

        if(isDeleteIconhide)
            holder.deleteIv.setVisibility(View.GONE);

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {             // 删除资源
            @Override
            public void onClick(View view) {

                //pathList.remove(position);                  // 列表中 -- 去掉"图片、视频、文件"
                //notifyDataSetChanged();                     // 刷新控件

                if (listener != null) {
                    listener.onDelete(position, path);      // 在Activity修改数据 -- 写到数据库之类 -- 子类实现
                }
            }
        });


        holder.pv.setOnClickListener(new View.OnClickListener() {                   // 预览图片/视频
            @Override
            public void onClick(View v) {
                Info info = holder.pv.getInfo();
                listener.onPreView(position, path, info);
            }
        });
    }


    /**
     * 暴露给子类调用
     *
     * @param pathStr
     */
    public void addPath(String pathStr) {
        if (pathList == null) {
            pathList = new ArrayList<>();
        }

        pathList.add(pathStr);
        notifyDataSetChanged();
    }

    /**
     * 设置图片
     */
    public void setPathList(String pathLists) {
        if (pathList == null) {
            pathList = new ArrayList<>();
        }

        pathList.clear();
        if (!TextUtils.isEmpty(pathLists)){
            for (String path : pathLists.split(",")) {
                if (StringUtils.isNotEmpty(path))
                    pathList.add(path);
            }
        }

        notifyDataSetChanged();
    }

    /**
     * 设置图片
     */
    public void setPathArrayList(List pathLists){
        if (pathList == null) {
            pathList = new ArrayList<>();
        }

        pathList.clear();
        pathList.addAll(pathLists);

        notifyDataSetChanged();
    }



    /**
     * 清空图片列表
     */
    public void clearPathList(){
        if (pathList == null) {
            pathList = new ArrayList<>();
        }

        pathList.clear();
        notifyDataSetChanged();
    }

    /**
     * 隐藏删除图片, 要在new之前设置
     * @param isHide
     */
    public void setDeleteIcon(boolean isHide){
        isDeleteIconhide = isHide;
        //notify();
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



    public class ViewHolder extends RecyclerView.ViewHolder {
        /** 资源图片 */
        public PhotoView pv;
        /** 资源图片 -- 右上角的"删除" */
        public ImageView deleteIv;

        public ViewHolder(View itemView) {
            super(itemView);
        }


        public void initView(View rootView) {
            this.pv = (PhotoView) rootView.findViewById(R.id.pv_pic_item);
            this.deleteIv = (ImageView) rootView.findViewById(R.id.iv_pic_item_delete);
        }
    }
}
