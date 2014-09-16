package org.jay3d.gamedemo;

import org.jay3d.engine.Camera;
import org.jay3d.engine.Input;
import org.jay3d.engine.Window;
import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.Mesh;
import org.jay3d.engine.render.Texture;
import org.jay3d.engine.render.Transform;
import org.jay3d.engine.render.Vertex;
import org.jay3d.engine.render.material.Material;
import org.jay3d.engine.render.shaders.Shader;
import org.jay3d.game.Game;
import org.jay3d.util.Time;

import java.util.Random;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Player {
    public static final float GUN_OFFSET = 0.3325f;

    public static final float SCALE = 0.0625f;

    public static final float START = 0;
    public static final float SIZEY = SCALE;
    public static final float SIZEX = (float)((double)SIZEY / (1.0379746835443037974683544303797 * 2.0));

    public static final float OFFSET_X = 0;
    public static final float OFFSET_Y = 0;

    public static final float TEX_MIN_X = -OFFSET_X;
    public static final float TEX_MAX_X = -1 - OFFSET_X;
    public static final float TEX_MIN_Y = -OFFSET_Y;
    public static final float TEX_MAX_Y = 1 - OFFSET_Y;

    private static final float MOUSE_SENSITIVITY = 0.125f;
    private static final float MOVE_SPEED = 3.5f;
    public static final float PLAYER_SIZE = 0.15f;
    public static final float SHOOT_DIST = 1000.0f;
    public static final int DAMAGE_MIN = 20;
    public static final int DAMAGE_MAX = 60;
    public static final int MAX_HEALTH = 100;

    private static final Vector3f ZERO_VECTOR = new Vector3f(0, 0, 0);

    private Camera camera;
    private static boolean mouseLocked = false;
    private Random rand;
    private int health;

    private Mesh mesh;
    private static Material gunMaterial;

    private Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
    private Vector3f movementVector;
    private Transform gunTransform;

    public Player(Vector3f position){
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

        if(gunMaterial == null)
            gunMaterial = new Material(new Texture("0.png"));

        camera = new Camera(position, new Vector3f(0,0,1), new Vector3f(0,1,0));
        rand = new Random();
        health = MAX_HEALTH;
        gunTransform = new Transform();
        gunTransform.setTranslation(new Vector3f(5.5f, 0 , 9f));

        movementVector = ZERO_VECTOR;
    }

    public void damage(int amount){
        health -= amount;

        if(health > MAX_HEALTH)
            health = MAX_HEALTH;

        System.out.println(health);

        if(health <= 0){
            Game.setIsRunning(false);
            System.out.println("YOU LOSE HURHUR!");
        }
    }
    public void input()
    {
        if(Input.getKeyDown(Input.KEY_E))
        {
            Game.getLevel().openDoors(camera.getPos(), true);
        }
        if(Input.getKey(Input.KEY_ESCAPE))
        {
            Input.setCursor(true);
            mouseLocked = false;
        }
        if(Input.getMouseDown(0))
        {
            if(!mouseLocked)
            {
                Input.setMousePosition(centerPosition);
                Input.setCursor(false);
                mouseLocked = true;
            }
            else
            {
                Vector2f lineStart = new Vector2f(camera.getPos().getX(), camera.getPos().getZ());
                Vector2f castDirection = new Vector2f(camera.getForward().getX(), camera.getForward().getZ()).normalise();
                Vector2f lineEnd = lineStart.add(castDirection.mul(SHOOT_DIST));

                System.out.println("Shooting");
                Game.getLevel().checkIntersection(lineStart, lineEnd, true);
            }
        }
        movementVector = ZERO_VECTOR;
        if(Input.getKey(Input.KEY_W))
            movementVector = movementVector.add(camera.getForward());//camera.move(camera.getForward(), movAmt);
        if(Input.getKey(Input.KEY_S))
            movementVector = movementVector.sub(camera.getForward());//camera.move(camera.getForward(), -movAmt);
        if(Input.getKey(Input.KEY_A))
            movementVector = movementVector.add(camera.getLeft());//camera.move(camera.getLeft(), movAmt);
        if(Input.getKey(Input.KEY_D))
            movementVector = movementVector.add(camera.getRight());//camera.move(camera.getRight(), movAmt);
        if(mouseLocked)
        {
            Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
            boolean rotY = deltaPos.getX() != 0;
            boolean rotX = deltaPos.getY() != 0;
            if(rotY)
                camera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
            if(rotX)
                camera.rotateX(-deltaPos.getY() * MOUSE_SENSITIVITY);
            if(rotY || rotX)
                Input.setMousePosition(centerPosition);
        }
    }

    public void update(){
        float movAmt = (float)(MOVE_SPEED * Time.getDelta());

        movementVector.setY(0);

        if(movementVector.length() > 0)
            movementVector = movementVector.normalise();

        Vector3f oldPos = camera.getPos();
        Vector3f newPos = oldPos.add(movementVector.mul(movAmt));

        Vector3f collisionVector = Game.getLevel().checkCollision(oldPos, newPos, PLAYER_SIZE, PLAYER_SIZE);
        movementVector = movementVector.mul(collisionVector);

        if(movementVector.length() > 0)
            camera.move(movementVector, movAmt);

        //Gun movement
        gunTransform.setTranslation(camera.getPos().add(camera.getForward().normalise().mul(0.105f)));
        gunTransform.getTranslation().setY(GUN_OFFSET);

        Vector3f dirToCam = gunTransform.getTranslation().sub(Transform.getCamera().getPos());

        float angleToFaceFromCamera = (float)Math.toDegrees(Math.atan(dirToCam.getZ() / dirToCam.getX()));

        if(dirToCam.getX() > 0)
            angleToFaceFromCamera += 180;

        gunTransform.getRotation().setY(angleToFaceFromCamera + 90);
    }

    public void render(){
        Shader shader = Game.getLevel().getShader();
        shader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMaterial);
        mesh.draw();
    }

    public Camera getCamera() {
        return camera;
    }

    public int getDamage(){
        return rand.nextInt(DAMAGE_MAX - DAMAGE_MIN) + DAMAGE_MAX;
    }

    public int getHealth(){
        return health;
    }

    public int getMaxHealth(){ return MAX_HEALTH; }
}
