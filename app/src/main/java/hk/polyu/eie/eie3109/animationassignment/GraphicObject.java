package hk.polyu.eie.eie3109.animationassignment;

import android.graphics.Bitmap;
import android.util.Log;

public class GraphicObject {
    private Bitmap bitmap;
    private Coordinates coordinates;
    private Movement movement;


    public GraphicObject(Bitmap bitmap){
        this.bitmap = bitmap;
        this.coordinates = new Coordinates(bitmap);
        this.movement = new Movement();
    }

    public Bitmap getGraphic(){
        return bitmap;
    }

    public Coordinates getCoordinates(){
        return coordinates;
    }

    public Movement getMovement(){
        return movement;
    }

    public Boolean isClicked(double x, double y){
        //the coordinates of the object
        double coordsX = this.getCoordinates().getX();
        double coordsY = this.getCoordinates().getY();
        //the borders of the object
        double Ybottom = coordsY + this.bitmap.getHeight();
        double Xright = coordsX + this.bitmap.getWidth();
        if((coordsX <= x && x <= Xright ) && (coordsY <= y && y <= Ybottom)){
            Log.d("graphicObject", "clicked");
            return true;
        }else{
            Log.d("graphicObject", "missed");
            return false;
        }
    }
}
