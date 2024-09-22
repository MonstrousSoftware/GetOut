package com.monstrous.getout.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.utils.Array;
import com.monstrous.getout.Assets;
import com.monstrous.getout.Settings;
import com.monstrous.getout.input.KeyBinding;
import com.monstrous.getout.screens.GameScreen;

import javax.xml.crypto.dsig.keyinfo.KeyName;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public static Assets assets;
    static public KeyName keyName;


    @Override
    public void create() {
        assets = new Assets();
        KeyBinding.load();
        Gdx.app.log("Gdx version", Version.VERSION);
        Gdx.app.log("OpenGL version", Gdx.gl.glGetString(Gdx.gl.GL_VERSION));
        setScreen( new LoadAssetsScreen(this) );
    }

    public void onLoadingComplete(){
        assets.finishLoading();
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
