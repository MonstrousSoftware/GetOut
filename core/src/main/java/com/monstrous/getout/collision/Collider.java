package com.monstrous.getout.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Collider {
    public String id;
    public Node node;
    public BoundingBox bbox;
    public Type type;
    public ModelInstance debugInstance;
    public int instanceNumber;

    public  enum Type {
        DEFAULT, PICKUP, OPEN_DOOR, CLOSED_DOOR;
    }

    public Collider(String id, Node node, BoundingBox bbox, Type type) {
        this.id = id;
        this.node = node;
        this.bbox = new BoundingBox(bbox);
        this.type = type;
    }



    // 2d intersection of rectangle and circle in the horizontal plane (XZ)
    public boolean intersects( Vector3 cylPos, float cylRadius){
        float distX = Math.abs(cylPos.x - bbox.getCenterX());
        float distZ = Math.abs(cylPos.z - bbox.getCenterZ());
        float hw = bbox.getWidth()/2f;
        float hd = bbox.getDepth()/2f;
        // quick exit for trivial cases
        if(distX > cylRadius + hw)
            return false;
        if(distZ > cylRadius + hd)
            return false;
        if(distX < hw)
            return true;
        if(distZ < hd)
            return true;
        // pythagoras for when cylinder is just (less than R) outside the box
        float d2 = (float)Math.pow(distX - hw,2) + (float)Math.pow(distZ - hd,2);
        return d2 <= cylRadius*cylRadius;
    }

    Vector3 newPos = new Vector3();

    public void collisionResponse(Vector3 position, float radius, Vector3 velocity, float deltaTime ) {
        newPos.set(velocity).scl(deltaTime).add(position);
        float hw = bbox.getWidth() / 2f;
        float hd = bbox.getDepth() / 2f;

        // work out which side of the box we are closest to
        float minDist = 999999f;
        int side = -1;

        float dist = Math.abs(newPos.x - (bbox.getCenterX() + hw));
        if(dist < radius && velocity.x < 0){
            velocity.x = 0;
        }
        float dist2 = Math.abs(newPos.x - (bbox.getCenterX() - hw));
        if(dist2 < radius && velocity.x > 0){
            velocity.x = 0;
        }
        float dist3 = Math.abs(newPos.z- (bbox.getCenterZ() + hd));
        if(dist3 < radius && velocity.z < 0){
            velocity.z = 0;
        }
        float dist4 = Math.abs(newPos.z - (bbox.getCenterZ() - hd));
        if(dist4 < radius && velocity.z > 0){
            velocity.z = 0;
        }
        //Gdx.app.log("collision", " d1:"+dist+" d2:"+dist2+" d3:"+dist3+" d4:"+dist4);


        //        float dist = Math.abs(newPos.x - (bbox.getCenterX() + hw));
//        if(dist < minDist){
//            minDist = dist;
//            side = 0;
//        }
//        float dist2 = Math.abs(newPos.x - (bbox.getCenterX() - hw));
//        if(dist2 < minDist){
//            minDist = dist2;
//            side = 1;
//        }
//        float dist3 = Math.abs(newPos.z- (bbox.getCenterZ() + hd));
//        if(dist3 < minDist){
//            minDist = dist3;
//            side = 2;
//        }
//        float dist4 = Math.abs(newPos.z - (bbox.getCenterZ() - hd));
//        if(dist4 < minDist){
//            minDist = dist4;
//            side = 3;
//        }
//        Gdx.app.log("collision", "side: "+side+" d1:"+dist+" d2:"+dist2+" d3:"+dist3+" d4:"+dist4);
//
//        switch(side){
//            case 0:
//            case 1: velocity.x = 0; break;
//            case 2:
//            case 3: velocity.z = 0; break;
//
//        }



    }
}
