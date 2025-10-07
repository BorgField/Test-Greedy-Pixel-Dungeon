package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText.MAGIC_DMG;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import javax.security.auth.callback.Callback;

public class PotionOfDispelling extends MiniPotion {
    //破魔试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_DISPELLING;
    }

    @Override
    public void apply(Hero hero) {
        identify();
        Buff.affect(hero, PotionOfDispelling.DispellingMini.class).setCount(2);
    }

    public static class DispellingMini extends Buff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public static final float DURATION = 20f;

        private Char attacker;
        private Char defender;

        @Override
        public int icon() {
            return BuffIndicator.MINIPOTION;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.560f, 0.194f, 0.968f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", count);
        }

        public int count = 0;

        public void setCount(int str) {
            count = str;
        }

        public int getCount() {
            return count;
        }

        public void lossCount(int ct) {
            count = Math.max(0, count - ct);
        }

        public void setAttacker(Char attacker) {
            this.attacker = attacker;
        }

        public void setDefender(Char defender) {
            this.defender = defender;
        }

        public Char getAttacker() {
            return attacker;
        }

        public Char getDefender() {
            return defender;
        }

        @Override
        public void detach() {
            super.detach();
        }

        // 处理法术伤害，如果存在攻击目标则免疫伤害并反弹法术
        public boolean handleMagicDamage(Object src, int damage) {
            // 检查是否有记录的攻击者和防御者
            if (attacker == null || defender == null) {
                return false;
            }

            // 检查双方是否都存活
            if (!defender.isAlive() || !attacker.isAlive()) {
                return false;
            }

            // 显示反弹效果
            defender.sprite.parent.add(new Beam.LightRay(defender.sprite.center(), attacker.sprite.center()));

            // 音效和视觉效果
            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
            defender.sprite.burst(0xFFFFFFFF, 10);

            // 对攻击者造成相同的法术伤害
            DM100.LightningBolt dm100 = new DM100.LightningBolt();
            attacker.damage(damage, dm100);

//            // 日志输出
//            if (Dungeon.level.heroFOV[defender.pos] || Dungeon.level.heroFOV[attacker.pos]) {
//                GLog.i(Messages.get(this, "magic_reflect", defender.name(), attacker.name(), damage));
//            }

            lossCount(1);
            if( count==0 )detach();

            return true; // 表示已处理伤害，免疫
        }
    }
}