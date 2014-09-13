package org.jay3d.engine.render.light;

import org.jay3d.engine.math.Vector3f;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 *
 * This is mainly a wrapper class for my phongFragment
 * shader class.
 */
public class BaseLight {
    private Vector3f colour;
    private float intensity;

    public BaseLight(Vector3f colour, float intensity){
        this.colour = colour;
        this.intensity = intensity;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
