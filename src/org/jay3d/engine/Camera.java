package org.jay3d.engine;

import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.util.Time;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Camera
{
    public static final Vector3f yAxis = new Vector3f(0,1,0);
    private Vector3f pos;
    private Vector3f forward;
    private Vector3f up;
    public Camera()
    {
        this(new Vector3f(0,0,0), new Vector3f(0,0,1), new Vector3f(0,1,0));
    }

    public Camera(Vector3f pos, Vector3f forward, Vector3f up)
    {
        this.pos = pos;
        this.forward = forward.normalise();
        this.up = up.normalise();
    }

    boolean mouseLocked = false;

    Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);

    public void input(){
    }
    public void move(Vector3f dir, float amt)
    {
        pos = pos.add(dir.mul(amt));
    }

    public void rotateY(float angle)
    {
        Vector3f Haxis = yAxis.cross(forward).normalise();
        forward = forward.rotate(angle, yAxis).normalise();
        up = forward.cross(Haxis).normalise();
    }
    public void rotateX(float angle)
    {
        Vector3f Haxis = yAxis.cross(forward).normalise();
        forward = forward.rotate(angle, Haxis).normalise();
        up = forward.cross(Haxis).normalise();
    }

    public Vector3f getLeft()
    {
        return forward.cross(up).normalise();
    }

    public Vector3f getRight()
    {
        return up.cross(forward).normalise();
    }

    public Vector3f getPos()
    {
        return pos;
    }

    public void setPos(Vector3f pos)
    {
        this.pos = pos;
    }

    public Vector3f getForward()
    {
        return forward;
    }

    public void setForward(Vector3f forward)
    {
        this.forward = forward;
    }

    public Vector3f getUp()
    {
        return up;
    }

    public void setUp(Vector3f up)
    {
        this.up = up;
    }
}