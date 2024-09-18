package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.getout.screens.Main;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GUI implements Disposable {

    private Stage stage;
    private Skin skin;
    private Label fpsLabel;

    public GUI(Main game) {
        //this.assets = assets;

        stage = new Stage(new ScreenViewport());
        skin = game.assets.SKIN;
        //skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    }


    private void rebuild() {
        stage.clear();

        String labelType  = "default";

        Table screenTable = new Table();
        screenTable.setFillParent(true);


        fpsLabel = new Label("", skin, labelType);
        screenTable.add(fpsLabel).left();
        screenTable.row();

        screenTable.bottom().left();
        screenTable.pack();

        stage.addActor(screenTable);
    }

    public void showMessage( String text, boolean priority ){
        Table screenTable = new Table();
        screenTable.setFillParent(true);

        Label message = new Label(text, skin);

        screenTable.align(Align.bottom);
        if(priority)
            screenTable.align(Align.top);
        screenTable.add(message).pad(50);
        screenTable.pack();

        if(!priority) {
            screenTable.setColor(1,1,1,0);                   // set alpha to zero
            screenTable.addAction(sequence(fadeIn(3f), delay(2f), fadeOut(1f), removeActor()));           // fade in .. fade out, then remove this actor
        }
        else {
            screenTable.setColor(Color.WHITE);
            screenTable.addAction(sequence(delay(0.5f), removeActor()));           // fade in .. fade out, then remove this actor
        }

        stage.addActor(screenTable);

    }

    private void updateLabels(){
        if(Settings.showFPS) {
            float fps = Gdx.graphics.getFramesPerSecond();
            fpsLabel.setText((int) fps);
        }
        else
            fpsLabel.setText("");
    }

    public void render(float deltaTime) {
        updateLabels();

        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        Gdx.app.log("GUI resize", "gui " + width + " x " + height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }



    @Override
    public void dispose () {
        Gdx.app.log("GUI dispose()", "");
        stage.dispose();
    }
}
