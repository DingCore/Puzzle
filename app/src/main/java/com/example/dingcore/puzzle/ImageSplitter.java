package com.example.dingcore.puzzle;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DingCore on 2018/1/14.
 */

public class ImageSplitter {
    public static List<ImagePiece> splitImage(Bitmap bitmap,int piece){
        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = Math.min(width,height)/piece;
        for(int i=0;i<piece;i++){
            for(int j=0;j<piece;j++){
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * piece);
                int x = j * pieceWidth;
                int y = i * pieceWidth;
                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,pieceWidth,pieceWidth));
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }
    public static List<ImagePiece> splitImage2(Bitmap bitmap,int piece){
        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = Math.min(width,height)/piece;
        for(int i=0;i<piece;i++){
            for(int j=0;j<piece;j++){
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j + i * piece);
                int x = j * pieceWidth;
                int y = i * pieceWidth;
                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,pieceWidth,pieceWidth));
                imagePieces.add(imagePiece);
            }
        }
        Bitmap bitmap1 = Bitmap.createBitmap(pieceWidth,pieceWidth,Bitmap.Config.ARGB_8888);
        bitmap1.eraseColor(Color.parseColor("#FFFFFF"));
        imagePieces.get(imagePieces.size() - 1).setBitmap(bitmap1);
        return imagePieces;
    }
}
