package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
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
        Buff.affect(hero, PotionOfDispelling.DispellingMini.class).setCount(100);
    }

    public static class DispellingMini extends Buff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        // 删除这行：private static Object owner; // 这会覆盖父类的owner字段

        public static final float DURATION = 20f;

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
            return Messages.get(this, "desc", dispTurns(visualcooldown()));
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

        @Override
        public void detach() {
            super.detach();
        }

        public boolean isImmune() {
            return true;
        }

        public void zapBack(Char attacker, int damage){
            // 获取拥有此buff的角色（防御者）
            Char defender = (Char) target;
            if (defender == null || attacker == null) {
                return;
            }

            // 检查双方是否都存活
            if (!defender.isAlive() || !attacker.isAlive()) {
                return;
            }

            // 创建从防御者到攻击者的弹道
            Ballistica bolt = new Ballistica(defender.pos, attacker.pos, Ballistica.PROJECTILE);

            // 显示魔法弹特效
            defender.sprite.parent.add(new Beam.LightRay(defender.sprite.center(), attacker.sprite.center()));

            // 对攻击者造成原始魔法伤害值的伤害
            attacker.damage(damage, defender);

            // 音效和视觉效果
            Sample.INSTANCE.play(Assets.Sounds.HIT_MAGIC, 1, Random.Float(0.87f, 1.15f));
            attacker.sprite.burst(0xFFFFFFFF, 10);

            // 日志输出（可选）
            if (Dungeon.level.heroFOV[defender.pos] || Dungeon.level.heroFOV[attacker.pos]) {
                GLog.i(Messages.get(this, "zap_back", defender.name(), attacker.name(), damage));
            }
        }

//        @Override
//        public int proc(Armor armor, Char attacker, Char defender, int damage) {
//            Buff.affect( attacker, Bleeding.class).set( 10 );
//
//            return damage;
//        }
    }

//    public static class RiposteTracker extends Buff implements ActionIndicator.Action {
//
//        { actPriority = VFX_PRIO; } // 设置行动优先级为视觉效果优先级
//
//        public Char enemy; // 要攻击的敌人
//
//        public RiposteTracker() {}
//
//        public RiposteTracker(Char enemy) {
//            this.enemy = enemy;
//        }
//
//        @Override
//        public boolean act() {
//            Hero hero = (Hero) target;
//
//            // 检查是否可以使用备用剑进行还击
//            if (hero.canAttack(enemy)) {
//
//                // 执行攻击动画和逻辑
//                target.sprite.attack(enemy.pos, new Callback() {
//                    @Override
//                    public void call() {
//                        // 直接使用当前武器攻击
//                        hero.attack(enemy);
//
//                        // 检查是否拥有弱点标记天赋
//                        if (hero.hasTalent(Talent.WEAKNESS_MARK) && enemy != null && enemy.isAlive()) {
//                            Buff.affect(enemy, ArmorBreak.class, hero.pointsInTalent(Talent.WEAKNESS_MARK));
//                        }
//
//                        next(); // 继续下一个动作
//                    }
//                });
//
//                detach(); // 移除这个Buff
//                return false; // 不消耗行动点
//
//            } else {
//                // 无法还击的情况
//                detach();
//                return true; // 消耗行动点
//            }
//        }
//
//        @Override
//        public void detach() {
//            super.detach();
//            enemy = null; // 清理引用
//        }
//
//        @Override
//        public void storeInBundle(Bundle bundle) {
//            super.storeInBundle(bundle);
//            bundle.put("enemy", enemy != null ? enemy.id() : -1);
//        }
//
//        @Override
//        public void restoreFromBundle(Bundle bundle) {
//            super.restoreFromBundle(bundle);
//            int enemyId = bundle.getInt("enemy");
//            if (enemyId != -1) {
//                enemy = (Char) Actor.findById(enemyId);
//            }
//        }
//
//        @Override
//        public String actionName() {
//            return "";
//        }
//
//        @Override
//        public int indicatorColor() {
//            return 0;
//        }
//
//        @Override
//        public void doAction() {
//
//        }
//    }
}