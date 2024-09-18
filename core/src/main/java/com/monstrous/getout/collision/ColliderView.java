package com.monstrous.getout.collision;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.getout.World;


public class ColliderView implements Disposable {

    // colours to use for active vs. sleeping geoms
    static private final Color COLOR_ACTIVE = Color.GREEN;
    static private final Color COLOR_SLEEPING = Color.TEAL;
    static private final Color COLOR_STATIC = Color.GRAY;

    private final ModelBatch modelBatch;
    private final World world;      // reference

    public ColliderView(World world) {
        this.world = world;
        modelBatch = new ModelBatch();
    }

    public void render( Camera cam ) {
        modelBatch.begin(cam);
        for(Collider collider : world.colliders.colliders) {
            modelBatch.render(collider.debugInstance);
        }
        modelBatch.end();
    }

//    private void renderCollisionShape(ModelInstance) {
//        // move & orient debug modelInstance in line with geom
//        if(body == null)
//            return;
//
//        body.debugInstance.transform.set(body.getPosition(), body.getBodyOrientation());
//
//        // use different colour for static/sleeping/active objects and for active ones
//        Color color = COLOR_STATIC;
//        if (body.geom.getBody() != null) {
//            if (body.geom.getBody().isEnabled())
//                color = COLOR_ACTIVE;
//            else
//                color = COLOR_SLEEPING;
//        }
//        body.debugInstance.materials.first().set(ColorAttribute.createDiffuse(color));   // set material colour
//
//        modelBatch.render(body.debugInstance);
//    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }
}
