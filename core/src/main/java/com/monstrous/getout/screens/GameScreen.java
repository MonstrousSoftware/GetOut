package com.monstrous.getout.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Cursor;
import com.monstrous.getout.*;
import com.monstrous.getout.collision.ColliderView;
import com.monstrous.getout.input.KeyBinding;
import com.monstrous.getout.input.MyControllerAdapter;

import javax.swing.*;


public class GameScreen extends StdScreenAdapter {
    private final Main game;
    public World world;
    public GameView gameView;
    private ColliderView colliderView;
    private GUI gui;
    private int numElements = 0;
    private boolean completed;
    public Music music;
    private MyControllerAdapter controllerAdapter;
    private Controller currentController;

    public GameScreen(Main game) {
        this.game = game;
        completed = false;

        world = new World(game);
        gameView = new GameView(game.assets);  // need to keep persistent because it holds camera (player) position

        music = Gdx.audio.newMusic(Gdx.files.internal("music/bossa-nova-echo.ogg"));
        if(Settings.playMusic){
            // don't use from Assets, broken on web?
            music.play();
            music.setLooping(true);
            music.setVolume(0.5f);
        }
    }


    @Override
    public void show() {
        gui = new GUI(game, world);
        colliderView = new ColliderView( world );

        // controller
        if (Settings.supportControllers) {
            currentController = Controllers.getCurrent();
            if (currentController != null) {
                Gdx.app.log("current controller", currentController.getName());
                controllerAdapter = new MyControllerAdapter(gameView.camController);
                // we define a listener that listens to all controllers, in case the current controller gets disconnected and reconnected
                Controllers.removeListener(game.controllerToInputAdapter);          // remove adapter for menu navigation with controller
                Controllers.addListener(controllerAdapter);                         // add adapter for game play with controller
            } else
                Gdx.app.log("current controller", "none");
        }


        // setting may have changed via pause menu
        if(Settings.playMusic)
            music.play();
        else
            music.stop();

        if(Settings.fullScreen)
            toFullScreen();
//        else
//            toWindowedScreen();

        InputMultiplexer im = new InputMultiplexer();
        //im.addProcessor(botController);
        im.addProcessor(gameView.camController);
        Gdx.input.setInputProcessor(im);

        // hide the mouse cursor and fix it to screen centre, so it doesn't go out the window canvas
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        // on teavm setCursorCatched() doesn't work so hide the cursor and let the user turn with the keyboard
        // (you can turn a bit with the mouse, until it reaches the side of the canvas).
        if (Gdx.app.getType() == Application.ApplicationType.WebGL)
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);     // hide cursor
        world.patrolBots.resumeSound();
        world.bullets.resumeSound();    // in case we come back from pause menu
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        if (world.menuRequested){
            world.menuRequested = false;
            game.setScreen(new PauseMenuScreen(game, this));
            return;
        }
        // restart game
        if(world.restartRequested){
            world.restartRequested = false;
            this.dispose();
            game.setScreen(new GameScreen(game));
            return;
        }

        world.update(gameView.camera, deltaTime);
        gameView.render( world, deltaTime );

        if(Settings.showColliders)
            colliderView.render(gameView.camera);

        if(world.numElements != numElements){   // force gui rebuild when new element is found
            numElements = world.numElements;
            gui.rebuild();
        }

        if(world.message != null){
            if(world.health <= 0 || completed )
                gui.showMessage(world.message,9999f);       // leave the message forever
            else
                gui.showMessage(world.message);
            world.message = null;
        }

        // play end music on game completion
        if(!completed && world.completed && Settings.playMusic){
            music.stop();
            music.dispose();
            music = Gdx.audio.newMusic(Gdx.files.internal("music/elevator-music-bossa-nova.ogg"));  // bright undistorted music
            music.play();
            music.setLooping(false);
            music.setVolume(0.7f);
            completed = true;
        }

        gui.render(deltaTime);
      }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        Gdx.app.log("GameScreen.resize", ""+width+" x "+height);
        gameView.resize(width, height);
        gui.resize(width, height);
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        // dispose what is created in show()
        Gdx.input.setCursorCatched(false);
        if(Gdx.app.getType() == Application.ApplicationType.WebGL)
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);     // show cursorGUI

        // pause sounds
        world.patrolBots.pauseSound();
        world.bullets.pauseSound();

        colliderView.dispose();
        gui.dispose();

        // toggle between controller adapters: from the game one to the menu one
        if(currentController != null) {
            Controllers.removeListener(controllerAdapter);          // remove adapter for game play with controller
            Controllers.addListener(game.controllerToInputAdapter); // adapter for menu navigation with controller
        }

    }

    @Override
    public void dispose() {
        // dispose what is created in constructor
        // called from PauseMenuScreen when the game is quit

        music.stop();
        music.dispose();


        gameView.dispose();
        world.dispose();
    }
}
