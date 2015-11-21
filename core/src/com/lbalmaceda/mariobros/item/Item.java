package com.lbalmaceda.mariobros.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.screens.PlayScreen;
import com.lbalmaceda.mariobros.sprites.Mario;

/**
 * Created by lbalmaceda on 11/21/15.
 */
public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body b2body;

    public Item(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    protected abstract void defineItem();

    public abstract void useItem(Mario mario);

    public void update(float dt) {
        if (toDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        }
    }

    public void destroy() {
        toDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }
}
