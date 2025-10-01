package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.items.potions.mini.PotionOfBurst.BurstMini.DURATION;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class PotionOfBurst extends MiniPotion {
    //力量迸发试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_BURST;

    }

    @Override
    public void apply(Hero hero) {
        identify();
        Buff.prolong(hero, BurstMini.class, DURATION);
    }

    public static class BurstMini extends FlavourBuff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public static final float DURATION	= 20f;
        @Override
        public int icon() { return BuffIndicator.MINIPOTION; }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(1f, 0.6f, 0f); }

        @Override
        public float iconFadePercent() { return Math.max(0, (DURATION - visualcooldown()) / DURATION); }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(visualcooldown()));
        }

        @Override
        public void detach() {
            super.detach();
            Buff.affect(hero, Weakness.class, 20f);
            BuffIndicator.refreshHero();
        }

    }
}
