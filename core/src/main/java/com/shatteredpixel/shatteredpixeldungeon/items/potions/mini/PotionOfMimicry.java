package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class PotionOfMimicry extends MiniPotion {
    //拟态试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_MIMICRY;

    }

    @Override
    public void apply(Hero hero) {
        identify();
        Buff.prolong(hero, PotionOfMimicry.MimicryMini.class, 20f);
    }

    public static class MimicryMini extends FlavourBuff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public static final float DURATION	= 20f;
        @Override
        public int icon() { return BuffIndicator.MINIPOTION; }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(0.560f, 0.194f, 0.968f); }

        @Override
        public float iconFadePercent() { return Math.max(0, (DURATION - visualcooldown()) / DURATION); }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(visualcooldown()));
        }

        @Override
        public void detach() {
            super.detach();
        }

    }
}
