package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class PotionOfMimicry extends MiniPotion {
    //拟态试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_MIMICRY;
    }

    @Override
    public void apply(Hero hero) {
        identify();
        // 应用Camouflage刻印效果
        MimicryMini buff = Buff.prolong(hero, MimicryMini.class, 20f);
        buff.setEffect(new Camouflage());
    }

    public static class MimicryMini extends FlavourBuff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public static final float DURATION = 20f;
        private Bundlable effect;

        @Override
        public int icon() { return BuffIndicator.MINIPOTION; }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.2f, 0.8f, 0.2f); // 使用绿色调表示Camouflage
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }

        public void setEffect(Bundlable effect){
            this.effect = effect;
        }

        public Armor.Glyph glyph(){
            if (effect instanceof Armor.Glyph){
                return (Armor.Glyph) effect;
            }
            return null;
        }

        public boolean hasGlyph(){
            return glyph() != null;
        }

        private static final String EFFECT = "effect";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(EFFECT, effect);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            effect = bundle.get(EFFECT);
        }

        @Override
        public void detach() {
            super.detach();
        }
    }
}
