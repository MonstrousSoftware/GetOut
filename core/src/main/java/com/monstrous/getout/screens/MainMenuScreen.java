package com.monstrous.getout.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.getout.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// main menu

public class MainMenuScreen extends MenuScreen {


    public MainMenuScreen(Main game) {
        super(game);
    }


    @Override
    protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       TextButton play = new TextButton("Play Game", skin);
       TextButton options = new TextButton("Options", skin);
       TextButton keys = new TextButton("Keys", skin);

       TextButton quit = new TextButton("Quit", skin);

       float pad = 10f;
       screenTable.add(play).pad(pad).row();
       screenTable.add(options).pad(pad).row();
       screenTable.add(keys).pad(pad).row();

       if(!(Gdx.app.getType() == Application.ApplicationType.WebGL) )
            screenTable.add(quit).pad(pad).row();


       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in
       stage.addActor(screenTable);

        Table screenTable2 = new Table();
        screenTable2.setFillParent(true);
        screenTable2.add(new Label(Settings.version, skin, "small")).pad(20).right().bottom().expand();
        stage.addActor(screenTable2);

       play.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen(new PreGameScreen( game ));
           }
       });

       options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new OptionsScreen( game, null ));
            }
        });

        keys.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new KeysScreen( game, null ));
            }
        });


       quit.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               Gdx.app.exit();
           }
       });

       // set up for keyboard/controller navigation
        stage.clearFocusableActors();
        stage.addFocusableActor(play);
        stage.addFocusableActor(options);
        stage.addFocusableActor(keys);
        stage.addFocusableActor(quit);
        stage.setFocusedActor(play);
        focusActor(play);

   }

}
