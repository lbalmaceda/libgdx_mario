package com.lbalmaceda.mariobros.item;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.screens.PlayScreen;
import com.lbalmaceda.mariobros.sprites.Mario;

/**
 * Created by lbalmaceda on 11/21/15.
 */
public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    protected void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fdef.filter.categoryBits = MarioBros.ITEM_BIT;
        fdef.filter.maskBits = MarioBros.MARIO_BIT | MarioBros.OBJECT_BIT |
                MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT | MarioBros.ENEMY_BIT;
        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void useItem(Mario mario) {
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        velocity.y = b2body.getLinearVelocity().y;
        b2body.setLinearVelocity(velocity);
    }
}
