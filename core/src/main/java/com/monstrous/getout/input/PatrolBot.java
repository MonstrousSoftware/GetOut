package com.monstrous.getout.input;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.scene3d.scene.Scene;

// manages robot patrol path using a spline

public class PatrolBot {
    private Scene scene;
    private CatmullRomSpline<Vector3> spline;
    private float time;
    private Vector3 pos;
    private Vector3 fwd;

    public PatrolBot(Scene scene, Array<Vector3> wayPoints) {
        this.scene = scene;
        time = 0;

        // calculate spline
        Vector3[] dataSet = new Vector3[wayPoints.size];
        for(int i = 0; i < wayPoints.size; i++)
            dataSet[i] = wayPoints.get(i);
        spline = new CatmullRomSpline<>(dataSet, true);

        pos = new Vector3();
        fwd = new Vector3();
        scene.animationController.setAnimation("Forward", -1);
    }

    public void update(float deltaTime ) {
        time += deltaTime;
        float t = time;
        t /= 20f;
        t = t % 1f;     // keep in range [0-1]

        spline.valueAt(pos, t);
        // todo: direction is not okay yet
        spline.derivativeAt(fwd, t);
//        Gdx.app.log("derivative", tmpVec.toString());
        scene.modelInstance.transform.setToRotation(Vector3.Z, fwd);
        scene.modelInstance.transform.setTranslation(pos);
    }
}
