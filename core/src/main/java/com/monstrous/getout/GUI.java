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
    private Label elementsLabel;
    private int numElements;
    private World world;
    private Image[] cards;
    private Texture[] textures;


    public GUI(Main game, World world) {
        this.world = world;
        //this.assets = assets;

        stage = new Stage(new ScreenViewport());
        skin = game.assets.SKIN;
        numElements = -1;
        cards = new Image[4];
        textures = new Texture[5];
        textures[0] = new Texture(Gdx.files.internal("images/cardEmpty.png"));
        textures[1]  = new Texture(Gdx.files.internal("images/cardEarth.png"));
        textures[2]  = new Texture(Gdx.files.internal("images/cardWater.png"));
        textures[3]  = new Texture(Gdx.files.internal("images/cardAir.png"));
        textures[4]  = new Texture(Gdx.files.internal("images/cardFire.png"));

        //rebuild();
    }


    private void rebuild() {
        stage.clear();

        String labelType  = "default";

        Table screenTable = new Table();
        screenTable.setFillParent(true);


        fpsLabel = new Label("", skin, labelType);
        screenTable.add(fpsLabel).left();

        elementsLabel = new Label("", skin, labelType);
        screenTable.add(elementsLabel).right().expandX();
        screenTable.row();

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

        if(world.numElements != numElements) {
            // compare with local variable to avoid string concatenation at every frame
            elementsLabel.setText("ELEMENTS: " + world.numElements);
            numElements = world.numElements;
            rebuild();
        }
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
