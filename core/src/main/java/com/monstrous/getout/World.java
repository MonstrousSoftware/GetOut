package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.collision.Collider;
import com.monstrous.getout.collision.Colliders;
import com.monstrous.getout.input.Bullet;
import com.monstrous.getout.input.Bullets;
import com.monstrous.getout.input.PatrolBots;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {
    static final float PLAYER_RADIUS = .5f;

    private SceneAsset sceneAsset;
    private SceneAsset sceneAsset2;
    public Array<Scene> scenes;
    public Bullets bullets;
    public Colliders colliders;
    public int numElements;
    public boolean[] foundCard;
    public Collider exitDoor;
    public PatrolBots patrolBots;
    public String message = null;   // to show in GUI via GameScreen
    public float health;
    public float batteryLevel;  // 0..100
    public float deathTimer;

    public World() {
        scenes = new Array();
        bullets = new Bullets();

        colliders = new Colliders();
        patrolBots = new PatrolBots();
        reload();
    }

    public void reload() {
        // reset
        if(sceneAsset != null)
            sceneAsset.dispose();
        scenes.clear();

        // create scene
        sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/patrolbot.gltf"));
//        patrolBot = new Scene(sceneAsset.scene, "Armature");
//        //patrolBot.animationController.setAnimation("Idle", 1);
//        scenes.add(patrolBot);

        sceneAsset2 = new GLTFLoader().load(Gdx.files.internal("models/officeMaze.gltf"));
        //sceneAsset2 = new GLTFLoader().load(Gdx.files.internal("models/coltest.gltf"));
        Scene level = new Scene(sceneAsset2.scene);
        parseLevel( level );
        scenes.add( level  );

        numElements = 0;
        foundCard = new boolean[4];
        for(int i = 0; i < 4; i++)
            foundCard[i] = false;

        health = 100f;
        batteryLevel = 100f;
        Settings.torchOn = true;
        message = "Find your way out";
        deathTimer = -1;
    }

    private void parseLevel(Scene level){
        BoundingBox bbox = new BoundingBox();
        Vector3 ctr = new Vector3();
        Array<Vector3> wayPoints = new Array<>();
        String group = null;


        int count = 0;
        for(Node node : level.modelInstance.nodes ){
            node.calculateBoundingBox(bbox);
            bbox.getCenter(ctr);
            Gdx.app.log("node:", node.id + " " +bbox.toString());
            Collider.Type type = Collider.Type.DEFAULT;


            // ignore nodes that you cannot collide with
            if(node.id.startsWith("Ceiling"))
                continue;
            if(node.id.startsWith("Floor"))
                continue;
            if(node.id.startsWith("Terrain"))
                continue;
            if(node.id.startsWith("Waypoint")) {    // we assume (!) they are found in the right order
                if(group == null){
                    group = node.id.substring(0, 9);    // name of first group e.g. "Waypoint1"
                }
                if(node.id.substring(0, 9).contentEquals(group)) {  // same group
                    addWaypoint(wayPoints, node);
                } else { // new group
                    if(!Settings.noBots) {
                        Scene patrolBot = new Scene(sceneAsset.scene, "Armature");
                        scenes.add(patrolBot);
                        patrolBots.setPatrolBot(this, patrolBot, wayPoints);
                    }
                    wayPoints.clear();
                    group = node.id.substring(0, 9);    // name of new group e.g. "Waypoint2"
                    addWaypoint(wayPoints, node);
                }
                hideNode(node); // make invisible
                continue;   // not a collider
            }

            if(node.id.startsWith("Card"))
                type = Collider.Type.PICKUP;

            if(node.id.startsWith("ExitDoor"))
                type = Collider.Type.CLOSED_DOOR;

            if(node.id.startsWith("OpenExitDoor"))
                type = Collider.Type.OPEN_DOOR;

            Collider collider = new Collider(node.id, node, bbox, type);
            colliders.add( collider );
            count++;

            if( type == Collider.Type.CLOSED_DOOR)
                exitDoor = collider;

        }
        Gdx.app.log("nodes:", ""+count);

        if(wayPoints.size > 0 && !Settings.noBots) {
            Scene patrolBot = new Scene(sceneAsset.scene, "Armature");
            scenes.add(patrolBot);
            patrolBots.setPatrolBot(this, patrolBot, wayPoints);
            wayPoints.clear();
        }

    }

    private void addWaypoint( Array<Vector3> wayPoints, Node node ){
        Vector3 pos = new Vector3();
        node.calculateWorldTransform();
        node.globalTransform.getTranslation(pos);
        wayPoints.add(pos);
    }


    private void animateSet( Array<Scene> set, String animationName){
        for(Scene scene : set){
            // queue the animation in case there is already one playing
            scene.animationController.queue(animationName, 0f, -1f, 1, 1f, null, 0f);
        }
    }

    private Matrix4 bulletTransform = new Matrix4();


    public void spawnBullet(Matrix4 botTransform){
        Scene bulletScene = new Scene(sceneAsset.scene, "Bullet");
        bulletTransform.set(botTransform);
        bulletTransform.translate(-0.32f, 0.69f, 0.595f);   // offset for barrel
        bulletScene.modelInstance.transform.set(bulletTransform);

        Bullet bullet = new Bullet(bulletScene);

        //scenes.add(bullet);
        bullets.add(bullet);
    }


    // returns null if you can go to the desired position
    public Collider canReach( Vector3 position ) {

        Collider collider = colliders.collisionTest(position, PLAYER_RADIUS);
        if (collider != null) {
            Gdx.app.log("canReach: collision", collider.id);
            if(collider.type == Collider.Type.PICKUP) {
                pickUp(collider);
                return null;
            }
            if(collider.type == Collider.Type.OPEN_DOOR) {
                exitLevel(collider);
                return null;
            }
            if(collider.type == Collider.Type.CLOSED_DOOR)
                message = "You lack the elements to open the door.";
            return collider;
        }
        return null;
    }

    private void pickUp(Collider collider){
        Gdx.app.log("pickup",  collider.id);
        colliders.remove(collider);
        hideNode( collider.node );

        // play sound
        numElements++;
        if(collider.id.contentEquals("Card")) {
            foundCard[0] = true;
            message = "You have found an element: EARTH";
        }
        if(collider.id.contentEquals("Card.001")) {
            foundCard[1] = true;
            message = "You have found an element: WATER";
        }
        if(collider.id.contentEquals("Card.002")) {
            foundCard[2] = true;
            message = "You have found an element: AIR";
        }
        if(collider.id.contentEquals("Card.003")) {
            foundCard[3] = true;
            message = "You have found an element: FIRE";
        }

        if(numElements == 4){
            message = "You have all the elements. Now you can escape!";
            colliders.remove(exitDoor); // remove exit door collider
            hideNode(exitDoor.node);
        }
    }

    private void hideNode( Node node ){
        for(NodePart part : node.parts)        // hide node parts
            part.enabled = false;
    }

    private void exitLevel(Collider collider){
        Gdx.app.log("all done",  collider.id);
        message = "You have escaped!";
        colliders.remove(collider);
        // play sound
        // fanfare etc.
    }

    public void playerGotHitByBullet(){
        if(health < 0)
            return; // you only die once
        health -= 35;
        if(health < 0) {
            message = "You got hit! You died!";
            deathTimer = 3f;    // timer for follow-up message
        }
        else
            message = "You got hit! Health at "+(int)health+" percent";
    }

    public void update(Camera camera, float deltaTime ){

        if(Settings.torchOn) {
            batteryLevel -= deltaTime;
            if (batteryLevel < 0) {
                batteryLevel = 0;
                Settings.torchOn = false;
            }
        } else {    // recharge magically when torch is off
            batteryLevel += deltaTime * 8f;
            if (batteryLevel > 100)
                batteryLevel = 100;

        }

        if(deathTimer > 0){
            deathTimer -= deltaTime;
            if(deathTimer < 0)
                message = "Press R to restart";
        }

        patrolBots.update(deltaTime, camera);
        // animate bullets
        bullets.update(this, camera, deltaTime);
    }

    @Override
    public void dispose() {

        sceneAsset.dispose();
        sceneAsset2.dispose();
        patrolBots.dispose();
    }


}
