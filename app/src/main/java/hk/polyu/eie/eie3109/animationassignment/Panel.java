package hk.polyu.eie.eie3109.animationassignment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap fly = BitmapFactory.decodeResource(getResources(), R.drawable.fly);
    private GameThread thread = new GameThread(getHolder(), this);

    private int score = 0;
    private int scoreDisp = 0; //this is for the animated scoreboard

    private int combo = 0;
    private final int initTime= 1000;
    private int time = initTime;

    private int lives;
    private boolean gameOver = false;

    private ArrayList<GraphicObject> graphics = new ArrayList<GraphicObject>();

    public Panel(Context context){
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public int getInterval(){
        return time;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Coordinates coordinates;
        canvas.drawColor(Color.BLACK);

        drawScoreboard(canvas);

        int x, y;
        for(GraphicObject graphic: graphics){ //for every object graphic in the array graphics
            coordinates = graphic.getCoordinates();
            x = coordinates.getX();
            y = coordinates.getY();
            canvas.drawBitmap(fly, x, y, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (thread.getSurfaceHolder()){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!gameOver){
                    judgeTap(x, y);
                }else{
                    reset();
                }
            }
        }
        return true;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setRunning(true);
        thread.start();
        this.generate();
        this.reset();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { //shut down and wait for the thread to finish
        boolean retry = true;
        thread.setRunning(false);
        while(retry){
            try{
                thread.join(); //wait for the thread to die
                retry = false;
            }catch (InterruptedException e){ //if the thread didn't die...
                retry = true; //keep on trying
            }
        }
    }

    public void updateMovement(){
        Coordinates coordinates;
        Movement movement;

        int x, y;

        for(GraphicObject graphic: graphics) { //for every object graphic in the array graphics
            coordinates = graphic.getCoordinates();
            movement = graphic.getMovement();

            x = (movement.getxDirection() == Movement.X_DIRECTION_RIGHT) ? coordinates.getX() + movement.getxSpeed() : coordinates.getX() - movement.getxSpeed();
            if (x < 0){ //if x reaches the border...
                movement.toggleXDirection(); //flip it's direction
                coordinates.setX(-x);
            }else if (x + graphic.getGraphic().getWidth() > getWidth()){
                movement.toggleXDirection(); //flip it's direction
                coordinates.setX(x + getWidth() - (x + graphic.getGraphic().getWidth()));
            }else{
                coordinates.setX(x);
            }

            y = (movement.getyDirection() == Movement.Y_DIRECTION_DOWN) ? coordinates.getY() + movement.getySpeed() : coordinates.getY() - movement.getySpeed();
            if (y < 0){ //if x reaches the border...
                movement.toggleYDirection(); //flip it's direction
                coordinates.setY(-y);
            }else if (y + graphic.getGraphic().getHeight() > getHeight()){
                movement.toggleYDirection(); //flip it's direction
                coordinates.setY(y + getHeight() - (y + graphic.getGraphic().getHeight()));
            }else{
                coordinates.setY(y);
            }
        }
    }

    public void drawScoreboard(Canvas canvas){
        //draw the scoreboards
        Paint scorePaint = new Paint();
        Paint comboPaint = new Paint();
        Paint comboTxtPaint = new Paint();
        Paint gameOverPaint = new Paint();
        scorePaint.setColor(Color.GRAY);
        scorePaint.setTextSize(275);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setAlpha(75);
        gameOverPaint.setColor(Color.GRAY);
        gameOverPaint.setTextSize(100);
        gameOverPaint.setTextAlign(Paint.Align.CENTER);
        gameOverPaint.setAlpha(75);
        comboPaint.setColor(Color.GRAY);
        comboPaint.setTextSize(100);
        comboPaint.setTextAlign(Paint.Align.RIGHT);
        comboPaint.setAlpha(75);
        comboTxtPaint.setColor(Color.GRAY);
        comboTxtPaint.setTextSize(50);
        comboTxtPaint.setTextAlign(Paint.Align.LEFT);
        comboTxtPaint.setAlpha(75);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((scorePaint.descent() + scorePaint.ascent()) / 2)) ;
        canvas.drawText(scoreDisp + "", xPos, yPos, scorePaint);
        if (gameOver){
            canvas.drawText("Game Over!", xPos, yPos - 500, gameOverPaint);
            canvas.drawText("Tap anywhere to restart.", xPos, yPos + 500, gameOverPaint);
        }
        canvas.drawText(combo + "", xPos * 2 - 350, yPos - 225, comboPaint);
        canvas.drawText("Combo", 350, yPos - 225, comboTxtPaint);
        canvas.drawText("Life", 350, yPos + 100, comboTxtPaint);
        canvas.drawText(lives + "", xPos * 2 - 350, yPos + 100, comboPaint);
    }

    public void generate(){
        if (!gameOver){
            if (graphics.size() <= 10){//limit the size of the graphics list
                GraphicObject graphic = new GraphicObject(fly);
                int flyW = graphic.getGraphic().getWidth() / 2;
                int flyH = graphic.getGraphic().getHeight() / 2;
                graphic.getCoordinates().setX((int)(this.getWidth() * Math.random()) - flyW / 2);
                graphic.getCoordinates().setY((int)(this.getHeight() * Math.random()) - flyH / 2);
                graphics.add(graphic);
            }

        }
    }

    public void judgeTap(int x, int y){
        int misses = 0; // as the click action is evaluated for everyone in the graphics array, there must be duplicated misses
        for (int i = 0; i < graphics.size(); i++){
            GraphicObject target = graphics.get(i);
            if (target.isClicked(x, y)){
                graphics.remove(target);
                combo += 1;
                score += 30 * (1 + combo * 0.075);
                if (time >= 200){
                    time -= 10 * (1 + combo * 0.05);
                }
                updateScore();
            }else{
                misses++;
                combo = 0;
                lives -= (1 / misses); //this is for counting the duplicated misses as 1 miss
                if (lives <= 0){
                    lives = 0;
                    gameOver = true;
                }
            }
        }
    }

    public void updateScore(){ //animated scoreboard. cool.
        final ValueAnimator scoreAnimator = new ValueAnimator();
        scoreAnimator.setIntValues(scoreDisp, score);
        scoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scoreDisp = (int)scoreAnimator.getAnimatedValue();
            }
        });
        scoreAnimator.setDuration(500);
        scoreAnimator.start();
    }

    public void reset(){
        graphics.clear();
        gameOver = false;
        score = 0;
        updateScore();
        time = initTime;
        lives = 10;
    }

}





