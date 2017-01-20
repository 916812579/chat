package org.free.chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.free.chat.R;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * Created by Administrator on 2017/1/6.
 */
public abstract class ImageTextAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    protected Context mContext;

    public ImageTextAdapter(Context context, List<T> datas) {
        if (datas == null) {
            this.mDatas = new ArrayList<>();
        } else {
            this.mDatas = datas;
        }
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.listview_image_text, null);
            holder = new ViewHolder();
            holder.imageView = (BGABadgeImageView)convertView.findViewById(R.id.iv_image_textview_image);
            holder.textView = (TextView)convertView.findViewById(R.id.iv_image_textview_textview);
            convertView.setTag(holder);
        } else {
            holder =  (ViewHolder)convertView.getTag();
        }

        setValues(mDatas.get(position), holder.imageView, holder.textView);

        return convertView;
    }

    /**
     * 界面赋值
     * @param t
     * @param imageView
     * @param textView
     */
    protected abstract void setValues(T t, ImageView imageView, TextView textView);

    class ViewHolder {
        BGABadgeImageView imageView;
        TextView textView;
    }
}
