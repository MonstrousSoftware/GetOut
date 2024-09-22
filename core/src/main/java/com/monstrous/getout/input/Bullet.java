package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.Settings;
import com.monstrous.getout.World;
import com.monstrous.getout.collision.Collider;
import com.monstrous.getout.screens.Main;
import net.mgsx.gltf.scene3d.scene.Scene;

public class Bullet implements Disposable {
    public static final float BULLET_SPEED = 10f;
    static final float BULLET_RADIUS = .2f;

    public Scene scene;
    public long soundId;
    public boolean isDead;
    private float lifeTime;
    private Vector3 vec = new Vector3();
    private Vector3 pos = new Vector3();
    private Array<Collider> collisions;

    public Bullet(Scene scene) {
        this.scene = scene;
        soundId = Main.assets.BUZZ.loop();
        isDead = false;
        lifeTime = 0;
        collisions = new Array<>();
    }

    // returns true if bullet disappears
    public boolean update(float deltaTime, World world, Camera camera) {
            lifeTime += deltaTime;
            vec.set(0,0,1f);    // forward vector
            vec.rot(scene.modelInstance.transform);    // rotate with bullet orientation
            vec.scl(deltaTime*BULLET_SPEED);        // scale with speed and delta time
            if(Settings.difficult)
                vec.scl(2f);
            scene.modelInstance.transform.trn(vec);    // translate position

            // todo collision detection
            scene.modelInstance.transform.getTranslation(pos);  // bullet position
            float distance = pos.dst(camera.position);
            if(distance < 1f){
                //Gdx.app.log("bullet", "hit you");
                world.playerGotHitByBullet();
                return true;
            }

            // check for wall collisions
            world.colliders.collisionTest(pos, BULLET_RADIUS, collisions);
            if (collisions.size > 0) {
                //Gdx.app.log("bullet collision", collider.id);
                // todo thud sound
                return true; // remove bullet
            }


            if(lifeTime > 5f) { // avoid bullets flying forever
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

    public void pauseSound(){
        Main.assets.BUZZ.pause(soundId);
    }
    public void resumeSound(){
        Main.assets.BUZZ.resume(soundId);
    }

    @Override
    public void dispose() {
        Main.assets.BUZZ.stop(soundId);
    }
}
