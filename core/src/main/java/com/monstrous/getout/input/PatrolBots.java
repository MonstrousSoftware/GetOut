package com.monstrous.getout.input;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.scene3d.scene.Scene;

// manages collection of robots

public class PatrolBots {
    private Array<PatrolBot> bots;

    public PatrolBots() {
        bots = new Array<>();
    }

    public void setPatrolBot(Scene scene, Array<Vector3> wayPoints){
        PatrolBot bot = new PatrolBot(scene, wayPoints);
        bots.add(bot);
    }

    public void update(float deltaTime ) {
        for(PatrolBot bot : bots)
            bot.update(deltaTime);
    }

}
