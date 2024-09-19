package com.monstrous.getout.input;

// manages robot patrol paths using splines

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.scene3d.scene.Scene;

public class BotPatrols {
    private float time;
    private Vector3 tmpVec = new Vector3();
    private Vector3 pos = new Vector3();
    private Array<Vector3> wayPoints;    // one robot
    private CatmullRomSpline<Vector3> robotSpline;
    private Scene patrolBot;


    public BotPatrols() {
        wayPoints = new Array<>();
        time = 0;
    }

    public void setPatrolBot(Scene scene, Array<Vector3> wayPoints){
        patrolBot = scene;
        patrolBot.animationController.setAnimation("Forward", -1);

        // calculate spline
        Vector3[] dataSet = new Vector3[wayPoints.size];
        for(int i = 0; i < wayPoints.size; i++)
            dataSet[i] = wayPoints.get(i);
        robotSpline = new CatmullRomSpline<>(dataSet, true);
    }


    private void followSpline(float t){
        t /= 20f;
        t = t % 1f;     // keep in range [0-1]

        robotSpline.valueAt(pos, t);
        // todo: direction is not okay yet
        robotSpline.derivativeAt(tmpVec, t);
//        Gdx.app.log("derivative", tmpVec.toString());
        patrolBot.modelInstance.transform.setToRotation(Vector3.Z, tmpVec);
        patrolBot.modelInstance.transform.setTranslation(pos);
    }

    public void update(float deltaTime ) {

        time += deltaTime;
        followSpline(time);
    }

}
