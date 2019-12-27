package hk.polyu.eie.eie3109.animationassignment;

import android.graphics.Bitmap;

public class Coordinates {

    private int x = 100;
    private int y = 100;
    private Bitmap bitmap;

    public Coordinates(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void setX(int x){
        this.x = x - bitmap.getWidth() / 2;
    }

    public void setY(int y){
        this.y = y - bitmap.getWidth() / 2;
    }

    public int getX(){
        return x + bitmap.getWidth() / 2;
    }

    public int getY(){
        return y + bitmap.getHeight() / 2;
    }
}
