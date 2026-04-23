package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;

/**
 * 药水使用事件
 * 当英雄使用药水时触发
 */
public class PotionUsedEvent extends GameEvent {
    private final Hero hero;
    private final Potion potion;
    private final int cell;
    private final float effectFactor;

    public PotionUsedEvent(Hero hero, Potion potion, int cell, float effectFactor) {
        this.hero = hero;
        this.potion = potion;
        this.cell = cell;
        this.effectFactor = effectFactor;
    }

    public Hero getHero() {
        return hero;
    }

    public Potion getPotion() {
        return potion;
    }

    public int getCell() {
        return cell;
    }

    public float getEffectFactor() {
        return effectFactor;
    }
}
