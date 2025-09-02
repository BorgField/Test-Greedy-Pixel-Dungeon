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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public abstract class Scroll extends Item {
	
	public static final String AC_READ	= "READ";
	
	protected static final float TIME_TO_READ	= 1f;

	public boolean copy = false;
	public boolean isCopy() { return copy; }
	public void setCopy(boolean copy) { this.copy = copy; }

	private static final LinkedHashMap<String, Integer> runes = new LinkedHashMap<String, Integer>() {
		{
			put("KAUNAN",ItemSpriteSheet.SCROLL_KAUNAN);
			put("SOWILO",ItemSpriteSheet.SCROLL_SOWILO);
			put("LAGUZ",ItemSpriteSheet.SCROLL_LAGUZ);
			put("YNGVI",ItemSpriteSheet.SCROLL_YNGVI);
			put("GYFU",ItemSpriteSheet.SCROLL_GYFU);
			put("RAIDO",ItemSpriteSheet.SCROLL_RAIDO);
			put("ISAZ",ItemSpriteSheet.SCROLL_ISAZ);
			put("MANNAZ",ItemSpriteSheet.SCROLL_MANNAZ);
			put("NAUDIZ",ItemSpriteSheet.SCROLL_NAUDIZ);
			put("BERKANAN",ItemSpriteSheet.SCROLL_BERKANAN);
			put("ODAL",ItemSpriteSheet.SCROLL_ODAL);
			put("TIWAZ",ItemSpriteSheet.SCROLL_TIWAZ);
		}
	};
	
	protected static ItemStatusHandler<Scroll> handler;
	
	protected String rune;

	//affects how strongly on-scroll talents trigger from this scroll
	protected float talentFactor = 1;
	//the chance (0-1) of whether on-scroll talents trigger from this potion
	protected float talentChance = 1;
	
	{
		stackable = true;
		defaultAction = AC_READ;
	}
	
	@SuppressWarnings("unchecked")
	public static void initLabels() {
		handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes );
	}

	public static void clearLabels(){
		handler = null;
	}
	
	public static void save( Bundle bundle ) {
		handler.save( bundle );
	}

	public static void saveSelectively( Bundle bundle, ArrayList<Item> items ) {
		ArrayList<Class<?extends Item>> classes = new ArrayList<>();
		for (Item i : items){
			if (i instanceof ExoticScroll){
				if (!classes.contains(ExoticScroll.exoToReg.get(i.getClass()))){
					classes.add(ExoticScroll.exoToReg.get(i.getClass()));
				}
			} else if (i instanceof Scroll){
				if (!classes.contains(i.getClass())){
					classes.add(i.getClass());
				}
			}
		}
		handler.saveClassesSelectively( bundle, classes );
	}

	@SuppressWarnings("unchecked")
	public static void restore( Bundle bundle ) {
		handler = new ItemStatusHandler<>( (Class<? extends Scroll>[])Generator.Category.SCROLL.classes, runes, bundle );
	}
	
	public Scroll() {
		super();
		reset();
	}
	
	//anonymous scrolls are always IDed, do not affect ID status,
	//and their sprite is replaced by a placeholder if they are not known,
	//useful for items that appear in UIs, or which are only spawned for their effects
	protected boolean anonymous = false;
	public void anonymize(){
		if (!isKnown()) image = ItemSpriteSheet.SCROLL_HOLDER;
		anonymous = true;
	}
	
	
	@Override
	public void reset(){
		super.reset();
		if (handler != null && handler.contains(this)) {
			image = handler.image(this);
			rune = handler.label(this);
		} else {
			image = ItemSpriteSheet.SCROLL_KAUNAN;
			rune = "KAUNAN";
		}
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_READ );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_READ )) {
			
			if (hero.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
			} else if (hero.buff( Blindness.class ) != null) {
				GLog.w( Messages.get(this, "blinded") );
			} else if (hero.buff(UnstableSpellbook.bookRecharge.class) != null
					&& hero.buff(UnstableSpellbook.bookRecharge.class).isCursed()
					&& !(this instanceof ScrollOfRemoveCurse || this instanceof ScrollOfAntiMagic)){
				GLog.n( Messages.get(this, "cursed") );
			} else {
				doRead();
			}
			
		}
	}
	
	public abstract void doRead();

	public void readAnimation() {
		Invisibility.dispel();
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();

		if (!anonymous) {
			Catalog.countUse(getClass());
			if (Random.Float() < talentChance) {
				Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
			}
		}

	}
	
	public boolean isKnown() {
		return anonymous || (handler != null && handler.isKnown( this ));
	}
	
	public void setKnown() {
		if (!anonymous) {
			if (!isKnown()) {
				handler.know(this);
				updateQuickslot();
			}
			
			if (Dungeon.hero.isAlive()) {
				Catalog.setSeen(getClass());
				Statistics.itemTypesDiscovered.add(getClass());
			}
		}
	}
	
	@Override
	public Item identify( boolean byHero ) {
		super.identify(byHero);

		if (!isKnown()) {
			setKnown();
		}
		return this;
	}

	@Override
	public String name() {
		return (isCopy() ? Messages.get(this, "copy_name") : "")
				+ (isKnown() ? super.name() : Messages.get(this, rune));
	}


	@Override
	public String info() {
		//skip custom notes if anonymized and un-Ided
		return (anonymous && (handler == null || !handler.isKnown( this ))) ? desc() : super.info();
	}

	@Override
	public String desc() {
		return (isKnown() ? super.desc() : Messages.get(this, "unknown_desc"))
				+(isCopy() ? "\n\n" + Messages.get(this, "copy_desc") : "");
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return isKnown();
	}
	
	public static HashSet<Class<? extends Scroll>> getKnown() {
		return handler.known();
	}
	
	public static HashSet<Class<? extends Scroll>> getUnknown() {
		return handler.unknown();
	}
	
	public static boolean allKnown() {
		return handler != null && handler.known().size() == Generator.Category.SCROLL.classes.length;
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 6 * quantity;
	}

	@Override
	public boolean isSimilar(Item item) {
		// 同类、同等级、同copy状态才可堆叠
		return super.isSimilar(item)
				&& item instanceof Scroll
				&& ((Scroll)item).copy == this.copy;
	}

	public static class PlaceHolder extends Scroll {

		{
			image = ItemSpriteSheet.SCROLL_HOLDER;
		}

		@Override
		public boolean isSimilar(Item item) {
			return ExoticScroll.regToExo.containsKey(item.getClass())
					|| ExoticScroll.regToExo.containsValue(item.getClass());
		}

		@Override
		public void doRead() {}

		@Override
		public String info() {
			return "";
		}
	}

	public static class ScrollToStone extends Recipe {

		private static HashMap<Class<?extends Scroll>, Class<?extends Runestone>> stones = new HashMap<>();
		static {
			stones.put(ScrollOfIdentify.class,      StoneOfIntuition.class);
			stones.put(ScrollOfLullaby.class,       StoneOfDeepSleep.class);
			stones.put(ScrollOfMagicMapping.class,  StoneOfClairvoyance.class);
			stones.put(ScrollOfMirrorImage.class,   StoneOfFlock.class);
			stones.put(ScrollOfRetribution.class,   StoneOfBlast.class);
			stones.put(ScrollOfRage.class,          StoneOfAggression.class);
			stones.put(ScrollOfRecharging.class,    StoneOfShock.class);
			stones.put(ScrollOfRemoveCurse.class,   StoneOfDetectMagic.class);
			stones.put(ScrollOfTeleportation.class, StoneOfBlink.class);
			stones.put(ScrollOfTerror.class,        StoneOfFear.class);
			stones.put(ScrollOfTransmutation.class, StoneOfAugmentation.class);
			stones.put(ScrollOfUpgrade.class,       StoneOfEnchantment.class);
		}

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 1
					|| !(ingredients.get(0) instanceof Scroll)
					|| !stones.containsKey(ingredients.get(0).getClass())){
				return false;
			}

			return true;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		private static int Count = 0;

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			Scroll s = (Scroll) ingredients.get(0);

			s.quantity(s.quantity() - 1);
			if (ShatteredPixelDungeon.scene() instanceof AlchemyScene){
				if (!s.isIdentified()){
					((AlchemyScene) ShatteredPixelDungeon.scene()).showIdentify(s);
				}
			} else {
				s.identify();
			}

			if (Random.Int(12)<=0){
				ScrollOfBlank blank =new ScrollOfBlank();
				blank.identify().collect();
				GLog.i(Messages.get(this, "blank_get"));
				Count=0;
			} else {
				Count++;
				if (Count%24==0){
					ScrollOfBlank blank =new ScrollOfBlank();
					blank.identify().collect();
					GLog.i(Messages.get(this, "blank_get"));
				}
			}

			int c = s.isCopy() ? 1 : 2;
			return Reflection.newInstance(stones.get(s.getClass())).quantity(c);
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			Scroll scroll = (Scroll) ingredients.get(0);

			int c = scroll.isCopy() ? 1 : 2;
			if (!scroll.isKnown()){
				return new Runestone.PlaceHolder().quantity(c);
			} else {
				return Reflection.newInstance(stones.get(scroll.getClass())).quantity(c);
			}
		}
	}

	public static Scroll sample = null;

	public static class ScrollToCopy extends Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 2) return false;
			boolean hasScroll = false;
			boolean hasBlank = false;

			for (Item item : ingredients) {
				if (item instanceof ScrollOfBlank) {
					hasBlank = true;
				} else if (item instanceof Scroll
						&& !((Scroll) item).isCopy()
						&& !(item instanceof ExoticScroll)) {
					hasScroll = true;
				}
			}
			return hasScroll && hasBlank;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 5;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			// 1. 提取原材料
			Scroll original = null;
			ScrollOfBlank blank = null;
			for (Item item : ingredients) {
				if (item instanceof ScrollOfBlank) {
					blank = (ScrollOfBlank) item;
				} else if (item instanceof Scroll) {
					original = (Scroll) item;
				}
			}
			if (original == null || blank == null) return null;

			// 2. 减少数量（确保数量 > 0）
			original.quantity(original.quantity() - 1);
			blank.quantity(blank.quantity() - 1);

			// 3. 生成副本
			Scroll copy = (Scroll) original;
			copy.quantity(2);
			copy.setCopy(true);

			return copy;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (ingredients == null || ingredients.isEmpty()) return null;

			// 1. 提取原始卷轴类型
			Scroll original = null;
			for (Item item : ingredients) {
				if (item instanceof Scroll && !(item instanceof ScrollOfBlank)) {
					original = (Scroll) item;
					break;
				}
			}
			if (original == null) return null;

			// 2. 创建预览实例（不修改原物品）
			Scroll sample;
			try {
				sample = (Scroll) Reflection.newInstance(original.getClass());
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException(e);
				return null;
			}
			sample.quantity(2);
			sample.setCopy(true);
			return sample;
		}

	}

	private static final String COPY = "copyScroll";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COPY, copy); // 保存标记状态
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		copy = bundle.getBoolean(COPY); // 读取标记状态
	}
}
