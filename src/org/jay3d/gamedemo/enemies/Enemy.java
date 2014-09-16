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
import org.jay3d.gamedemo.Player;
import org.jay3d.util.Time;

import java.util.Random;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Enemy {
    public static final float SCALE = 0.65f;

    public static final float START = 0;
    public static final float SIZEY = SCALE;
    public static final float SIZEX = (float)((double)SIZEY / (1.9310344827586206896551724137931 * 2.0));

    public static final float OFFSET_X = 0;
    public static final float OFFSET_Y = 0;

    public static final float OFFSET_FROM_GROUND = 0.0f;

    public static final float TEX_MIN_X = -OFFSET_X;
    public static final float TEX_MAX_X = -1 - OFFSET_X;
    public static final float TEX_MIN_Y = -OFFSET_Y;
    public static final float TEX_MAX_Y = 1 - OFFSET_Y;

    public static final int IDLE = 0;
    public static final int CHASE = 1;
    public static final int ATTACK = 2;
    public static final int DYING = 3;
    public static final int DEAD = 4;

    public static final float MOVE_SPEED = 1.0f;//TODO: Make value appropiate
    public static final float MOVE_STOP_DIST = 2f;
    public static final float WIDTH = 0.2f;
    public static final float LENGTH = 0.2f;
    public static final float SHOT_ANGLE = 10f;
    public static final float ATTACK_CHANCE = 0.5f;
    public static final int MAX_HEALTH = 100;

    public static final int DAMAGE_MIN = 15;
    public static final int DAMAGE_MAX = 30;

    private boolean canLook;
    private boolean canAttack;

    private static final float SHOOT_DISTANCE = 1000.0f;

    private static Mesh mesh;
    private Material material;
    private Transform transform;
    private int state;
    private int health;
    private Random rand;

    public Enemy(Transform transform) {
        this.rand = new Random();
        this.canLook = false;
        this.health = MAX_HEALTH;
        this.canAttack = false;
        this.state = IDLE;
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

    public void damage(int amount){
        if(state == IDLE)
            state = CHASE;

        health -= amount;

        if(health <= 0)
            state = DYING;
    }

    private void idleUpdate(Vector3f orientation, float distance)
    {
        double time = ((double)Time.getTime())/((double)Time.SECOND);
        double timeDecimals = time - (double)((int)time);

        if(timeDecimals < 0.5){
            canLook = true;
        }else if(canLook){
            Vector2f lineStart = new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
            Vector2f castDirection = new Vector2f(orientation.getX(), orientation.getZ());
            Vector2f lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));
            Vector2f collisionVector = Game.getLevel().checkIntersection(lineStart, lineEnd, false);
            Vector2f playerIntersectVector = Game.getLevel().lineIntersectRect(lineStart, lineEnd,
                    new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()),
                    new Vector2f(Player.PLAYER_SIZE, Player.PLAYER_SIZE));
            if(playerIntersectVector != null && (collisionVector == null ||
                    playerIntersectVector.sub(lineStart).length() < collisionVector.sub(lineStart).length())){
                System.out.println("We've seen the player!");
                state = CHASE;
            }
            canLook = false;
        }
    }

    private void chaseUpdate(Vector3f orientation, float distance){
        if(rand.nextDouble() < ATTACK_CHANCE * Time.getDelta())
            state = ATTACK;

        if(distance > MOVE_STOP_DIST) {

            float movAmt = MOVE_SPEED * (float) Time.getDelta();

            Vector3f oldPos = transform.getTranslation();
            Vector3f newPos = transform.getTranslation().add(orientation.mul(movAmt));

            Vector3f collisionVector = Game.getLevel().checkCollision(oldPos, newPos, WIDTH, LENGTH);

            Vector3f movementVector = collisionVector.mul(orientation);

            if(movementVector.length() > 0)
                transform.setTranslation(transform.getTranslation().add(movementVector.mul(movAmt)));

            if(movementVector.sub(orientation).length() != 0)
                Game.getLevel().openDoors(transform.getTranslation());
        }else
            state = ATTACK;
    }

    private void attackUpdate(Vector3f orientation, float distance){
        double time = ((double)Time.getTime())/((double)Time.SECOND);
        double timeDecimals = time - (double)((int)time);

        if(timeDecimals < 0.5)
        {
            canAttack = true;
        }else if(canAttack){
            Vector2f lineStart = new Vector2f(transform.getTranslation().getX(), transform.getTranslation().getZ());
            Vector2f castDirection = new Vector2f(orientation.getX(), orientation.getZ()).rotate((rand.nextFloat() - 0.5f) * SHOT_ANGLE);
            Vector2f lineEnd = lineStart.add(castDirection.mul(SHOOT_DISTANCE));

            Vector2f collisionVector = Game.getLevel().checkIntersection(lineStart, lineEnd, false);

            Vector2f playerIntersect = Game.getLevel().lineIntersectRect(lineStart, lineEnd,
                    new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()),
                    new Vector2f(Player.PLAYER_SIZE, Player.PLAYER_SIZE));

            if (playerIntersect != null && (collisionVector == null ||
                    playerIntersect.sub(lineStart).length() < collisionVector.sub(lineStart).length())) {
                Game.getLevel().damagePlayer(rand.nextInt(DAMAGE_MAX - DAMAGE_MIN));
                System.out.println("Player shot ma nigga");
            }

            canAttack = false;
            state = CHASE;
        }
    }

    private void dyingUpdate(Vector3f orientation, float distance){
        state = DEAD;
    }

    private void deadUpdate(Vector3f orientation, float distance){
        System.out.println("Dead ma nigga");
    }

    private void alignWithGround(){
        transform.getTranslation().setY(OFFSET_FROM_GROUND);
    }

    private void faceCamera(Vector3f orientation, float distance){
        Vector3f dirToCam = transform.getTranslation().sub(Transform.getCamera().getPos());

        float angleToFaceFromCamera = (float)Math.toDegrees(Math.atan(dirToCam.getZ() / dirToCam.getX()));

        if(dirToCam.getX() > 0)
            angleToFaceFromCamera += 180;

        transform.getRotation().setY(angleToFaceFromCamera + 90);
    }

    public void update() {
        Vector3f dirToCam = Transform.getCamera().getPos().sub(transform.getTranslation());

        float distance = dirToCam.length();
        Vector3f orientation = dirToCam.div(distance);

        alignWithGround();
        faceCamera(orientation, distance);

        switch(state){
            case IDLE: idleUpdate(orientation, distance);
                break;
            case CHASE: chaseUpdate(orientation, distance);
                break;
            case ATTACK: attackUpdate(orientation, distance);
                break;
            case DYING: dyingUpdate(orientation, distance);
                break;
            case DEAD: deadUpdate(orientation, distance);
                break;
        }
    }

    public void render() {
        Shader shader = Game.getLevel().getShader();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
        mesh.draw();
    }

    public Transform getTransform(){
        return transform;
    }

    public Vector2f getSize(){
        return new Vector2f(WIDTH, LENGTH);
    }
}
