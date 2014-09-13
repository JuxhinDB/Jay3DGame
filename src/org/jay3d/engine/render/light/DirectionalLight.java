package org.jay3d.engine.render.light;

import org.jay3d.engine.math.Vector3f;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 *
 * This is mainly a wrapper class for my phongFragment
 * shader class.
 */
public class DirectionalLight {
    private BaseLight base;
    private Vector3f direction;

    public DirectionalLight(BaseLight base, Vector3f direction){
        this.base = base;
        this.direction = direction.normalise();
    }

    public BaseLight getBase() {
        return base;
    }

    public void setBase(BaseLight base) {
        this.base = base;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
}
