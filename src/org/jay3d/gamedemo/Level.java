package org.jay3d.gamedemo;

import org.jay3d.engine.Input;
import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.*;
import org.jay3d.engine.render.material.Material;
import org.jay3d.engine.render.shaders.BasicShader;
import org.jay3d.engine.render.shaders.Shader;
import org.jay3d.game.Game;
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

    private ArrayList<Vector2f> collisionPosStart;
    private ArrayList<Vector2f> collisionPosEnd;
    private ArrayList<Enemy> enemies;
    private ArrayList<MedKit> medKits;
    private ArrayList<Door> doors;
    private ArrayList<Vector3f> exits;

    private ArrayList<MedKit> medKitsToRemove;

    public Player getPlayer(){
        return player;
    }

    public Level(String levelName, String textureName) {
        exits = new ArrayList<>();
        material = new Material(new Texture(textureName));
        level = new Bitmap(levelName).flipY();
        medKitsToRemove = new ArrayList<>();

        shader = BasicShader.getInstance();

        transform = new Transform();

        generateLevel();

        //enemies.add(new Enemy(tempTransform));
    }

    public void openDoors(Vector3f pos, boolean exitLevel){
        for(Door d : doors){
            if(d.getTransform().getTranslation().sub(pos).length() < OPEN_DISTANCE){
                d.open();
            }
        }

        if(exitLevel){
            for(Vector3f exit : exits){
                if(exit.sub(pos).length() < OPEN_DISTANCE)
                    Game.loadNextLevel();
            }
        }
    }

    public void input() {
        player.input();
    }

    public void update() {
        for(Door d : doors)
            d.update();

        player.update();

        for(MedKit mk : medKits)
            mk.update();

        for(Enemy e : enemies)
            e.update();

        for(MedKit mk : medKitsToRemove)
            medKits.remove(mk);
    }

    public void render() {
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(),
                material);
        mesh.draw();

        for(Door d : doors)
            d.render();

        for(Enemy e : enemies)
            e.render();

        for(MedKit mk : medKits)
            mk.render();


        player.render();
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
        if(blueValue == 1)
            player = new Player(new Vector3f((x + 0.5f) * SPOT_WIDTH , 0.4375f, (y + 0.5f) * SPOT_LENGTH));
        if(blueValue == 128) {
            Transform monsterTransform = new Transform();
            monsterTransform.setTranslation(new Vector3f((x + 0.5f) * SPOT_WIDTH , 0, (y + 0.5f) * SPOT_LENGTH));
            enemies.add(new Enemy(monsterTransform));
        }
        if(blueValue == 192){
            medKits.add(new MedKit(new Vector3f((x + 0.5f) * SPOT_WIDTH , 0, (y + 0.5f) * SPOT_LENGTH)));
        }
        if(blueValue == 97){
            exits.add(new Vector3f((x + 0.5f) * SPOT_WIDTH , 0, (y + 0.5f) * SPOT_LENGTH));
        }

    }

    private void generateLevel() {
        doors = new ArrayList<>();
        enemies = new ArrayList<>();
        collisionPosStart = new ArrayList<>();
        collisionPosEnd = new ArrayList<>();
        medKits = new ArrayList<>();

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
                    collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                    collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                    addFace(indices, vertices.size(), false);
                    addVertices(vertices, i, 0, j, true, false, true, texCoords);
                }
                if((level.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                    collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                    collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                    addFace(indices, vertices.size(), true);
                    addVertices(vertices, i, 0, (j + 1), true, false, true, texCoords);
                }
                if((level.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                    collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                    collisionPosEnd.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                    addFace(indices, vertices.size(), true);
                    addVertices(vertices, 0, j, i, true, true, false, texCoords);
                }
                if((level.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                    collisionPosStart.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                    collisionPosEnd.add(new Vector2f((i + 1)  * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
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

    public Vector2f checkIntersection(Vector2f lineStart, Vector2f lineEnd, boolean hurtMonsters){
        Vector2f nearestIntersection = null;

        for(int i = 0; i < collisionPosStart.size(); i++){
            Vector2f collisionVector = lineIntersect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));

            nearestIntersection = findNearestVector2f(nearestIntersection, collisionVector, lineStart);
        }

        for(Door d : doors){
            Vector2f doorSize = d.getDoorSize();
            Vector3f doorPos3f = d.getTransform().getTranslation();
            Vector2f doorPos2f = new Vector2f(doorPos3f.getX(), doorPos3f.getZ());
            Vector2f collisionVector = lineIntersectRect(lineStart, lineEnd, doorPos2f, doorSize);

            findNearestVector2f(nearestIntersection, collisionVector, lineStart);
        }
        if(hurtMonsters){
            Vector2f nearestEnemyIntersect = null;
            Enemy nearestEnemy = null;

            for(Enemy e : enemies){
                Vector2f enemySize = e.getSize();
                Vector3f enemyPos3f = e.getTransform().getTranslation();
                Vector2f enemyPos2f = new Vector2f(enemyPos3f.getX(), enemyPos3f.getZ());
                Vector2f collisionVector = lineIntersectRect(lineStart, lineEnd, enemyPos2f, enemySize);

                nearestEnemyIntersect = findNearestVector2f(nearestEnemyIntersect, collisionVector, lineStart);

                if(nearestEnemyIntersect == collisionVector)
                    nearestEnemy = e;

            }
            if(nearestEnemyIntersect != null && (nearestIntersection == null ||
                    nearestEnemyIntersect.sub(lineStart).length() < nearestIntersection.sub(lineStart).length()))
            {
                if(nearestEnemy != null)
                    nearestEnemy.damage(player.getDamage());
            }
        }

        return nearestIntersection;
    }

    private float cross(Vector2f a, Vector2f b){
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    private Vector2f lineIntersect(Vector2f lineStart1, Vector2f lineEnd1, Vector2f lineStart2, Vector2f lineEnd2)
    {
        Vector2f line1 = lineEnd1.sub(lineStart1);
        Vector2f line2 = lineEnd2.sub(lineStart2);

        float cross = cross(line1, line2);
        if(cross == 0)
            return null;

        Vector2f distanceBetweenLineStarts = lineStart2.sub(lineStart1);

        float a = cross(distanceBetweenLineStarts, line2) / cross;
        float b = cross(distanceBetweenLineStarts, line1) / cross;

        if(0.0f < a && a < 1.0f && 0.0f < b && b < 1.0f)
            return lineStart1.add(line1.mul(a));

        return null;
    }

    private Vector2f findNearestVector2f(Vector2f a, Vector2f b, Vector2f relativePos){
        if(b != null &&  (a == null ||
                a.sub(relativePos).length() > b.sub(relativePos).length()))
            return b;

        return a;
    }

    public Vector2f lineIntersectRect(Vector2f lineStart, Vector2f lineEnd, Vector2f rectPos, Vector2f rectSize)
    {
        Vector2f result = null;
        Vector2f collisionVector = lineIntersect(lineStart, lineEnd, rectPos, new Vector2f(rectPos.getX() + rectSize.getX(), rectPos.getY()));
        result = findNearestVector2f(result, collisionVector, lineStart);

        collisionVector = lineIntersect(lineStart, lineEnd, rectPos, new Vector2f(rectPos.getX(), rectPos.getY() + rectSize.getY()));
        result = findNearestVector2f(result, collisionVector, lineStart);

        collisionVector = lineIntersect(lineStart, lineEnd, new Vector2f(rectPos.getX(), rectPos.getY() + rectSize.getY()), rectPos.add(rectSize));
        result = findNearestVector2f(result, collisionVector, lineStart);

        collisionVector = lineIntersect(lineStart, lineEnd, new Vector2f(rectPos.getX() + rectSize.getX(), rectPos.getY()), rectPos.add(rectSize));
        result = findNearestVector2f(result, collisionVector, lineStart);

        return result;
    }

    public void damagePlayer(int amount){
        player.damage(amount);
    }

    public Shader getShader() {
        return shader;
    }

    public void removeMedKit(MedKit medKit){
        medKitsToRemove.add(medKit);
    }
}
