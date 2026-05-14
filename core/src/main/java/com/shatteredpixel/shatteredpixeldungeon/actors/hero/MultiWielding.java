package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.offhand.OffHandWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;

import java.util.ArrayList;
import java.util.List;

public class MultiWielding {
    private final Hero hero;
    private int currentAttackIndex = 0;
    private final KindOfWeapon[] weapons = new KindOfWeapon[4];
    
    // 武器槽位索引（对应 Belongings.weapon/weapon2/weapon3/weapon4）
    private static final int SLOT_1 = 0;  // 第一组主手
    private static final int SLOT_2 = 1;  // 第一组副手
    private static final int SLOT_3 = 2;  // 第二组主手
    private static final int SLOT_4 = 3;  // 第二组副手
    
    // 双持伤害倍率配置（可根据物品特性调整）
    private static final float MAIN_HAND = 0.75f;  // 双持主手倍率
    private static final float OFF_HAND = 0.50f;   // 双持副手倍率
    private static final float MULTIPLIER = 1.0f;  // 单持/剑盾倍率


    public MultiWielding(Hero hero) {
        this.hero = hero;
        updateWeapons();
    }

    // 更新武器数组
    public void updateWeapons() {
        weapons[0] = hero.belongings.weapon;
        weapons[1] = hero.belongings.weapon2;
        weapons[2] = hero.belongings.weapon3;
        weapons[3] = hero.belongings.weapon4;

        // 如果当前索引指向的武器已被卸下，重置为 -1
        if (currentAttackIndex >= 0 && currentAttackIndex < weapons.length) {
            if (weapons[currentAttackIndex] == null) {
                currentAttackIndex = -1;  // 无效状态
            }
        }
    }

    public boolean hasAnyWeapon() {
        return weapons[0] != null
                || weapons[1] != null
                || weapons[2] != null
                || weapons[3] != null;
    }

    public KindOfWeapon currentWeapon() {
        if (currentAttackIndex >= 0 && currentAttackIndex < weapons.length) {
            return weapons[currentAttackIndex];
        }
        return null;
    }

    public int weaponProc(Char attacker, Char defender, int damage) {
        KindOfWeapon wep = currentWeapon();
        if (wep == null) return damage;

        // 远程武器不参与双持倍率计算，直接返回proc结果
        if (wep instanceof MissileWeapon || wep instanceof SpiritBow) {
            return wep.proc(attacker, defender, damage);
        }

        return calculateWeaponProc(attacker, defender, damage, wep);
    }

    // 统一处理武器proc计算
    private int calculateWeaponProc(Char attacker, Char defender, int damage, KindOfWeapon wep) {
        float groupMultiplier = getGroupMultiplierByIndex(currentAttackIndex);

        int baseProcDamage = wep.proc(attacker, defender, damage);
        if (baseProcDamage == damage) {
            return baseProcDamage;
        }

        int extraProcDamage = baseProcDamage - damage;
        int adjustedExtraDamage = Math.round(extraProcDamage * groupMultiplier);
        return damage + adjustedExtraDamage;
    }
    
    /**
     * 判断指定组是否为真双持状态
     * @param group 组号（0=第一组，1=第二组）
     * @return true 如果该组装备了两把非盾牌且参与战斗的武器
     */
    private boolean isTrueDualGroup(int group) {
        if (group < 0 || group > 1) {
            return false;  // 非法组号，返回 false
        }

        int mainIdx = group * 2;      // 组0→0, 组1→2
        int offIdx = group * 2 + 1;   // 组0→1, 组1→3

        KindOfWeapon main = weapons[mainIdx];
        KindOfWeapon off = weapons[offIdx];

        if (main == null || off == null) return false;
        
        // 排除盾牌
        if (KindOfWeapon.isShield(main) || KindOfWeapon.isShield(off)) return false;
        
        // 排除纯辅助型副手装备
        if (off instanceof OffHandWeapon && !((OffHandWeapon) off).participatesInCombat()) return false;


        return true;
    }
    
    /**
     * 根据武器索引获取组的倍率
     * @param weaponIndex 武器在数组中的索引 (0-3)
     * @return 伤害倍率：真双持主手0.75，副手0.50，其他情况1.0
     */
    private float getGroupMultiplierByIndex(int weaponIndex) {
        if (weaponIndex < 0 || weaponIndex > 3) {
            return MULTIPLIER;
        }
        
        // 确定属于哪个组
        int group;
        if (weaponIndex == SLOT_1 || weaponIndex == SLOT_2) {
            group = 0;
        } else if (weaponIndex == SLOT_3 || weaponIndex == SLOT_4) {
            group = 1;
        } else {
            return MULTIPLIER;
        }
        
        if (!isTrueDualGroup(group)) {
            return MULTIPLIER;  // 非真双持，无惩罚
        }
        
        // 判断是否为主手（SLOT_1 或 SLOT_3）
        boolean isMain = (weaponIndex == SLOT_1 || weaponIndex == SLOT_3);
        
        // 根据职业特性调整双持倍率
        return getDualWieldMultiplier(isMain);
    }
    
    /**
     * 根据职业获取双持倍率
     * @param isMain 是否为主手
     * @return 对应的伤害倍率
     */
    private float getDualWieldMultiplier(boolean isMain) {
        // 决斗者职业：双持惩罚更小
        if (hero.heroClass == HeroClass.DUELIST) {
            return isMain ? 0.90f : 0.70f;
        }
        
        // 默认倍率
        return isMain ? MAIN_HAND : OFF_HAND;
    }

    public boolean isAttack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        updateWeapons();

