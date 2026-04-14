package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/**
 * 脱离战斗事件
 * 当英雄脱离战斗状态时触发
 */
public class ExitCombatEvent extends GameEvent {
    private final Hero hero;
    private final int turnsWaited; // 等待了多少回合才脱离

    public ExitCombatEvent(Hero hero, int turnsWaited) {
        this.hero = hero;
        this.turnsWaited = turnsWaited;
    }

    public Hero getHero() {
        return hero;
    }

    public int getTurnsWaited() {
        return turnsWaited;
    }
}
