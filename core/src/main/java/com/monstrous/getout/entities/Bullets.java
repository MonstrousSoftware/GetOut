package com.monstrous.getout.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import com.monstrous.getout.World;

public class Bullets {

    public Array<Bullet> bullets;
    private Array<Bullet> deleteList;

    public Bullets() {
        bullets = new Array<>();
        deleteList = new Array<>();
    }

    public void add(Bullet bullet){
        bullets.add(bullet);
    }



    public void update(World world, Camera camera, float deltaTime ){
        // animate bullets
        deleteList.clear();
        for(Bullet bullet : bullets){
            if(bullet.update(deltaTime, world, camera)) {
                deleteList.add(bullet);
                bullet.dispose();
            }
        }
        bullets.removeAll(deleteList, true);
    }

    public void pauseSound(){
        for(Bullet bullet : bullets)
            bullet.pauseSound();
    }

    public void resumeSound(){
        for(Bullet bullet : bullets)
            bullet.resumeSound();
    }
}
