package com.monstrous.getout.collision;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Collider {
    public String id;
    public Node node;
    public BoundingBox bbox;
    public Type type;
    public ModelInstance debugInstance;

    public  enum Type {
        DEFAULT, PICKUP, OPEN_DOOR, CLOSED_DOOR;
    }

    public Collider(String id, Node node, BoundingBox bbox, Type type) {
        this.id = id;
        this.node = node;
        this.bbox = new BoundingBox(bbox);
        this.type = type;
    }
}
