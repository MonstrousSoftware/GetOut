package com.monstrous.getout.screens;

import com.badlogic.gdx.Game;
import com.monstrous.getout.Assets;
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
        assets.finishLoading();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        assets.dispose();
        super.dispose();
    }
}
