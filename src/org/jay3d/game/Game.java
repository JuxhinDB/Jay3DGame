package org.jay3d.game;

import org.jay3d.engine.Camera;
import org.jay3d.engine.Window;
import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.*;
import org.jay3d.engine.render.material.Material;
import org.jay3d.engine.render.shaders.BasicShader;
import org.jay3d.engine.render.shaders.Shader;
import org.jay3d.util.Util;

import java.util.ArrayList;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Game {

    private Bitmap level;
    private Shader shader;
    private Material material;
    private Mesh mesh;
    private Transform transform;

    private static final float SPOT_WIDTH = 1;
    private static final float SPOT_LENGTH = 1;
    private static final float SPOT_HEIGHT = 1;

    private static final int NUM_TEX_EXP = 4;
    private static final int NUM_TEXTURES = (int)Math.pow(2, NUM_TEX_EXP);

    public Game() {
        level = new Bitmap("level1.png").flipY();


        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for(int i = 0; i < level.getWidth(); i++){
            for(int j = 0; j < level.getHeight(); j++){
                if((level.getPixel(i, j) & 0xFFFFFF) == 0)
                    continue;

                int texX = ((level.getPixel(i, j) & 0x00FF00) >> 8) / NUM_TEXTURES;
                int texY = texX % NUM_TEX_EXP;
                texX /= NUM_TEX_EXP;

                float xHigher = 1 - (float)texX/(float)NUM_TEX_EXP;
                float xLower = xHigher - (1 / (float)NUM_TEX_EXP);
                float yLower= 1 - (float)texY/(float)NUM_TEX_EXP;
                float yHigher = yLower - (1 / (float)NUM_TEX_EXP);

                //Generate floor
                indices.add(vertices.size() + 2);
                indices.add(vertices.size() + 1);
                indices.add(vertices.size() + 0);

                indices.add(vertices.size() + 3);
                indices.add(vertices.size() + 2);
                indices.add(vertices.size() + 0);

                vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH),
                        new Vector2f(xLower, yLower)));

                vertices.add(new Vertex(new Vector3f((i + 1)* SPOT_WIDTH, 0, j * SPOT_LENGTH),
                        new Vector2f(xHigher, yLower)));

                vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH),
                        new Vector2f(xHigher, yHigher)));

                vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH),
                        new Vector2f(xLower, yHigher)));

                texX = ((level.getPixel(i, j) & 0xFF0000) >> 16) / NUM_TEXTURES;
                texY = texX % NUM_TEX_EXP;
                texX /= NUM_TEX_EXP;

                xHigher = 1 - (float)texX/(float)NUM_TEX_EXP;
                xLower = xHigher - (1 / (float)NUM_TEX_EXP);
                yLower= 1 - (float)texY/(float)NUM_TEX_EXP;
                yHigher = yLower - (1 / (float)NUM_TEX_EXP);

                //Generate ceiling
                indices.add(vertices.size() + 0);
                indices.add(vertices.size() + 1);
                indices.add(vertices.size() + 2);

                indices.add(vertices.size() + 0);
                indices.add(vertices.size() + 2);
                indices.add(vertices.size() + 3);

                vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH),
                        new Vector2f(xLower, yLower)));

                vertices.add(new Vertex(new Vector3f((i + 1)* SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH),
                        new Vector2f(xHigher, yLower)));

                vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                        new Vector2f(xHigher, yHigher)));

                vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                        new Vector2f(xLower, yHigher)));

                //Generate walls

                if((level.getPixel(i, j - 1) & 0xFFFFFF) == 0) {
                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 1);
                    indices.add(vertices.size() + 2);

                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 3);

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH),
                            new Vector2f(xLower, yLower)));

                    vertices.add(new Vertex(new Vector3f((i + 1)* SPOT_WIDTH, 0, j * SPOT_LENGTH),
                            new Vector2f(xHigher, yLower)));

                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH),
                            new Vector2f(xHigher, yHigher)));

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH),
                            new Vector2f(xLower, yHigher)));
                }
                if((level.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 1);
                    indices.add(vertices.size() + 0);

                    indices.add(vertices.size() + 3);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 0);

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xLower, yLower)));

                    vertices.add(new Vertex(new Vector3f((i + 1)* SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xHigher, yLower)));

                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xHigher, yHigher)));

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xLower, yHigher)));
                }
                if((level.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 1);
                    indices.add(vertices.size() + 0);

                    indices.add(vertices.size() + 3);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 0);

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH),
                            new Vector2f(xLower, yLower)));

                    vertices.add(new Vertex(new Vector3f(i* SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xHigher, yLower)));

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xHigher, yHigher)));

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH),
                            new Vector2f(xLower, yHigher)));
                }
                if((level.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 1);
                    indices.add(vertices.size() + 2);

                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 3);

                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH),
                            new Vector2f(xLower, yLower)));

                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xHigher, yLower)));

                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                            new Vector2f(xHigher, yHigher)));

                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, SPOT_HEIGHT, j * SPOT_LENGTH),
                            new Vector2f(xLower, yHigher)));
                }
            }
        }

        shader = BasicShader.getInstance();
        material = new Material(new Texture("wolfpack.png"));

        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];

        vertices.toArray(vertArray);
        indices.toArray(intArray);

        mesh = new Mesh(vertArray, Util.toIntArray(intArray));
        transform = new Transform();
        Transform.setCamera(new Camera());
        Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
    }

    public void input(){
        Transform.getCamera().input();
    }

    public void update(){

    }

    public void render(){
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(),
                material);
        mesh.draw();
    }
}
