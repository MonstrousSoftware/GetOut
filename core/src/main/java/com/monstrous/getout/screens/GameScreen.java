package com.monstrous.getout.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Cursor;
import com.monstrous.getout.*;
import com.monstrous.getout.input.PatrolBotController;


public class GameScreen extends ScreenAdapter {
    private final Main game;
    public World world;
    public GameView gameView;
    private ColliderView colliderView;
    private PatrolBotController botController;
    private GUI gui;

    public GameScreen(Main game) {
        this.game = game;
    }


    @Override
    public void show() {

        gameView = new GameView();
        world = new World();
        colliderView = new ColliderView( world );
        gui = new GUI(game);
        gui.showMessage("WELCOME", false);

        botController = new PatrolBotController();

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(botController);
        im.addProcessor(gameView.camController);
        Gdx.input.setInputProcessor(im);

        // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        // on teavm setCursorCatched() doesn't work so hide the cursor and let the user turn with the keyboard
        // (you can turn a bit with the mouse, until it reaches the side of the canvas).
        if (Gdx.app.getType() == Application.ApplicationType.WebGL)
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);     // hide cursor


    }

    @Override
    public void render(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            game.setScreen(new MainMenuScreen(game));
            return;
        }


        botController.update(world, deltaTime, gameView.camera);
        world.update(gameView.camera.position, deltaTime);
        gameView.render( world, deltaTime );

        if(Settings.showColliders)
            colliderView.render(gameView.camera);

        gui.render(deltaTime);
      }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        gameView.resize(width, height);
        gui.resize(width, height);
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        gameView.dispose();
        world.dispose();
        colliderView.dispose();
        Gdx.input.setCursorCatched(false);
        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);     // show cursorGUI
        gui.dispose();
    }
}
