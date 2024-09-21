package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.monstrous.getout.Assets;
import com.monstrous.getout.Settings;
import com.monstrous.getout.World;
import com.monstrous.getout.collision.Collider;


public class CameraController extends InputAdapter {

    private final float WALK_SPEED = 4f;
    private final float RUN_FACTOR = 2f;


    private final PerspectiveCamera camera;
    private float speed = 0;
    protected final IntIntMap keys = new IntIntMap();
    protected final Vector3 tmp = new Vector3();
    protected final Vector3 tmp2 = new Vector3();
    protected final Vector3 tmp3 = new Vector3();
    private final Vector3 newPos = new Vector3();
    private final Vector3 fwdHorizontal = new Vector3();
    private final Vector3 sideChange = new Vector3();
    protected final Vector3 velocity = new Vector3();
    private float bobAngle;
    private float jumpVelocity;
    private float jumpHeight;
    private Sound walkSound;
    private Sound runSound;

    public CameraController(Assets assets, PerspectiveCamera camera) {
        this.camera = camera;
        jumpVelocity = 0;
        jumpHeight = 0;
        walkSound = assets.FOOT_STEPS;
        runSound = assets.RUNNING;
    }


    public void update(World world, float deltaTime) {
        if(world.health < 0)
            return;         // player is dead, controls are blocked

        float bobSpeed = 0;

        fwdHorizontal.set(camera.direction).y = 0;
        fwdHorizontal.nor();


        if (keys.containsKey(KeyBinding.FORWARD.getKeyCode())) {
            speed = WALK_SPEED;
            if (keys.containsKey(KeyBinding.RUN.getKeyCode()) ) {
                speed  *= RUN_FACTOR;
            }
        }

        if (keys.containsKey(KeyBinding.BACK.getKeyCode())) {
            speed = -WALK_SPEED;
            if (keys.containsKey(KeyBinding.RUN.getKeyCode()) ) {
                speed  *= RUN_FACTOR;
            }
        }


        // slow down and stop when forward key or backward key is released
        if ( !keys.containsKey(KeyBinding.FORWARD.getKeyCode()) && !keys.containsKey(KeyBinding.BACK.getKeyCode()) && Math.abs(speed) > 0 ) {
            speed -= speed * 10f * deltaTime;
            if (Math.abs(speed) < 1f)
                speed = 0;
        }

        sideChange.set(0,0,0);
        if (keys.containsKey(KeyBinding.STRAFE_LEFT.getKeyCode())) {
            sideChange.set(fwdHorizontal).crs(camera.up).nor().scl(-WALK_SPEED);     // strafe velocity
            bobSpeed = 1;
        }
        if (keys.containsKey(KeyBinding.STRAFE_RIGHT.getKeyCode())) {
            sideChange.set(fwdHorizontal).crs(camera.up).nor().scl(WALK_SPEED);
            bobSpeed = 1;
        }



        bobSpeed += Math.abs(speed);


        if (keys.containsKey(KeyBinding.TURN_LEFT.getKeyCode())) {
            camera.direction.rotate(camera.up, deltaTime * 120f);
        }
        if (keys.containsKey(KeyBinding.TURN_RIGHT.getKeyCode())) {
            camera.direction.rotate(camera.up, -deltaTime * 120f);
        }

        velocity.set(fwdHorizontal).scl(speed).add(sideChange);     // make velocity vector
        newPos.set(velocity).scl(deltaTime).add(camera.position);

        if(!Settings.noClip) {
            Collider collider = world.canReach(newPos);
            if (collider != null) {
                //newPos.set(camera.position);// todo
                Gdx.app.log("collision", "");
                collider.collisionResponse(camera.position, 0.5f, velocity, deltaTime); // modifies velocity
                newPos.set(velocity).scl(deltaTime).add(camera.position);
            }
        }
        camera.position.set(newPos);


        if(Settings.camStabilisation)
            camera.up.set(Vector3.Y);

        camera.position.y = Settings.eyeHeight + jumpHeight + bobHeight( bobSpeed, deltaTime); // apply some head bob if we're moving

        camera.update(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        walkSounds(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        walkSounds(keycode);
        return true;
    }

    private void walkSounds(int keycode) {
        // ignore irrelevant key events
        if( (keycode != KeyBinding.FORWARD.getKeyCode()) &&
            (keycode != KeyBinding.BACK.getKeyCode()) &&
            (keycode != KeyBinding.RUN.getKeyCode()) )
            return;

        if(keys.containsKey(KeyBinding.FORWARD.getKeyCode()) || keys.containsKey(KeyBinding.BACK.getKeyCode())) {
            if (keys.containsKey(KeyBinding.RUN.getKeyCode())) {
                runSound.loop();
                walkSound.stop();
            } else {
                walkSound.loop();
                runSound.stop();
            }
        }
        else {
            walkSound.stop();
            runSound.stop();
        }
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * Settings.degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * Settings.degreesPerPixel;
        if(Math.abs(deltaX) > 20 )
            return true;

        if(Settings.invertLook)
            deltaY = -deltaY;

        camera.direction.rotate(camera.up, deltaX);

        // avoid gimbal lock when looking straight up or down
        Vector3 oldPitchAxis = tmp.set(camera.direction).crs(camera.up).nor();
        Vector3 newDirection = tmp2.set(camera.direction).rotate(tmp, deltaY);
        Vector3 newPitchAxis = tmp3.set(tmp2).crs(camera.up);
        if (!newPitchAxis.hasOppositeDirection(oldPitchAxis))
            camera.direction.set(newDirection);

        return true;
    }


    private float bobHeight(float speed, float deltaTime ) {

        float bobHeight = 0;
        if(Math.abs(speed) > 0.1f ) {
            bobAngle += deltaTime * 2.0f * Math.PI / Settings.headBobDuration;
            //bobAngle += MathUtils.random(1f) - 0.5f;  // add bit of noise to the angle

            // move the head up and down in a sine wave
            bobHeight = (float) (Settings.headBobHeight * Math.sin(bobAngle));

        }
        return bobHeight;
    }
}
