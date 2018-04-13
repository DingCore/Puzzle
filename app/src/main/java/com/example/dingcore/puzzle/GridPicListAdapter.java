package com.example.dingcore.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;

import java.util.List;

/**
 * Created by DingCore on 2018/3/28.
 */

public class GridPicListAdapter extends BaseAdapter {

    private List<Bitmap> picList;
    private Context context;

    public GridPicListAdapter(Context context,List<Bitmap> picList) {
        this.context = context;
        this.picList = picList;
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int position) {
        return picList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView pic_item = null;
        if(convertView == null) {
            pic_item = new ImageView(context);
            pic_item.setLayoutParams(new GridView.LayoutParams(200,300)); //设置布局图片
            pic_item.setScaleType(ImageView.ScaleType.FIT_XY);  //设置显示比例类型
        } else {
            pic_item = (ImageView) convertView;
        }
        pic_item.setBackgroundColor(Color.parseColor("#000000"));
        pic_item.getBackground().setAlpha(0);
        pic_item.setImageBitmap(picList.get(position));
        return pic_item;
    }
}
