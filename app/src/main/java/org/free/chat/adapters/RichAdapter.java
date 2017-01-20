package org.free.chat.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.free.chat.R;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * Created by Administrator on 2017/1/10.
 */
public abstract class RichAdapter<T> extends BaseAdapter {
    List<T> mDatas;
    Context mContext;

    public RichAdapter(Context context, List<T> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.mDatas = datas;
        this.mContext = context;
    }

    public List<T> getDatas() {
        return mDatas;
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
            convertView = View.inflate(mContext, R.layout.listview_rich, null);
            holder = new ViewHolder();
            holder.imageView = (BGABadgeImageView)convertView.findViewById(R.id.iv_image_textview_image);
            holder.title = (TextView)convertView.findViewById(R.id.tv_title);
            holder.extraTitle = (TextView)convertView.findViewById(R.id.tv_extra_title);
            holder.subTitle = (TextView)convertView.findViewById(R.id.tv_sub_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        T t = mDatas.get(position);
        setValues(t, holder.imageView, holder.title, holder.extraTitle, holder.subTitle);
        return convertView;
    }

    public abstract void setValues(T t, BGABadgeImageView imageView, TextView title, TextView extraTitle, TextView subTitle);

    class ViewHolder {
        BGABadgeImageView imageView;
        TextView title;
        TextView extraTitle;
        TextView subTitle;
    }
}
