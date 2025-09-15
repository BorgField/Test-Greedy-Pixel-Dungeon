/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.WheelChair;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

abstract public class KindOfWeapon extends EquipableItem {

    // 常量定义
    protected static final float TIME_TO_EQUIP = 1f;
    protected static final int MAX_NAME_LENGTH = 18;

    // 音效相关属性
    protected String hitSound = Assets.Sounds.HIT;
    protected float hitSoundPitch = 1f;

    // 武器槽类型枚举
    public enum WeaponSlot {
        PRIMARY_1, // slot0
        PRIMARY_2, // slot1
        PRIMARY_3, // slot2
        PRIMARY_4, // slot3
        SECONDARY   // 副武器槽
    }

    // 快速装备状态
    private static boolean isSwiftEquipping = false;

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_EQUIP)) {
            boolean isTwoHanded = this instanceof MeleeWeapon && ((MeleeWeapon)this).twoHanded;

            if (isTwoHanded) {
                // 双手武器特殊处理
                handleTwoHandedWeaponEquip(hero);
            } else {
                // 单手武器处理
                handleOneHandedWeaponEquip(hero);
            }
        } else {
            super.execute(hero, action);
        }
    }

    // 处理双手武器装备
    private void handleTwoHandedWeaponEquip(Hero hero) {
        // 检查当前是否有轮椅充能器buff
        boolean hasShivate = hero.buff(WheelChair.wheelRecharge.class) != null;

        // 检查连续槽组是否可用
        boolean group1Available =
                isSlotGroupAvailable(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        // 第二组仅在拥有buff时可用
        boolean group2Available = hasShivate &&
                isSlotGroupAvailable(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);

        if (group1Available && group2Available) {
            // 两组都可用，优先选择第一组
            equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        } else if (group1Available) {
            equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        } else if (group2Available) {
            equipToSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
        } else {
            showTwoHandedEquipSelection(hero);
        }
    }

    // 处理单手武器装备
    private void handleOneHandedWeaponEquip(Hero hero) {
        // 检查当前是否有buff
        boolean hasShivate = hero.buff(WheelChair.wheelRecharge.class) != null;

        // 动态构建槽位列表
        List<WeaponSlot> slots = new ArrayList<>();
        slots.add(WeaponSlot.PRIMARY_1);
        slots.add(WeaponSlot.PRIMARY_2);
        // 只有拥有buff时才添加第二组槽位
        if (hasShivate) {
            slots.add(WeaponSlot.PRIMARY_3);
            slots.add(WeaponSlot.PRIMARY_4);
        }

        for (WeaponSlot slot : slots) {
            KindOfWeapon currentWeapon = getWeaponInSlot(hero, slot);

            if (currentWeapon == null) {
                // 槽位为空，直接装备
                equipToSlot(hero, slot);
                return;
            }

            // 检查当前武器是否为双手武器
            boolean isCurrentTwoHanded = currentWeapon instanceof MeleeWeapon && ((MeleeWeapon)currentWeapon).twoHanded;

            if (isCurrentTwoHanded) {
                // 卸下双手武器及其连续组
                if (unequipTwoHandedWeaponGroup(hero, slot)) {
                    // 装备当前武器到目标槽位
                    equipToSlot(hero, slot);
                    return;
                }
            }
        }

        // 所有槽位都被占用（非双手武器），弹出选择窗口
        showOneHandedEquipSelection(hero);
    }

    // 检查连续槽组是否可用
    private boolean isSlotGroupAvailable(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        return getWeaponInSlot(hero, slot1) == null && getWeaponInSlot(hero, slot2) == null;
    }

    // 装备到连续槽组
    private void equipToSlotGroup(Hero hero, WeaponSlot mainSlot, WeaponSlot secondarySlot) {
        // 清空次要槽位
        setWeaponInSlot(hero, secondarySlot, null);

        // 装备到主槽位
        equipToSlot(hero, mainSlot);
    }

    // 显示双手武器装备选择窗口
    private void showTwoHandedEquipSelection(Hero hero) {
        // 检查当前是否有轮椅充能器buff
        boolean hasShivate = hero.buff(WheelChair.wheelRecharge.class) != null;

        String group1Status = getSlotGroupStatus(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        String group2Status = hasShivate ?
                getSlotGroupStatus(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4) :
                Messages.get(KindOfWeapon.class, "group_disabled");

        GameScene.show(new WndOptions(
                new ItemSprite(this),
                Messages.titleCase(name()),
                Messages.get(KindOfWeapon.class, "two_handed_equip_msg"),
                Messages.get(KindOfWeapon.class, "group1_status", group1Status),
                Messages.get(KindOfWeapon.class, "group2_status", group2Status)
        ) {
            @Override
            protected void onSelect(int index) {
                if (index == 0) {
                    // 选择第一组
                    unequipSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
                    equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
                } else if (index == 1 && hasShivate) {
                    // 选择第二组（仅在buff存在时可用）
                    unequipSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
                    equipToSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
                }
            }
        });
    }

    // 显示单手武器装备选择窗口
    private void showOneHandedEquipSelection(Hero hero) {
        List<String> options = new ArrayList<>();
        List<WeaponSlot> slotList = new ArrayList<>();

        boolean hasShivate = hero.buff(WheelChair.wheelRecharge.class) != null;

        // 添加主武器槽选项
        addSlotOption(hero, options, slotList, WeaponSlot.PRIMARY_1);
        addSlotOption(hero, options, slotList, WeaponSlot.PRIMARY_2);

        if (hasShivate) {
            addSlotOption(hero, options, slotList, WeaponSlot.PRIMARY_3);
            addSlotOption(hero, options, slotList, WeaponSlot.PRIMARY_4);
        }

        // 如果是决斗家，添加副武器槽选项
        if (hero.subClass == HeroSubClass.CHAMPION) {
            addSlotOption(hero, options, slotList, WeaponSlot.SECONDARY);
        }

        GameScene.show(new WndOptions(
                new ItemSprite(this),
                Messages.titleCase(name()),
                Messages.get(KindOfWeapon.class, "one_handed_equip_msg"),
                options.toArray(new String[0])
        ) {
            @Override
            protected void onSelect(int index) {
                if (index >= 0 && index < slotList.size()) {
                    WeaponSlot selectedSlot = slotList.get(index);
                    KindOfWeapon currentWeapon = getWeaponInSlot(hero, selectedSlot);

                    // 检查当前武器是否为双手武器
                    boolean isCurrentTwoHanded = currentWeapon instanceof MeleeWeapon && ((MeleeWeapon)currentWeapon).twoHanded;

                    if (isCurrentTwoHanded) {
                        // 卸下双手武器及其连续组
                        unequipTwoHandedWeaponGroup(hero, selectedSlot);
                    }

                    // 装备当前武器
                    equipToSlot(hero, selectedSlot);
                }
            }
        });
    }

    // 添加槽位选项
    private void addSlotOption(Hero hero, List<String> options, List<WeaponSlot> slotList, WeaponSlot slot) {
        KindOfWeapon weapon = getWeaponInSlot(hero, slot);
        String weaponName = weapon != null ? Messages.titleCase(weapon.trueName()) : Messages.get(KindOfWeapon.class, "empty");

        if (weaponName.length() > MAX_NAME_LENGTH) {
            weaponName = weaponName.substring(0, MAX_NAME_LENGTH - 3) + "...";
        }

        String slotName = Messages.get(KindOfWeapon.class, "slot_" + slot.name().toLowerCase());
        options.add(Messages.get(KindOfWeapon.class, "slot_option", slotName, weaponName));
        slotList.add(slot);
    }

    // 获取槽组状态
    private String getSlotGroupStatus(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        KindOfWeapon weapon1 = getWeaponInSlot(hero, slot1);
        KindOfWeapon weapon2 = getWeaponInSlot(hero, slot2);

        if (weapon1 == null && weapon2 == null) {
            return Messages.get(KindOfWeapon.class, "group_empty");
        }

        String weapon1Name = weapon1 != null ? Messages.titleCase(weapon1.trueName()) : Messages.get(KindOfWeapon.class, "empty");
        String weapon2Name = weapon2 != null ? Messages.titleCase(weapon2.trueName()) : Messages.get(KindOfWeapon.class, "empty");

        if (weapon1Name.length() > MAX_NAME_LENGTH) {
            weapon1Name = weapon1Name.substring(0, MAX_NAME_LENGTH - 3) + "...";
        }
        if (weapon2Name.length() > MAX_NAME_LENGTH) {
            weapon2Name = weapon2Name.substring(0, MAX_NAME_LENGTH - 3) + "...";
        }

        return Messages.get(KindOfWeapon.class, "group_occupied", weapon1Name, weapon2Name);
    }

    // 卸下连续槽组
    private void unequipSlotGroup(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        unequipWeaponInSlot(hero, slot1);
        unequipWeaponInSlot(hero, slot2);
    }

    // 卸下单个槽位的武器
    private void unequipWeaponInSlot(Hero hero, WeaponSlot slot) {
        KindOfWeapon weapon = getWeaponInSlot(hero, slot);
        if (weapon != null) {
            weapon.doUnequip(hero, true, false);
        }
    }

    // 卸下双手武器及其连续组
    private boolean unequipTwoHandedWeaponGroup(Hero hero, WeaponSlot slot) {
        // 确定连续组
        WeaponSlot groupSlot1, groupSlot2;
        if (slot == WeaponSlot.PRIMARY_1 || slot == WeaponSlot.PRIMARY_2) {
            groupSlot1 = WeaponSlot.PRIMARY_1;
            groupSlot2 = WeaponSlot.PRIMARY_2;
        } else {
            groupSlot1 = WeaponSlot.PRIMARY_3;
            groupSlot2 = WeaponSlot.PRIMARY_4;
        }

        // 卸下组内所有武器
        unequipSlotGroup(hero, groupSlot1, groupSlot2);
        return true;
    }

    // 获取指定槽位的武器
    private KindOfWeapon getWeaponInSlot(Hero hero, WeaponSlot slot) {
        switch (slot) {
            case PRIMARY_1: return hero.belongings.weapon;
            case PRIMARY_2: return hero.belongings.weapon2;
            case PRIMARY_3: return hero.belongings.weapon3;
            case PRIMARY_4: return hero.belongings.weapon4;
            case SECONDARY: return hero.belongings.secondWep;
            default: return null;
        }
    }

    @Override
    public boolean isEquipped(Hero hero) {
        if (hero == null) return false;
        return this == hero.belongings.weapon ||
                this == hero.belongings.weapon2 ||
                this == hero.belongings.weapon3 ||
                this == hero.belongings.weapon4 ||
                this == hero.belongings.secondWep;
    }

    // 装备时间计算
    protected float timeToEquip(Hero hero) {
        return isSwiftEquipping ? 0f : TIME_TO_EQUIP;
    }

    // 通用装备方法
    public boolean equipToSlot(Hero hero, WeaponSlot slot) {
        // 检查是否双手武器
        if (this instanceof MeleeWeapon && ((MeleeWeapon)this).twoHanded) {
            // 双手武器只能装备在主武器槽
            if (slot == WeaponSlot.SECONDARY) {
                GLog.w(Messages.get(KindOfWeapon.class, "cannot_equip_secondary"));
                return false;
            }

            // 确定连续组
            WeaponSlot secondarySlot;
            if (slot == WeaponSlot.PRIMARY_1) {
                secondarySlot = WeaponSlot.PRIMARY_2;
            } else if (slot == WeaponSlot.PRIMARY_3) {
                secondarySlot = WeaponSlot.PRIMARY_4;
            } else {
                GLog.w(Messages.get(KindOfWeapon.class, "invalid_slot_for_two_handed"));
                return false;
            }

            // 确保连续组可用
            if (!isSlotGroupAvailable(hero, slot, secondarySlot)) {
                GLog.w(Messages.get(KindOfWeapon.class, "slot_group_not_available"));
                return false;
            }

            // 清空次要槽位
            setWeaponInSlot(hero, secondarySlot, null);
        }

        // 检查PRIMARY_3和PRIMARY_4槽位是否需要buff
        if ((slot == WeaponSlot.PRIMARY_3 || slot == WeaponSlot.PRIMARY_4) &&
                hero.buff(WheelChair.wheelRecharge.class) == null) {
            GLog.w(Messages.get(KindOfWeapon.class, "wheel_recharge_required"));
            return false;
        }

        isSwiftEquipping = false;

        // 检查快速装备天赋
        if (hero.belongings.contains(this) && hero.hasTalent(Talent.SWIFT_EQUIP)) {
            if (hero.buff(Talent.SwiftEquipCooldown.class) == null ||
                    hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
                isSwiftEquipping = true;
            }
        }

        // 圣洁直觉天赋检测诅咒
        if (hero.heroClass != HeroClass.CLERIC &&
                hero.hasTalent(Talent.HOLY_INTUITION) &&
                cursed && !cursedKnown &&
                Random.Int(20) < 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION)) {
            cursedKnown = true;
            GLog.p(Messages.get(this, "curse_detected"));
            return false;
        }

        detachAll(hero.belongings.backpack);

        // 获取目标槽位的当前武器
        KindOfWeapon currentWeapon = getWeaponInSlot(hero, slot);

        // 尝试卸载当前武器
        if (currentWeapon == null || currentWeapon.doUnequip(hero, true, false)) {
            // 设置新武器到指定槽位
            setWeaponInSlot(hero, slot, this);
            activate(hero);
            Talent.onItemEquipped(hero, this);
            Badges.validateDuelistUnlock();
            updateQuickslot();

            cursedKnown = true;
            if (cursed) {
                equipCursed(hero);
                GLog.n(Messages.get(KindOfWeapon.class, "equip_cursed"));
            }

            hero.spendAndNext(timeToEquip(hero));
            if (isSwiftEquipping) {
                GLog.i(Messages.get(this, "swift_equip"));
                handleSwiftEquipCooldown(hero);
                isSwiftEquipping = false;
            }
            return true;
        } else {
            isSwiftEquipping = false;
            collect(hero.belongings.backpack);
            return false;
        }
    }

    // 设置指定槽位的武器
    private void setWeaponInSlot(Hero hero, WeaponSlot slot, KindOfWeapon weapon) {
        switch (slot) {
            case PRIMARY_1: hero.belongings.weapon = weapon; break;
            case PRIMARY_2: hero.belongings.weapon2 = weapon; break;
            case PRIMARY_3: hero.belongings.weapon3 = weapon; break;
            case PRIMARY_4: hero.belongings.weapon4 = weapon; break;
            case SECONDARY: hero.belongings.secondWep = weapon; break;
        }
    }

    // 处理快速装备冷却
    private void handleSwiftEquipCooldown(Hero hero) {
        if (hero.buff(Talent.SwiftEquipCooldown.class) == null) {
            Buff.affect(hero, Talent.SwiftEquipCooldown.class, 19f)
                    .secondUse = hero.pointsInTalent(Talent.SWIFT_EQUIP) == 2;
        } else if (hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
            hero.buff(Talent.SwiftEquipCooldown.class).secondUse = false;
        }
    }

    // 保持原有方法作为默认装备方式
    @Override
    public boolean doEquip(Hero hero) {
        return equipToSlot(hero, WeaponSlot.PRIMARY_1);
    }

    // 装备到副武器槽
    public boolean equipSecondary(Hero hero) {
        return equipToSlot(hero, WeaponSlot.SECONDARY);
    }

    // 装备到指定主武器槽
    public boolean equipToPrimarySlot(Hero hero, int slotIndex) {
        if (slotIndex < 1 || slotIndex > 4) {
            GLog.w(Messages.get(this, "invalid_slot"));
            return false;
        }
        return equipToSlot(hero, WeaponSlot.values()[slotIndex - 1]);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        WeaponSlot slot = findEquippedSlot(hero);
        if (slot == null) return false; // 不在任何槽位中

        // 清空槽位
        setWeaponInSlot(hero, slot, null);

        if (super.doUnequip(hero, collect, single)) {
            return true;
        } else {
            // 恢复槽位
            setWeaponInSlot(hero, slot, this);
            return false;
        }
    }

    // 查找当前武器所在的槽位
    private WeaponSlot findEquippedSlot(Hero hero) {
        if (this == hero.belongings.weapon) return WeaponSlot.PRIMARY_1;
        if (this == hero.belongings.weapon2) return WeaponSlot.PRIMARY_2;
        if (this == hero.belongings.weapon3) return WeaponSlot.PRIMARY_3;
        if (this == hero.belongings.weapon4) return WeaponSlot.PRIMARY_4;
        if (this == hero.belongings.secondWep) return WeaponSlot.SECONDARY;
        return null;
    }

    // 伤害计算方法
    public int min() {
        return min(buffedLvl());
    }

    public int max() {
        return max(buffedLvl());
    }

    abstract public int min(int lvl);
    abstract public int max(int lvl);

    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            return Hero.heroDamageIntRange(min(), max());
        } else {
            return Random.NormalIntRange(min(), max());
        }
    }

    // 战斗属性方法
    public float accuracyFactor(Char owner, Char target) { return 1f;}

    public float delayFactor(Char owner) { return 1f;}

    public int reachFactor(Char owner) { return 1;}

    public boolean canReach(Char owner, int target) {
        int reach = reachFactor(owner);
        if (Dungeon.level.distance(owner.pos, target) > reach) {
            return false;
        } else {
            boolean[] passable = BArray.not(Dungeon.level.solid, null);
            for (Char ch : Actor.chars()) {
                if (ch != owner) passable[ch.pos] = false;
            }

            PathFinder.buildDistanceMap(target, passable, reach);
            return PathFinder.distance[owner.pos] <= reach;
        }
    }

    public int defenseFactor(Char owner) {
        return 0;
    }

    public int proc(Char attacker, Char defender, int damage) {
        return damage;
    }

    // 音效方法
    public void hitSound(float volume, float pitch) {
        Sample.INSTANCE.play(hitSound, volume, pitch * hitSoundPitch);
    }
}