        // 预先收集可触及的武器（用于延迟计算和空手判断）
        List<KindOfWeapon> reachableWeapons = new ArrayList<>();
        for (KindOfWeapon w : weapons) {
            if (w != null && w.canReach(hero, enemy.pos)) {
                // 排除纯辅助型副手装备（不参与战斗计算）
                if (!(w instanceof OffHandWeapon) || ((OffHandWeapon) w).participatesInCombat()) {
                    reachableWeapons.add(w);
                }
            }
        }

        if (reachableWeapons.isEmpty()) {
            boolean hit = hero.attack(enemy, dmgMulti, dmgBonus, accMulti);
            hero.spend(hero.attackDelay());
            return hit;
        }

        // 计算平均延迟（依然使用 reachableWeapons）
        float totalDelay = 0f;
        for (KindOfWeapon w : reachableWeapons) {
            totalDelay += w.delayFactor(hero);
        }
        float averageDelay = totalDelay / reachableWeapons.size();

        // 执行分组攻击（不再传递 reachableWeapons）
        boolean anyHit = executeGroupedAttacks(enemy, dmgMulti, dmgBonus, accMulti);

        hero.spend(averageDelay);
        return anyHit;
    }

    private boolean executeGroupedAttacks(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        boolean anyHit = false;
        KindOfWeapon originalAbilityWeapon = hero.belongings.abilityWeapon;

        // 第一组攻击
        anyHit |= executeGroupAttack(enemy, dmgMulti, dmgBonus, accMulti, 0, 1);
        if (!enemy.isAlive()) return anyHit;

        // 第二组攻击
        anyHit |= executeGroupAttack(enemy, dmgMulti, dmgBonus, accMulti, 2, 3);

        hero.belongings.abilityWeapon = originalAbilityWeapon;
        return anyHit;
    }

    private boolean executeGroupAttack(Char enemy, float dmgMulti, float dmgBonus, float accMulti,
                                       int mainIdx, int offIdx) {
        boolean anyHit = false;
        // 主手攻击
        anyHit |= executeWeaponAttack(enemy, dmgMulti, dmgBonus, accMulti, mainIdx, true);
        
        // 副手攻击（仅当副手存在、不是盾牌、且参与战斗）
        KindOfWeapon offWep = weapons[offIdx];
        if (enemy.isAlive() && offWep != null 
                && !KindOfWeapon.isShield(offWep)
                && (!(offWep instanceof OffHandWeapon) || ((OffHandWeapon) offWep).participatesInCombat())) {
            anyHit |= executeWeaponAttack(enemy, dmgMulti, 0, accMulti, offIdx, false);
        }
        return anyHit;
    }

    //执行单个武器的攻击

    private boolean executeWeaponAttack(Char enemy, float dmgMulti, float dmgBonus, float accMulti,
                                        int weaponIndex, boolean isFirstWeapon) {
        KindOfWeapon weapon = weapons[weaponIndex];
        // 实时判断武器是否存在且可触及
        if (weapon == null || !weapon.canReach(hero, enemy.pos)) {
            return false;
        }

        // 更新当前攻击索引，确保 weaponProc 能获取正确的武器
        currentAttackIndex = weaponIndex;

        // 远程武器不参与双持，单独处理（固定100%伤害，无倍率惩罚）
        if (weapon instanceof MissileWeapon || weapon instanceof SpiritBow) {
            hero.belongings.abilityWeapon = weapon;
            return hero.attack(enemy, dmgMulti, dmgBonus, accMulti);
        }

        hero.belongings.abilityWeapon = weapon;

        // 计算当前武器所在组的倍率
        float groupMultiplier = getGroupMultiplierByIndex(weaponIndex);
        float currentDmgMulti = dmgMulti * groupMultiplier;

        // 副手攻击不享受伤害加成
        float currentDmgBonus = isFirstWeapon ? dmgBonus : 0;

        return hero.attack(enemy, currentDmgMulti, currentDmgBonus, accMulti);
    }

    //判断指定组是否为双盾状态（两格都是盾牌）

    private boolean isDualShieldGroup(int group) {
        if (group < 0 || group > 1) return false;
        int mainIdx = group * 2;
        int offIdx = group * 2 + 1;
        KindOfWeapon main = weapons[mainIdx];
        KindOfWeapon off = weapons[offIdx];
        
        // 纯辅助装备不参与双盾判定
        if (off instanceof OffHandWeapon && !((OffHandWeapon) off).participatesInCombat()) {
            return false;
        }
        
        return main != null && off != null
                && KindOfWeapon.isShield(main)
                && KindOfWeapon.isShield(off);
    }

    //获取指定武器防御值的倍率（用于双盾惩罚）
    private float getDefenseMultiplier(int weaponIndex) {
        if (weaponIndex < 0 || weaponIndex >= weapons.length) return 1.0f;
        int group = weaponIndex / 2;   // 0:第一组, 1:第二组
        if (!isDualShieldGroup(group)) {
            return 1.0f;
        }
        // 判断主手 (索引 0 或 2)
        boolean isMain = (weaponIndex == 0 || weaponIndex == 2);
        // 复用双持惩罚倍率（与伤害惩罚一致）
        return getDualWieldMultiplier(isMain);
    }

    // 防御计算 - 累加所有武器的防御加成，双盾时应用惩罚
    public int weaponDefenseFactor(Char owner) {
        int defenceFactor = 0;
        for (int i = 0; i < weapons.length; i++) {
            KindOfWeapon w = weapons[i];
            if (w != null) {
                float multiplier = getDefenseMultiplier(i);
                int value = w.defenseFactor(owner);
                defenceFactor += Math.round(value * multiplier);
            }
        }
        return defenceFactor;
    }
    
    //判断是否有任何一组处于真双持状态，可用于外部查询或技能判断
    public boolean hasAnyTrueDualGroup() {
        return isTrueDualGroup(0) || isTrueDualGroup(1);
    }
}