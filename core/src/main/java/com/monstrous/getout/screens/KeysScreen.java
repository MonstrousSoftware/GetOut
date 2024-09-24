package com.monstrous.getout.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.monstrous.getout.input.KeyBinding;

//import org.lwjgl.glfw.GLFW;

// key bindings menu
// shows key bindings and allows the user to modify them

public class KeysScreen extends MenuScreen implements InputProcessor {
    // can't extend InputAdapter because we are already extending MenuScreen.

    private TextButton pressedButton;
    private KeyBinding selectedBinding;
    private Label debugLabel;
    private GameScreen gameScreen;

    public KeysScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(this);
        Gdx.input.setInputProcessor(im);
        selectedBinding = null;
    }

    private String keyName( int keycode ){
        return Input.Keys.toString(keycode);
        //return Main.keyName.getKeyName(keycode);
    }

    @Override
    public void rebuild() {
        stage.clear();
        String style = "default";

        Table screenTable = new Table();
        screenTable.setFillParent(true);

        // set up for keyboard/controller navigation
        stage.clearFocusableActors();

        Table keyTable = new Table();
        for(KeyBinding binding : KeyBinding.values()) {
            keyTable.add(new Label(binding.getDescription(), skin, style)).left();
            int keycode = binding.getKeyCode();


            String text = keyName(keycode);
            //String text = Lwjgl3Launcher.get

            //String text = Input.Keys.toString(keycode);
            if(keycode == 0)    // unbound action
                text = "- -";
            TextButton button = new TextButton(text, skin, "framed");
            keyTable.add(button);
            keyTable.row();

            stage.addFocusableActor(button);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    rebind(button, binding);
                }
            });


        }
        // Note: Input.Keys.toString() follows US keyboard layout in naming keys
        // It should really present the name following the regional keyboard setting.
        // Waiting for libGDX issue #6962 to be resolved.

        TextButton reset = new TextButton(" RESET ", skin, "framed");
        TextButton okay = new TextButton(" OK ", skin, "framed");

        debugLabel = new Label("To modify a key binding, click a button", skin, "small");

        screenTable.top();
        screenTable.add(keyTable).pad(50).row();
        screenTable.add(debugLabel).pad(20).row();
        screenTable.add(reset).width(200).pad(10).row();
        screenTable.add(okay).width(200).pad(10).row();
        screenTable.pack();

        stage.addActor(screenTable);

        // set up for keyboard/controller navigation
        stage.addFocusableActor(reset);
        stage.addFocusableActor(okay);
        stage.setFocusedActor(okay);
        focusActor(okay);

        okay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                KeyBinding.save();          // save changes to file
                if(gameScreen != null)
                    game.setScreen(new PauseMenuScreen( game, gameScreen ));
                else
                    game.setScreen(new MainMenuScreen( game ));
            }
        });

        reset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                for(KeyBinding binding : KeyBinding.values())
                    binding.resetKeyBinding();
                rebuild();  // update button labels
            }
        });

    }

    private void rebind(TextButton button, KeyBinding binding){
        pressedButton = button;
        pressedButton.setText("???");
        pressedButton.setColor(Color.RED);
        selectedBinding = binding;
        debugLabel.setText("Press the key to assign to this action (ESC to cancel)");
    }


    @Override
    public boolean keyDown(int keycode) {
        if(selectedBinding == null)
            return false;
        //debugLabel.setText("keycode : "+keycode);
        if(keycode != Input.Keys.ESCAPE) {
            selectedBinding.setKeyBinding(keycode);
            removeDupes(selectedBinding, keycode);
        }
        pressedButton.setText(keyName(selectedBinding.getKeyCode()));
        pressedButton.setColor(Color.WHITE);
        selectedBinding = null;

        return true;
    }



    // clear other keybindings to the same keycode
    private void removeDupes(KeyBinding changedBinding, int keycode ) {
        boolean changed = false;
        for(KeyBinding binding : KeyBinding.values()){
            if(binding == changedBinding)
                continue;
            if(binding.getKeyCode() == keycode){
                binding.setKeyBinding(0);
                changed = true;
            }
        }
        if(changed)
            rebuild();
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }


}
