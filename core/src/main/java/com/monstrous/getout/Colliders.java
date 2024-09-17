package com.monstrous.getout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;


// to improve:
// support oriented bounding boxes
// sphere/bbox intersection
// collision response: bounce player of wall normal

public class Colliders {
    static final float RADIUS = 1f;

    private Array<Collider> colliders;
    private BoundingBox player;
    private Vector3 min;
    private Vector3 max;

    public Colliders() {
        colliders = new Array<>();
        player = new BoundingBox();
        min = new Vector3();
        max = new Vector3();
    }

    public void add(String id, BoundingBox bbox){
        Collider col = new Collider(id, bbox);
        colliders.add(col);
    }

    // returns null if no collision, otherwise the node id
    public String collisionTest( Vector3 position ){
        // define a bounding box for the player
        min.set(position);
        min.x -= RADIUS;
        min.z -= RADIUS;
        max.set(position);
        max.x += RADIUS;
        max.z += RADIUS;
        player.set(min, max);

        // test collision of player with a collider by bounding box intersection
        for(Collider collider : colliders){
            if(collider.bbox.intersects(player))
                return collider.id;
        }
        return null;
    }


}
