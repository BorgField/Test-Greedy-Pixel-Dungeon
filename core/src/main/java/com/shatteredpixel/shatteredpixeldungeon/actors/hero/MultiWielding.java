package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.WheelChair;
import com.watabou.utils.QuietCallable;

import java.util.Arrays;

public class MultiWielding {
    private final QuietCallable<KindOfWeapon> weapon, weapon2, weapon3, weapon4;
    private int currentAttackIndex = 0; // 当前攻击武器索引
    private static final KindOfWeapon[] weapons = new KindOfWeapon[4]; // 武器数组
    private final boolean[] canAttack = new boolean[4]; // 每把武器是否可攻击
    private boolean hasBracelet; // 是否有手镯

    public MultiWielding(
            QuietCallable<KindOfWeapon> weapon,
            QuietCallable<KindOfWeapon> weapon2,
            QuietCallable<KindOfWeapon> weapon3,
            QuietCallable<KindOfWeapon> weapon4
    ) {
        this.weapon = weapon;
        this.weapon2 = weapon2;
        this.weapon3 = weapon3;
        this.weapon4 = weapon4;

        // 初始化武器数组
        weapons[0] = weapon.call();
        weapons[1] = weapon2.call();
        weapons[2] = weapon3.call();
        weapons[3] = weapon4.call();
    }

    // 获取当前武器
    public KindOfWeapon currentWeapon() {
        return weapons[currentAttackIndex];
    }

    public void nextWeapon() {
        int startIndex = currentAttackIndex;
        do {
            currentAttackIndex = (currentAttackIndex + 1) % 4;
        } while (weapons[currentAttackIndex] == null && currentAttackIndex != startIndex);
    }

    // 新增方法：直接切换到指定武器（用于外部调用）
    public void switchToNextWeapon() {
        nextWeapon();
    }

    // 新增方法：获取当前武器索引（调试用）
    public int getCurrentIndex() {
        return currentAttackIndex;
    }

    // 武器攻击音效
//    public static void weaponHitSound(float pitch) {
//        KindOfWeapon current = currentWeapon();
//        if (current != null) {
//            float volume = weaponNotNull() ? 0.7f : 1.0f;
//            current.hitSound(volume, pitch);
//        }
//    }
//
//
//    // 检查是否有武器存在
//    public static boolean weaponNotNull() {
//        for (KindOfWeapon wep : weapons) {
//            if (wep != null) return true;
//        }
//        return false;
//    }

    // 检查武器是否可攻击
    public boolean weaponCanAttack(Char owner, Char enemy) {
        if (enemy == null || owner.pos == enemy.pos || !Actor.chars().contains(enemy)) {
            Arrays.fill(canAttack, false);
            return false;
        }

        for (int i = 0; i < 4; i++) {
            KindOfWeapon wep = weapons[i];
            boolean reachable = wep != null && wep.canReach(owner, enemy.pos);

            // 第3和第4把武器需要手镯
            if (i >= 2 && wep != null) {
                canAttack[i] = hasBracelet && reachable;
            } else {
                canAttack[i] = reachable;
            }
        }
        return canAttack[0] || canAttack[1] || canAttack[2] || canAttack[3];
    }

    // 武器伤害计算
    public int weaponDamageRoll(Char owner) {
        KindOfWeapon wep = currentWeapon();
        if (wep == null) return 0;

        // 远程武器直接使用其伤害计算
        if (wep instanceof MissileWeapon || wep instanceof SpiritBow) {
            return wep.damageRoll(owner);
        }

        return wep.damageRoll(owner);
    }

    // 武器特效处理
    public int weaponProc(Char attacker, Char defender, int damage) {
        KindOfWeapon wep = currentWeapon();
        if (wep == null) return damage;

        // 远程武器直接使用其特效处理
        if (wep instanceof MissileWeapon || wep instanceof SpiritBow) {
            return wep.proc(attacker, defender, damage);
        }

        return wep.proc(attacker, defender, damage);
    }

    // 武器防御因子计算
    public int weaponDefenseFactor(Char owner) {
        int defenceFactor = 0;
        for (KindOfWeapon wep : weapons) {
            if (wep != null) {
                defenceFactor += wep.defenseFactor(owner);
            }
        }
        return defenceFactor;
    }

    // 武器准确度因子计算
//    public float weaponAccuracyFactor(Char owner) {
//        KindOfWeapon wep = currentWeapon();
//        if (wep == null) return 0;
//
//        // 远程武器直接使用其准确度
//        if (wep instanceof MissileWeapon || wep instanceof SpiritBow) {
//            return wep.accuracyFactor(owner);
//        }
//
//        return wep.accuracyFactor(owner);
//    }

    // 武器攻击延迟因子计算
    public float weaponDelayFactor(Char owner) {
        KindOfWeapon wep = currentWeapon();
        if (wep == null) return 0;

        // 远程武器直接使用其延迟因子
        if (wep instanceof MissileWeapon || wep instanceof SpiritBow) {
            return wep.delayFactor(owner);
        }

        return wep.delayFactor(owner);
    }

    // 设置手镯状态
    public void setHasBracelet(boolean hasBracelet) {
        this.hasBracelet = hasBracelet;
    }

    // 获取当前武器索引
    public int getCurrentAttackIndex() {
        return currentAttackIndex;
    }

    // 设置当前武器索引
    public void setCurrentAttackIndex(int index) {
        if (index >= 0 && index < 4 && weapons[index] != null) {
            currentAttackIndex = index;
        }
    }
}