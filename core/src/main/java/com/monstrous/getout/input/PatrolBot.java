package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.World;
import com.monstrous.getout.screens.Main;
import net.mgsx.gltf.scene3d.scene.Scene;

// manages robot patrol path using a spline

public class PatrolBot implements Disposable {
    private Scene scene;
    private World world;
    private CatmullRomSpline<Vector3> spline;
    private float time;
    private float speed;
    private Vector3 pos;
    private Vector3 fwd;
    private Vector3 vec;
    private Vector3 side;
    private boolean canSee;
    private float fireTimer = -1;
    private boolean motorSoundPlaying = false;
    private long motorSoundId;
    private long shotSoundId;
    private AnimationController.AnimationDesc walkAnimation;

    public PatrolBot(World world, Scene scene, Array<Vector3> wayPoints) {
        this.world = world;
        this.scene = scene;
        time = 0;
        speed = 1;

        // calculate spline
        Vector3[] dataSet = new Vector3[wayPoints.size];
        for(int i = 0; i < wayPoints.size; i++)
            dataSet[i] = wayPoints.get(i);
        spline = new CatmullRomSpline<>(dataSet, true);

        pos = new Vector3();
        fwd = new Vector3();
        vec = new Vector3();
        side = new Vector3();

        walkAnimation = scene.animationController.setAnimation("Forward", -1);
        motorSoundId = Main.assets.MOTOR.loop();
        motorSoundPlaying = true;
        shotSoundId = -1;
    }

    public void update(float deltaTime, Camera camera ) {

        fireTimer -= deltaTime;
        if (fireTimer < 0) {
            scene.animationController.setAnimation("Idle", -1);
        }
        if (fireTimer < -5f) {
            speed = 1;  // restart the patrol
            walkAnimation = scene.animationController.setAnimation("Forward", -1);
            Main.assets.MOTOR.resume(motorSoundId);
            motorSoundPlaying = true;
        }

        if(speed > 0) {
            time += speed * deltaTime;
            float t = time;
            t /= 20f;
            t = t % 1f;     // keep in range [0-1]

            spline.valueAt(pos, t);
            // todo: direction is not okay yet
            spline.derivativeAt(fwd, t);


            scene.modelInstance.transform.setToRotation(Vector3.Z, fwd);
            scene.modelInstance.transform.setTranslation(pos);
        }

        checkForPlayer(camera.position, deltaTime);

        if(motorSoundPlaying)
            adaptSoundVolumeAndPan(motorSoundId, Main.assets.MOTOR, scene.modelInstance.transform, camera);
    }


    private void checkForPlayer(Vector3 playerPosition, float deltaTime){
        canSee = false;

        vec.set(playerPosition).sub(pos);
        if(vec.len() < 35){
            //Gdx.app.log("bot", "close by");
            vec.nor();
            fwd.set(Vector3.Z).rot(scene.modelInstance.transform);  // direction vector
            float dot = fwd.dot(vec);
            if(dot > 0.7f) {        // you are more or less in front of the bot
                // todo check for walls etc. between bot and player
                canSee = true;
                //Gdx.app.log("bot", "spotted you, dot:"+dot);
            }
        }
        if(canSee) {
            speed = 0;
            walkAnimation = null;
            Main.assets.MOTOR.pause(motorSoundId);
            motorSoundPlaying = false;

            // turn towards player
            side.set(Vector3.X).rot(scene.modelInstance.transform);  // sideways vector
            if(side.dot(vec) < 0){
                scene.modelInstance.transform.rotate(Vector3.Y, -0.25f*deltaTime*60f);
            } else if(side.dot(vec) > 0) {
                scene.modelInstance.transform.rotate(Vector3.Y, 0.25f*deltaTime*60f);
            }

            fireWeapon();
        }

    }

    private void fireWeapon(){
        if (fireTimer < 0 ) {
            Gdx.app.log("bot", "fire!");
            fireTimer = 1f; // allow time for fire animation

            scene.animationController.setAnimation("Fire", 1);
            world.spawnBullet(scene.modelInstance.transform);
            shotSoundId = Main.assets.SHOT.play();
        }
    }
    public void pauseSound(){
        Main.assets.MOTOR.pause(motorSoundId);
        Main.assets.SHOT.pause(shotSoundId);
    }
    public void resumeSound(){
        if(motorSoundPlaying)
            Main.assets.MOTOR.resume(motorSoundId);
        Main.assets.SHOT.resume(shotSoundId);
    }

    @Override
    public void dispose() {
        Main.assets.MOTOR.stop(motorSoundId);
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
//            Gdx.app.log("distance", ""+distance+" volume:"+volume);
//            Gdx.app.log("pan", " dotR="+dotR);
        float pan = dotR;
        sound.setPan(soundId, pan, volume);
    }

}
