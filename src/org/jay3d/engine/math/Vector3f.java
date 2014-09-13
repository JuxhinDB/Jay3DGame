package org.jay3d.engine.math;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Vector3f {
    private float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float length(){
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float dot(Vector3f v){
        return x * v.getX() + y * v.getY() + z * v.getZ();
    }

    public Vector3f normalise(){
        float length = length();

        x /= length;
        y /= length;
        z /= length;

        return new Vector3f(x, y, z);
    }

    public Vector3f rotate(float angle, Vector3f axis){
        float sinHalfAngle = (float)Math.sin(Math.toRadians(angle / 2));
        float cosHalfAngle = (float)Math.cos(Math.toRadians(angle / 2));

        float rX = axis.getX() * sinHalfAngle;
        float rY = axis.getY() * sinHalfAngle;
        float rZ = axis.getZ() * sinHalfAngle;
        float rW = 1 * cosHalfAngle;

        Quaternion rotation = new Quaternion(rX, rY, rZ, rW);
        Quaternion conjugate = rotation.conjugate();
        Quaternion w = rotation.mul(this).mul(conjugate);

        x = w.getX();
        y = w.getY();
        z = w.getZ();

        return this;
    }

    public Vector3f abs(){
        return new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Vector3f add(Vector3f v){
        return new Vector3f(x + v.getX(), y + v.getY(), z + v.getZ());
    }

    public Vector3f add(float v){
        return new Vector3f(x + v, y + v, z + v);
    }

    public Vector3f sub(Vector3f v){
        return new Vector3f(x - v.getX(), y - v.getY(), z - v.getZ());
    }

    public Vector3f sub(float v){
        return new Vector3f(x - v, y - v, z - v);
    }

    public Vector3f mul(Vector3f v){
        return new Vector3f(x * v.getX(), y * v.getY(), z * v.getZ());
    }

    public Vector3f mul(float v){
        return new Vector3f(x * v, y * v, z * v);
    }

    public Vector3f div(Vector3f v){
        return new Vector3f(x / v.getX(), y / v.getY(), z / v.getZ());
    }

    public Vector3f div(float v){
        return new Vector3f(x / v, y / v, z / v);
    }

    public Vector3f cross(Vector3f v){
        float x_ = y * v.getZ() - z * v.getY();
        float y_ = z * v.getX() - x * v.getZ();
        float z_ = x * v.getY() - y * v.getX();

        return new Vector3f(x_, y_, z_);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
