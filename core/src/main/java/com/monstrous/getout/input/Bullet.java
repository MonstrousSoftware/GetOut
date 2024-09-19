package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.getout.screens.Main;
import net.mgsx.gltf.scene3d.scene.Scene;

public class Bullet {
    public static final float BULLET_SPEED = 10f;

    public Scene scene;
    public long soundId;
    public boolean isDead;
    private float lifeTime;
    private Vector3 vec = new Vector3();

    public Bullet(Scene scene) {
        this.scene = scene;
        soundId = Main.assets.BUZZ.loop();
        isDead = false;
        lifeTime = 0;
    }

    // returns true if bullet disappears
    public boolean update(float deltaTime, Camera camera) {
            lifeTime += deltaTime;
            vec.set(0,0,1f);    // forward vector
            vec.rot(scene.modelInstance.transform);    // rotate with bullet orientation
            vec.scl(deltaTime*BULLET_SPEED);        // scale with speed and delta time
            scene.modelInstance.transform.trn(vec);    // translate position

            // todo collision detection
            scene.modelInstance.transform.getTranslation(vec);  // bullet position
            float distance = vec.dst(camera.position);
            if(distance < 1f){
                Gdx.app.log("bullet", "hit you");
                Main.assets.BUZZ.stop(soundId);
                return true;
            }

            if(lifeTime > 5f) {
                Main.assets.BUZZ.stop(soundId);
                return true;
            }
            adaptSoundVolumeAndPan(soundId, Main.assets.BUZZ, scene.modelInstance.transform, camera);
            return false;
    }

    private static float SOUND_MAX_DISTANCE = 32f;
    private Vector3 dir = new Vector3();

    // sound is affected by first person camera position and orientation
    private void adaptSoundVolumeAndPan(long soundId, Sound sound, Matrix4 sndTransform, Camera camera ){

        sndTransform.getTranslation(vec);
        vec.sub(camera.position);
        float distance = vec.len();
        float volume = 1.0f - Math.min(distance/SOUND_MAX_DISTANCE, 1.f);   // linear attenuation of sound volume over distance up to a maximum distance
        vec.nor();
        dir.set(camera.direction).rotate(Vector3.Y, -90);   // vector to the right of the camera
        float dotR = dir.dot(vec);      // "how much is the vector towards the right?"
        float pan = dotR;
        sound.setPan(soundId, pan, volume);
    }

}
