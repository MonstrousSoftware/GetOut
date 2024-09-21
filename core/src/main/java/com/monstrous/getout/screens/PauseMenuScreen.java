package com.monstrous.getout.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.getout.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// pause menu (called from game screen on Escape key)

// Having a pause menu screen means we can change settings without restarting the game and losing progress.
// This is why we keep a reference to the active GameScreen instance, so we can return to it.

public class PauseMenuScreen extends MenuScreen {

    private GameScreen gameScreen;


    public PauseMenuScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

        TextButton resume = new TextButton("Resume", skin);
        TextButton options = new TextButton("Options", skin);
        TextButton keys = new TextButton("Keys", skin);
        TextButton stop = new TextButton("Stop", skin);

       float pad = 10f;

       screenTable.add(resume).pad(pad).row();
       screenTable.add(options).pad(pad).row();
       screenTable.add(keys).pad(pad).row();
       screenTable.add(stop).pad(pad).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in


       stage.addActor(screenTable);

        //set up for keyboard/controller navigation
        stage.clearFocusableActors();
        stage.addFocusableActor(resume);
        stage.addFocusableActor(options);
        stage.addFocusableActor(keys);
        stage.addFocusableActor(stop);
        stage.setFocusedActor(resume);
        stage.setEscapeActor(resume);
        focusActor(resume);


       options.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen(new OptionsScreen( game, gameScreen ));
           }
       });

        keys.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new KeysScreen( game, gameScreen ));
            }
        });

       resume.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen( gameScreen );
           }
       });

       stop.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               gameScreen.dispose();
               game.setScreen(new MainMenuScreen( game ));
           }
       });

   }

}
