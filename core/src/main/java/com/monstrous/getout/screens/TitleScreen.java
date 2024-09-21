package com.monstrous.getout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



// this version loads the title from an image file

public class TitleScreen extends StdScreenAdapter {

    private Main game;
    private SpriteBatch batch;
    private int width;
    private int height;
    private Texture titleTexture;
    private float titleWidth, titleHeight;
    private Viewport viewport;
    private float timer;


    public TitleScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        titleTexture = new Texture("images/title.png");
        titleWidth = titleTexture.getWidth();
        titleHeight = titleTexture.getHeight();
        viewport = new ScreenViewport();
        timer = 0;
   }


    @Override
    public void render(float delta) {
        timer += delta;
        if(timer > 4f || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) ||Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        batch.setProjectionMatrix( viewport.getCamera().combined );
        batch.draw(titleTexture, 0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        this.width = width;
        this.height = height;
        //batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);
        viewport.update(width, height, true);

    }



    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
