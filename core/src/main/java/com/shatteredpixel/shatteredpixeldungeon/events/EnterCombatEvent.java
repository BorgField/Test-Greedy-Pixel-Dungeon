package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/**
 * 进入战斗事件
 * 当英雄进入战斗状态时触发
 */
public class EnterCombatEvent extends GameEvent {
    private final Hero hero;

    public EnterCombatEvent(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }
}
