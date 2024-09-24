package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;


// to handle game controllers in-game.
// relays events to player controller: button presses are mapped to key presses
// axis movements are passed to the player controller.


public class MyControllerAdapter extends ControllerAdapter {
    private final CameraController playerController;

    public MyControllerAdapter(CameraController playerController) {
        super();
        this.playerController = playerController;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        Gdx.app.log("controller", "button down: "+buttonIndex);
        int code = mapButton(controller, buttonIndex);
        if(code != Input.Keys.ANY_KEY)
            playerController.keyDown(code);

        return super.buttonDown(controller, buttonIndex);
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        int code = mapButton(controller, buttonIndex);
        if(code != Input.Keys.ANY_KEY)
            playerController.keyUp(code);
        return super.buttonUp(controller, buttonIndex);
    }

    // map controller button to corresponding keyboard code
    private int mapButton(Controller controller, int buttonIndex){
        if(buttonIndex == controller.getMapping().buttonDpadUp)
            return KeyBinding.FORWARD.getKeyCode();
        if(buttonIndex == controller.getMapping().buttonDpadDown)
            return KeyBinding.BACK.getKeyCode();
        if(buttonIndex == controller.getMapping().buttonDpadLeft)
            return KeyBinding.STRAFE_LEFT.getKeyCode();
        if(buttonIndex == controller.getMapping().buttonDpadRight)
            return KeyBinding.STRAFE_RIGHT.getKeyCode();

        if(buttonIndex == controller.getMapping().buttonL1) // run
            return KeyBinding.RUN.getKeyCode();
        if(buttonIndex == controller.getMapping().buttonR1) // also run
            return KeyBinding.RUN.getKeyCode();

        if(buttonIndex == controller.getMapping().buttonA) // torch
            return KeyBinding.TORCH.getKeyCode();
        Gdx.app.log("controller", "unsupported button: "+buttonIndex);
        return Input.Keys.ANY_KEY; // not mapped
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        Gdx.app.log("controller", "axis moved: "+axisIndex+" : "+value);

        // todo
        if(axisIndex == controller.getMapping().axisRightX)     // right stick for steering left/right (X-axis)
            playerController.turnAxisMoved(-value);
        if(axisIndex == controller.getMapping().axisLeftY)     // left stick for forward/backwards (Y-axis)
            playerController.verticalAxisMoved(-value);
        if(axisIndex == controller.getMapping().axisLeftX)     // left stick for strafe (X-axis)
            playerController.strafeAxisMoved(value);
        if(axisIndex == 4)     // left button
            playerController.boostAxisMoved(value);
        return super.axisMoved(controller, axisIndex, value);
    }

    @Override
    public void connected(Controller controller) {
        Gdx.app.log("controller", "connected");
        super.connected(controller);
    }

    @Override
    public void disconnected(Controller controller) {
        Gdx.app.log("controller", "disconnected");
        super.disconnected(controller);
    }
}
