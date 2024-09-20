package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.monstrous.getout.Settings;
import com.monstrous.getout.World;


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
    private float bobAngle;
    private boolean isJumping;
    private boolean isCrouching;
    private float jumpVelocity;
    private float jumpHeight;
    private Sound walkSound;
    private Sound runSound;

    public CameraController(PerspectiveCamera camera) {
        this.camera = camera;
        isJumping = false;
        isCrouching = false;
        jumpVelocity = 0;
        jumpHeight = 0;
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footsteps.ogg"));
        runSound = Gdx.audio.newSound(Gdx.files.internal("sounds/metal-running.ogg"));
    }


    public void update(World world, float deltaTime) {
        if(world.health < 0)
            return;         // player is dead, controls are blocked

        float bobSpeed = 0;

        fwdHorizontal.set(camera.direction).y = 0;
        fwdHorizontal.nor();
        sideChange.set(0,0,0);

        if (keys.containsKey(KeyBinding.FORWARD.getKeyCode())) {
            speed = WALK_SPEED;
            if (keys.containsKey(KeyBinding.RUN.getKeyCode()) &&!isCrouching) {
                speed  *= RUN_FACTOR;
            }
        }

        if (keys.containsKey(KeyBinding.BACK.getKeyCode())) {
            speed = -WALK_SPEED;
            if (keys.containsKey(KeyBinding.RUN.getKeyCode()) && !isCrouching) {
                speed  *= RUN_FACTOR;
            }
        }


        // slow down and stop when forward key or backward key is released
        if ( !keys.containsKey(KeyBinding.FORWARD.getKeyCode()) && !keys.containsKey(KeyBinding.BACK.getKeyCode()) && Math.abs(speed) > 0 && !isJumping) {
            speed -= speed * 10f * deltaTime;
            if (Math.abs(speed) < 1f)
                speed = 0;
        }

        if (keys.containsKey(KeyBinding.STRAFE_LEFT.getKeyCode())) {
            sideChange.set(fwdHorizontal).crs(camera.up).nor().scl(-deltaTime * WALK_SPEED);
            bobSpeed = 1;
        }
        if (keys.containsKey(KeyBinding.STRAFE_RIGHT.getKeyCode())) {
            sideChange.set(fwdHorizontal).crs(camera.up).nor().scl(deltaTime * WALK_SPEED);
            bobSpeed = 1;
        }



        bobSpeed += Math.abs(speed);


        if (keys.containsKey(KeyBinding.TURN_LEFT.getKeyCode())) {
            camera.direction.rotate(camera.up, deltaTime * 120f);
        }
        if (keys.containsKey(KeyBinding.TURN_RIGHT.getKeyCode())) {
            camera.direction.rotate(camera.up, -deltaTime * 120f);
        }

        if (keys.containsKey(KeyBinding.JUMP.getKeyCode()) && !isJumping) {
            isJumping = true;

            jumpVelocity = 3;
        }
        if(isJumping){
            bobSpeed = 0;
            jumpVelocity -= deltaTime*5;
            jumpHeight += jumpVelocity*deltaTime;
            if(jumpVelocity < 0 && jumpHeight < 0){
                isJumping = false;
                jumpHeight = 0;
            }
        }

        // crouching
        if (keys.containsKey(KeyBinding.CROUCH.getKeyCode()) && !isJumping ) {
            isCrouching = true;
            if(jumpHeight > -0.5f)
                jumpHeight -= 5*deltaTime;
        }
        else {
            isCrouching = false;
            if(jumpHeight < 0) {
                jumpHeight += 5*deltaTime;
                if(jumpHeight >= 0) {
                    jumpVelocity = 0;
                    jumpHeight = 0;
                }
            }
        }




        newPos.set(fwdHorizontal).scl(deltaTime * speed);
        newPos.add(sideChange);
        newPos.add(camera.position);

        if(Settings.noClip || world.canReach(newPos))
            camera.position.set(newPos);

        if(Settings.camStabilisation)
            camera.up.set(Vector3.Y);

        camera.position.y = Settings.eyeHeight + jumpHeight + bobHeight( bobSpeed, deltaTime); // apply some head bob if we're moving



        camera.update(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        if(keycode == KeyBinding.FORWARD.getKeyCode()) {
            if(keys.containsKey(KeyBinding.RUN.getKeyCode())) {
                runSound.loop();
                walkSound.stop();
            }
            else {
                walkSound.loop();
                runSound.stop();
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        if(keycode == KeyBinding.FORWARD.getKeyCode()) {
            walkSound.stop();
            runSound.stop();
        }
        return true;
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
