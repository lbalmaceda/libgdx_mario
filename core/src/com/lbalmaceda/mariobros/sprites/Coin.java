package com.lbalmaceda.mariobros.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.item.ItemDef;
import com.lbalmaceda.mariobros.item.Mushroom;
import com.lbalmaceda.mariobros.scenes.Hud;
import com.lbalmaceda.mariobros.screens.PlayScreen;

/**
 * Created by lbalmaceda on 11/21/15.
 */
public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() != BLANK_COIN) {
            MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            Hud.addScore(100);
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class));
        } else {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }
}
