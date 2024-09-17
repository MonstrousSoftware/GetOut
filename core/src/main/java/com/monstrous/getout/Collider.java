package com.monstrous.getout;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Collider {
    public String id;
    public BoundingBox bbox;
    public ModelInstance debugInstance;

    public Collider(String id, BoundingBox bbox) {
        this.id = id;
        this.bbox = new BoundingBox(bbox);
    }
}
