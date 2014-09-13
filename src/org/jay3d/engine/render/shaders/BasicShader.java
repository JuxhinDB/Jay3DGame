package org.jay3d.engine.render.shaders;

import org.jay3d.engine.math.Matrix4f;
import org.jay3d.engine.render.RenderUtil;
import org.jay3d.engine.render.material.Material;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class BasicShader extends Shader{
    private static final BasicShader INSTANCE = new BasicShader();

    public static BasicShader getInstance(){
        return INSTANCE;
    }
    public BasicShader(){
        super();

        addVertexShaderFromFile("basicVertex.vs");
        addFragmentShaderFromFile("basicFragment.fs");
        compileShader();

        addUniform("transform");
        addUniform("colour");
    }

    @Override
    public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material){
        if(material.getTexture() != null)
            material.getTexture().bind();
        else
            RenderUtil.unbindTextures();

        setUniform("transform", projectedMatrix);
        setUniform("colour", material.getColour());
    }
}
