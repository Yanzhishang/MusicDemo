package com.yq.yzs.musicdemo21;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @作者 :Yzs
 * 2017/9/11.
 * 描述 :
 * 实现:
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "MyRecycler";
    ArrayList<Data> list;
    public static int         mPosition;


    public MyAdapter(ArrayList<Data> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //实例化子项布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        //创建MyHolder的实类
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    int mSelect = 0;   //选中项

    public void changeSelected(int positon) { //刷新方法
        if (positon != mSelect) {
            mSelect = positon;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Data data = list.get(position);
        holder.tv_singer.setText(data.getName());
        holder.tv_name.setText(data.getTitle());
        holder.tv_time.setText(data.getTime());
        if (mSelect == position) {
            holder.music_image.setVisibility(View.VISIBLE);  //选中项背景
        } else {
            holder.music_image.setVisibility(View.GONE);   //其他项背景
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView music_image;
        TextView  tv_name;
        TextView  tv_singer;
        TextView  tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            music_image = itemView.findViewById(R.id.music_image);
            tv_name = itemView.findViewById(R.id.name);
            tv_singer = itemView.findViewById(R.id.singer);
            tv_time = itemView.findViewById(R.id.time);
        }
    }
}
