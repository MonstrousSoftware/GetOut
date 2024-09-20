package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.getout.screens.Main;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

// note: a resize will clear any active messages

public class GUI implements Disposable {

    private Stage stage;
    private Skin skin;
    private Label fpsLabel;
    private World world;
    private Image[] cards;
    private Texture[] textures;
    private Table messageTable;

    public GUI(Main game, World world) {
        Gdx.app.log("GUI constructor", "");
        this.world = world;
        //this.assets = assets;

        stage = new Stage(new ScreenViewport());
        skin = game.assets.SKIN;
        cards = new Image[4];
        textures = new Texture[5];
        textures[0] = new Texture(Gdx.files.internal("images/cardEmpty.png"));
        textures[1]  = new Texture(Gdx.files.internal("images/cardEarth.png"));
        textures[2]  = new Texture(Gdx.files.internal("images/cardWater.png"));
        textures[3]  = new Texture(Gdx.files.internal("images/cardAir.png"));
        textures[4]  = new Texture(Gdx.files.internal("images/cardFire.png"));

        messageTable = new Table();
        //rebuild();
    }


    public void rebuild() {
        Gdx.app.log("GUI rebuild", "");
        stage.clear();

        String labelType  = "default";

        Table screenTable = new Table();
        screenTable.setFillParent(true);


        fpsLabel = new Label("", skin, labelType);
        screenTable.add(fpsLabel).left();

        screenTable.bottom().left();
        screenTable.pack();

        stage.addActor(screenTable);

        Table screenTable2 = new Table();
        screenTable2.setFillParent(true);



        for(int i = 0; i < 4; i++){
            Image card;
            if(world.foundCard[i])
                card = new Image(textures[i+1]);
            else
                card = new Image(textures[0]);
            screenTable2.add(card);
        }
        screenTable2.bottom().right();
        screenTable2.pack();

        stage.addActor(screenTable2);
    }

    public void showMessage( String text ){
        messageTable.clear();       // any old message will be overwritten
        messageTable.setFillParent(true);

        Label message = new Label(text, skin);

        messageTable.align(Align.top);
        messageTable.add(message).pad(50);
        messageTable.pack();

        messageTable.setColor(1,1,1,0);       // set alpha to zero
        messageTable.addAction(sequence(fadeIn(2f), delay(1f), fadeOut(1f), removeActor()));           // fade in .. fade out, then remove this actor

        stage.addActor(messageTable);
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
