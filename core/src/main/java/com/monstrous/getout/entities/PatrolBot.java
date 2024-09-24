package com.monstrous.getout.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.Settings;
import com.monstrous.getout.World;
import com.monstrous.getout.collision.Collider;
import com.monstrous.getout.screens.Main;
import net.mgsx.gltf.scene3d.scene.Scene;

// manages robot patrol path using a spline
// Spline is in 2d because all movement is on an even floor

public class PatrolBot implements Disposable {
    private static float SOUND_MAX_DISTANCE = 30f;
    private static float VIEW_MAX_DISTANCE = 30f;

    private Scene scene;
    private World world;
    private CatmullRomSpline<Vector2> spline;
    private float time;
    private float speed;
    private Vector2 pos2;
    private Vector2 fwd2;
    private Vector3 position;
    private Vector3 direction;
    private Vector3 vec;
    private Vector3 side;
    private float fireTimer = -1;
    private boolean motorSoundPlaying = false;
    private long motorSoundId;
    private long shotSoundId;
    private AnimationController.AnimationDesc walkAnimation;

    public PatrolBot(World world, Scene scene, Array<Vector2> wayPoints) {
        this.world = world;
        this.scene = scene;
        time = 0;
        speed = 1;

        // calculate spline
        Vector2[] dataSet = new Vector2[wayPoints.size];
        for(int i = 0; i < wayPoints.size; i++)
            dataSet[i] = wayPoints.get(i);
        spline = new CatmullRomSpline<>(dataSet, true);

        pos2 = new Vector2();
        fwd2 = new Vector2();
        position = new Vector3();
        direction = new Vector3();
        vec = new Vector3();
        side = new Vector3();

        walkAnimation = scene.animationController.setAnimation("Forward", -1);
        motorSoundId = Main.assets.MOTOR.loop();
        Main.assets.MOTOR.pause(motorSoundId);      // start with sound paused, let update() resume as needed
        motorSoundPlaying = false;
        shotSoundId = -1;
    }

    public void update(float deltaTime, Camera camera ) {

        fireTimer -= deltaTime;

        if (fireTimer < -5f) {
            speed = 1;  // restart the patrol
            walkAnimation = scene.animationController.setAnimation("Forward", -1);
            Main.assets.MOTOR.resume(motorSoundId);
            motorSoundPlaying = true;
        }
        else if (fireTimer < 0) {
            scene.animationController.setAnimation("Idle", -1);
        }

        if(speed > 0) {
            time += speed * deltaTime;
            float t = time;
            t /= 20f;
            t = t % 1f;     // keep in range [0-1]

            spline.valueAt(pos2, t);
            // todo: direction is not okay yet
            spline.derivativeAt(fwd2, t);

            position.set(pos2.x, 0, pos2.y);

            float degrees = fwd2.angleDeg();
            scene.modelInstance.transform.setToRotation(Vector3.Y, -degrees+90);
            scene.modelInstance.transform.setTranslation(position);
        }

        checkForPlayer(camera.position, deltaTime);

        if(motorSoundPlaying)
            adaptSoundVolumeAndPan(motorSoundId, Main.assets.MOTOR, scene.modelInstance.transform, camera);
    }


    private void checkForPlayer(Vector3 playerPosition, float deltaTime){
        if(Settings.playerIsInvisible || world.health <= 0)
            return;

        boolean canSee = false;
        vec.set(playerPosition).sub(position);
        float maxDistance = VIEW_MAX_DISTANCE;
        if(Settings.torchOn)                        // robots can see you further away with the torch on
            maxDistance *= 2f;
        if(vec.len() < maxDistance){
            //Gdx.app.log("bot", "close by");
            vec.nor();
            direction.set(Vector3.Z).rot(scene.modelInstance.transform);  // direction vector
            float dot = direction.dot(vec);
            float threshold = 0.55f;
            if(Settings.difficult)
                threshold = -0.8f;
            if(dot > threshold) {        // you are more or less in front of the bot
                //  check for walls etc. between bot and player
                canSee = haveLineOfSight(playerPosition);
//                if(canSee)
//                    Gdx.app.log("bot", "spotted you, dot:"+dot);
            }
        }
        if(canSee) {
            speed = 0;
            walkAnimation = null;
            Main.assets.MOTOR.pause(motorSoundId);
            motorSoundPlaying = false;

            // turn towards player
            side.set(Vector3.X).rot(scene.modelInstance.transform);  // sideways vector
            float turnSpeed = 0.25f*60f;
            if(Settings.difficult)
                turnSpeed *= 3.5f;
            if(side.dot(vec) < 0){
                scene.modelInstance.transform.rotate(Vector3.Y, -turnSpeed*deltaTime);
            } else if(side.dot(vec) > 0) {
                scene.modelInstance.transform.rotate(Vector3.Y, turnSpeed*deltaTime);
            }

            fireWeapon();
        }

    }

    private Vector3 intersect = new Vector3();

    private boolean haveLineOfSight(Vector3 playerPosition){
        vec.set(playerPosition).sub(position).nor();
        Ray ray = new Ray(position, vec);
        for(Collider collider : world.colliders.colliders){
            if(Intersector.intersectRayBounds(ray, collider.bbox, intersect)){
                if(position.dst2(intersect) <= position.dst2(playerPosition))
                    return false;       // intersection with bounding box closer than player position
            }
        }
        return true;
    }

    private void fireWeapon(){
        if (fireTimer < 0 ) {
            //Gdx.app.log("bot", "fire!");
            fireTimer = 1f; // allow time for fire animation
            if(Settings.difficult)
                fireTimer /= 2f;

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
