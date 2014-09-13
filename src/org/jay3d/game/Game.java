package org.jay3d.game;

import org.jay3d.engine.Camera;
import org.jay3d.engine.Window;
import org.jay3d.engine.render.*;
import org.jay3d.gamedemo.Level;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Game {

    private Level level;

    public Game() {
        Transform.setCamera(new Camera());
        Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
        level = new Level("level1.png", "wolfpack.png");
    }

    public void input(){
        Transform.getCamera().input();
    }

    public void update(){

    }

    public void render(){
        level.render();
    }
}
