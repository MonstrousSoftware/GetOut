package com.monstrous.getout.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerMapping;
import de.golfgl.gdx.controllers.mapping.ConfiguredInput;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;

// used?


public class MyControllerMappings extends ControllerMappings {

    // define input events you need in the game or menu's and give each a unique code
    public static final int BUTTON_A = 0;
    public static final int BUTTON_MENU = 1;
    public static final int AXIS_VERTICAL = 2;
    public static final int AXIS_HORIZONTAL = 3;
    public static final int AXIS_STRAFE = 4;

    public MyControllerMappings() {
        super();
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_A));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_MENU));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, AXIS_VERTICAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, AXIS_HORIZONTAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axisDigital, AXIS_STRAFE));
        commitConfig();
    }

    @Override
    public boolean getDefaultMapping(MappedInputs defaultMapping, Controller controller) {
        ControllerMapping controllerMapping = controller.getMapping();

        defaultMapping.putMapping(new MappedInput(BUTTON_A, new ControllerButton(controllerMapping.buttonA)));
        defaultMapping.putMapping(new MappedInput(BUTTON_MENU, new ControllerButton(controllerMapping.buttonX)));
        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL, new ControllerAxis(controllerMapping.axisLeftY)));
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL, new ControllerAxis(controllerMapping.axisRightX)));
        defaultMapping.putMapping(new MappedInput(AXIS_STRAFE, new ControllerAxis(controllerMapping.axisLeftX)));

        return true;
    }
}
