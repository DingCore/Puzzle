package com.example.dingcore.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by DingCore on 2018/1/14.
 */

public class PuzzleLayout extends RelativeLayout implements OnClickListener {

    private int column = 3;
    private int width;
    private int padding;
    private ImageView[] puzzleItems;
    private int itemWidth;
    private int margin = 3;
    private Bitmap bitmap;
    private List<ImagePiece> itemBitmaps;
    private boolean once;
    private ImageView first;
    private ImageView second;
    private SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC,5);
    private int music = sp.load(getContext(),R.raw.click,1);
    private CallBack callBack;
    private boolean isChange = false;

    public PuzzleLayout(Context context){
        this(context,null);
    }
    public PuzzleLayout(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public PuzzleLayout(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,margin,getResources().getDisplayMetrics());
        padding = min(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom());
    }
    public int min(int a,int b,int c,int d){
        int[] arr = {a,b,c,d};
        Arrays.sort(arr);
        return arr[0];
    }

    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(int id) {
        this.bitmap = BitmapFactory.decodeResource(getResources(),id);
    }

    public void setBitmap(String pathName) {
        this.bitmap = BitmapFactory.decodeFile(pathName);
    }

    public void setBitmap(Uri imageUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        options.inSampleSize = 5;
        try {
            bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri),null,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.bitmap = bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = Math.min(getMeasuredHeight(),getMeasuredWidth());
        if(!once){
            initBitmap();
            initItem();
        }
        once = true;
        setMeasuredDimension(width,width);
    }
    private void initBitmap(){
         itemBitmaps = ImageSplitter.splitImage(bitmap,column);
        Collections.sort(itemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }
    private void initItem(){
        itemWidth = (width - padding * 2 - margin * (column - 1)) / column;
        puzzleItems = new ImageView[column * column];
        for(int i=0;i<puzzleItems.length;i++){
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(itemBitmaps.get(i).getBitmap());
            puzzleItems[i] = item;
            item.setId(i + 1);
            item.setTag(i + "_" + itemBitmaps.get(i).getIndex());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(itemWidth,itemWidth);
            if((i + 1) % column != 0){  //不是最右一列，就有右边距
                lp.rightMargin = margin;
            }
            if(i % column !=0){  //不是最左一列，就摆在上一个编号的右边
                lp.addRule(RelativeLayout.RIGHT_OF,puzzleItems[i - 1].getId());
            }
            if((i + 1) > column){  //不是最上一行，就有上边距，且摆在i-column编号的下边
                lp.topMargin = margin;
                lp.addRule(RelativeLayout.BELOW,puzzleItems[i - column].getId());
            }
            addView(item,lp);
        }
    }

    @Override
    public void onClick(View v) {
        sp.play(music,1,1,0,0,1);
        if(first == v){
            first.setColorFilter(null);
            first = null;
            return;
        }
        if(first == null){
            first = (ImageView) v;
            first.setColorFilter(Color.parseColor("#55FF0000"));
        }else{
            second = (ImageView) v;
            exchangeView();
            if (callBack != null) {
                callBack.postExec();
            }
        }
    }
    private void exchangeView(){
        first.setColorFilter(null);
        String firstTag = (String) first.getTag();
        String secondTag = (String) second.getTag();
        String[] firstImageIndex = firstTag.split("_");
        String[] secondImageIndex = secondTag.split("_");
        first.setImageBitmap(itemBitmaps.get(Integer.parseInt(secondImageIndex[0])).getBitmap());
        second.setImageBitmap(itemBitmaps.get(Integer.parseInt(firstImageIndex[0])).getBitmap());
        first.setTag(secondTag);
        second.setTag(firstTag);
        first = second = null;
        isChange = true;
    }
    public boolean checkSucess(){
        boolean isSuccess = false;
        boolean flag = true;
        for (int i = 0;i < puzzleItems.length;i++){
            ImageView imageView = puzzleItems[i];
            int x = getImageIndexByTag((String) imageView.getTag());
            if(x != i){
                flag = false;
                Log.d("PuzzleLayout",Integer.toString(x));
                Log.d("PuzzleLayout", Boolean.toString(flag));
                break;
            }
        }
        if (flag) {
            isSuccess = true;
        }
        return isSuccess;
    }
    private int getImageIndexByTag(String tag){
        String[] s = tag.split("_");
        return Integer.parseInt(s[1]);
    }

    public void cleanConfig() {
        itemBitmaps.clear();
        bitmap = null;
    }

}
