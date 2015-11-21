package com.lbalmaceda.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.scenes.Hud;

/**
 * Created by lbalmaceda on 11/20/15.
 */
public class PlayScreen implements Screen {
    private final MarioBros game;
    private final Hud hud;
    private OrthographicCamera gameCam;
    private final FitViewport gamePort;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public PlayScreen(MarioBros game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, gameCam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
    }

    @Override
    public void show() {
    }

    public void update(float dt) {
        handleInput(dt);

        gameCam.update();
        renderer.setView(gameCam);
    }

    public void handleInput(float dt) {
        if (Gdx.input.isTouched()) {
            gameCam.position.x += 100 * dt;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        renderer.render();
        hud.stage.draw();
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

    }
}
