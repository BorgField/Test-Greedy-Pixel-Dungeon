package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;


import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class TendonHookSickle extends MeleeWeapon {
    private static int energy = 50;
    private boolean potionsSTR= false;

    {
        image = ItemSpriteSheet.SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        tier = 2;
        DLY = extraDLY();
        RCH = 2;
    }

    public int extraSTR() {
        return hero != null ? Math.max(0, hero.STR() - STRReq()) : 0;
    }

    public boolean getPotionsSTR() {
        return potionsSTR;
    }

    public void setPotionsSTR() {
        if( !(Dungeon.hero ==null) &&  isEquipped(hero) ) {
            if (!potionsSTR && masteryPotionBonus) {
                potionsSTR = true;
            }
        }
    }

    public float extraDLY() {
        int i = extraSTR();
        return Math.max(0.5f, 1.5f - i * 0.1f);
    }

    @Override
    public int min(int lvl) {
        return  1*(tier) + lvl*(tier-1);
    }

    @Override
    public int max(int lvl) {
        int i = extraSTR();
        return  3*(tier) + lvl*(tier-1)+ 2*(i);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", extraSTR()*3, extraDLY(), energy , RCH) ;
    }

    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("energy", energy);
        bundle.put("potionsSTR", potionsSTR);
        bundle.put("RCH", RCH);
    }

    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        energy = bundle.getInt("energy");
        potionsSTR = bundle.getBoolean("potionsSTR");
        RCH = bundle.getInt("RCH");
    }

    private void applyEquipDamage(Hero hero) {
        int damage = (int) (hero.HP * 0.33f);
        if (damage < 1) { damage = 1;}
        Bleeding bleeding = new Bleeding();
        hero.damage(damage, bleeding);
    }

    public int damageRoll(int lvl) {
        int minDmg = min(lvl);
        int maxDmg = max(lvl);
        int randomDmg = Random.IntRange(minDmg, maxDmg);
        return Math.round(randomDmg);
    }

    public int lvl = TendonHookSickle.this.level();

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            applyEquipDamage(hero);
            ActionIndicator.refresh();
            DLY = extraDLY();
            setPotionsSTR();
            return true;
        }
        return false;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        PredatorStance stance = hero.buff(PredatorStance.class);
        if (stance != null) {
            if (stance.active) {
                Buff.affect(hero, PredatorCooldown.class, 74f);
            }
            stance.detach();
        }
        if (super.doUnequip(hero, collect, single)) {
            applyEquipDamage(hero);
            ActionIndicator.refresh();
            DLY = extraDLY();
            setPotionsSTR();
            return true;
        }
        return false;
    }

    //能量获取
    public int proc(Char attacker, Char defender, int damage) {
        damage = super.proc(attacker, defender, damage);
        if (damage > 0 && attacker == hero) {
            Hero hero = (Hero) attacker;
            PredatorStance stance = hero.buff(PredatorStance.class);
            if (stance == null || !stance.active) {
                int energyGain = damage * 2;
                if (energyGain > 0) {
                    addEnergy(energyGain);
                }
            }

            // 施加血液沸腾buff
            if (stance != null && stance.active ) {
                if(defender.isAlive()) { Buff.prolong(defender, BloodBoil.class, 10);}
            }
            if (defender.isAlive() && defender.buff(BloodBoil.class) != null) {
                // 计算33%的额外伤害（四舍五入）
                int extraDmg = Math.round(damage * 0.33f);
                // 确保至少造成1点伤害
                if (extraDmg < 1) extraDmg = 1;
                // 对defender施加额外伤害
                Bleeding bleeding = new Bleeding();
                defender.damage( extraDmg, bleeding);
                // 添加视觉效果
                if (defender.sprite.visible) {
                    Splash.at(defender.sprite.center(), PointF.PI, PointF.PI / 4,
                            defender.sprite.blood(), Math.min(5 * extraDmg / defender.HT, 6));
                }
            }
        }
        return damage;
    }

    // 能量管理方法
    public static int getEnergy() {
        return energy;
    }

    public static void setEnergy(int value) {
        energy = Math.max(-50, Math.min(value, 250));
    }

    public static void addEnergy(int amount) {
        setEnergy(energy + amount);
    }

    private static final String AC_PREDATOR = "PREDATOR";

    @Override
    public String defaultAction() { return AC_GLUTTONY; }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_PREDATOR);
        actions.add(AC_GLUTTONY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_PREDATOR)){
            // 检查状态
            if (!isEquipped(hero)) {
                GLog.w(Messages.get(this, "need_equip"));
                return;
            }
            if (hero.buff(PredatorCooldown.class) != null) {
                GLog.w(Messages.get(this, "cooldown"));
                return;
            }

            PredatorStance stance = hero.buff(PredatorStance.class);
            if (stance != null) {
                if (!stance.active) {
                    stance.reset();
                } else {
                    stance.active = false;
                    Buff.affect(hero, PredatorCooldown.class, 74f); // 触发冷却
                }
            } else {
                Buff.affect(hero, PredatorStance.class).reset();
                hero.spendAndNext(Actor.TICK);
            }

            BuffIndicator.refreshHero();
            AttackIndicator.updateState();
            hero.sprite.operate(hero.pos);
        }

        if (action.equals(AC_GLUTTONY)) {
            if (hero.buff(GluttonyCooldown.class) != null) {
                GLog.w(Messages.get(this, "cooldown"));
            }else  {
                curUser = hero; // 设置当前使用者
                GameScene.selectCell(caster); // 打开目标选择器
            }
        }else {
            super.execute(hero, action);
        }
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        gluttonyAbility(hero, target, 5, 1.5f, 0, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 5 + Math.round(1.5f*buffedLvl()) : 5;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

    public static void gluttonyAbility(Hero hero, Integer target, int distance, float dmgMulti, int dmgBoost, MeleeWeapon wep) {
        if (target == null){
            return;
        }

        Char enemy = Actor.findChar(target);
        if (Dungeon.level.heroFOV[target]) {
            if (enemy == null || enemy == hero || hero.isCharmedBy(enemy)) {
                GLog.w(Messages.get(wep, "ability_no_target"));
                return;
            }
        }


    }

    private static final String AC_GLUTTONY = "GLUTTONY";

    public CellSelector.Listener caster = new CellSelector.Listener(){
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                if (target == curUser.pos) {
                    GLog.i(Messages.get(TendonHookSickle.class, "select"));
                    return;
                }
                int distance = Dungeon.level.distance(curUser.pos, target);
                // 检查距离是否超过6
                if (distance > 6) {
                    GLog.w(Messages.get(TendonHookSickle.class, "out_of_range"));
                    return;
                }
                // 创建投射路径计算
                final Ballistica chain = new Ballistica(
                        curUser.pos, target, Ballistica.STOP_TARGET
                );
                if (chain.collisionPos == curUser.pos) {
                    GLog.w(Messages.get(TendonHookSickle.class, "obstacle"));
                    return;
                }
                // 检查目标位置是否有敌人
                Char enemy = Actor.findChar(chain.collisionPos);
                if(enemy != null && !(enemy instanceof NPC) ){
                    curUser.busy();
                    int damage = 0;

                    curUser.sprite.parent.add(new Chains(
                                    curUser.sprite.center(),
                                    enemy.sprite.center(),
                                    Effects.Type.ETHEREAL_CHAIN,
                                    new Callback() {
                                        @Override
                                        public void call() {
                                            int dmg = damageRoll(lvl);
                                            float threshold = (enemy.properties().contains(Char.Property.BOSS) ||
                                                    enemy.properties().contains(Char.Property.MINIBOSS)) ? 0.1f : 0.3f;

                                            GluttonyCooldown cooldown = hero.buff(GluttonyCooldown.class);
                                            if (cooldown == null) {
                                                if (enemy.HP < threshold * enemy.HT || enemy.HP < dmg*0.6) {
                                                    enemy.die(null);
                                                    hero.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(TendonHookSickle.class,"devoured"));
                                                    hero.sprite.emitter().start(BloodParticle.BURST, 0.05f, 100);
                                                    Buff.affect(hero, Gluttony.class).setHeal(enemy.HT);
                                                    Buff.affect(hero, Healing.class).setHeal((enemy.HT), 0.2f, 0);
                                                    Buff.affect(hero, GluttonyCooldown.class, (float)(enemy.HT*(15-extraSTR())*0.1));
                                                } else {
                                                    hero.attack(enemy, 0,(int) (dmg*0.6f),Char.INFINITE_ACCURACY);
                                                    addEnergy((int) (dmg*0.6f));
                                                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                                                    int gcd = Math.max(0, 12-extraSTR());
                                                    if (gcd != 0) { Buff.affect(hero, GluttonyCooldown.class, gcd);}
                                                }}
//                                     获取路径上除起点外的所有单元格
                                            PredatorStance stance = hero.buff(PredatorStance.class);
                                            if(stance != null && stance.active) {
                                                List<Integer> pathCells = chain.subPath(1, chain.dist-1);
                                                for (int cell : pathCells) {
                                                    Char ch = Actor.findChar(cell);
                                                    if (ch != null && ch != hero && ch.alignment != Char.Alignment.ALLY) {
                                                        ch.damage((int) (dmg*0.4f), hero);
                                                        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                                                        Buff.prolong(ch, BloodBoil.class, 5);
                                                    }
                                                }
                                            }
                                            Dungeon.observe();
                                            GameScene.updateMap();
                                            Invisibility.dispel();
                                            hero.spendAndNext(1f);
                                            hero.sprite.operate(hero.pos);
                                        }}
                            )
                    );

                    Sample.INSTANCE.play(Assets.Sounds.CHAINS);

                }else {
                    GLog.w(Messages.get(TendonHookSickle.class, "notthere"));
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(TendonHookSickle.class, "prompt");
        }
    };

    public static class PredatorStance extends Buff {

        {
            announced = true;
            type = buffType.POSITIVE;
        }

        public boolean active = true;
        private int minTurnsLeft;

        public void reset(){
            if (!active){
                active = true;
                target.sprite.showStatus(CharSprite.POSITIVE, Messages.titleCase(name()));
            }
            minTurnsLeft = 50;
        }

        @Override
        public int icon() {
            return active ? BuffIndicator.DUEL_BRAWL : BuffIndicator.NONE;
        }

        @Override
        public boolean act() {
            minTurnsLeft--;
            if (active) {
                // 每回合减少10点能量
                TendonHookSickle weapon = (TendonHookSickle) ((Hero) target).belongings.weapon;
                if (weapon != null) {
                    addEnergy(-10);
                    // 能量过低buff惩罚
                    if (getEnergy() <= -50) {
                        active = false;
                        int damage = target.HP / 2;
                        Bleeding bleeding = new Bleeding();
                        target.damage(damage, bleeding);
                        GLog.w(Messages.get(this, "energy_depleted"));
                        Buff.affect(target, PredatorCooldown.class, 150f);
                        detach();
                        return true;
                    }
                }
            }
            if (!active && minTurnsLeft <= 0){
                detach();
            }
            refreshRCH();
            spend(TICK);
            return true;
        }

        private void refreshRCH() {
            PredatorStance stance = target.buff(PredatorStance.class);
            if (stance != null && stance.active) {
                ((TendonHookSickle) ((Hero) target).belongings.weapon).RCH = 4;
            } else {
                ((TendonHookSickle) ((Hero) target).belongings.weapon).RCH = 2;
            }
        }

        @Override
        public void detach() {
            super.detach();
            refreshRCH();
        }

        public static final String ACTIVE = "active";
        public static final String MIN_TURNS_LEFT = "min_turns_left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ACTIVE, active);
            bundle.put(MIN_TURNS_LEFT, minTurnsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            active = bundle.getBoolean(ACTIVE);
            minTurnsLeft = bundle.getInt(MIN_TURNS_LEFT);
        }
    }
    // 冷却buff类
    public static class PredatorCooldown extends FlavourBuff {
        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() { return BuffIndicator.TIME; }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }

        @Override
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 75f); }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(visualcooldown()));
        }

    }
    //血液沸腾buff
    public static class BloodBoil extends FlavourBuff {
        {
            type = buffType.NEGATIVE;
            announced = true;
        }
        public static final float DURATION	= 10f;

        @Override
        public int icon() { return BuffIndicator.ENDURE; }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(visualcooldown()));
        }

    }

    public static class Gluttony extends Buff{
        private int energyLeft;
        private int totalEnergy;
        int lvl;

        { type = buffType.POSITIVE;}

        @Override
        public boolean act(){
            if (energy < 250) {
                addEnergy(energyTick());
            }
            energyLeft -= energyTick();

            if (energyLeft <= 0){
                if (target instanceof Hero) {
                    ((Hero) target).resting = false;
                }
                detach();
            }
            spend( TICK );
            return true;
        }
        private int energyTick(){
            int ery = TendonHookSickle.getEnergy();
            int totalHeal = (int) GameMath.gate(1, Math.round(energyLeft * 0.1) , energyLeft);
            int lvlHeal = (int) GameMath.gate(1, Math.round(lvl*2) , energyLeft);
            int heal = Math.max(totalHeal, lvlHeal);
            if (ery + heal > 250){
                heal = 250 - ery;
            }
            return heal;
        }

        public void setHeal(int totalHeal){
            totalEnergy = totalHeal;
            energyLeft = Math.max(energyLeft, totalHeal);
        }

        public static final String TOTAL_ENERGY = "total_energy";
        public static final String ENERGY_LEFT = "energy_left";
        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(TOTAL_ENERGY, totalEnergy);
            bundle.put(ENERGY_LEFT, energyLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            totalEnergy = bundle.getInt(TOTAL_ENERGY);
            energyLeft = bundle.getInt(ENERGY_LEFT);
        }

        @Override
        public int icon() {
            return BuffIndicator.BERSERK;
        }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(1f, 0.1f, 0.1f); }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(energyLeft);
        }

        @Override
        public String desc() {
            return Messages.get(TendonHookSickle.class, "gluttony_desc", energyTick() ,energyLeft);
        }
    }

    public static class GluttonyCooldown extends FlavourBuff {
        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() { return BuffIndicator.TIME; }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(0.8f, 0.2f, 0.2f); }

        @Override
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 10f); }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(visualcooldown()));
        }

    }
}