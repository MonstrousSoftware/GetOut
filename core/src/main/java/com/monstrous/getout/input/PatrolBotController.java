package com.monstrous.getout.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.monstrous.getout.Main;
import com.monstrous.getout.World;
import net.mgsx.gltf.scene3d.scene.Scene;


// control patrol bot with keyboard

// should we do sound elsewhere?
// should we have PatrolBot instance separately? This code doesn't scale to multiple bots

public class PatrolBotController extends InputAdapter {

    private final float WALK_SPEED = 0.05f;
    private final float TURN_SPEED = 120f;

    private float speed = 0;
    private boolean isTurning;
    private float fireTimer = -1;
    private boolean collapsed = false;
    private boolean motorSoundPlaying = false;
    private long motorSoundId;
    private boolean bulletSoundPlaying = false;
    private long bulletSoundId;
    private Matrix4 bulletTransform;
    protected final IntIntMap keys = new IntIntMap();
    private AnimationController.AnimationDesc walkAnimation;

    public PatrolBotController() {

    }


    public void update(World world, float deltaTime, Camera camera) {


        if (!collapsed && keys.containsKey(KeyBinding.BOT_COLLAPSE.getKeyCode())) {
            speed = 0;
            fireTimer = -1;
            collapsed = true;
            walkAnimation = null;
            Main.assets.MOTOR.stop();
            motorSoundPlaying = false;
            world.patrolBot.animationController.setAnimation("Collapse", 1);
        }
        if (collapsed && keys.containsKey(KeyBinding.BOT_REVIVE.getKeyCode())) {
            speed = 0;
            fireTimer = -1;
            collapsed = false;
            walkAnimation = null;
            world.patrolBot.animationController.setAnimation("Idle", -1);
        }
        if(collapsed)
            return;

        if (fireTimer < 0 && keys.containsKey(KeyBinding.BOT_FIRE.getKeyCode())) {
            speed = 0;
            fireTimer = 1f; // allow time for fire animation
            walkAnimation = null;
            Main.assets.MOTOR.stop();
            motorSoundPlaying = false;
            world.patrolBot.animationController.setAnimation("Fire", 1);
            Scene bullet = world.spawnBullet();
            bulletTransform = bullet.modelInstance.transform;
            bulletSoundId = Main.assets.BUZZ.play();
            bulletSoundPlaying = true;
            Main.assets.SHOT.play();

        }
        else if (fireTimer > 0) {
            fireTimer -= deltaTime;
            if(fireTimer < 0) {
                world.patrolBot.animationController.setAnimation("Idle", -1);
                bulletSoundId = -1;
                bulletSoundPlaying = false;
            }
        }

        if (keys.containsKey(KeyBinding.BOT_FORWARD.getKeyCode())) {
            speed = WALK_SPEED;
            if(walkAnimation == null) {
                walkAnimation = world.patrolBot.animationController.setAnimation("Forward", -1);
                motorSoundId = Main.assets.MOTOR.loop();
                motorSoundPlaying = true;
            }
        }

        if (keys.containsKey(KeyBinding.BOT_BACK.getKeyCode())) {
            speed = -WALK_SPEED;
            if(walkAnimation == null) {
                walkAnimation = world.patrolBot.animationController.setAnimation("Forward", -1);    // will be played in reverse
                motorSoundId = Main.assets.MOTOR.loop();
                motorSoundPlaying = true;
            }
        }

        // slow down and stop when forward key or backward key is released
        if ( !keys.containsKey(KeyBinding.BOT_FORWARD.getKeyCode()) && !keys.containsKey(KeyBinding.BOT_BACK.getKeyCode()) && Math.abs(speed) > 0 ) {
            speed -= Math.signum(speed) *3f * deltaTime;
            if (Math.abs(speed) < 0.1f) {
                speed = 0;
                walkAnimation = null;
                Main.assets.MOTOR.stop();
                motorSoundPlaying = false;
                world.patrolBot.animationController.setAnimation("Idle", -1);
            }
        }

        // speed up (or reverse) animation depending on velocity
        if(walkAnimation != null)
            walkAnimation.speed = speed * 100f;     // can be negative if reversing

        isTurning = false;
        Matrix4 transform = world.patrolBot.modelInstance.transform;
        if (keys.containsKey(KeyBinding.BOT_TURN_LEFT.getKeyCode())) {
            transform.rotate(Vector3.Y, deltaTime * TURN_SPEED);
            isTurning = true;
            walkAnimation = world.patrolBot.animationController.setAnimation("Forward", -1);
            if(!motorSoundPlaying) {
                motorSoundId = Main.assets.MOTOR.loop();
                motorSoundPlaying = true;
            }
        }
        if (keys.containsKey(KeyBinding.BOT_TURN_RIGHT.getKeyCode())) {
            transform.rotate(Vector3.Y, -deltaTime * TURN_SPEED);
            isTurning = true;
            walkAnimation = world.patrolBot.animationController.setAnimation("Forward", -1);
            if(!motorSoundPlaying) {
                motorSoundId = Main.assets.MOTOR.loop();
                motorSoundPlaying = true;
            }
        }

        transform.translate(0, 0, speed);

        if(speed == 0 && !isTurning) {
            Main.assets.MOTOR.stop();
            motorSoundPlaying = false;
            walkAnimation = null;
            world.patrolBot.animationController.setAnimation("Idle", -1);
        }

        if(motorSoundPlaying)
            adaptSoundVolumeAndPan(motorSoundId, Main.assets.MOTOR, transform, camera);
        if(bulletSoundPlaying)
            adaptSoundVolumeAndPan(bulletSoundId, Main.assets.SHOT, bulletTransform, camera);
    }

    private static float SOUND_MAX_DISTANCE = 16f;
    private Vector3 vec = new Vector3();
    private Vector3 dir = new Vector3();

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

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return handleKey(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        return handleKey(keycode);
    }

    private boolean handleKey(int keycode ){
        if( KeyBinding.BOT_FORWARD.getKeyCode() == keycode)
            return true;
        if( KeyBinding.BOT_BACK.getKeyCode() == keycode)
            return true;
        if( KeyBinding.BOT_TURN_LEFT.getKeyCode() == keycode)
            return true;
        if( KeyBinding.BOT_TURN_RIGHT.getKeyCode() == keycode)
            return true;
        if( KeyBinding.BOT_COLLAPSE.getKeyCode() == keycode)
            return true;
        if( KeyBinding.BOT_FIRE.getKeyCode() == keycode)
            return true;
        if( KeyBinding.BOT_REVIVE.getKeyCode() == keycode)
            return true;
        return false;
    }

}
