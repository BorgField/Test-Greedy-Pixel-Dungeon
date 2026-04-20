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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DualWieldTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ShivaBangle;
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

    protected static final float TIME_TO_EQUIP = 1f;
    protected static final int MAX_NAME_LENGTH = 18;

    protected String hitSound = Assets.Sounds.HIT;
    protected float hitSoundPitch = 1f;

    public enum WeaponSlot {
        PRIMARY_1,
        PRIMARY_2,
        PRIMARY_3,
        PRIMARY_4,
        SECONDARY
    }

    private static boolean isSwiftEquipping = false;

    @Override
    public boolean doEquip(Hero hero) {
        boolean hasShiva = hero.buff(ShivaBangle.MultiArmBlows.class) != null;
        
        // 只有 MeleeWeapon 才有 handedType 属性
        if (!(this instanceof MeleeWeapon)) {
            return equipOneHanded(hero, hasShiva);
        }

        MeleeWeapon meleeWeapon = (MeleeWeapon) this;
        
        // 根据武器类型进入不同的装备流程
        switch (meleeWeapon.handedType) {
            case TWO_HANDED:
                return equipTwoHanded(hero, hasShiva);
            case DUAL_PURPOSE:
                return equipDualPurpose(hero, hasShiva);
            case OFF_HAND:
                return equipOffHand(hero, hasShiva);
            case ONE_HANDED:
            default:
                return equipOneHanded(hero, hasShiva);
        }
    }

    private boolean equipTwoHanded(Hero hero, boolean hasShiva) {
        // 检查第一组槽位是否可用
        boolean group1Available =
                getWeaponInSlot(hero, WeaponSlot.PRIMARY_1) == null &&
                getWeaponInSlot(hero, WeaponSlot.PRIMARY_2) == null;

        if (group1Available) {
            return equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        } else {
            showTwoHandedEquipSelection(hero, hasShiva);
            return false;
        }
    }

    //装备单手武器到可用的槽位
    private boolean equipOneHanded(Hero hero, boolean hasShiva) {
        // 遍历所有可用的主武器槽位
        for (WeaponSlot slot : getAvailablePrimarySlots(hasShiva)) {
            if (getWeaponInSlot(hero, slot) == null && !isTwoHandedGroupBlockedSlot(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        // 所有槽位都被占用，弹出选择窗口
        showOneHandedEquipSelection(hero, hasShiva);
        return false;
    }

    private boolean equipOffHand(Hero hero, boolean hasShiva) {
        List<WeaponSlot> targetSlots = new ArrayList<>();
        targetSlots.add(WeaponSlot.PRIMARY_2);
        if (hasShiva) {
            targetSlots.add(WeaponSlot.PRIMARY_4);
        }

        //寻找第一个可用的位置
        for (WeaponSlot slot : targetSlots) {
            KindOfWeapon currentWeapon = getWeaponInSlot(hero, slot);
            if ((currentWeapon == null || currentWeapon.doUnequip(hero, true, false))
                    && !isTwoHandedGroupBlockedSlot(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        GLog.w(Messages.get(KindOfWeapon.class, "offhand_no_slot"));
        return false;
    }

    private boolean equipDualPurpose(Hero hero, boolean hasShiva) {
        // 优先尝试单手模式 - 遍历所有可用的主武器槽位
        for (WeaponSlot slot : getAvailablePrimarySlots(hasShiva)) {
            if (getWeaponInSlot(hero, slot) == null && !isTwoHandedGroupBlockedSlot(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        
        // 单手模式失败,降级为双手模式
        return equipTwoHanded(hero, hasShiva);
    }

    // 检查指定槽位是否被双手武器锁定
    private boolean isTwoHandedGroupBlockedSlot(Hero hero, WeaponSlot slot) {
        WeaponSlot groupSlot1, groupSlot2;

        if (slot == WeaponSlot.PRIMARY_1 || slot == WeaponSlot.PRIMARY_2) {
            groupSlot1 = WeaponSlot.PRIMARY_1;
            groupSlot2 = WeaponSlot.PRIMARY_2;
        } else if (slot == WeaponSlot.PRIMARY_3 || slot == WeaponSlot.PRIMARY_4) {
            groupSlot1 = WeaponSlot.PRIMARY_3;
            groupSlot2 = WeaponSlot.PRIMARY_4;
        } else {
            return false; // 其他槽位不属于双手武器组
        }

        // 检查组内是否有双手武器
        return isSlotOccupiedByTwoHandedWeapon(hero, groupSlot1) ||
                isSlotOccupiedByTwoHandedWeapon(hero, groupSlot2);
    }


    private boolean equipToSlotGroup(Hero hero, WeaponSlot mainSlot, WeaponSlot secondarySlot) {
        // 卸下次要槽位的武器
        KindOfWeapon secondaryWeapon = getWeaponInSlot(hero, secondarySlot);
        if (secondaryWeapon != null) {
            if (!secondaryWeapon.doUnequip(hero, true, false)) {
                return false; // 如果卸下失败（例如被诅咒），则取消装备
            }
        }

        // 装备到主槽位
        return equipToSlot(hero, mainSlot);
    }

    private void showTwoHandedEquipSelection(Hero hero, boolean hasShiva) {
        String group1Status =
                getSlotGroupStatus(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        String group2Status = hasShiva ?
                getSlotGroupStatus(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4) :
                null;

        List<String> options = new ArrayList<>();
            options.add(Messages.get(KindOfWeapon.class, "group1_status", group1Status));
        if (hasShiva) {
            options.add(Messages.get(KindOfWeapon.class, "group2_status", group2Status));
        }

        GameScene.show(new WndOptions(
                new ItemSprite(this),
                Messages.titleCase(name()),
                Messages.get(KindOfWeapon.class, "two_handed_equip_msg"),
                options.toArray(new String[0])
        ) {
            @Override
            protected void onSelect(int index) {
                if (index == 0) {
                    // 选择第一组
                    unequipSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
                    equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
                } else if (index == 1 && hasShiva) {
                    // 选择第二组（仅在buff存在时可用）
                    unequipSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
                    equipToSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
                }
            }
        });
    }

    //获取可用的主武器槽位列表
    private List<WeaponSlot> getAvailablePrimarySlots(boolean hasShiva) {
        List<WeaponSlot> slots = new ArrayList<>();
        slots.add(WeaponSlot.PRIMARY_1);
        slots.add(WeaponSlot.PRIMARY_2);
        if (hasShiva) {
            slots.add(WeaponSlot.PRIMARY_3);
            slots.add(WeaponSlot.PRIMARY_4);
        }
        return slots;
    }

    // 检查并卸下单手武器选择窗口中选中槽位所在组的双手武器
    private void checkAndUnequipTwoHandedGroupIfNeeded(Hero hero, WeaponSlot selectedSlot) {
        // 确定选中槽位属于哪个组
        WeaponSlot groupSlot1, groupSlot2;

        if (selectedSlot == WeaponSlot.PRIMARY_1 || selectedSlot == WeaponSlot.PRIMARY_2) {
            groupSlot1 = WeaponSlot.PRIMARY_1;
            groupSlot2 = WeaponSlot.PRIMARY_2;
        } else if (selectedSlot == WeaponSlot.PRIMARY_3 || selectedSlot == WeaponSlot.PRIMARY_4) {
            groupSlot1 = WeaponSlot.PRIMARY_3;
            groupSlot2 = WeaponSlot.PRIMARY_4;
        } else {
            return; // SECONDARY 或其他槽位不属于双手武器组
        }

        // 如果该组有双手武器,则卸下
        if (isSlotOccupiedByTwoHandedWeapon(hero, groupSlot1) ||
                isSlotOccupiedByTwoHandedWeapon(hero, groupSlot2)) {
            unequipSlotGroup(hero, groupSlot1, groupSlot2);
        }
    }

    private void showOneHandedEquipSelection(Hero hero, boolean hasShiva) {
        List<String> options = new ArrayList<>();
        List<WeaponSlot> slotList = new ArrayList<>();

        // 添加所有可用的主武器槽位
        for (WeaponSlot slot : getAvailablePrimarySlots(hasShiva)) {
            addSlotOption(hero, options, slotList, slot);
        }

        // 如果是 CHAMPION 职业,添加副武器槽位
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

                    // 检查并卸下选中槽位所在组的双手武器
                    checkAndUnequipTwoHandedGroupIfNeeded(hero, selectedSlot);

                    // 装备当前武器到指定槽位
                    equipToSlot(hero, selectedSlot);
                }
            }
        });
    }

    private void addSlotOption(Hero hero, List<String> options, List<WeaponSlot> slotList, WeaponSlot slot) {
        String weaponName = getWeaponNameForSlot(hero, slot);

        if (weaponName.length() > MAX_NAME_LENGTH) {
            weaponName = weaponName.substring(0, MAX_NAME_LENGTH - 3) + "...";
        }

        String slotName = Messages.get(KindOfWeapon.class, "slot_" + slot.name().toLowerCase());
        options.add(Messages.get(KindOfWeapon.class, "slot_option", slotName, weaponName));
        slotList.add(slot);
    }

    // 获取槽位中武器的名称，如果是次要槽位且被双手武器占用，则使用主槽位的武器名称
    private String getWeaponNameForSlot(Hero hero, WeaponSlot slot) {
        KindOfWeapon weapon = getWeaponInSlot(hero, slot);

        // 检查是否是次要槽位且被双手武器占用
        if (slot == WeaponSlot.PRIMARY_2 && isSlotOccupiedByTwoHandedWeapon(hero, WeaponSlot.PRIMARY_1)) {
            weapon = getWeaponInSlot(hero, WeaponSlot.PRIMARY_1);
        } else if (slot == WeaponSlot.PRIMARY_4 && isSlotOccupiedByTwoHandedWeapon(hero, WeaponSlot.PRIMARY_3)) {
            weapon = getWeaponInSlot(hero, WeaponSlot.PRIMARY_3);
        }

        return weapon != null ? Messages.titleCase(weapon.trueName()) : Messages.get(KindOfWeapon.class, "empty");
    }


    // 检查槽位是否被双手武器占用
    private boolean isSlotOccupiedByTwoHandedWeapon(Hero hero, WeaponSlot slot) {
        KindOfWeapon weapon = getWeaponInSlot(hero, slot);
        return weapon instanceof MeleeWeapon && ((MeleeWeapon) weapon).isTwoHanded();
    }

    private String getSlotGroupStatus(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        String weapon1Name = getWeaponNameForSlot(hero, slot1);
        String weapon2Name = getWeaponNameForSlot(hero, slot2);

        if (weapon1Name.length() > MAX_NAME_LENGTH) {
            weapon1Name = weapon1Name.substring(0, MAX_NAME_LENGTH - 3) + "...";
        }
        if (weapon2Name.length() > MAX_NAME_LENGTH) {
            weapon2Name = weapon2Name.substring(0, MAX_NAME_LENGTH - 3) + "...";
        }

        return Messages.get(KindOfWeapon.class, "group_occupied", weapon1Name, weapon2Name);
    }

    public static void unequipSlotGroup(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        // 批量卸下,避免多次UI刷新
        KindOfWeapon weapon1 = getWeaponInSlot(hero, slot1);
        KindOfWeapon weapon2 = getWeaponInSlot(hero, slot2);

        if (weapon1 != null) weapon1.doUnequip(hero, true, false);
        if (weapon2 != null) weapon2.doUnequip(hero, true, false);
    }

    private static KindOfWeapon getWeaponInSlot(Hero hero, WeaponSlot slot) {
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

    protected float timeToEquip(Hero hero) {
        return isSwiftEquipping ? 0f : TIME_TO_EQUIP;
    }

    public boolean equipToSlot(Hero hero, WeaponSlot slot) {
        isSwiftEquipping = false;

        if (hero.belongings.contains(this) && hero.hasTalent(Talent.SWIFT_EQUIP)) {
            if (hero.buff(Talent.SwiftEquipCooldown.class) == null ||
                    hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
                isSwiftEquipping = true;
            }
        }

        if (hero.heroClass != HeroClass.CLERIC &&
                hero.hasTalent(Talent.HOLY_INTUITION) &&
                cursed && !cursedKnown &&
                Random.Int(20) < 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION)) {
            cursedKnown = true;
            GLog.p(Messages.get(this, "curse_detected"));
            return false;
        }

        detachAll(hero.belongings.backpack);

        KindOfWeapon currentWeapon = getWeaponInSlot(hero, slot);

        if (currentWeapon == null || currentWeapon.doUnequip(hero, true, false)) {
            setWeaponInSlot(hero, slot, this);
            activate(hero);
            Talent.onItemEquipped(hero, this);
            Badges.validateDuelistUnlock();
            updateQuickslot();
            
            // 激活双持武器状态追踪器buff
            DualWieldTracker.ensureActive(hero);

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

    public static void setWeaponInSlot(Hero hero, WeaponSlot slot, KindOfWeapon weapon) {
        switch (slot) {
            case PRIMARY_1: hero.belongings.weapon = weapon; break;
            case PRIMARY_2: hero.belongings.weapon2 = weapon; break;
            case PRIMARY_3: hero.belongings.weapon3 = weapon; break;
            case PRIMARY_4: hero.belongings.weapon4 = weapon; break;
            case SECONDARY: hero.belongings.secondWep = weapon; break;
        }
    }

    private void handleSwiftEquipCooldown(Hero hero) {
        if (hero.buff(Talent.SwiftEquipCooldown.class) == null) {
            Buff.affect(hero, Talent.SwiftEquipCooldown.class, 19f)
                    .secondUse = hero.pointsInTalent(Talent.SWIFT_EQUIP) == 2;
        } else if (hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
            hero.buff(Talent.SwiftEquipCooldown.class).secondUse = false;
        }
    }

    public boolean equipSecondary(Hero hero) {
        return equipToSlot(hero, WeaponSlot.SECONDARY);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        WeaponSlot slot = findEquippedSlot(hero);
        if (slot == null) return false;

        setWeaponInSlot(hero, slot, null);

        if (super.doUnequip(hero, collect, single)) {
            return true;
        } else {
            setWeaponInSlot(hero, slot, this);
            return false;
        }
    }

    private WeaponSlot findEquippedSlot(Hero hero) {
        if (this == hero.belongings.weapon) return WeaponSlot.PRIMARY_1;
        if (this == hero.belongings.weapon2) return WeaponSlot.PRIMARY_2;
        if (this == hero.belongings.weapon3) return WeaponSlot.PRIMARY_3;
        if (this == hero.belongings.weapon4) return WeaponSlot.PRIMARY_4;
        if (this == hero.belongings.secondWep) return WeaponSlot.SECONDARY;
        return null;
    }

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

    public void hitSound(float volume, float pitch) {
        Sample.INSTANCE.play(hitSound, volume, pitch * hitSoundPitch);
    }
}