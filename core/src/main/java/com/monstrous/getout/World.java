package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {
    private SceneAsset sceneAsset;
    private SceneAsset sceneAsset2;
    public Array<Scene> scenes;
    public Array<Scene> bullets;
    private Array<Scene> deleteList;
    public Scene patrolBot;
    public Scene bullet;
    public Colliders colliders;


    public World() {
        scenes = new Array();
        bullets = new Array<>();
        deleteList = new Array<>();
        colliders = new Colliders();
        reload();
    }

    public void reload() {
        // reset
        if(sceneAsset != null)
            sceneAsset.dispose();
        scenes.clear();

        // create scene
        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/patrolbot.gltf"));
        patrolBot = new Scene(sceneAsset.scene, "Armature");
        patrolBot.animationController.setAnimation("Idle", 1);
//        scenes.add(patrolBot);


        sceneAsset2 = new GLTFLoader().load(Gdx.files.internal("models/officeMaze.gltf"));
        //sceneAsset2 = new GLTFLoader().load(Gdx.files.internal("models/coltest.gltf"));
        Scene level = new Scene(sceneAsset2.scene);
        parseLevel( level );
        scenes.add( level  );


    }

    private void parseLevel(Scene level){
        BoundingBox bbox = new BoundingBox();
        Vector3 ctr = new Vector3();

        int count = 0;
        for(Node node : level.modelInstance.nodes ){
            node.calculateBoundingBox(bbox);
            bbox.getCenter(ctr);
            Gdx.app.log("node:", node.id + " " +bbox.toString());

            if(node.id.startsWith("Ceiling"))
                continue;
            if(node.id.startsWith("Floor"))
                continue;
            if(node.id.startsWith("OuterWall"))     // not a collider as its wall face inwards, todo boundary
                continue;

            colliders.add(node.id, bbox);
            count++;
        }
        Gdx.app.log("nodes:", ""+count);
    }


    private void animateSet( Array<Scene> set, String animationName){
        for(Scene scene : set){
            // queue the animation in case there is already one playing
            scene.animationController.queue(animationName, 0f, -1f, 1, 1f, null, 0f);
        }
    }

    private Matrix4 bulletTransform = new Matrix4();

    public Scene spawnBullet(){
        bullet = new Scene(sceneAsset.scene, "Bullet");
        bulletTransform.set(patrolBot.modelInstance.transform);
        bulletTransform.translate(-0.32f, 0.69f, 0.595f);   // offset for barrel
        bullet.modelInstance.transform.set(bulletTransform);
        //scenes.add(bullet);
        bullets.add(bullet);
        return bullet;
    }

    public static final float BULLET_SPEED = 10f;
    public static final float MAX_BULLET_DIST = 50f;    // maximum distance from origin before bullet is deleted
    private Vector3 vec = new Vector3();

    public boolean canReach( Vector3 position ) {

        String colliderId = colliders.collisionTest(position);
        if (colliderId != null) {
            Gdx.app.log("collision", colliderId);
            return false;
        }
        return true;
    }

    public void update( Vector3 cameraPosition, float deltaTime ){

//        String colliderId = colliders.collisionTest(cameraPosition);
//        if (colliderId != null) {
//            Gdx.app.log("collided!", colliderId);
//        }


        // animate bullets
        deleteList.clear();
        for(Scene bullet : bullets){
            vec.set(0,0,1f);    // forward vector
            vec.rot(bullet.modelInstance.transform);    // rotate with bullet orientation
            vec.scl(deltaTime*BULLET_SPEED);        // scale with speed and delta time
            bullet.modelInstance.transform.trn(vec);    // translate position

            bullet.modelInstance.transform.getTranslation(vec);
            if(vec.len() > MAX_BULLET_DIST)
                deleteList.add(bullet);
        }
        bullets.removeAll(deleteList, true);
        //scenes.removeAll(deleteList, true);
    }

    @Override
    public void dispose() {

        sceneAsset.dispose();
        sceneAsset2.dispose();
    }


}
