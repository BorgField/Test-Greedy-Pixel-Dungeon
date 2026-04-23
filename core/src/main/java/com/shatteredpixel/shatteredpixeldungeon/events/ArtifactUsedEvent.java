package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;

/**
 * 神器使用事件
 * 当英雄使用神器时触发
 */
public class ArtifactUsedEvent extends GameEvent {
    private final Hero hero;
    private final Artifact artifact;

    public ArtifactUsedEvent(Hero hero, Artifact artifact) {
        this.hero = hero;
        this.artifact = artifact;
    }

    public Hero getHero() {
        return hero;
    }

    public Artifact getArtifact() {
        return artifact;
    }
}
