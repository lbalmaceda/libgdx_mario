package com.lbalmaceda.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.item.Item;
import com.lbalmaceda.mariobros.item.ItemDef;
import com.lbalmaceda.mariobros.item.Mushroom;
import com.lbalmaceda.mariobros.scenes.Hud;
import com.lbalmaceda.mariobros.sprites.Mario;
import com.lbalmaceda.mariobros.sprites.enemies.Enemy;
import com.lbalmaceda.mariobros.tools.B2WorldCreator;
import com.lbalmaceda.mariobros.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lbalmaceda on 11/20/15.
 */
public class PlayScreen implements Screen {
    private final MarioBros game;
    private final Hud hud;
    private final Music music;
    private OrthographicCamera gameCam;
    private final FitViewport gamePort;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Mario player;
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private TextureAtlas atlas;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBros game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        //set gamecam to the center at the start of the game
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = MarioBros.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef def) {
        itemsToSpawn.add(def);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type.equals(Mushroom.class)) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    @Override
    public void show() {
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public void update(float dt) {
        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);
        if (player.currentState != Mario.State.DEAD) {
            gameCam.position.x = player.b2body.getPosition().x;
        }
        hud.update(dt);
        player.update(dt);
        for (Enemy enemy : creator.getGoombas()) {
            if (enemy.getX() < player.getX() + 224 / MarioBros.PPM) {
                enemy.b2body.setActive(true);
            }
            enemy.update(dt);
        }
        for (Item item : items) {
            item.update(dt);
        }
        gameCam.update();
        renderer.setView(gameCam);
    }

    public void handleInput(float dt) {
        if (player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.b2body.applyLinearImpulse(new Vector2(0, 4), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getGoombas()) {
            enemy.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        b2dr.dispose();
        world.dispose();
        hud.dispose();
        music.dispose();
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public boolean gameOver() {
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 3) {
            return true;
        } else {
            return false;
        }
    }
}
