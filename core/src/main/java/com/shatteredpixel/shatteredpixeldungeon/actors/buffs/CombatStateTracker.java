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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.events.EnterCombatEvent;
import com.shatteredpixel.shatteredpixeldungeon.events.ExitCombatEvent;
import com.shatteredpixel.shatteredpixeldungeon.events.EventManager;
import com.shatteredpixel.shatteredpixeldungeon.events.SubscribeEvent;
import com.shatteredpixel.shatteredpixeldungeon.events.TurnEndEvent;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class CombatStateTracker extends Buff {
	
	{
		type = buffType.NEUTRAL;
		announced = false;
	}

	// 脱离战斗所需的回合数
	private static final int TURNS_TO_EXIT_COMBAT = 6;

	// 上次战斗的回合数
	int lastCombatTurn = 0;

	// 是否在战斗中
	boolean inCombat = false;

	// 缓存当前回合数,避免重复计算
	private static int cachedCurrentTurn = -1;

	//设置当前回合数(由事件监听器调用)
	public static void setCurrentTurn(int turn) {
		cachedCurrentTurn = turn;
	}

	@Override
	public boolean act() {
		// 这个buff主要通过事件驱动,act只用于保持活跃
		spend(TICK);
		return true;
	}

	//在攻击或被攻击时调用

	public void enterCombat() {
		// 无论是否已经在战斗中,都更新最后战斗回合
		// 这样可以确保遇到新敌人时重置脱离倒计时
		boolean wasInCombat = inCombat;
		inCombat = true;
		lastCombatTurn = getCurrentTurn();

		if (!wasInCombat) {
			// 发布进入战斗事件
			EventManager.emit(new EnterCombatEvent((Hero) target));
			onEnterCombat();
		}
	}

	//监听回合结束事件,检查战斗状态

	@SubscribeEvent(event = TurnEndEvent.class)
	public static void onTurnEnd(TurnEndEvent event) {
		if (Dungeon.hero == null) return;
		CombatStateTracker tracker = Dungeon.hero.buff(CombatStateTracker.class);
		if (tracker == null) return;

		int currentTurn = event.getTotalTurn();
		// 更新缓存的当前回合数
		setCurrentTurn(currentTurn);

		boolean hasNearbyEnemy = tracker.isCurrentlyInCombat();

		if (tracker.inCombat) {
			if (hasNearbyEnemy) {
				// 仍有可见敌人,更新最后战斗回合(重置脱离倒计时)
				tracker.lastCombatTurn = currentTurn;
			} else {
				// 没有可见敌人,检查是否已经过了足够多的回合
				int turnsSinceCombat = currentTurn - tracker.lastCombatTurn;
				if (turnsSinceCombat >= TURNS_TO_EXIT_COMBAT) {
					tracker.exitCombat();
				}
			}
		}
	}

	//检查当前是否真的在战斗中 判断标准:周围8格内是否有可见的存活敌对生物
	private boolean isCurrentlyInCombat() {
		Hero hero = (Hero) target;

		// 检查周围8格内是否有可见的敌人
		for (Mob mob : Dungeon.level.mobs) {
			if (mob.alignment == Char.Alignment.ENEMY &&
				mob.isAlive() &&
				Dungeon.level.distance(hero.pos, mob.pos) <= 8 &&
				Dungeon.level.heroFOV[mob.pos]) { // 必须在英雄的视野范围内
				return true;
			}
		}

		return false;
	}

	//脱离战斗时触发
	private void exitCombat() {
		inCombat = false;
		int turnsWaited = getCurrentTurn() - lastCombatTurn;
		// 发布脱离战斗事件
		EventManager.emit(new ExitCombatEvent((Hero) target, turnsWaited));
		onExitCombat();
	}

	//进入战斗时的回调
	protected void onEnterCombat() {
		// 默认无效果,子类可以重写 例如:播放音效、显示提示等
	}

	//脱离战斗时的回调

	protected void onExitCombat() {
		// 默认无效果,子类可以重写
		// 例如:恢复生命、增加buff、显示提示等
	}

	//获取当前回合数
	private int getCurrentTurn() {
		// 优先使用缓存的回合数(由事件监听器设置)
		if (cachedCurrentTurn >= 0) {
			return cachedCurrentTurn;
		}
		// 否则使用估算值(可能不准确)
		return (int)(Statistics.duration + Dungeon.depth);
	}

	//公开方法:检查是否在战斗中
	public boolean isInCombat() {
		return inCombat;
	}

	//公开方法:获取脱离战斗剩余回合数
	public int getTurnsUntilOutOfCombat() {
		if (!inCombat) return 0;
		int currentTurn = getCurrentTurn();
		int remaining = TURNS_TO_EXIT_COMBAT - (currentTurn - lastCombatTurn);
		return Math.max(0, remaining);
	}

	//调试方法:打印当前战斗状态信息
	public void debugPrintStatus() {
		GLog.i("=== 战斗状态追踪器 ===");
		GLog.i("是否在战斗中: " + inCombat);
		GLog.i("上次战斗回合: " + lastCombatTurn);
		GLog.i("当前回合: " + getCurrentTurn());
		if (inCombat) {
			GLog.i("脱离战斗剩余: " + getTurnsUntilOutOfCombat() + " 回合");
			boolean hasNearbyEnemy = isCurrentlyInCombat();
			GLog.i("周围8格有敌人: " + hasNearbyEnemy);
		} else {
			GLog.i("状态: 非战斗状态");
		}
		GLog.i("=====================");
	}
	
	private static final String IN_COMBAT = "in_combat";
	private static final String LAST_COMBAT_TURN = "last_combat_turn";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(IN_COMBAT, inCombat);
		bundle.put(LAST_COMBAT_TURN, lastCombatTurn);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		inCombat = bundle.getBoolean(IN_COMBAT);
		lastCombatTurn = bundle.getInt(LAST_COMBAT_TURN);
	}

	@SubscribeEvent(event = EnterCombatEvent.class)
	public static void onEnterCombat(EnterCombatEvent event) {
		Hero hero = event.getHero();
		GLog.w("你进入了战斗!");
	}

	@SubscribeEvent(event = ExitCombatEvent.class)
	public static void onExitCombat(ExitCombatEvent event) {
		Hero hero = event.getHero();
		GLog.p("你离开了战斗.");
	}
}
