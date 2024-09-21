package com.monstrous.getout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerPowerLevel;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.getout.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// todo there is a bug in ControllerMenuStage that up/down breaks if elements lengths differ.
// that is why Cam Stab is skipped

public class OptionsScreen extends MenuScreen {
    private GameScreen gameScreen;    // to keep track where we were called from
    private Controller controller;
    private Label controllerLabel;
    //private Table screenTable;


    public OptionsScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        super.show();

        if(!Settings.supportControllers)
            return;

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("Controllers", controller.getName());
        }


        controller = Controllers.getCurrent();
        if(controller != null ) {

            Gdx.app.log("current controller", controller.getName());
            Gdx.app.log("unique id", controller.getUniqueId());
            Gdx.app.log("is connected", "" + controller.isConnected());
            ControllerPowerLevel powerLevel = controller.getPowerLevel();
            Gdx.app.log("power level", "" + powerLevel.toString());
            Gdx.app.log("can vibrate", "" + controller.canVibrate());
            if (controller.canVibrate()) {
                controller.startVibration(500, 1f);
            }
        }
        else
            Gdx.app.log("current controller", "none");
   }

   private void checkControllerChanges() {
       Controller currentController = Controllers.getCurrent();
       if(currentController != controller ) {
           controller = currentController;
           if (controller != null) {

               Gdx.app.log("current controller", controller.getName());
               Gdx.app.log("unique id", controller.getUniqueId());
               Gdx.app.log("is connected", "" + controller.isConnected());
               ControllerPowerLevel powerLevel = controller.getPowerLevel();
               Gdx.app.log("power level", "" + powerLevel.toString());
               Gdx.app.log("can vibrate", "" + controller.canVibrate());
               if (controller.canVibrate()) {
                   controller.startVibration(500, 1f);
               }
           } else
               Gdx.app.log("current controller", "none");

           if(controller != null)
               controllerLabel.setText(controller.getName());
           else
               controllerLabel.setText("None");
       }
   }

   @Override
   protected void rebuild() {
       stage.clear();

       Table screenTable = new Table();
       screenTable.setFillParent(true);

       CheckBox music = new CheckBox("Music", skin);
       music.setChecked(Settings.playMusic);

       CheckBox fullScreen = new CheckBox("Full Screen", skin);
       fullScreen.setChecked(Settings.fullScreen);

       CheckBox invertLook = new CheckBox("Invert Look", skin);
       invertLook.setChecked(Settings.invertLook);

       CheckBox freeLook = new CheckBox("Free Look", skin);
       freeLook.setChecked(Settings.freeLook);

       CheckBox vcr = new CheckBox("VCR effect", skin);
       vcr.setChecked(Settings.postFilter);

       CheckBox camStab = new CheckBox("Camera stabilisation ", skin);
       camStab.setChecked(Settings.camStabilisation);

       CheckBox showColliders = new CheckBox("Show colliders", skin);
       showColliders.setChecked(Settings.showColliders);

       CheckBox showFPS = new CheckBox("Show frames per second", skin);
       showFPS.setChecked(Settings.showFPS);

       controllerLabel = new Label("None", skin);
       if(controller != null)
           controllerLabel.setText(controller.getName());

       TextButton done = new TextButton("Done", skin);

       int pad = 10;

       screenTable.add(music).pad(pad).left().row();
       screenTable.add(fullScreen).pad(pad).left().row();
       screenTable.add(invertLook).pad(pad).left().row();
       screenTable.add(freeLook).pad(pad).left().row();
       screenTable.add(vcr).pad(pad).left().row();
       screenTable.add(camStab).pad(pad).left().row();
       screenTable.add(showColliders).pad(pad).left().row();
       screenTable.add(showFPS).pad(pad).left().row();
       screenTable.add(new Label("Controller: ", skin)).pad(pad).left();
       screenTable.add(controllerLabel).left().row();
       screenTable.add(done).pad(20).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in

       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
       //if(Settings.supportControllers) {
       //    ControllerMenuStage cStage = (ControllerMenuStage) stage;
       stage.clearFocusableActors();
       stage.addFocusableActor(music);
       stage.addFocusableActor(fullScreen);
       stage.addFocusableActor(invertLook);
       stage.addFocusableActor(freeLook);
       stage.addFocusableActor(vcr);
       stage.addFocusableActor(camStab);
       stage.addFocusableActor(showColliders);
       stage.addFocusableActor(showFPS);
           //stage.addFocusableActor(controllerLabel);
       stage.addFocusableActor(done);
       stage.setFocusedActor(music);
       stage.setEscapeActor(done);
       focusActor(music);
       //}


       music.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.playMusic = music.isChecked();
               if(!Settings.playMusic && game.assets.MUSIC.isPlaying())
                   game.assets.MUSIC.stop();
               else if(Settings.playMusic && !game.assets.MUSIC.isPlaying() && gameScreen != null) {
                   game.assets.MUSIC.play();
                   game.assets.MUSIC.setLooping(true);
                   game.assets.MUSIC.setVolume(0.5f);
               }
           }
       });
       fullScreen.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.fullScreen = fullScreen.isChecked();
               Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
               if(Settings.fullScreen)
                   Gdx.graphics.setFullscreenMode(currentMode);
               else
                   Gdx.graphics.setWindowedMode(1200, 800);         // todo
           }
       });

       invertLook.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.invertLook = invertLook.isChecked();
           }
       });
       freeLook.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.freeLook = freeLook.isChecked();
           }
       });
       vcr.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.postFilter = vcr.isChecked();
           }
       });
       camStab.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.camStabilisation = camStab.isChecked();
           }
       });
       showColliders.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.showColliders = showColliders.isChecked();
           }
       });
       showFPS.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.showFPS = showFPS.isChecked();
           }
       });

       done.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               if(gameScreen == null)
                   game.setScreen(new MainMenuScreen( game ));
               else
                   game.setScreen(new PauseMenuScreen( game, gameScreen ));
           }
       });

   }


    @Override
    public void render(float delta) {
        if(Settings.supportControllers)
            checkControllerChanges();
        super.render(delta);
    }


}
