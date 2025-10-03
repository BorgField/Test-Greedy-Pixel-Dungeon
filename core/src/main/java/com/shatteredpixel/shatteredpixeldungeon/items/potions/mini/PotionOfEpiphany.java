package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class PotionOfEpiphany extends MiniPotion {
    // 顿悟试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_EPIPHANY;
    }

    @Override
    public void apply(Hero hero) {
        identify();
        EpiphanyMini existingBuff = hero.buff(EpiphanyMini.class);
        if (existingBuff == null) {
            new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
            Buff.affect(hero, EpiphanyMini.class).attachBuff();
        } else {
            new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
            existingBuff.left = 21;
        }
    }


    public static class EpiphanyMini extends Buff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public static final float DURATION = 21f;

        private int originalLevel;
        private float left;

        @Override
        public boolean act() {
            if (left <= 0) {
                detach();
            } else {
                spend(TICK);
                left -= TICK;
            }
            return true;
        }

        public void attachBuff() {
            originalLevel = Dungeon.hero.lvl;

            // 提升等级和更新生命上限
            Dungeon.hero.lvl += 5;
            Dungeon.hero.updateHT(true);

            // 设置剩余时间
            left = DURATION;
        }

        @Override
        public int icon() {
            return BuffIndicator.MINIPOTION;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 1f, 0.2f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - left) / DURATION);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(left));
        }

        @Override
        public void detach() {
            super.detach();
            // 恢复原始等级和经验值
            Dungeon.hero.lvl = originalLevel;
            Dungeon.hero.updateHT(true);
        }

        private static final String ORIGINAL_LEVEL = "originalLevel";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ORIGINAL_LEVEL, originalLevel);
            bundle.put(LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            originalLevel = bundle.getInt(ORIGINAL_LEVEL);
            left = bundle.getFloat(LEFT);
        }
    }


}
