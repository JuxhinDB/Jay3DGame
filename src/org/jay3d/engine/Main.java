package org.jay3d.engine;

import org.jay3d.engine.render.RenderUtil;
import org.jay3d.game.Game;
import org.jay3d.util.Time;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Main {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final String TITLE = "Jay3D";
    public static final double FRAME_CAP = 5000.0;

    private boolean isRunning;
    private Game game;

    public Main(){
        System.out.println(RenderUtil.getOpenGLVersion());
        RenderUtil.initGraphics();
        game = new Game();
        isRunning = false;
    }

    public void start(){
        if(isRunning)
            return;

        run();
    }

    public void stop(){
        if(!isRunning)
            return;

        isRunning = false;
    }

    private void run(){
        isRunning = true;

        int frames = 0;
        long frameCounter = 0;

        final double frameTime = 1.0 / FRAME_CAP;

        long lastTime = Time.getTime();
        double unprocessedTime = 0;

        while(isRunning){
            boolean render = false;

            long startTime = Time.getTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double)Time.SECOND;
            frameCounter += passedTime;

            while(unprocessedTime > frameTime){
                render = true;

                unprocessedTime -= frameTime;

                if(Window.isCloseRequested())
                    stop();

                Time.setDelta(frameTime);
                Input.update();

                game.input();
                game.update();
            }

            if(frameCounter >= Time.SECOND){
                System.out.println("FPS: " + frames);
                frames = 0;
                frameCounter = 0;
            }

            if(render){
                render();
                frames++;
            }else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cleanup();
    }

    private void render(){
        RenderUtil.clearScreen();
        game.render();
        Window.render();
    }

    private void cleanup(){
        Window.dispose();
    }

    public static void main(String[] args) {
        Window.createWindow(WIDTH, HEIGHT, TITLE);
        Main game = new Main();
        game.start();
    }
}
