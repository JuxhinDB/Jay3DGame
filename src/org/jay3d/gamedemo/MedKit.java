package org.jay3d.gamedemo;

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
public class MedKit {
    public static final float PICKUP_DIST = 0.5f;
    public static final int HEAL_UP = 20;

    public static final float SCALE = 0.4f;
    public static final float START = 0;
    public static final float SIZEY = SCALE;
    public static final float SIZEX = (float)((double)SIZEY / (0.67857142857142857142857142857143 * 2.5));


    public static final float OFFSET_X = 0;
    public static final float OFFSET_Y = 0;


    public static final float TEX_MIN_X = -OFFSET_X;
    public static final float TEX_MAX_X = -1 - OFFSET_X;
    public static final float TEX_MIN_Y = -OFFSET_Y;
    public static final float TEX_MAX_Y = 1 - OFFSET_Y;

    private static Material material;

    private Mesh mesh;
    private Transform transform;


    public MedKit(Vector3f position) {
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
        if(material == null){
           material = new Material(new Texture("medipack.png"));
        }

        this.transform = new Transform();
        transform.setTranslation(position);
    }

    public void render(){
        Shader shader = Game.getLevel().getShader();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
        mesh.draw();
    }

    public void update(){
        Vector3f dirToCam = transform.getTranslation().sub(Transform.getCamera().getPos());

        float angleToFaceFromCamera = (float)Math.toDegrees(Math.atan(dirToCam.getZ() / dirToCam.getX()));

        if(dirToCam.getX() > 0)
            angleToFaceFromCamera += 180;

        transform.getRotation().setY(angleToFaceFromCamera + 90);

        if(dirToCam.length() < PICKUP_DIST){
            Player player = Game.getLevel().getPlayer();

            if(player.getHealth() < player.getMaxHealth()) {
                Game.getLevel().removeMedKit(this);
                player.damage(-HEAL_UP);
            }
        }
    }
}
