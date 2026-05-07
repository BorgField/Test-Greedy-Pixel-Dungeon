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
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StewedMeat extends Food {
	
	{
		image = ItemSpriteSheet.STEWED;
		energy = Hunger.HUNGRY/2f;
	}
	
	@Override
	public int value() {
		return 8 * quantity;
	}
	
	/**
	 * 处理炖肉制作时的魔法转化逻辑
	 * @param result 基础产出物品
	 * @return 转化后的物品（可能包含魔法炖肉和普通炖肉）
	 */
	protected static Item processMagicalTransformation(Item result) {
		if (result == null) return null;
		
		// 对每个单位的产出独立判断是否变为魔法炖肉
		int totalQuantity = result.quantity();
		int magicalCount = 0;
		
		for (int i = 0; i < totalQuantity; i++) {
			if (Random.Int(3) == 0) {
				magicalCount++;
			}
		}
		
		// 根据随机结果决定返回类型
		if (magicalCount > 0) {
			// 有魔法炖肉，返回魔法炖肉（数量为魔法炖肉的数量）
			MagicalStewedMeat magicalStew = new MagicalStewedMeat();
			magicalStew.quantity(magicalCount);
			
			// 如果还有普通炖肉，需要单独添加到背包
			int normalCount = totalQuantity - magicalCount;
			if (normalCount > 0) {
				StewedMeat normalStew = new StewedMeat();
				normalStew.quantity(normalCount);
				normalStew.collect();
			}
			
			return magicalStew;
		} else {
			// 没有魔法炖肉，返回普通炖肉
			return result;
		}
	}
	
	public static class oneMeat extends Recipe.SimpleRecipe {
		{
			inputs =  new Class[]{MysteryMeat.class};
			inQuantity = new int[]{1};
			
			cost = 1;
			
			output = StewedMeat.class;
			outQuantity = 1;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			// 先调用父类方法处理数量扣除和基础产出
			Item result = super.brew(ingredients);
			return processMagicalTransformation(result);
		}
	}
	
	public static class twoMeat extends Recipe.SimpleRecipe {
		{
			inputs =  new Class[]{MysteryMeat.class};
			inQuantity = new int[]{2};
			
			cost = 2;
			
			output = StewedMeat.class;
			outQuantity = 2;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			// 先调用父类方法处理数量扣除和基础产出
			Item result = super.brew(ingredients);
			return processMagicalTransformation(result);
		}
	}
	
	//red meat
	//blue meat
	
	public static class threeMeat extends Recipe.SimpleRecipe {
		{
			inputs =  new Class[]{MysteryMeat.class};
			inQuantity = new int[]{3};
			
			cost = 2;
			
			output = StewedMeat.class;
			outQuantity = 3;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			// 先调用父类方法处理数量扣除和基础产出
			Item result = super.brew(ingredients);
			return processMagicalTransformation(result);
		}
	}

}
