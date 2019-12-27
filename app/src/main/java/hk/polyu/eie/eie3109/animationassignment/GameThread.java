package hk.polyu.eie.eie3109.animationassignment;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GameThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private Panel panel;
    private boolean run = false;

    private Timer timer = new Timer();


    public GameThread(SurfaceHolder surfaceHolder, Panel panel){
        this.surfaceHolder = surfaceHolder;
        this.panel = panel;
    }

    public void setRunning(boolean run){
        this.run = run;
    }

    @Override
    public void run() {
        super.run();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                panel.generate();
            }
        }, 0, panel.getInterval());

        Canvas canvas;
        while (run){
            canvas = null;
            try{
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    panel.updateMovement();
                    panel.onDraw(canvas);
                }
            }finally{
                if (canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }
}
