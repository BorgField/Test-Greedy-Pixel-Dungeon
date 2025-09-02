package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.WheelChair;
import com.watabou.utils.QuietCallable;

import java.util.Arrays;

public class MultiWielding {
    private final QuietCallable<KindOfWeapon> weapon, weapon2, weapon3, weapon4;
    private int currentAttackIndex = 0; // 当前攻击武器索引
    private final KindOfWeapon[] weapons = new KindOfWeapon[4]; // 武器数组
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

    // 切换到下一把武器
    public void nextWeapon() {
        int startIndex = currentAttackIndex;
        do {
            currentAttackIndex = (currentAttackIndex + 1) % 4;
            // 跳过空槽位
        } while (weapons[currentAttackIndex] == null && currentAttackIndex != startIndex);
    }

    // 武器攻击音效
    public void weaponHitSound(float pitch) {
        KindOfWeapon current = currentWeapon();
        if (current != null) {
            current.hitSound(pitch);
        }
    }

    // 检查武器是否可攻击
    public boolean weaponCanAttack(Char owner, Char enemy) {
        if (enemy == null || !Actor.chars().contains(enemy)) {
            Arrays.fill(canAttack, false);
            return false;
        }

        for (int i = 0; i < 4; i++) {
            KindOfWeapon wep = weapons[i];
            boolean reachable = wep != null && wep.canReach(owner, enemy.pos);
            if (i >= 2 && wep != null) { // weapon3 and weapon4
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
        return wep != null ? wep.damageRoll(owner) : 0;
    }

    // 武器特效处理
    public int weaponProc(Char attacker, Char defender, int damage) {
        KindOfWeapon wep = currentWeapon();
        return wep != null ? wep.proc(attacker, defender, damage) : damage;
    }

    // 设置手镯状态
    public void setHasBracelet(boolean hasBracelet) {
        this.hasBracelet = hasBracelet;
    }
}
