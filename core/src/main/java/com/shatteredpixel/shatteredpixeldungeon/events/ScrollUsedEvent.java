package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;

/**
 * 卷轴使用事件
 * 当英雄使用卷轴时触发
 */
public class ScrollUsedEvent extends GameEvent {
    private final Hero hero;
    private final Scroll scroll;
    private final int position;
    private final float effectFactor;
    private final Class<? extends Scroll> scrollClass;

    public ScrollUsedEvent(Hero hero, Scroll scroll, int position, float effectFactor) {
        this.hero = hero;
        this.scroll = scroll;
        this.position = position;
        this.effectFactor = effectFactor;
        this.scrollClass = scroll.getClass();
    }

    public Hero getHero() {
        return hero;
    }

    public Scroll getScroll() {
        return scroll;
    }

    public int getPosition() {
        return position;
    }

    public float getEffectFactor() {
        return effectFactor;
    }

    public Class<? extends Scroll> getScrollClass() {
        return scrollClass;
    }
}
