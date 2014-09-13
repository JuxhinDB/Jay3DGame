package org.jay3d.game;

import org.jay3d.engine.Window;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.*;
import org.jay3d.gamedemo.Level;
import org.jay3d.gamedemo.Player;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Game {

    private static Level level;
    private Player player;

    public Game() {
        player = new Player(new Vector3f(5.5f, 0.4375f ,8.5f));
        level = new Level("level1.png", "wolfpack.png", player);

        Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
        Transform.setCamera(player.getCamera());
    }

    public void input(){
        level.input();
    }

    public void update(){
        level.update();
    }

    public void render(){
        level.render();
    }

    public static Level getLevel(){
        return level;
    }
}
