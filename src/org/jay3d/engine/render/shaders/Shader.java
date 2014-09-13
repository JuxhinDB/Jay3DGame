package org.jay3d.engine.render.shaders;

import org.jay3d.engine.math.Matrix4f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.material.Material;
import org.jay3d.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Shader {
    private int program;
    private HashMap<String, Integer> uniforms;

    public Shader(){
        program = glCreateProgram();
        uniforms = new HashMap<>();

        if(program == 0){
            System.err.print("SHADER ERROR: Could not find valid memory location in constructor");
            System.exit(1);
        }
    }

    public void bind(){
        glUseProgram(program);
    }

    public void addUniform(String uniformName){
        int uniformLocation = glGetUniformLocation(program, uniformName);
        if(uniformLocation == 0xFFFFFFFF){
            System.err.println("ERROR: Could not find uniform: " + uniformName);
            new Exception().printStackTrace();
            System.exit(4);
        }

        uniforms.put(uniformName, uniformLocation);
    }

    public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material){}

    public void addVertexShader(String text){
        addProgram(text, GL_VERTEX_SHADER);
    }

    public void addGeometryShader(String text){
        addProgram(text, GL_GEOMETRY_SHADER);
    }

    public void addFragmentShader(String text){
        addProgram(text, GL_FRAGMENT_SHADER);
    }

    public void addVertexShaderFromFile(String text){
        addProgram(loadShader(text), GL_VERTEX_SHADER);
    }

    public void addGeometryShaderFromFile(String text){
        addProgram(loadShader(text), GL_GEOMETRY_SHADER);
    }

    public void addFragmentShaderFromFile(String text){
        addProgram(loadShader(text), GL_FRAGMENT_SHADER);
    }

    public void compileShader(){
        glLinkProgram(program);
        if(glGetProgram(program, GL_LINK_STATUS) == 0){
            System.err.print(glGetShaderInfoLog(program, 1024));
            System.exit(3);
        }
        glValidateProgram(program);

        if(glGetProgrami(program, GL_VALIDATE_STATUS) == 0){
            System.err.print(glGetProgramInfoLog(program, 1024));
            System.exit(3);
        }
    }

    private void addProgram(String text, int type){
        int shader = glCreateShader(type);

        if(shader == 0){
            System.err.print("SHADER ERROR: Could not find valid memory location when adding shader");
            System.exit(2);
        }

        glShaderSource(shader, text);
        glCompileShader(shader);

        if(glGetShader(shader, GL_COMPILE_STATUS) == 0){
            System.err.print(glGetShaderInfoLog(shader, 1024));
            System.exit(2);
        }

        glAttachShader(program, shader);
    }

    public void setUniformi(String uniformName, int value){
        glUniform1i(uniforms.get(uniformName) ,value);
    }

    public void setUniformf(String uniformName, float value){
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value){
        glUniform3f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ());
    }

    public void setUniform(String uniformName, Matrix4f value){
        glUniformMatrix4(uniforms.get(uniformName), true, Util.createFlippedBuffer(value));
    }


    private static String loadShader(String fileName){
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader reader;

        try{
            reader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
            String line;
            while((line = reader.readLine()) != null){
                shaderSource.append(line).append("\n");
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        return shaderSource.toString();
    }
}
