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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.EnumSet;

/**
 * 物品标签枚举 - 用于标记物品的特性
 * 
 * 使用示例:
 * - item.hasTag(ItemTag.SWORD_SYSTEM)  // 检查是否有标签
 * - item.addTag(ItemTag.CURSED)         // 添加标签
 * - item.removeTag(ItemTag.BROKEN)      // 移除标签
 */
public enum ItemTag {
	
	// ==================== 武器类型标签 ====================
	SWORD_SYSTEM("tag.sword_system"),
	DAGGER_SYSTEM("tag.dagger_system"),
	AXE_SYSTEM("tag.axe_system"),
	BLUNT_SYSTEM("tag.blunt_system"),
	SPEAR_SYSTEM("tag.spear_system"),
	UNARMED_SYSTEM("tag.unarmed_system"),
	SHIELD_SYSTEM("tag.shield_system"),
	BOW_SYSTEM("tag.bow_system"),
	SPECIAL_SYSTEM("tag.special_system"),

	
	// ==================== 物品大类标签 ====================
	WEAPON_SYSTEM("tag.weapon_system"),
	WANDS_SYSTEM("tag.wands_system"),
	ARMOR_SYSTEM("tag.armor_system"),
	RING_SYSTEM("tag.ring_system"),
	ARTIFACT_SYSTEM("tag.artifact_system"),
	MISSILE_WEAPON("tag.missile_weapon"),

	
	// ==================== 消耗品类型标签 ====================
	FOOD("tag.food"),
	BOMBS("tag.bombs"),
	POTIONS("tag.potions"),
	SCROLLS("tag.scrolls"),
	STONES("tag.stones"),
	CONSUMABLE("tag.consumable"),
	MISC("tag.misc"),
	TOOLS("tag.tools"),
	
	// ==================== 材质标签 ====================
	IRON("tag.iron"),
	STEEL("tag.steel"),
	CRYSTAL("tag.crystal"),
	WOODEN("tag.wooden"),
	
	// ==================== 状态标签 (高优先级) ====================
	CURSED("tag.cursed"),
	BLESSED("tag.blessed"),
	BROKEN("tag.broken"),
	ENCHANTED("tag.enchanted"),
	GLOWING("tag.glowing"),
	
	// ==================== 隐藏标签 (内部逻辑用) ====================
	FRAGILE("tag.fragile", false),
	MELEE("tag.melee", false),
	RANGED("tag.ranged", false),
	MAGIC("tag.magic", false),
	ONEHAND("tag.onehand", false),
	TWOHANDED("tag.twohanded", false);
	
	// ==================== 字段 ====================
	private final String i18nKey;    // 国际化键值
	private final boolean visible;   // 是否对玩家可见
	
	// ==================== 构造函数 ====================
	ItemTag(String i18nKey) {
		this(i18nKey, true);
	}
	
	ItemTag(String i18nKey, boolean visible) {
		this.i18nKey = i18nKey;
		this.visible = visible;
	}
	
	// ==================== 方法 ====================
	
	/**
	 * 获取显示名称(自动根据当前语言环境返回翻译)
	 */
	public String getDisplayName() {
		return Messages.get((Class<?>)null, i18nKey);
	}
	
	/**
	 * 是否对玩家可见
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * 获取所有可见标签
	 */
	public static EnumSet<ItemTag> getAllVisibleTags() {
		EnumSet<ItemTag> visible = EnumSet.noneOf(ItemTag.class);
		for (ItemTag tag : values()) {
			if (tag.isVisible()) {
				visible.add(tag);
			}
		}
		return visible;
	}
	
	/**
	 * 获取所有隐藏标签
	 */
	public static EnumSet<ItemTag> getAllHiddenTags() {
		EnumSet<ItemTag> hidden = EnumSet.noneOf(ItemTag.class);
		for (ItemTag tag : values()) {
			if (!tag.isVisible()) {
				hidden.add(tag);
			}
		}
		return hidden;
	}
}
