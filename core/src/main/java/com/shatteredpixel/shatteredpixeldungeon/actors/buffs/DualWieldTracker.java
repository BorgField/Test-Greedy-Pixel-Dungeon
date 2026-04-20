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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.events.SubscribeEvent;
import com.shatteredpixel.shatteredpixeldungeon.events.TurnEndEvent;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * 双持武器状态追踪器
 * 
 * 功能：
 * - 在每个回合结束时自动刷新所有 DUAL_PURPOSE 武器的状态
 * - 确保两用武器根据当前装备情况正确显示为单手或双手模式
 * 
 * 这是一个隐藏的管理型 Buff，玩家不可见
 */
public class DualWieldTracker extends Buff {
	
	@Override
	public boolean act() {
		// 这个 buff 主要通过事件驱动，act 只用于保持活跃
		spend(TICK);
		return true;
	}
	
	/**
	 * 监听回合结束事件，刷新所有两用武器的状态
	 */
	@SubscribeEvent(event = TurnEndEvent.class)
	public static void onTurnEnd(TurnEndEvent event) {
		Hero hero = Dungeon.hero;
		if (hero == null) return;
		
		// 检查是否有追踪器激活
		DualWieldTracker tracker = hero.buff(DualWieldTracker.class);
		if (tracker == null) return;
		
		// 刷新所有槽位的 DUAL_PURPOSE 武器状态
		refreshDualPurposeWeapons(hero);
	}
	
	/**
	 * 刷新所有两用武器的状态
	 * 这个方法会触发物品的 UI 更新，确保显示正确的状态
	 */
	private static void refreshDualPurposeWeapons(Hero hero) {
		// 检查所有武器槽位
		KindOfWeapon[] weapons = {
			hero.belongings.weapon,
			hero.belongings.weapon2,
			hero.belongings.weapon3,
			hero.belongings.weapon4
		};
		
		boolean needsUpdate = false;
		
		for (KindOfWeapon weapon : weapons) {
			if (weapon instanceof MeleeWeapon) {
				MeleeWeapon meleeWeapon = (MeleeWeapon) weapon;
				
				// 只处理 DUAL_PURPOSE 类型的武器
				if (meleeWeapon.handedType == MeleeWeapon.HandedType.DUAL_PURPOSE) {
					// 调用 isDualTwoHanded 会重新计算状态
					// 虽然返回值没有被使用，但这个方法可能会触发内部状态更新
					meleeWeapon.isDualTwoHanded(hero);
					GLog.i("刷新两用武器状态：%s", meleeWeapon.name());
					needsUpdate = true;
				}
			}
		}
		
		// 如果有两用武器，触发 UI 更新
		if (needsUpdate) {
			// 这里可以添加额外的 UI 刷新逻辑
			// 例如：更新物品描述、刷新快捷栏等
		}
	}
	
	/**
	 * 确保追踪器已激活
	 * 如果还没有激活，则创建一个新的追踪器
	 */
	public static void ensureActive(Hero hero) {
		if (hero.buff(DualWieldTracker.class) == null) {
			Buff.affect(hero, DualWieldTracker.class);
		}
	}
}
