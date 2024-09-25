package com.monstrous.getout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.getout.Settings;
import com.monstrous.getout.input.MyControllerMenuStage;
import de.golfgl.gdx.controllers.ControllerMenuStage;

import static com.badlogic.gdx.Gdx.input;


// abstract menu screen to derive from, this is the base class for different menu screens
// Provides a common look and feel for all menu screens


public class MenuScreen extends StdScreenAdapter {

    protected Main game;
    protected Viewport viewport;
    protected MyControllerMenuStage stage;      // from gdx-controllers-utils
    protected Skin skin;
    private MenuBackground background;


    public MenuScreen(Main game) {
        this.game = game;
        viewport = new ScreenViewport();

        skin = game.assets.SKIN;
        stage = new MyControllerMenuStage(viewport);          // we can use this even without controllers
        input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        rebuild();

        input.setCatchKey(Input.Keys.UP, true);
        input.setCatchKey(Input.Keys.DOWN, true);
        if(Settings.supportControllers)
            game.controllerToInputAdapter.setInputProcessor(stage); // forward controller input to stage

        background = new MenuBackground();
    }

    protected void playSelectNoise() {
        game.assets.MENU_CLICK.play();
    }

    // override this!
    protected void rebuild() {

    }

    // This is like stage.setFocusedActor(actor) but works when actor is not hittable.
    // (perhaps not yet while we rebuild the stage?)
    // This solves the issue that the focused menu item is not highlighted when the menu is first shown.
    //
    public void focusActor(Actor actor) {
        InputEvent event = new InputEvent();
        event.setType(InputEvent.Type.enter);
        event.setStage(stage);
        event.setPointer(-1);
        event.setButton(-1);
        event.setStageX(0);
        event.setStageY(0);
        actor.fire(event);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        background.render();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.

        //Gdx.app.log("MenuScreen","resize "+width+" x "+height);
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        rebuild();
        background.resize(width, height);
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        stage.dispose();
        background.dispose();
    }
}
