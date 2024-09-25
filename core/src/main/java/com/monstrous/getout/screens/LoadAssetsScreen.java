package com.monstrous.getout.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.getout.Settings;


// on web this screen is to get a user key press, so we can start playing sound
// also shows progress on asset loading

public class LoadAssetsScreen extends StdScreenAdapter {

    private Main game;
    private Stage stage;
    private Skin skin;
    private ProgressBar progressBar;
    private Texture texture;
    private boolean loaded;
    private Label prompt;
    private float timer;


    public LoadAssetsScreen(Main game) {
        this.game = game;

    }


    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("ui/gtho.json"));
        stage = new Stage(new ScreenViewport());

        progressBar = new ProgressBar(0f, 1.0f, 0.01f, false, skin);
        progressBar.setSize(300, 50);
        progressBar.setValue(0);

        texture =  new Texture(Gdx.files.internal("images/monstrous.png"));
        Image logo = new Image( new TextureRegion(texture));

        prompt = new Label("Continue",skin, "default");
        prompt.setColor(Color.DARK_GRAY);
        prompt.setVisible(false);

        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(logo).pad(10).row();
        screenTable.add(progressBar).row();
        screenTable.add(prompt).pad(60);
        screenTable.pack();

        stage.addActor(screenTable);

        Table screenTable2 = new Table();
        screenTable2.setFillParent(true);
        screenTable2.add(new Label(Settings.version, skin, "small")).bottom().right().expand();
        stage.addActor(screenTable2);


        loaded = false;
        timer = 0;

    }


    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);


        // load assets asynchronously
        if(!loaded) {
            loaded = game.assets.update();
            float fraction = game.assets.getProgress();
            progressBar.setValue(fraction);
        }
        else {
            prompt.setVisible(true);
            timer += deltaTime;
            if((timer > 2f && Gdx.app.getType() != Application.ApplicationType.WebGL)       // force an input for web client before proceeding
                ||  Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){

                game.onLoadingComplete();
                return;
            }
        }

        ScreenUtils.clear(.4f, .7f, .9f, 1f);
        stage.act(deltaTime);
        stage.draw();

    }

    @Override
    public void resize(int w, int h) {

        stage.getViewport().update(w, h, true);

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        texture.dispose();
    }

}
