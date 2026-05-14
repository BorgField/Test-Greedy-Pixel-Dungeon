/*
 * Greedy Pixel Dungeon
 * Copyright (C) 2024
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.offhand;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/**
 * 副手武器基类
 * 所有副手装备都应该继承此类
 * handedType 自动设为 OFF_HAND，只能装备到 PRIMARY_2 或 PRIMARY_4 槽位
 */
public abstract class  OffHandWeapon extends MeleeWeapon {
	
	{
		handedType = HandedType.OFF_HAND;
	}
	
	/**
	 * 是否为纯辅助型副手装备（不造成伤害）
	 * 子类可以重写此方法
	 */
	public boolean isUtilityOnly() {
		return false;
	}
	
	/**
	 * 是否参与双持战斗计算
	 * 纯辅助装备不参与双持伤害/延迟计算
	 */
	public boolean participatesInCombat() {
		return !isUtilityOnly();
	}
	
	@Override
	public int damageRoll(Char owner) {
		if (isUtilityOnly()) {
			return 0;  // 辅助装备不造成伤害
		}
		return super.damageRoll(owner);
	}
	
	@Override
	public String info() {
		String info = desc();
		
		if (levelKnown && !isUtilityOnly()) {
			info += "\n\n" + Messages.get(this, "stats", min(), max());
		}
		
		if (isUtilityOnly()) {
			info += "\n\n" + Messages.get(this, "utility_desc");
		} else {
			info += "\n\n" + Messages.get(this, "offhand_weapon_desc");
		}
		
		if (isEquipped(Dungeon.hero)) {
			info += "\n\n" + Messages.get(this, "equipped_desc");
		}
		
		return info;
	}
}
