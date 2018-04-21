package com.example.dingcore.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by DingCore on 2018/4/11.
 */

public class PuzzleLayout2 extends RelativeLayout implements View.OnClickListener {

    private int column = 3;
    private int width;
    private int padding;
    private ImageView[] puzzleItems;
    private int itemWidth;
    private int margin = 3;
    private Bitmap bitmap;
    private List<ImagePiece> itemBitmaps;
    private boolean once;
    private ImageView click;
    private ImageView blank;
    private int blankId;

    public PuzzleLayout2(Context context){
        this(context,null);
    }
    public PuzzleLayout2(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public PuzzleLayout2(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,margin,getResources().getDisplayMetrics());
        padding = min(getPaddingLeft(),getPaddingTop(),getPaddingRight(),getPaddingBottom());
    }
    public int min(int a,int b,int c,int d){
        int[] arr = {a,b,c,d};
        Arrays.sort(arr);
        return arr[0];
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
        itemBitmaps = ImageSplitter.splitImage2(bitmap,column);
        Collections.sort(itemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0;i < itemBitmaps.size();i++) {
            int x = itemBitmaps.get(i).getIndex();
            Log.d("PuzzleLayout2",Integer.toString(x) );
            if (x == itemBitmaps.size() - 1) {
                blankId = i + 1;
                data.add(0);
            } else {
                data.add(x + 1);
            }
        }
        if (canSolve(data)) {
            for (int i = 0;i < data.size();i++) {
                int x = data.get(i);
                Log.d("PuzzleLayout2","s:"+ Integer.toString(x) );
            }
            return;
        } else {
            initBitmap();
        }
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
        click = (ImageView) v;
        int position = v.getId();
        if (isMoveable(position)) {
            exchangeView();
        }
    }

    private boolean isMoveable(int position) {
        if (Math.abs(blankId - position) == column) {
            return true;
        }
        if(((blankId - 1) / column == (position - 1) / column) && Math.abs(blankId - position) == 1) {
            return true;
        }
        return false;
    }

    private void exchangeView() {
        blank = (ImageView) puzzleItems[blankId - 1];
        String blankTag = (String) blank.getTag();
        String clickTag = (String) click.getTag();
        String[] blankImageIndex = blankTag.split("_");
        String[] clickImageIndex = clickTag.split("_");
        blank.setImageBitmap(itemBitmaps.get(Integer.parseInt(clickImageIndex[0])).getBitmap());
        click.setImageBitmap(itemBitmaps.get(Integer.parseInt(blankImageIndex[0])).getBitmap());
        blank.setTag(clickTag);
        click.setTag(blankTag);
        blankId = click.getId();
        click = blank =  null;
    }

    public boolean checkSucess(){
        boolean isGameSuccess = false;
        boolean isSuccess = true;
        for(int i=0;i<puzzleItems.length;i++){
            ImageView imageView = puzzleItems[i];
            if(getImageIndexByTag((String) imageView.getTag()) != i){
                isSuccess = false;
            }
            if(isSuccess){
                isGameSuccess = true;
            }
        }
        return isGameSuccess;
    }

    private int getImageIndexByTag(String tag){
        String[] s = tag.split("_");
        return Integer.parseInt(s[1]);
    }

    private boolean canSolve(List<Integer> data) {
        //若宽度为奇数，倒置和需为偶数方有解
        //若宽度为偶数，空格位于从下往上数的奇数行中时，倒置和为偶数方有解
        //若宽度为偶数，空格位于从下往上数的偶数行中时，倒置和为奇数方有解
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            //从底往上数，空格位于奇数行
            if (((int) (blankId - 1) / column) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
                return getInversions(data) % 2 == 1;
            }
        }
    }

    private int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0;i < data.size();i++) {
            for (int j = i + 1;j < data.size();j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++; //计算数组中每个数的倒置变量值
                }
            }
            inversions += inversionCount; //计算倒置变量和
            inversionCount = 0;
        }
        return inversions;
    }

    public void cleanConfig() {
        itemBitmaps.clear();
        bitmap = null;
    }
}
