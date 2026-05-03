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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.*;

/**
 * 套装系统基类
 * 用于定义和管理装备套装
 */
public abstract class EquipmentSet {
    
    // 套装ID
    private final String setId;
    
    public EquipmentSet(String setId) {
        this.setId = setId;
    }
    
    /**
     * 检查英雄是否满足套装条件
     * 子类根据需要检查装备状态或背包状态
     */
    public abstract boolean checkSetCondition(Hero hero);
    
    /**
     * 应用套装效果
     */
    public abstract void applySetEffect(Hero hero);
    
    /**
     * 移除套装效果
     */
    public abstract void removeSetEffect(Hero hero);
    
    /**
     * 获取套装名称
     */
    public String setName() {
        return Messages.get(this, "name");
    }
    
    /**
     * 获取套装描述
     */
    public String setDescription() {
        return Messages.get(this, "desc");
    }
    
    /**
     * 获取套装ID
     */
    public String getSetId() {
        return setId;
    }
    
    /**
     * 获取此套装监控的物品类型列表
     * 用于优化性能,避免遍历所有套装
     */
    public abstract Set<Class<? extends Item>> getRelatedItemTypes();
    
    /**
     * 当装备变化时调用
     * 只检查与变化物品相关的套装
     */
    public static void onEquipmentChanged(Hero hero, Item changedItem) {
        Set<EquipmentSet> relatedSets = itemToSetsMap.get(changedItem.getClass());
        
        if (relatedSets != null && !relatedSets.isEmpty()) {
            for (EquipmentSet set : relatedSets) {
                boolean conditionMet = set.checkSetCondition(hero);
                boolean wasActive = set.activeHeroes.contains(hero);
                
                // 只有状态改变时才应用/移除效果
                if (conditionMet && !wasActive) {
                    set.applySetEffect(hero);
                    set.activeHeroes.add(hero);
                } else if (!conditionMet && wasActive) {
                    set.removeSetEffect(hero);
                    set.activeHeroes.remove(hero);
                }
            }
        }
    }
    
    /**
     * 当背包内容变化时调用(拾取、丢弃物品等)
     * 只检查与变化物品相关的套装
     * 
     * 性能优化：
     * - 使用 HashMap 快速定位相关套装 O(1)
     * - 只检查与该物品类型相关的套装，而非全部套装
     * - 平均情况下，一个物品只属于 1-3 个套装
     */
    public static void onBackpackChanged(Hero hero, Item changedItem) {
        // 背包变化也可能影响套装，所以同样检查相关套装
        Set<EquipmentSet> relatedSets = itemToSetsMap.get(changedItem.getClass());
        
        // 如果没有相关套装，直接返回（大多数物品的情况）
        if (relatedSets == null || relatedSets.isEmpty()) {
            return;
        }
        
        // 只检查与该物品相关的少量套装（通常 1-3 个）
        for (EquipmentSet set : relatedSets) {
            boolean conditionMet = set.checkSetCondition(hero);
            boolean wasActive = set.activeHeroes.contains(hero);
            
            // 只有状态改变时才应用/移除效果
            if (conditionMet && !wasActive) {
                set.applySetEffect(hero);
                set.activeHeroes.add(hero);
            } else if (!conditionMet && wasActive) {
                set.removeSetEffect(hero);
                set.activeHeroes.remove(hero);
            }
        }
    }
    
    // 注册的所有套装
    private static final Set<EquipmentSet> registeredSets = new HashSet<>();
    
    // 物品到套装的映射表,用于快速查找相关套装
    private static final Map<Class<? extends Item>, Set<EquipmentSet>> itemToSetsMap = new HashMap<>();
    
    // 跟踪每个套装在哪些英雄身上已激活，防止重复应用
    private final Set<Hero> activeHeroes = new HashSet<>();
    
    /**
     * 注册套装
     */
    public static void registerSet(EquipmentSet set) {
        registeredSets.add(set);
        rebuildItemToSetsMap();
    }
    
    /**
     * 重建物品到套装的映射表
     */
    private static void rebuildItemToSetsMap() {
        itemToSetsMap.clear();
        
        for (EquipmentSet set : registeredSets) {
            Set<Class<? extends Item>> relatedTypes = set.getRelatedItemTypes();
            for (Class<? extends Item> itemType : relatedTypes) {
                // 兼容 API 19 的写法，替代 computeIfAbsent
                Set<EquipmentSet> sets = itemToSetsMap.get(itemType);
                if (sets == null) {
                    sets = new HashSet<>();
                    itemToSetsMap.put(itemType, sets);
                }
                sets.add(set);
            }
        }
    }
    
    /**
     * 初始化所有套装
     */
    public static void initializeSets() {
        // 装备类套装:需要物品被装备
//        registerSet(new SwordShieldSet());
        
        // 背包类套装:只需要物品在背包里
//        registerSet(new VitalitySet());
        
        // 混合类套装:部分需要装备,部分只需要在背包里
        // registerSet(new MixedSet());
    }
}
