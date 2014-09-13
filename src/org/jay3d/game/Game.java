package org.jay3d.game;

import org.jay3d.engine.Camera;
import org.jay3d.engine.Window;
import org.jay3d.engine.math.Vector2f;
import org.jay3d.engine.math.Vector3f;
import org.jay3d.engine.render.*;
import org.jay3d.engine.render.light.*;
import org.jay3d.engine.render.material.Material;
import org.jay3d.engine.render.shaders.PhongShader;
import org.jay3d.engine.render.shaders.Shader;
import org.jay3d.util.Time;

/**
 * Created by Juxhin
 * Do not distribute code without permission!
 */
public class Game {
    private Mesh mesh;
    private Shader shader;
    private Transform transform;
    private Material material;
    private Camera camera;

    PointLight pLight1 = new PointLight(new BaseLight(new Vector3f(1,0.5f,0), 0.8f),
                         new Attenuation(0.5f,0.5f,0.5f), new Vector3f(-2,0,5f), 10);
    PointLight pLight2 = new PointLight(new BaseLight(new Vector3f(0,0.5f,1), 0.8f),
                         new Attenuation(0.5f,0.5f,0.5f), new Vector3f(2,0,7f), 10);

    SpotLight sLight1 = new SpotLight(new PointLight(new BaseLight(new Vector3f(1,1, 1), 0.8f),
                        new Attenuation( 0.5f, 0.5f, 0.5f), new Vector3f(-2,0,5f), 30), new Vector3f(1, 1, 1), 0.7f);

    public Game() {


        material = new Material(new Texture("test.png"), new Vector3f(1,1,1), 1, 8);
        camera = new Camera();
        shader = PhongShader.getInstance();
        transform = new Transform();

        float fieldDepth = 10.0f;
        float fieldWidth = 10.0f;

        Vertex[] vertices = new Vertex[] { new Vertex( new Vector3f(-fieldWidth, 0.0f, -fieldDepth), new Vector2f(0.0f, 0.0f)),
                new Vertex( new Vector3f(-fieldWidth, 0.0f, fieldDepth * 3), new Vector2f(0.0f, 1.0f)),
                new Vertex( new Vector3f(fieldWidth * 3, 0.0f, -fieldDepth), new Vector2f(1.0f, 0.0f)),
                new Vertex( new Vector3f(fieldWidth * 3, 0.0f, fieldDepth * 3), new Vector2f(1.0f, 1.0f))};

        int indices[] = { 0, 1, 2,
                          2, 1, 3};

        mesh = new Mesh(vertices, indices, true);

        Transform.setProjection(70f, Window.getWidth(), Window.getHeight(), 0.1f, 1000);
        Transform.setCamera(camera);

        PhongShader.setAmbientLight(new Vector3f(0.1f, 0.1f, 0.1f));
        PhongShader.setDirectionalLight(new DirectionalLight(new BaseLight
                (new Vector3f(1, 1, 1), 0.1f), new Vector3f(1, 1, 1)));

        PhongShader.setPointLights(new PointLight[]{pLight1, pLight2});

        PhongShader.setSpotLights(new SpotLight[]{sLight1});
    }

    public void input(){
        camera.input();
        /*
        if(Input.getKeyDown(Keyboard.KEY_UP))
            System.out.println("KEY_UP: DOWN");
        if(Input.getKeyUp(Keyboard.KEY_UP))
            System.out.println("KEY_UP: UP");

        if(Input.getMouseDown(1))
            System.out.println("RIGHT_CLICK: DOWN" + " POS: " + Input.getMousePosition().toString());
        if(Input.getMouseUp(1))
            System.out.println("RIGHT_CLICK: UP"  + " POS: " + Input.getMousePosition().toString());
        */
    }

    float temp = 0.0f;

    public void update(){
        temp += Time.getDelta();

        float sinTemp = (float)Math.sin(temp);

        transform.setTranslation(sinTemp, -1, 5);
        //transform.setRotation(0, sinTemp * 180 / 2, 0);

        pLight1.setPosition(new Vector3f(3,0,8.0f * (float)(Math.sin(temp) + 1.0/2.0) + 10));
        pLight2.setPosition(new Vector3f(7,0,8.0f * (float)(Math.cos(temp) + 1.0/2.0) + 10));

        sLight1.getPointLight().setPosition(camera.getPos());
        sLight1.setDirection(camera.getForward());
    }

    public void render(){
        RenderUtil.setClearColor(Transform.getCamera().getPos().div(2048f).abs());
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
        mesh.draw();
    }
}
