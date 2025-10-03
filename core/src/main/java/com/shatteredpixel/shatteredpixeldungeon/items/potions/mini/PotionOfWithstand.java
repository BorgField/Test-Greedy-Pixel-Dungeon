package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class PotionOfWithstand extends MiniPotion {
    //抵抗试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_WITHSTAND;

    }

    public static final float DURATION = 20f;

    @Override
    public void apply(Hero hero) {
        identify();
        WithstandMini withstandMini = hero.buff(WithstandMini.class);
        if (withstandMini != null) {
            withstandMini.detach();
        } else {
            int acc = 10 + hero.lvl/2;
            Buff.affect(hero, WithstandMini.class).accumulateShield(acc);
        }
    }

    public static class WithstandMini extends Buff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private int shieldAccumulator;
        private float left;

        @Override
        public boolean act() {
            if (hero != null && shieldAccumulator < hero.HT) {
                spend(TICK);
                if (hero.HP == hero.HT) {
                    shieldAccumulator++;
                } else {
                    left += (int) TICK;
                    if (left >= 2) {
                        shieldAccumulator++;
                        left -= 2;
                    }
                }
            }
            return true;
        }

        @Override
        public int icon() {
            return BuffIndicator.MINIPOTION;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.8f, 0.6f, 0f);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", shieldAccumulator);
        }

        @Override
        public void detach() {
            new Flare( 6, 26 ).color(0xcc9900, true).show( curUser.sprite, 2f );
            Buff.affect(hero, Barrier.class).setShield(shieldAccumulator);
            hero.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(shieldAccumulator), FloatingText.SHIELDING);
            super.detach();
        }

        public void accumulateShield(int intShield) {
            shieldAccumulator = intShield;
        }


        private static final String SHIELD_ACC = "shieldAcc";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SHIELD_ACC, shieldAccumulator);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            shieldAccumulator = bundle.getInt(SHIELD_ACC);
            left = bundle.getInt(LEFT);
        }
    }
}
