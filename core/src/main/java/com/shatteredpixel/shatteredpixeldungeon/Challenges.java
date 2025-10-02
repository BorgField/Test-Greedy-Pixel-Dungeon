/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;

public class Challenges {

	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int CHAMPION_ENEMIES   = 1;      // 索引0
	public static final int STRONGER_BOSSES    = 1<<1;   // 索引1
	public static final int NO_FOOD            = 1<<2;   // 索引2
	public static final int NO_ARMOR           = 1<<3;   // 索引3
	public static final int NO_HEALING         = 1<<4;   // 索引4
	public static final int NO_HERBALISM       = 1<<5;   // 索引5
	public static final int SWARM_INTELLIGENCE = 1<<6;   // 索引6
	public static final int DARKNESS           = 1<<7;   // 索引7
	public static final int NO_SCROLLS         = 1<<8;   // 索引8

	public static final int TEST_MODE          = 1<<10;   // 索引10
	public static final int MINI_POTIONS   	   = 1<<11;

	public static final int MAX_VALUE          = (1<<19)-1;

	public static final int CHALLENGE_COUNT    = 1<<9; // 对应索引0-9

	// 确保NAME_IDS和MASKS严格按索引顺序排列
	public static final String[] NAME_IDS = {
			"champion_enemies",        // 0
			"stronger_bosses",         // 1
			"no_food",                 // 2
			"no_armor",                // 3
			"no_healing",              // 4
			"no_herbalism",            // 5
			"swarm_intelligence",      // 6
			"darkness",                // 7
			"no_scrolls",              // 8

			"test_mode",               // 10
			"mini_potions"             // 11
	};

	public static final long[] MASKS = {
			CHAMPION_ENEMIES,
			STRONGER_BOSSES,
			NO_FOOD,
			NO_ARMOR,
			NO_HEALING,
			NO_HERBALISM,
			SWARM_INTELLIGENCE,
			DARKNESS,
			NO_SCROLLS,

			TEST_MODE,
			MINI_POTIONS
	};

	public static boolean isItemBlocked( Item item ){

		if (Dungeon.isChallenged(NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

		return false;

	}

	public static int activeChallenges(){
		int chCount = 0;
		for (long ch : Challenges.MASKS){
			if ((Dungeon.challenges & ch) != 0 && ch <= CHALLENGE_COUNT) chCount++;
		}
		return chCount;
	}

}