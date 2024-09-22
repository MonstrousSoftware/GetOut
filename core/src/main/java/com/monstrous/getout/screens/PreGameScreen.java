package com.monstrous.getout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.monstrous.getout.Settings;
import com.monstrous.getout.filters.PostProcessor;

import static com.badlogic.gdx.Gdx.gl;

public class PreGameScreen extends StdScreenAdapter {

    private Main game;

    private SpriteBatch batch;
    private OrthographicCamera cam;
    private int width, height;
    private float timer = 1.0f;
    private Texture titleTexture;
    private FrameBuffer fbo;
    private PostProcessor filter;

    public PreGameScreen(Main game) {
        Gdx.app.log("PreGameScreen constructor", "");
        this.game = game;
        titleTexture = new Texture("images/start.png");
        filter = new PostProcessor();
    }


    @Override
    public void show() {
        Gdx.app.log("PreGameScreen show()", "");

        batch = new SpriteBatch();
        cam = new OrthographicCamera();

        if(Settings.fullScreen)
            toFullScreen();
    }


    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        timer -= deltaTime;
        if(timer <= 0) {
            game.setScreen(new GameScreen(game));
            return;
        }

        cam.update();

        fbo.begin();
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(titleTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        fbo.end();
        filter.render(fbo);

    }

    @Override
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        Gdx.app.log("PreGameScreen resize()", "");
        cam.setToOrtho(false, width, height);
        if(fbo != null)
            fbo.dispose();
        if(width*height > 0) {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            filter.resize(width, height);
        }
    }

    @Override
    public void hide() {
        Gdx.app.log("PreGameScreen hide()", "");
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log("PreGameScreen dispose()", "");
        batch.dispose();
        fbo.dispose();
        filter.dispose();
    }

}
