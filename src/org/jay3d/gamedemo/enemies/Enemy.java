package org.jay3d.gamedemo.enemies;

import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.Mesh;
import org.jay3d.engine.render.Texture;
import org.jay3d.engine.render.Transform;
import org.jay3d.engine.render.Vertex;
import org.jay3d.engine.render.material.Material;
import org.jay3d.engine.render.shaders.Shader;
import org.jay3d.game.Game;
/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Enemy {
    private static Mesh mesh;

    public static final float SCALE = 0.65f;

    public static final float START = 0;
    public static final float SIZEY = SCALE;
    public static final float SIZEX = (float)((double)SIZEY / (1.9310344827586206896551724137931 * 2.0));

    public static final float OFFSET_X = 0;
    public static final float OFFSET_Y = 0;

    public static final float TEX_MIN_X = -OFFSET_X;
    public static final float TEX_MAX_X = -1 - OFFSET_X;
    public static final float TEX_MIN_Y = -OFFSET_Y;
    public static final float TEX_MAX_Y = 1 - OFFSET_Y;

    private Material material;
    private Transform transform;

    public Enemy(Transform transform) {
        this.transform = transform;
        material = new Material(new Texture("enemy1.png"));

        if (mesh == null) {
            Vertex[] vertices = new Vertex[]{
                    new Vertex(new Vector3f(-SIZEX, START, START), new Vector2f(TEX_MAX_X, TEX_MAX_Y)),
                    new Vertex(new Vector3f(-SIZEX, SIZEY, START), new Vector2f(TEX_MAX_X, TEX_MIN_Y)),
                    new Vertex(new Vector3f(SIZEX, SIZEY, START), new Vector2f(TEX_MIN_X, TEX_MIN_Y)),
                    new Vertex(new Vector3f(SIZEX, START, START), new Vector2f(TEX_MIN_X, TEX_MAX_Y)),
            };

            int[] indices = new int[]{
                    0, 1, 2,
                    0, 2, 3};

            mesh = new Mesh(vertices, indices);
        }
    }

    public void update() {

    }

    public void render() {
        Shader shader = Game.getLevel().getShader();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
        mesh.draw();
    }
}
