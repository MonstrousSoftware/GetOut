package com.monstrous.getout.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

// manages collection of robots

public class PatrolBots implements Disposable {
    private Array<PatrolBot> bots;

    public PatrolBots() {
        bots = new Array<>();
    }

    public void add( PatrolBot bot ){
        bots.add(bot);
    }

//    public void setPatrolBot(World world, Scene scene, Array<Vector2> wayPoints){
//        PatrolBot bot = new PatrolBot(world, scene, wayPoints);
//        bots.add(bot);
//    }

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
