package com.monstrous.getout.input;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.World;
import net.mgsx.gltf.scene3d.scene.Scene;

// manages collection of robots

public class PatrolBots implements Disposable {
    private Array<PatrolBot> bots;

    public PatrolBots() {
        bots = new Array<>();
    }

    public void setPatrolBot(World world, Scene scene, Array<Vector3> wayPoints){
        PatrolBot bot = new PatrolBot(world, scene, wayPoints);
        bots.add(bot);
    }

    public void update(float deltaTime, Camera camera ) {
        for(PatrolBot bot : bots)
            bot.update(deltaTime, camera);
    }

    public void pauseSound(){
        for(PatrolBot bot : bots)
            bot.pauseSound();
    }

    public void resumeSound(){
        for(PatrolBot bot : bots)
            bot.resumeSound();
    }

    @Override
    public void dispose(){
        for(PatrolBot bot : bots)
            bot.dispose();
    }

}
