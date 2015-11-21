package com.lbalmaceda.mariobros.item;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by lbalmaceda on 11/21/15.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
