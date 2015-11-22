package com.lbalmaceda.mariobros.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.screens.PlayScreen;
import com.lbalmaceda.mariobros.sprites.Mario;
import com.lbalmaceda.mariobros.tools.B2WorldCreator;

/**
 * Created by lbalmaceda on 11/21/15.
 */
public class Turtle extends Enemy {
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean setToDestroy;
    private boolean destroyed;

    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;

    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}

    public State currentState;
    public State previousState;

    private float deadRotationDegrees;


    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        //walking
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        walkAnimation = new Animation(0.2f, frames);

        //shell
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        currentState = State.WALKING;
        previousState = State.WALKING;
        setBounds(getX(), getY(), 16 / MarioBros.PPM, 24 / MarioBros.PPM);

        deadRotationDegrees = 0;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / MarioBros.PPM);

        if (currentState == State.DEAD) {
            deadRotationDegrees += 2;
            rotate(deadRotationDegrees);
            if (stateTime > 5 && !destroyed) {
                world.destroyBody(b2body);
                destroyed = true;
                B2WorldCreator.scheduleEnemyDelete(this);
            }
        } else {
            b2body.setLinearVelocity(velocity);
        }
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Goomba) {
            if (currentState != State.MOVING_SHELL) {
                reverseVelocity(true, false);
            }
        } else if (enemy instanceof Turtle) {
            Turtle other = (Turtle) enemy;
            if (other.getCurrentState() == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                kill();
            } else if (other.getCurrentState() == State.WALKING && currentState == State.MOVING_SHELL) {
                return;
            } else {
                reverseVelocity(true, false);
            }
        }
    }

    private TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case MOVING_SHELL:
            case STANDING_SHELL:
                region = shell;
                break;
            default:
            case WALKING:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
            currentState = Turtle.State.MOVING_SHELL;
        }
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }


    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);

        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.MARIO_BIT
                | MarioBros.BRICK_BIT | MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT;
        fdef.shape = shape;

        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.3f;
        fdef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;

        b2body.createFixture(fdef).setUserData(this);

    }

    public State getCurrentState() {
        return currentState;
    }

    public void kill() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;

        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }

        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }
}
