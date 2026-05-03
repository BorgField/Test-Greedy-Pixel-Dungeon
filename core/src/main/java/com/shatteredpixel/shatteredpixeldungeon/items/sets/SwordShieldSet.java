/*
 * Greedy Pixel Dungeon
 * Copyright (C) 2024
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.sets;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.HashSet;
import java.util.Set;

/**
 * 剑盾套装示例
 * 装备短剑(Shortsword)和圆盾(RoundShield)时触发
 * 效果:获得20回合极速buff
 * 
 * 注意:武器需要被装备,而不是在背包里
 */
public class SwordShieldSet extends EquipmentSet {
    
    private static final float HASTE_DURATION = 20f;
    
    public SwordShieldSet() {
        super("sword_shield_set");
    }
    
    @Override
    public boolean checkSetCondition(Hero hero) {
        // 检查是否同时装备了短剑和圆盾
        // 武器类物品需要被装备才算
        boolean hasShortsword = false;
        boolean hasRoundShield = false;
        
        // 检查所有主武器槽位是否有短剑（包括湿婆手环解锁的槽位）
        if (hero.belongings.weapon instanceof Shortsword ||
            hero.belongings.weapon2 instanceof Shortsword ||
            hero.belongings.weapon3 instanceof Shortsword ||
            hero.belongings.weapon4 instanceof Shortsword) {
            hasShortsword = true;
        }
        
        // 圆盾应该在副手槽位（weapon2 或 weapon4）
        if (hero.belongings.weapon2 instanceof RoundShield ||
            hero.belongings.weapon4 instanceof RoundShield) {
            hasRoundShield = true;
        }
        
        return hasShortsword && hasRoundShield;
    }
    
    @Override
    public void applySetEffect(Hero hero) {
        // 应用20回合极速buff
        Buff.prolong(hero, Haste.class, HASTE_DURATION);
        GLog.p(Messages.get(this, "activated", setName()));
    }
    
    @Override
    public void removeSetEffect(Hero hero) {
        // Haste buff会自然过期,无需特殊处理
        // 如果需要立即移除,可以调用: Buff.detach(hero, Haste.class);
    }
    
    @Override
    public Set<Class<? extends Item>> getRelatedItemTypes() {
        // 声明此套装监控的物品类型
        Set<Class<? extends Item>> types = new HashSet<>();
        types.add(Shortsword.class);
        types.add(RoundShield.class);
        return types;
    }
}
