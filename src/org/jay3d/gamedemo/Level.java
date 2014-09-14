package org.jay3d.gamedemo;

import org.jay3d.engine.Input;
import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.*;
import org.jay3d.engine.render.material.Material;
import org.jay3d.engine.render.shaders.BasicShader;
import org.jay3d.engine.render.shaders.Shader;
import org.jay3d.gamedemo.enemies.Enemy;
import org.jay3d.gamedemo.objects.Door;
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
    private static final float OPEN_DISTANCE = 1.0f;
    private static final float DOOR_OPEN_MOVEMENT_AMOUNT = 0.9f;

    private Bitmap level;
    private Shader shader;
    private Material material;
    private Mesh mesh;
    private Transform transform;
    private Player player;


    //WARNING: TEMPORARY VAR
    private Enemy enemy;
    private ArrayList<Door> doors;

    public Level(String levelName, String textureName, Player player) {
        this.player = player;
        Transform tempTransform = new Transform();
        tempTransform.setTranslation(new Vector3f(6f, 0 ,8.5f));
        material = new Material(new Texture(textureName));

        //door = new Door(tempTransform, material);
        doors = new ArrayList<>();
        level = new Bitmap(levelName).flipY();

        shader = BasicShader.getInstance();

        transform = new Transform();

        enemy = new Enemy(tempTransform);

        generateLevel();
    }

    public void input() {
        if(Input.getKeyDown(Input.KEY_E))
            for(Door d : doors){
                if(d.getTransform().getTranslation().sub(player.getCamera().getPos()).length() < OPEN_DISTANCE){
                    d.open();
                }
            }
        player.input();
    }

    public void update() {
        for(Door d : doors)
            d.update();
        player.update();
        enemy.update();
    }

    public void render() {
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(),
                material);
        mesh.draw();
        for(Door d : doors)
            d.render();
        player.render();
        enemy.render();
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

    private void addDoor(int x, int y){
        Transform doorTransform = new Transform();

        boolean xDoor = (level.getPixel(x, y - 1) & 0xFFFFFF) == 0
                        &&(level.getPixel(x, y + 1) & 0xFFFFFF) == 0;

        boolean yDoor = (level.getPixel(x - 1, y) & 0xFFFFFF) == 0
                &&(level.getPixel(x + 1, y) & 0xFFFFFF) == 0;

        if(!(xDoor ^ yDoor)){
            System.err.println("Level generation has failed! Door placed in invalid location" + x + "," + y);
            new Exception().printStackTrace();
            System.exit(1);
        }

        Vector3f openPosition = null;

        if(yDoor){
            doorTransform.setTranslation(x, 0, y + SPOT_LENGTH / 2);
            openPosition = doorTransform.getTranslation().sub(new Vector3f(DOOR_OPEN_MOVEMENT_AMOUNT, 0.0f, 0.0f));
        }

        if(xDoor){
            doorTransform.setTranslation(x + SPOT_WIDTH / 2, 0, y);
            doorTransform.setRotation(0, 90, 0);
            openPosition = doorTransform.getTranslation().sub(new Vector3f(0.0f, 0.0f, DOOR_OPEN_MOVEMENT_AMOUNT));
        }

        doors.add(new Door(doorTransform, material, openPosition));
    }

    private void addSpecial(int blueValue, int x, int y){
        if(blueValue == 16)
            addDoor(x, y);
    }

    private void generateLevel() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for(int i = 0; i < level.getWidth(); i++){
            for(int j = 0; j < level.getHeight(); j++){
                if((level.getPixel(i, j) & 0xFFFFFF) == 0)
                    continue;

                float[] texCoords = calcTexCoords((level.getPixel(i, j) & 0x00FF00) >> 8);

                addSpecial((level.getPixel(i, j) & 0x0000FF), i, j);

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


    public Vector3f checkCollision(Vector3f oldPos, Vector3f newPos, float objectWidth,
                                   float objectLength){
        Vector2f collisionVector = new Vector2f(1, 1);
        Vector3f movementVector = newPos.sub(oldPos);

        if(movementVector.length() > 0){
            Vector2f blockSize = new Vector2f(SPOT_WIDTH, SPOT_LENGTH);
            Vector2f objectSize = new Vector2f(objectWidth, objectLength);

            Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
            Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());

            for(int i = 0; i < level.getWidth(); i++) {
                for (int j = 0; j < level.getHeight(); j++) {
                    if ((level.getPixel(i, j) & 0xFFFFFF) == 0) {
                        collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize,
                                blockSize.mul(new Vector2f(i, j)), blockSize));
                    }
                }
            }

            for(Door d : doors){
                //TODO: Make this take into account door's orientation
                Vector2f doorSize = d.getDoorSize();
                Vector3f doorPos3f = d.getTransform().getTranslation();
                Vector2f doorPos2f = new Vector2f(doorPos3f.getX(), doorPos3f.getZ());
                collisionVector = collisionVector.mul(rectCollide(oldPos2, newPos2, objectSize,
                        doorPos2f, doorSize));
            }
        }

        return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
    }

    private Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1,
                                 Vector2f pos2, Vector2f size2){
        Vector2f res = new Vector2f(0, 0);

        if(newPos.getX() + size1.getX() < pos2.getX() ||
           newPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() * size2.getX() ||
           oldPos.getY() + size1.getY() < pos2.getY() ||
           oldPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY() * size2.getY()) {
            res.setX(1);
        }

        if(oldPos.getX() + size1.getX() < pos2.getX() ||
           oldPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() * size2.getX() ||
           newPos.getY() + size1.getY() < pos2.getY() ||
           newPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY() * size2.getY()){
            res.setY(1);
        }

        return res;
    }

    public Shader getShader() {
        return shader;
    }
}
