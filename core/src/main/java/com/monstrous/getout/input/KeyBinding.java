package com.monstrous.getout.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.monstrous.getout.Settings;

public enum KeyBinding {
    FORWARD ("Move forward", Keys.W),
    BACK ("Move back", Keys.S),
    TURN_LEFT ("Turn left", Keys.A),
    TURN_RIGHT ("Turn right", Keys.D),
    STRAFE_LEFT("Strafe left", Keys.Q),
    STRAFE_RIGHT("Strafe right", Keys.E),
    RUN ("Run modifier", Keys.SHIFT_LEFT),
    TORCH ("Toggle torch", Keys.T),
//    CROUCH ("Crouch", Keys.C),
//    JUMP ("Jump", Keys.SPACE),

//    BOT_FORWARD ("Move bot forward", Keys.UP),
//    BOT_BACK ("Move bot back", Keys.DOWN),
//    BOT_TURN_LEFT ("Turn bot left", Keys.LEFT),
//    BOT_TURN_RIGHT ("Turn bot right", Keys.RIGHT),
//    BOT_FIRE ("Fire bot weapon", Keys.FORWARD_DEL),
//    BOT_COLLAPSE ("Bot Collapse", Keys.END),
//    BOT_REVIVE ("Bot Revive", Keys.HOME),

    TOGGLE_FULLSCREEN ("Toggle Full-Screen", Keys.F11);

    private final String description;      // action
    private final int defaultKeyCode;     // original code, used on reset
    private int keyCode;            // can be user configured

    KeyBinding(String description, int defaultKeyCode) {
        this.description = description;
        this.defaultKeyCode = defaultKeyCode;
        this.keyCode = defaultKeyCode;
    }

    public int getKeyCode(){
        return keyCode;
    }

    public void setKeyBinding(int keyCode ){
        this.keyCode = keyCode;
    }

    public void resetKeyBinding(){
        keyCode = defaultKeyCode;
    }

    public String getDescription() {
        return description;
    }

    // save key bindings to preferences file
    // call this after changing key bindings
    static public void save(){
        Preferences prefs = Gdx.app.getPreferences(Settings.title);
        for(KeyBinding binding : values()){
            // only save key binding that differ from the default
            if(binding.keyCode != binding.defaultKeyCode)
                prefs.putInteger( String.valueOf(binding), binding.keyCode);
        }
        prefs.flush();  // save to file
    }

    // load key bindings from preferences file
    // call this once on startup
    static public void load() {
        Preferences prefs = Gdx.app.getPreferences(Settings.title);
        for(KeyBinding binding : values()){
            int keycode = prefs.getInteger(String.valueOf(binding), binding.defaultKeyCode);
            binding.keyCode = keycode;
        }
    }
}
