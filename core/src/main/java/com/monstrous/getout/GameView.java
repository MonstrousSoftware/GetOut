package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.CascadeShadowMap;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GameView implements Disposable {

    public SceneManager sceneManager;
    public PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;
    public DirectionalShadowLight light;
    private float camDist;
    private float time;
    private CascadeShadowMap csm;
    public CameraController camController;     // public so that GameScreen can link it to input multiplexer



    public GameView() {

        sceneManager = new SceneManager();

        // setup camera
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camDist = 5f;
        camera.near = 0.1f;
        camera.far = 50f;
        camera.position.set(-3,1.5f, 5).nor().scl(camDist);
        camera.up.set(Vector3.Y);
        camera.lookAt(Vector3.Zero);
        camera.update();
        sceneManager.setCamera(camera);

        camController = new CameraController(camera);

        // setup light
        light = new DirectionalShadowLight(Settings.shadowMapSize, Settings.shadowMapSize);
        light.setViewport(Settings.shadowViewportSize,Settings.shadowViewportSize,Settings.shadowNear, Settings.shadowFar);

        light.direction.set(0.3f, -1f, -0.5f).nor();
        light.color.set(Color.WHITE);
        light.intensity = Settings.directionalLightLevel;
//        sceneManager.environment.add(light);

        if(Settings.cascadedShadows) {
            csm = new CascadeShadowMap(Settings.numCascades);
            sceneManager.setCascadeShadowMap(csm);
        }

        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 1f/Settings.inverseShadowBias));


        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(Settings.ambientLightLevel);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);
    }

    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
    }

    public void refresh(World world) {

        // Here we add all scenes to the scene manager.  This is called every frame.
        sceneManager.getRenderableProviders().clear();        // remove all scenes

        // add scenes
        int num = world.scenes.size;
        for(int i = 0; i < num; i++){
            Scene scene =  world.scenes.get(i);
            sceneManager.addScene(scene, false);
        }
        for(Scene bullet : world.bullets)
            sceneManager.addScene(bullet, false);
    }

    public void render(World world, float deltaTime ){

        refresh(world);

        // animate camera

        camController.update(world, deltaTime);
        camera.update();

        if(Settings.cascadedShadows) {
            csm.setCascades(camera, light, 0, Settings.cascadeSplitDivisor);
        }
        else {
            light.setCenter(Vector3.Zero); // keep shadow light on origin so that we have shadows
        }

        // render
        ScreenUtils.clear(Color.WHITE, true);

        sceneManager.update(deltaTime);
        sceneManager.render();
    }



    @Override
    public void dispose() {
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();
    }
}
