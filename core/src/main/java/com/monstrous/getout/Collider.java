package com.monstrous.getout;

import com.badlogic.gdx.math.collision.BoundingBox;

public class Collider {
    public String id;
    public BoundingBox bbox;

    public Collider(String id, BoundingBox bbox) {
        this.id = id;
        this.bbox = new BoundingBox(bbox);
    }
}
