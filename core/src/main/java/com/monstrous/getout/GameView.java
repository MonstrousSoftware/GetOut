package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.filters.PostProcessor;
import com.monstrous.getout.filters.VCRFilter;
import com.monstrous.getout.input.Bullet;
import com.monstrous.getout.input.CameraController;
import net.mgsx.gltf.scene3d.attributes.FogAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.CascadeShadowMap;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import static com.badlogic.gdx.Gdx.gl;

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
    private FrameBuffer fbo;
    private PostProcessor filter;


    public GameView() {

        PBRShaderConfig colorConfig = new PBRShaderConfig();

        // todo tweak
        colorConfig.numDirectionalLights = 1;
        colorConfig.numPointLights = 0;
        colorConfig.numSpotLights = 0;
        colorConfig.numBones = 12;      // patrol bot has 12 bones

        DepthShader.Config depthConfig= new DepthShader.Config();
        depthConfig.numBones = 12;

        sceneManager = new SceneManager(
            new PBRShaderProvider(colorConfig),
            new PBRDepthShaderProvider(depthConfig)
        );

        //sceneManager = new SceneManager();

        // setup camera
        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camDist = 5f;
        camera.near = 0.1f;
        camera.far = 150f;
        //camera.position.set(0,1.8f, 5).nor().scl(camDist);
        camera.position.set(4,1.8f, -7);
        camera.up.set(Vector3.Y);
        camera.lookAt(0, 1.8f, 100f);
        camera.update();
        sceneManager.setCamera(camera);

        camController = new CameraController(camera);

        // setup light
        light = new DirectionalShadowLight(Settings.shadowMapSize, Settings.shadowMapSize);
        light.setViewport(Settings.shadowViewportSize,Settings.shadowViewportSize,Settings.shadowNear, Settings.shadowFar);

        light.direction.set(0.3f, -1f, -0.5f).nor();
        light.color.set(Color.WHITE);
        light.intensity = Settings.directionalLightLevel;
//        sceneManager.environment.add(light);      // sunlight

        if(Settings.cascadedShadows) {
            csm = new CascadeShadowMap(Settings.numCascades);
            sceneManager.setCascadeShadowMap(csm);
        }

        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 1f/Settings.inverseShadowBias));


        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = EnvironmentUtil.createCubemap(new InternalFileHandleResolver(),
            "skybox/side-", ".png", EnvironmentUtil.FACE_NAMES_NEG_POS);

//        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(Settings.ambientLightLevel);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        sceneManager.environment.set(new ColorAttribute(ColorAttribute.Fog, Settings.fogColour));
        sceneManager.environment.set(new FogAttribute(FogAttribute.FogEquation).set(2, 35, 2.0f));  // close fog


        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);

        filter = new PostProcessor();

    }

    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
        if(fbo != null)
            fbo.dispose();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        filter.resize(width, height);
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
        for(Bullet bullet : world.bullets)
            sceneManager.addScene(bullet.scene, false);
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
        sceneManager.update(deltaTime);
        if(Settings.postFilter) {
            sceneManager.renderShadows();
            fbo.begin();
            gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            sceneManager.renderColors();
            fbo.end();
            filter.render(fbo);
        }
        else {
            gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            sceneManager.render();
        }
    }



    @Override
    public void dispose() {
        sceneManager.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();
        filter.dispose();
    }
}
