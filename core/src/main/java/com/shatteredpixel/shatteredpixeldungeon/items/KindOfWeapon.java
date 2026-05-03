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
import com.shatteredpixel.shatteredpixeldungeon.items.sets.EquipmentSet;
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
        PRIMARY_1(HandType.MAIN_HAND, false),   // 主手槽位1
        PRIMARY_2(HandType.OFF_HAND,  false),   // 副手槽位1
        PRIMARY_3(HandType.MAIN_HAND, true),    // 主手槽位2
        PRIMARY_4(HandType.OFF_HAND,  true),    // 副手槽位2
        SECONDARY(HandType.SECONDARY, false);   // 冠军副武器槽位

        public enum HandType { MAIN_HAND, OFF_HAND, SECONDARY }

        private final HandType handType;
        private final boolean requiresShiva;

        WeaponSlot(HandType handType, boolean requiresShiva) {
            this.handType = handType;
            this.requiresShiva = requiresShiva;
        }

        public boolean isMainHand()   { return handType == HandType.MAIN_HAND; }
        public boolean isOffHand()    { return handType == HandType.OFF_HAND; }
        public boolean isSecondary()  { return handType == HandType.SECONDARY; }
        public boolean isAvailable(boolean hasShiva) { return !requiresShiva || hasShiva; }

        // 获取同组的配对槽位（仅用于双手武器组，硬编码）
        public WeaponSlot getPartnerSlot() {
            switch (this) {
                case PRIMARY_1: return PRIMARY_2;
                case PRIMARY_2: return PRIMARY_1;
                case PRIMARY_3: return PRIMARY_4;
                case PRIMARY_4: return PRIMARY_3;
                default: return null;
            }
        }
    }

    private static boolean isSwiftEquipping = false;

    @Override
    public boolean doEquip(Hero hero) {
        boolean hasShiva = hero.buff(ShivaBangle.MultiArmBlows.class) != null;

        if (!(this instanceof MeleeWeapon)) {
            return equipOneHanded(hero, hasShiva);
        }

        MeleeWeapon meleeWeapon = (MeleeWeapon) this;
        switch (meleeWeapon.handedType) {
            case TWO_HANDED:   return equipTwoHanded(hero, hasShiva);
            case DUAL_PURPOSE: return equipDualPurpose(hero, hasShiva);
            case OFF_HAND:     return equipOffHand(hero, hasShiva);
            default:           return equipOneHanded(hero, hasShiva);
        }
    }

    // ========================= 双手武器逻辑 =========================
    private boolean equipTwoHanded(Hero hero, boolean hasShiva) {
        // 第一组（槽1+槽2）是否全空？
        boolean group1Empty = (getWeaponInSlot(hero, WeaponSlot.PRIMARY_1) == null &&
                getWeaponInSlot(hero, WeaponSlot.PRIMARY_2) == null);
        if (group1Empty) {
            return equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        } else {
            showTwoHandedEquipSelection(hero, hasShiva);
            return false;
        }
    }

    // 展示两组可选位置（如果hasShiva激活则第二组可用）
    private void showTwoHandedEquipSelection(Hero hero, boolean hasShiva) {
        String group1Status = getSlotGroupStatus(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
        List<String> options = new ArrayList<>();
        options.add(Messages.get(KindOfWeapon.class, "group1_status", group1Status));
        if (hasShiva) {
            String group2Status = getSlotGroupStatus(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
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
                    unequipSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
                    equipToSlotGroup(hero, WeaponSlot.PRIMARY_1, WeaponSlot.PRIMARY_2);
                } else if (index == 1 && hasShiva) {
                    unequipSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
                    equipToSlotGroup(hero, WeaponSlot.PRIMARY_3, WeaponSlot.PRIMARY_4);
                }
            }
        });
    }

    // 装备到一组槽位（双手武器实际只占主槽位，但会清空副槽位）
    private boolean equipToSlotGroup(Hero hero, WeaponSlot mainSlot, WeaponSlot offSlot) {
        KindOfWeapon offWeapon = getWeaponInSlot(hero, offSlot);
        if (offWeapon != null && !offWeapon.doUnequip(hero, true, false)) {
            return false;   // 副槽位被诅咒武器卡住
        }
        return equipToSlot(hero, mainSlot);
    }

    // 卸下一整组（通常用于双手武器替换）
    public static void unequipSlotGroup(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        KindOfWeapon w1 = getWeaponInSlot(hero, slot1);
        KindOfWeapon w2 = getWeaponInSlot(hero, slot2);
        if (w1 != null) w1.doUnequip(hero, true, false);
        if (w2 != null) w2.doUnequip(hero, true, false);
    }

    // ========================= 单手武器逻辑 =========================
    // 优先尝试所有主手槽位，再尝试副手槽位
    private boolean equipOneHanded(Hero hero, boolean hasShiva) {
        for (WeaponSlot slot : getAvailableSlots(hasShiva)) {
            if (slot.isMainHand() && getWeaponInSlot(hero, slot) == null && !isTwoHandedGroupBlocked(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        for (WeaponSlot slot : getAvailableSlots(hasShiva)) {
            if (slot.isOffHand() && getWeaponInSlot(hero, slot) == null && !isTwoHandedGroupBlocked(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        showOneHandedEquipSelection(hero, hasShiva);
        return false;
    }

    // 两用武器：先尝试单手（主手→副手），失败则按双手处理
    private boolean equipDualPurpose(Hero hero, boolean hasShiva) {
        for (WeaponSlot slot : getAvailableSlots(hasShiva)) {
            if (slot.isMainHand() && getWeaponInSlot(hero, slot) == null && !isTwoHandedGroupBlocked(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        for (WeaponSlot slot : getAvailableSlots(hasShiva)) {
            if (slot.isOffHand() && getWeaponInSlot(hero, slot) == null && !isTwoHandedGroupBlocked(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        return equipTwoHanded(hero, hasShiva);
    }

    // 副手专用武器：只允许装备到副手槽位（槽2或槽4）
    private boolean equipOffHand(Hero hero, boolean hasShiva) {
        List<WeaponSlot> offSlots = new ArrayList<>();
        offSlots.add(WeaponSlot.PRIMARY_2);
        if (hasShiva) offSlots.add(WeaponSlot.PRIMARY_4);
        for (WeaponSlot slot : offSlots) {
            KindOfWeapon current = getWeaponInSlot(hero, slot);
            if ((current == null || current.doUnequip(hero, true, false)) && !isTwoHandedGroupBlocked(hero, slot)) {
                return equipToSlot(hero, slot);
            }
        }
        GLog.w(Messages.get(KindOfWeapon.class, "offhand_no_slot"));
        return false;
    }

    // 检查某个槽位是否被同一组内的双手武器锁死
    private boolean isTwoHandedGroupBlocked(Hero hero, WeaponSlot slot) {
        // 第一组（槽1+槽2）中有任何一槽被双手武器占用 → 另一槽不可用
        if (slot == WeaponSlot.PRIMARY_1 || slot == WeaponSlot.PRIMARY_2) {
            return isSlotOccupiedByTwoHandedWeapon(hero, WeaponSlot.PRIMARY_1) ||
                    isSlotOccupiedByTwoHandedWeapon(hero, WeaponSlot.PRIMARY_2);
        }
        // 第二组（槽3+槽4）
        if (slot == WeaponSlot.PRIMARY_3 || slot == WeaponSlot.PRIMARY_4) {
            return isSlotOccupiedByTwoHandedWeapon(hero, WeaponSlot.PRIMARY_3) ||
                    isSlotOccupiedByTwoHandedWeapon(hero, WeaponSlot.PRIMARY_4);
        }
        return false; // 副武器槽不受双手武器影响
    }

    // 当用户手动选择单手武器槽位时，若选中槽位所在组有双手武器，则先卸下那组
    private void checkAndUnequipTwoHandedGroup(Hero hero, WeaponSlot selectedSlot) {
        WeaponSlot groupSlotA = null, groupSlotB = null;
        if (selectedSlot == WeaponSlot.PRIMARY_1 || selectedSlot == WeaponSlot.PRIMARY_2) {
            groupSlotA = WeaponSlot.PRIMARY_1;
            groupSlotB = WeaponSlot.PRIMARY_2;
        } else if (selectedSlot == WeaponSlot.PRIMARY_3 || selectedSlot == WeaponSlot.PRIMARY_4) {
            groupSlotA = WeaponSlot.PRIMARY_3;
            groupSlotB = WeaponSlot.PRIMARY_4;
        }
        if (groupSlotA != null && (isSlotOccupiedByTwoHandedWeapon(hero, groupSlotA) ||
                isSlotOccupiedByTwoHandedWeapon(hero, groupSlotB))) {
            unequipSlotGroup(hero, groupSlotA, groupSlotB);
        }
    }

    // 弹出单手武器选择窗口（显示各槽位当前武器）
    private void showOneHandedEquipSelection(Hero hero, boolean hasShiva) {
        List<String> options = new ArrayList<>();
        List<WeaponSlot> slotList = new ArrayList<>();
        for (WeaponSlot slot : getAvailableSlots(hasShiva)) {
            addSlotOption(hero, options, slotList, slot);
        }
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
                    WeaponSlot selected = slotList.get(index);
                    checkAndUnequipTwoHandedGroup(hero, selected);
                    equipToSlot(hero, selected);
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

    // 获取槽位中的武器名称（若副手被双手武器占用，显示主手武器名）
    private String getWeaponNameForSlot(Hero hero, WeaponSlot slot) {
        KindOfWeapon weapon = getWeaponInSlot(hero, slot);
        // 副手槽位如果被双手武器占用，实际武器在主手，需要显示主手武器名称
        if (slot.isOffHand()) {
            WeaponSlot partner = slot.getPartnerSlot();
            if (partner != null && isSlotOccupiedByTwoHandedWeapon(hero, partner)) {
                weapon = getWeaponInSlot(hero, partner);
            }
        }
        return weapon != null ? Messages.titleCase(weapon.trueName()) : Messages.get(KindOfWeapon.class, "empty");
    }

    // 检查某槽位是否装备了双手武器
    private boolean isSlotOccupiedByTwoHandedWeapon(Hero hero, WeaponSlot slot) {
        KindOfWeapon weapon = getWeaponInSlot(hero, slot);
        return weapon instanceof MeleeWeapon && ((MeleeWeapon) weapon).isTwoHanded();
    }

    // 获取当前可用的所有主武器槽位（根据湿婆手环状态）
    private List<WeaponSlot> getAvailableSlots(boolean hasShiva) {
        List<WeaponSlot> slots = new ArrayList<>();
        slots.add(WeaponSlot.PRIMARY_1);
        slots.add(WeaponSlot.PRIMARY_2);
        if (hasShiva) {
            slots.add(WeaponSlot.PRIMARY_3);
            slots.add(WeaponSlot.PRIMARY_4);
        }
        return slots;
    }

    // 生成一组槽位的状态字符串（用于双手武器选择窗口）
    private String getSlotGroupStatus(Hero hero, WeaponSlot slot1, WeaponSlot slot2) {
        String name1 = getWeaponNameForSlot(hero, slot1);
        String name2 = getWeaponNameForSlot(hero, slot2);
        if (name1.length() > MAX_NAME_LENGTH) name1 = name1.substring(0, MAX_NAME_LENGTH - 3) + "...";
        if (name2.length() > MAX_NAME_LENGTH) name2 = name2.substring(0, MAX_NAME_LENGTH - 3) + "...";
        return Messages.get(KindOfWeapon.class, "group_occupied", name1, name2);
    }

    // ========================= 通用装备/卸下逻辑 =========================
    
    /**
     * 判断武器是否为盾牌类型
     * 使用 ItemTag.SHIELD_SYSTEM 标签进行可靠识别
     */
    public static boolean isShield(KindOfWeapon weapon) {
        return weapon != null && weapon.hasTag(ItemTag.SHIELD_SYSTEM);
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

    public static void setWeaponInSlot(Hero hero, WeaponSlot slot, KindOfWeapon weapon) {
        switch (slot) {
            case PRIMARY_1: hero.belongings.weapon = weapon; break;
            case PRIMARY_2: hero.belongings.weapon2 = weapon; break;
            case PRIMARY_3: hero.belongings.weapon3 = weapon; break;
            case PRIMARY_4: hero.belongings.weapon4 = weapon; break;
            case SECONDARY: hero.belongings.secondWep = weapon; break;
        }
    }

    public boolean equipToSlot(Hero hero, WeaponSlot slot) {
        isSwiftEquipping = false;
        if (hero.belongings.contains(this) && hero.hasTalent(Talent.SWIFT_EQUIP)) {
            Talent.SwiftEquipCooldown cd = hero.buff(Talent.SwiftEquipCooldown.class);
            if (cd == null || cd.hasSecondUse()) isSwiftEquipping = true;
        }

        // 圣职者以外的职业有概率识破诅咒
        if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION) && cursed && !cursedKnown) {
            int chance = 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION);
            if (Random.Int(20) < chance) {
                cursedKnown = true;
                GLog.p(Messages.get(this, "curse_detected"));
                return false;
            }
        }

        detachAll(hero.belongings.backpack);
        KindOfWeapon current = getWeaponInSlot(hero, slot);
        if (current == null || current.doUnequip(hero, true, false)) {
            setWeaponInSlot(hero, slot, this);
            activate(hero);
            Talent.onItemEquipped(hero, this);
            Badges.validateDuelistUnlock();
            updateQuickslot();
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
            
            // 触发套装检测(优化版本:传入变化的物品)
            EquipmentSet.onEquipmentChanged(hero, this);
            
            return true;
        } else {
            isSwiftEquipping = false;
            collect(hero.belongings.backpack);
            return false;
        }
    }

    private void handleSwiftEquipCooldown(Hero hero) {
        Talent.SwiftEquipCooldown cd = hero.buff(Talent.SwiftEquipCooldown.class);
        if (cd == null) {
            cd = Buff.affect(hero, Talent.SwiftEquipCooldown.class, 19f);
            cd.secondUse = hero.pointsInTalent(Talent.SWIFT_EQUIP) == 2;
        } else if (cd.hasSecondUse()) {
            cd.secondUse = false;
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
            // 触发套装检测(优化版本:传入变化的物品)
            EquipmentSet.onEquipmentChanged(hero, this);
            return true;
        } else {
            setWeaponInSlot(hero, slot, this);
            return false;
        }
    }

    private WeaponSlot findEquippedSlot(Hero hero) {
        if (this == hero.belongings.weapon)   return WeaponSlot.PRIMARY_1;
        if (this == hero.belongings.weapon2)  return WeaponSlot.PRIMARY_2;
        if (this == hero.belongings.weapon3)  return WeaponSlot.PRIMARY_3;
        if (this == hero.belongings.weapon4)  return WeaponSlot.PRIMARY_4;
        if (this == hero.belongings.secondWep) return WeaponSlot.SECONDARY;
        return null;
    }

    // 公共 API：供外部查询武器当前装备位置
    public WeaponSlot getEquippedSlot(Hero hero) {
        return findEquippedSlot(hero);
    }
    public boolean isEquippedInMainHand(Hero hero) {
        WeaponSlot slot = findEquippedSlot(hero);
        return slot != null && slot.isMainHand();
    }
    public boolean isEquippedInOffHand(Hero hero) {
        WeaponSlot slot = findEquippedSlot(hero);
        return slot != null && slot.isOffHand();
    }
    public String getHandTypeDescription(Hero hero) {
        WeaponSlot slot = findEquippedSlot(hero);
        if (slot == null) return Messages.get(KindOfWeapon.class, "not_equipped");
        if (slot.isMainHand()) return Messages.get(KindOfWeapon.class, "main_hand");
        if (slot.isOffHand())  return Messages.get(KindOfWeapon.class, "off_hand");
        return Messages.get(KindOfWeapon.class, "secondary_weapon");
    }

    // ========================= 原有抽象方法 =========================
    protected float timeToEquip(Hero hero) { return isSwiftEquipping ? 0f : TIME_TO_EQUIP; }

    @Override
    public boolean isEquipped(Hero hero) {
        return hero != null && (this == hero.belongings.weapon  ||
                this == hero.belongings.weapon2 ||
                this == hero.belongings.weapon3 ||
                this == hero.belongings.weapon4 ||
                this == hero.belongings.secondWep);
    }

    public int min() { return min(buffedLvl()); }
    public int max() { return max(buffedLvl()); }
    public abstract int min(int lvl);
    public abstract int max(int lvl);

    public int damageRoll(Char owner) {
        if (owner instanceof Hero) return Hero.heroDamageIntRange(min(), max());
        else return Random.NormalIntRange(min(), max());
    }

    public float accuracyFactor(Char owner, Char target) { return 1f; }
    public float delayFactor(Char owner) { return 1f; }
    public int reachFactor(Char owner) { return 1; }

    public boolean canReach(Char owner, int target) {
        int reach = reachFactor(owner);
        if (Dungeon.level.distance(owner.pos, target) > reach) return false;
        boolean[] passable = BArray.not(Dungeon.level.solid, null);
        for (Char ch : Actor.chars()) if (ch != owner) passable[ch.pos] = false;
        PathFinder.buildDistanceMap(target, passable, reach);
        return PathFinder.distance[owner.pos] <= reach;
    }

    public int defenseFactor(Char owner) { return 0; }
    public int proc(Char attacker, Char defender, int damage) { return damage; }
    public void hitSound(float volume, float pitch) { Sample.INSTANCE.play(hitSound, volume, pitch * hitSoundPitch); }
}