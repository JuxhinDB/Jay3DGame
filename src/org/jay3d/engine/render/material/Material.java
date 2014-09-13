package org.jay3d.engine.render.material;

import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.Texture;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Material {
    private Texture texture;
    private Vector3f colour;
    private float specularIntensity;
    private float specularPower;

    public Material(Texture texture) {
        this(texture, new Vector3f(1, 1, 1));
    }


    public Material(Texture texture, Vector3f colour) {
        this(texture, colour, 2, 32);
    }

    public Material(Texture texture, Vector3f colour, float specularIntensity,
                    float specularPower) {
        this.texture = texture;
        this.colour = colour;
        this.specularPower = specularPower;
        this.specularIntensity = specularIntensity;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(float specularIntensity) {
        this.specularIntensity = specularIntensity;
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }
}
