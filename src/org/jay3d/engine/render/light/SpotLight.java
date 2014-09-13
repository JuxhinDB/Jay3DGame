package org.jay3d.engine.render.light;

import org.jay3d.engine.math.Vector3f;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class SpotLight {
    private PointLight pointLight;
    private Vector3f direction;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f direction, float cutoff) {
        this.pointLight = pointLight;
        this.direction = direction.normalise();
        this.cutoff = cutoff;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction.normalise();
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }
}
