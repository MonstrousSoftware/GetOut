package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {

    public Skin     SKIN;

    public Sound    MOTOR;
    public Sound    SHOT;
    public Sound    BUZZ;
    public Sound    MENU_CLICK;
    public Sound    FOOT_STEPS;
    public Sound    RUNNING;

    public Music    MUSIC;

    private AssetManager assets;


    public Assets() {
        Gdx.app.log("Assets constructor", "");
        assets = new AssetManager();

        assets.load("ui/fright/fright.json", Skin.class);

        assets.load("sounds/bullet.mp3", Sound.class);
        assets.load("sounds/buzz.mp3", Sound.class);
        assets.load("sounds/motor.mp3", Sound.class);
        assets.load("sounds/click_002.ogg", Sound.class);
        assets.load("sounds/footsteps.ogg", Sound.class);
        assets.load("sounds/running.mp3", Sound.class);

        assets.load("music/bossa-nova-echo.ogg", Music.class);
    }


    public boolean update() {
        return assets.update();
    }


    public void finishLoading() {
        assets.finishLoading();
        SKIN = assets.get("ui/fright/fright.json");

        SHOT = assets.get("sounds/bullet.mp3");
        BUZZ = assets.get("sounds/buzz.mp3");
        MOTOR = assets.get("sounds/motor.mp3");
        MENU_CLICK = assets.get("sounds/click_002.ogg");
        FOOT_STEPS = assets.get("sounds/footsteps.ogg");
        RUNNING = assets.get("sounds/running.mp3");

        MUSIC = assets.get("music/bossa-nova-echo.ogg");
    }

    public float getProgress() {
        return assets.getProgress();
    }


    public <T> T get(String name ) {
        return assets.get(name);
    }

    @Override
    public void dispose() {
        Gdx.app.log("Assets dispose()", "");
        assets.dispose();
        assets = null;
    }
}
