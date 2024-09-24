package com.monstrous.getout.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.monstrous.getout.Assets;
import com.monstrous.getout.Settings;
import com.monstrous.getout.input.KeyBinding;
import com.monstrous.getout.input.MyControllerMappings;
import com.monstrous.getout.screens.GameScreen;
import de.golfgl.gdx.controllers.mapping.ControllerToInputAdapter;

import javax.xml.crypto.dsig.keyinfo.KeyName;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static Assets assets;
    public ControllerToInputAdapter controllerToInputAdapter;


    @Override
    public void create() {
        assets = new Assets();
        KeyBinding.load();
        Gdx.app.log("Gdx version", Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));

        if (Settings.supportControllers) {
            controllerToInputAdapter = new ControllerToInputAdapter(new MyControllerMappings());
            // bind controller events to keyboard keys
            controllerToInputAdapter.addButtonMapping(MyControllerMappings.BUTTON_A, Input.Keys.ENTER);
            controllerToInputAdapter.addAxisMapping(MyControllerMappings.AXIS_VERTICAL, Input.Keys.UP, Input.Keys.DOWN);
            Controllers.addListener(controllerToInputAdapter);
        }

        setScreen( new LoadAssetsScreen(this) );
    }

    public void onLoadingComplete(){
        assets.finishLoading();

        //assets.MUSIC.play();
        if(Settings.release)
            setScreen(new TitleScreen(this));
        else
            setScreen(new GameScreen(this));
    }

    @Override
    public void dispose() {
        Gdx.app.log("Main.dispose", "");
        KeyBinding.save();
        assets.dispose();
        super.dispose();
    }
}
