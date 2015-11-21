package com.lbalmaceda.mariobros.sprites.tiles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.lbalmaceda.mariobros.MarioBros;
import com.lbalmaceda.mariobros.scenes.Hud;
import com.lbalmaceda.mariobros.screens.PlayScreen;
import com.lbalmaceda.mariobros.sprites.Mario;

/**
 * Created by lbalmaceda on 11/21/15.
 */
public class Brick extends InteractiveTileObject {

    public Brick(PlayScreen screen, MapObject bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else {
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();

        }

    }
}
