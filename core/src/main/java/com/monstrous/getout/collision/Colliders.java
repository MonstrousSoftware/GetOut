package com.monstrous.getout.collision;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


// to improve:
// support oriented bounding boxes
// sphere/bbox intersection
// collision response: bounce player of wall normal

public class Colliders implements Disposable {

    public Array<Collider> colliders;
    private BoundingBox player;
    private Vector3 min;
    private Vector3 max;
    private Vector3 ctr;
    private ModelBuilder modelBuilder;
    private Material material;
    private Array<Disposable> disposables;


    public Colliders() {
        colliders = new Array<>();
        player = new BoundingBox();
        min = new Vector3();
        max = new Vector3();
        ctr = new Vector3();
        modelBuilder = new ModelBuilder();
        material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
        disposables = new Array<>();
    }

    public void add(Collider collider) {
        colliders.add(collider);
        addDebugModel(collider);
    }

    public void remove(Collider collider) {
        colliders.removeValue(collider, true);
    }

    // returns null if no collision, otherwise the node id
    public Collider collisionTest(Vector3 position, float radius) {

        // test collision of player with a colliders bounding box
        for (Collider collider : colliders) {
            if(intersects(collider.bbox, position, radius))
                return collider;
        }
        return null;
    }

    // 2d intersection of rectangle and circle in the horizontal plane (XZ)
    private boolean intersects(BoundingBox bbox, Vector3 cylPos, float cylRadius){
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

    private void addDebugModel(Collider collider) {
        // create a debug model matching the collision geom shape
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        meshBuilder =modelBuilder.part("part", GL20.GL_LINES, VertexAttributes.Usage.Position ,material);
        BoundingBox bb = collider.bbox;
        BoxShapeBuilder.build(meshBuilder, bb.getWidth(), bb.getHeight(), bb.getDepth());
        Model modelShape = modelBuilder.end();
        disposables.add(modelShape);
        bb.getCenter(ctr);
        collider.debugInstance = new ModelInstance(modelShape, ctr);
    }


    @Override
    public void dispose() {
        for(Disposable d : disposables)
            d.dispose();
    }
}
