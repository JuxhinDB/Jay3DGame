package org.jay3d.gamedemo;

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
public class Level {

    private static final float SPOT_WIDTH = 1;
    private static final float SPOT_LENGTH = 1;
    private static final float SPOT_HEIGHT = 1;

    private static final int NUM_TEX_EXP = 4;
    private static final int NUM_TEXTURES = (int)Math.pow(2, NUM_TEX_EXP);

    private Bitmap level;
    private Shader shader;
    private Material material;
    private Mesh mesh;
    private Transform transform;

    public Level(String levelName, String textureName) {
        level = new Bitmap(levelName).flipY();

        shader = BasicShader.getInstance();

        material = new Material(new Texture(textureName));
        transform = new Transform();

        generateLevel();
    }

    public void input() {

    }

    public void update() {

    }

    public void render() {
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(),
                material);
        mesh.draw();
    }

    private void addFace(ArrayList<Integer> indices, int startLocation, boolean direction) {
        if(direction) {
            indices.add(startLocation + 2);
            indices.add(startLocation + 1);
            indices.add(startLocation);
            indices.add(startLocation + 3);
            indices.add(startLocation + 2);
            indices.add(startLocation);
        }else{
            indices.add(startLocation);
            indices.add(startLocation + 1);
            indices.add(startLocation + 2);
            indices.add(startLocation);
            indices.add(startLocation + 2);
            indices.add(startLocation + 3);
        }
    }

    private float[] calcTexCoords(int value) {
        int texX = (value / NUM_TEXTURES);
        int texY = texX % NUM_TEX_EXP;
        texX /= NUM_TEX_EXP;

        float[] res = new float[4];

        res[0] = (1 - (float)texX/(float)NUM_TEX_EXP);
        res[1] = res[0] - (1 / (float)NUM_TEX_EXP);
        res[3] = 1 - (float)texY/(float)NUM_TEX_EXP;
        res[2] = res[3] - (1 / (float)NUM_TEX_EXP);

        return res;
    }

    private void addVertices(ArrayList<Vertex> vertices, int i, int j, float offset, boolean y, boolean z, boolean x,
                             float[] texCoords) {
        if(x && z) {
            vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * SPOT_HEIGHT, j * SPOT_LENGTH),
                    new Vector2f(texCoords[1], texCoords[3])));
            vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * SPOT_HEIGHT, j * SPOT_LENGTH),
                    new Vector2f(texCoords[0], texCoords[3])));
            vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                    new Vector2f(texCoords[0],  texCoords[2])));
            vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                    new Vector2f( texCoords[1],  texCoords[2])));
        }else if(x && y) {
            vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, j * SPOT_HEIGHT, offset * SPOT_LENGTH),
                    new Vector2f(texCoords[1], texCoords[3])));

            vertices.add(new Vertex(new Vector3f((i + 1)* SPOT_WIDTH, j * SPOT_HEIGHT, offset * SPOT_LENGTH),
                    new Vector2f(texCoords[0], texCoords[3])));

            vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_HEIGHT, offset * SPOT_LENGTH),
                    new Vector2f(texCoords[0],  texCoords[2])));

            vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, (j + 1) * SPOT_HEIGHT, offset * SPOT_LENGTH),
                    new Vector2f( texCoords[1],  texCoords[2])));
        }else if(y && z) {
            vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i  * SPOT_HEIGHT, j * SPOT_LENGTH),
                    new Vector2f(texCoords[1], texCoords[3])));

            vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i  * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                    new Vector2f(texCoords[0], texCoords[3])));

            vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * SPOT_HEIGHT, (j + 1) * SPOT_LENGTH),
                    new Vector2f(texCoords[0],  texCoords[2])));

            vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * SPOT_HEIGHT, j * SPOT_LENGTH),
                    new Vector2f( texCoords[1],  texCoords[2])));
        }else{
            System.err.println("Invalid plane used in level generator. Level#addVertices");
            new Exception().printStackTrace();
            System.exit(1);
        }
    }

    private void generateLevel() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for(int i = 0; i < level.getWidth(); i++){
            for(int j = 0; j < level.getHeight(); j++){
                if((level.getPixel(i, j) & 0xFFFFFF) == 0)
                    continue;

                float[] texCoords = calcTexCoords((level.getPixel(i, j) & 0x00FF00) >> 8);

                //Generate floor
                addFace(indices, vertices.size(), true);
                addVertices(vertices, i, j, 0, false, true, true, texCoords);

                //Generate ceiling
                addFace(indices, vertices.size(), false);
                addVertices(vertices, i, j, 1, false, true, true, texCoords);

                //Generate walls
                texCoords = calcTexCoords((level.getPixel(i, j) & 0xFF0000) >> 16);


                if((level.getPixel(i, j - 1) & 0xFFFFFF) == 0) {
                    addFace(indices, vertices.size(), false);
                    addVertices(vertices, i, 0, j, true, false, true, texCoords);
                }
                if((level.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                    addFace(indices, vertices.size(), true);
                    addVertices(vertices, i, 0, (j + 1), true, false, true, texCoords);
                }
                if((level.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                    addFace(indices, vertices.size(), true);
                    addVertices(vertices, 0, j, i, true, true, false, texCoords);
                }
                if((level.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                    addFace(indices, vertices.size(), false);
                    addVertices(vertices, 0, j, (i + 1), true, true, false, texCoords);
                }
            }
        }

        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];

        vertices.toArray(vertArray);
        indices.toArray(intArray);

        mesh = new Mesh(vertArray, Util.toIntArray(intArray));
    }
}
