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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class MagicalStewedMeat extends StewedMeat {

	private static List<Class<? extends Potion>> normalPotions;
	private static List<Class<? extends ExoticPotion>> exoticPotions;
	
	static {
		// 初始化普通药水列表（排除力量药水）
		normalPotions = new ArrayList<>();
		for (Class<?> potionClass : Generator.Category.POTION.classes) {
			if (potionClass != PotionOfStrength.class) {
				normalPotions.add((Class<? extends Potion>) potionClass);
			}
		}
		
		// 初始化异域药水列表
		exoticPotions = new ArrayList<>(ExoticPotion.regToExo.values());
	}

	{
		image = ItemSpriteSheet.STEWED;
		energy = Hunger.HUNGRY/2f;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_EAT)) {
			// 根据概率分布获得不同类型的药水效果
			// 85% 普通药水（排除力量），13% 异域药水，2% 力量药水
			int roll = Random.Int(100);
			Potion randomPotion;
			
			if (roll < 85) {
				// 85% 概率：随机普通药水（排除力量药水）
				Class<? extends Potion> selectedClass = Random.element(normalPotions);
				randomPotion = com.watabou.utils.Reflection.newInstance(selectedClass);
			} else if (roll < 98) {
				// 13% 概率：随机异域药水
				Class<? extends ExoticPotion> exoticClass = Random.element(exoticPotions);
				randomPotion = com.watabou.utils.Reflection.newInstance(exoticClass);
			} else {
				// 2% 概率：力量药水
				randomPotion = new PotionOfStrength();
			}
			
			randomPotion.apply(hero);
			Catalog.countUse(randomPotion.getClass());
		}
	}
}
