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

package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.actors.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CombatStateTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Fury;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Speed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.AuraOfProtection;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BeamingRay;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ShieldOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.modifier.CombatModifier;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Bulk;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ShivaBangle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.mini.PotionOfBurning;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.mini.PotionOfBurst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.mini.PotionOfDispelling;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.mini.PotionOfSwift;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.FerretTuft;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GnollRockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class Char extends Actor {
	
	public int pos = 0;
	
	public CharSprite sprite;
	
	public int HT;
	public int HP;
	
	protected float baseSpeed	= 1;
	protected PathFinder.Path path;

	public int paralysed	    = 0;
	public boolean rooted		= false;
	public boolean flying		= false;
	public int invisible		= 0;

	//these are relative to the hero
	public enum Alignment{
		ENEMY,
		NEUTRAL,
		ALLY
	}
	public Alignment alignment;
	
	public int viewDistance	= 8;
	
	public boolean[] fieldOfView = null;
	
	private LinkedHashSet<Buff> buffs = new LinkedHashSet<>();
	
	@Override
	protected boolean act() {
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		//throw any items that are on top of an immovable char
		if (properties().contains(Property.IMMOVABLE)){
			throwItems();
		}
		return false;
	}

	protected void throwItems(){
		Heap heap = Dungeon.level.heaps.get( pos );
		if (heap != null && heap.type == Heap.Type.HEAP
				&& !(heap.peek() instanceof Tengu.BombAbility.BombItem)
				&& !(heap.peek() instanceof Tengu.ShockerAbility.ShockerItem)) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8){
				if (Dungeon.level.passable[pos+n]){
					candidates.add(pos+n);
				}
			}
			if (!candidates.isEmpty()){
				Dungeon.level.drop( heap.pickUp(), Random.element(candidates) ).sprite.drop( pos );
			}
		}
	}

	public String name(){
		return Messages.get(this, "name");
	}

	public boolean canInteract(Char c){
		if (Dungeon.level.adjacent( pos, c.pos )){
			return true;
		} else if (c instanceof Hero
				&& alignment == Alignment.ALLY
				&& !hasProp(this, Property.IMMOVABLE)
				&& Dungeon.level.distance(pos, c.pos) <= 2*Dungeon.hero.pointsInTalent(Talent.ALLY_WARP)){
			return true;
		} else {
			return false;
		}
	}
	
	//swaps places by default
	public boolean interact(Char c){

		//don't allow char to swap onto hazard unless they're flying
		//you can swap onto a hazard though, as you're not the one instigating the swap
		if (!Dungeon.level.passable[pos] && !c.flying){
			return true;
		}

		//can't swap into a space without room
		if (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[c.pos]
			|| c.properties().contains(Property.LARGE) && !Dungeon.level.openSpace[pos]){
			return true;
		}

		//we do a little raw position shuffling here so that the characters are never
		// on the same cell when logic such as occupyCell() is triggered
		int oldPos = pos;
		int newPos = c.pos;

		//can't swap or ally warp if either char is immovable
		if (hasProp(this, Property.IMMOVABLE) || hasProp(c, Property.IMMOVABLE)){
			return true;
		}

		//warp instantly with allies in this case
		if (c == Dungeon.hero && Dungeon.hero.hasTalent(Talent.ALLY_WARP)){
			PathFinder.buildDistanceMap(c.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
			if (PathFinder.distance[pos] == Integer.MAX_VALUE){
				return true;
			}
			pos = newPos;
			c.pos = oldPos;
			ScrollOfTeleportation.appear(this, newPos);
			ScrollOfTeleportation.appear(c, oldPos);
			Dungeon.observe();
			GameScene.updateFog();
			return true;
		}

		//can't swap places if one char has restricted movement
		if (paralysed > 0 || c.paralysed > 0 || rooted || c.rooted
				|| buff(Vertigo.class) != null || c.buff(Vertigo.class) != null){
			return true;
		}

		c.pos = oldPos;
		moveSprite( oldPos, newPos );
		move( newPos );

		c.pos = newPos;
		c.sprite.move( newPos, oldPos );
		c.move( oldPos );
		
		c.spend( 1 / c.speed() );

		if (c == Dungeon.hero){
			if (Dungeon.hero.subClass == HeroSubClass.FREERUNNER){
				Buff.affect(Dungeon.hero, Momentum.class).gainStack();
			}
			Dungeon.hero.justMoved = true;

			Dungeon.hero.busy();
		}
		
		return true;
	}
	
	protected boolean moveSprite( int from, int to ) {
		
		if (sprite.isVisible() && sprite.parent != null && (Dungeon.level.heroFOV[from] || Dungeon.level.heroFOV[to])) {
			sprite.move( from, to );
			return true;
		} else {
			sprite.turnTo(from, to);
			sprite.place( to );
			return true;
		}
	}

	public void hitSound( float pitch ){
		Sample.INSTANCE.play(Assets.Sounds.HIT, 1, pitch);
	}

	public boolean blockSound( float pitch ) {
		return false;
	}
	
	protected static final String POS       = "pos";
	protected static final String TAG_HP    = "HP";
	protected static final String TAG_HT    = "HT";
	protected static final String TAG_SHLD  = "SHLD";
	protected static final String BUFFS	    = "buffs";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );
		
		bundle.put( POS, pos );
		bundle.put( TAG_HP, HP );
		bundle.put( TAG_HT, HT );
		bundle.put( BUFFS, buffs );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );
		
		pos = bundle.getInt( POS );
		HP = bundle.getInt( TAG_HP );
		HT = bundle.getInt( TAG_HT );
		
		for (Bundlable b : bundle.getCollection( BUFFS )) {
			if (b != null) {
				((Buff)b).attachTo( this );
			}
		}
	}

	final public boolean attack( Char enemy ){
		return attack(enemy, 1f, 0f, 1f);
	}

	public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {

		if (enemy == null) return false;

		// 创建伤害计算上下文
		DamageContext ctx = new DamageContext(this, enemy, dmgMulti, dmgBonus, accMulti);

		// 标记战斗状态
		markCombat(this);
		if (enemy instanceof Hero) {
			markCombat(enemy);
		}

		// ========== 阶段1: 无敌判定 ==========
		if (handleInvulnerability(ctx)) {
			return false;
		}

		// ========== 阶段2: 命中判定 ==========
		calculateHit(ctx);
		if (!ctx.isHit) {
			showMissFeedback(ctx);
			return false;
		}

		// ========== 阶段3-6: 伤害计算流水线 ==========
		calculateBaseDamage(ctx);           // 基础伤害 + 防御骰
		applyAttackerModifiers(ctx);        // 攻击者增伤因子
		applyDefenderModifiers(ctx);        // 防御者减伤因子
		processDefenseAndArmor(ctx);        // 防御Proc + 护甲减免

		if (ctx.defenseProcRejected) {
			return true; // 防御Proc拒绝了攻击
		}

		// ========== 阶段7: 后效处理 ==========
		applyPostAttackEffects(ctx);

		return true;
	}

	public static int INFINITE_ACCURACY = 1_000_000;
	public static int INFINITE_EVASION = 1_000_000;

	final public static boolean hit( Char attacker, Char defender, boolean magic ) {
		return hit(attacker, defender, magic ? 2f : 1f, magic);
	}

	public static boolean hit( Char attacker, Char defender, float accMulti, boolean magic ) {

		PotionOfDispelling.DispellingMini dispellingBuff = defender.buff(PotionOfDispelling.DispellingMini.class);
		if (dispellingBuff != null && magic) {
			dispellingBuff.setAttacker(attacker);
			dispellingBuff.setDefender(defender);
		}

		float acuStat = attacker.attackSkill( defender );
		float defStat = defender.defenseSkill( attacker );

		if (defender instanceof Hero && ((Hero) defender).damageInterrupt){
			((Hero) defender).interrupt();
		}

		//invisible chars always hit (for the hero this is surprise attacking)
		if (attacker.invisible > 0 && attacker.canSurpriseAttack()){
			acuStat = INFINITE_ACCURACY;
		}

		if (defender.buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class) != null){
			defStat = INFINITE_EVASION;
		}

		//if accuracy or evasion are large enough, treat them as infinite.
		//note that infinite evasion beats infinite accuracy
		if (defStat >= INFINITE_EVASION){
			return false;
		} else if (acuStat >= INFINITE_ACCURACY){
			return true;
		}

		float acuRoll = Random.Float( acuStat );
		if (attacker.buff(Bless.class) != null) acuRoll *= 1.25f;
		if (attacker.buff(  Hex.class) != null) acuRoll *= 0.8f;
		if (attacker.buff( Daze.class) != null) acuRoll *= 0.5f;
		for (ChampionEnemy buff : attacker.buffs(ChampionEnemy.class)){
			acuRoll *= buff.evasionAndAccuracyFactor();
		}
		acuRoll *= AscensionChallenge.statModifier(attacker);
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.BLESS)
				&& attacker.alignment == Alignment.ALLY){
			// + 3%/5%
			acuRoll *= 1.01f + 0.02f*Dungeon.hero.pointsInTalent(Talent.BLESS);
		}
		acuRoll *= accMulti;

		float defRoll = Random.Float( defStat );
		if (defender.buff(Bless.class) != null) defRoll *= 1.25f;
		if (defender.buff(  Hex.class) != null) defRoll *= 0.8f;
		if (defender.buff( Daze.class) != null) defRoll *= 0.5f;
		if (defender.buff(PotionOfSwift.SwiftMini.class) != null) defRoll *= 1.25f;

		for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)){
			defRoll *= buff.evasionAndAccuracyFactor();
		}
		defRoll *= AscensionChallenge.statModifier(defender);
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.BLESS)
				&& defender.alignment == Alignment.ALLY){
			// + 3%/5%
			defRoll *= 1.01f + 0.02f*Dungeon.hero.pointsInTalent(Talent.BLESS);
		}

		if (defRoll < acuRoll && (defRoll*FerretTuft.evasionMultiplier()) >= acuRoll){
			tuftDodged = true;
		}
		defRoll *= FerretTuft.evasionMultiplier();

		return acuRoll >= defRoll;
	}

	//TODO this is messy and hacky atm, should consider standardizing this so we can have many 'dodge reasons'
	private static boolean tuftDodged = false;

	public int attackSkill( Char target ) {
		return 0;
	}
	
	public int defenseSkill( Char enemy ) {
		return 0;
	}
	
	public String defenseVerb() {
		return Messages.get(this, "def_verb");
	}
	
	public int drRoll() {
		int dr = 0;

		dr += Random.NormalIntRange( 0 , Barkskin.currentLevel(this) );

		return dr;
	}
	
	public int damageRoll() {
		return 1;
	}
	
	//TODO it would be nice to have a pre-armor and post-armor proc.
	// atm attack is always post-armor and defence is already pre-armor
	
	public int attackProc( Char enemy, int damage ) {
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			buff.onAttackProc( enemy );
		}

		return damage;
	}
	
	public int defenseProc( Char enemy, int damage ) {

		Earthroot.Armor armor = buff( Earthroot.Armor.class );
		if (armor != null) {
			damage = armor.absorb( damage );
		}

		PotionOfBurning.BurningMini potion = buff( PotionOfBurning.BurningMini.class );
		if (potion != null) {
			int a =potion.getLvl();
			int b = potion.getCount();
			if(a > 0 && b > 0) {
				Burning burn = new Burning();
				this.damage((int) (this.HT * 0.10f * a), burn);}
				potion.lossCount(1);
			if(!this.isActive()){

			}
		}

		ShieldOfLight.ShieldOfLightTracker shield = buff( ShieldOfLight.ShieldOfLightTracker.class);
		if (shield != null && shield.object == enemy.id()){
			int min = 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT);
			damage -= Random.NormalIntRange(min, 2*min);
			damage = Math.max(damage, 0);
		} else if (this == Dungeon.hero
				&& Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(Talent.SHIELD_OF_LIGHT)
				&& TargetHealthIndicator.instance.target() == enemy){
			//33/50%
			if (Random.Int(6) < 1+Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT)){
				damage -= 1;
			}
		}

		// hero and pris images skip this as they already benefit from hero's armor glyph proc
		if (!(this instanceof Hero || this instanceof PrismaticImage)) {
			if (Dungeon.hero.alignment == alignment && Dungeon.hero.belongings.armor() != null
					&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
					&& (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2 || buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
				damage = Dungeon.hero.belongings.armor().proc( enemy, this, damage );
			}
		}

		return damage;
	}

	//Returns the level a glyph is at for a char, or -1 if they are not benefitting from that glyph
	//This function is needed as (unlike enchantments) many glyphs trigger in a variety of cases
	public int glyphLevel(Class<? extends Armor.Glyph> cls){
		if (Dungeon.hero != null && Dungeon.level != null
				&& this != Dungeon.hero && Dungeon.hero.alignment == alignment
				&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
				&& (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2 || buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
			return Dungeon.hero.glyphLevel(cls);
		} else {
			return -1;
		}
	}
	
	public float speed() {
		float speed = baseSpeed;
		if ( buff( Cripple.class ) != null ) speed /= 2f;
		if ( buff( Stamina.class ) != null) speed *= 1.5f;
		if ( buff( Adrenaline.class ) != null) speed *= 2f;
		if ( buff( Haste.class ) != null) speed *= 3f;
		if ( buff( Dread.class ) != null) speed *= 2f;

		speed *= Swiftness.speedBoost(this, glyphLevel(Swiftness.class));
		speed *= Flow.speedBoost(this, glyphLevel(Flow.class));
		speed *= Bulk.speedBoost(this, glyphLevel(Bulk.class));

		return speed;
	}

	//currently only used by invisible chars, or by the hero
	public boolean canSurpriseAttack(){
		return true;
	}
	
	//used so that buffs(Shieldbuff.class) isn't called every time unnecessarily
	private int cachedShield = 0;
	public boolean needsShieldUpdate = true;
	
	public int shielding(){
		if (!needsShieldUpdate){
			return cachedShield;
		}
		
		cachedShield = 0;
		for (ShieldBuff s : buffs(ShieldBuff.class)){
			cachedShield += s.shielding();
		}
		needsShieldUpdate = false;
		return cachedShield;
	}
	
	@Deprecated
	public void damage( int dmg, Object src ) {
		damage(dmg, src, inferDamageType(src));
	}

	public void damage( int dmg, Object src, DamageType type ) {
		if (!isAlive() || dmg < 0) return;

		// 阶段 0: 特殊中断处理 (收割流血、驱散、无敌)
		if (buff(Sickle.HarvestBleedTracker.class) != null) { handleHarvestBleed(dmg, src); return; }
		if (handleDispellingMagic(src, type, dmg)) return;
		if (isInvulnerable(src.getClass())) { sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable")); return; }

		// 阶段 1: 伤害分摊 (生命链接)
		int rawDmg = distributeLifeLinkDamage(dmg, src, type);

		// 阶段 2: 全局减伤与状态清理 (光环、末日、死亡标记等)
		float damage = applyDamageReductions(rawDmg, src, type);

		// 阶段 3: 伤害抵抗层 (免疫、抗性、冠军减免)
		damage = applyResistancesAndChamps(damage, type);

		// 阶段 4: 二次减免层 (魔法减免、麻痹触发)
		int roundedDmg = applySpecialDefense(Math.round(damage), type);

		// 阶段 5: 战斗修饰器 (模组扩展点)
		roundedDmg = CombatModifier.INSTANCE.damage(this, roundedDmg, src);

		// 阶段 6: 最终结算 (护盾、附魔特效、死亡判定)
		applyFinalDamageAndDeath(roundedDmg, src, type);
	}

	private DamageType inferDamageType(Object src) {
		// 临时过渡逻辑
		if (src instanceof Hunger) return DamageType.HUNGER;
		if (src instanceof Burning) return DamageType.BURNING;
		if (src instanceof Frost || src instanceof Chill) return DamageType.FROST;
		if (src instanceof Electricity) return DamageType.SHOCKING;
		if (src instanceof Bleeding) return DamageType.BLEEDING;
		if (src instanceof ToxicGas) return DamageType.TOXIC;
		if (src instanceof Corrosion) return DamageType.CORROSION;
		if (src instanceof Poison) return DamageType.POISON;
		if (src instanceof Ooze) return DamageType.OOZE;
		if (src instanceof Viscosity.DeferedDamage) return DamageType.DEFERRED;
		if (src instanceof Corruption) return DamageType.CORRUPTION;
		if (src instanceof AscensionChallenge) return DamageType.AMULET;
		if (src instanceof Pickaxe) return DamageType.PICK;
		if (AntiMagic.RESISTS.contains(src.getClass())) return DamageType.MAGICAL;
		if (NO_ARMOR_PHYSICAL_SOURCES.contains(src.getClass())) return DamageType.PHYSICAL_NO_BLOCK;
		return DamageType.PHYSICAL;
	}

	//these are misc. sources of physical damage which do not apply armor, they get a different icon
	private static HashSet<Class> NO_ARMOR_PHYSICAL_SOURCES = new HashSet<>();
	{
		NO_ARMOR_PHYSICAL_SOURCES.add(CrystalSpire.SpireSpike.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(GnollGeomancer.Boulder.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(GnollGeomancer.GnollRockFall.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(GnollRockfallTrap.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(DwarfKing.KingDamager.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(DwarfKing.Summoning.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(LifeLink.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(Chasm.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(WandOfBlastWave.Knockback.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(Heap.class); //damage from wraiths attempting to spawn from heaps
		NO_ARMOR_PHYSICAL_SOURCES.add(Necromancer.SummoningBlockDamage.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(DriedRose.GhostHero.NoRoseDamage.class);
	}
	
	public void destroy() {
		HP = 0;
		Actor.remove( this );

		for (Char ch : Actor.chars().toArray(new Char[0])){
			if (ch.buff(Charm.class) != null && ch.buff(Charm.class).object == id()){
				ch.buff(Charm.class).detach();
			}
			if (ch.buff(Dread.class) != null && ch.buff(Dread.class).object == id()){
				ch.buff(Dread.class).detach();
			}
			if (ch.buff(Terror.class) != null && ch.buff(Terror.class).object == id()){
				ch.buff(Terror.class).detach();
			}
			if (ch.buff(SnipersMark.class) != null && ch.buff(SnipersMark.class).object == id()){
				ch.buff(SnipersMark.class).detach();
			}
			if (ch.buff(Talent.FollowupStrikeTracker.class) != null
					&& ch.buff(Talent.FollowupStrikeTracker.class).object == id()){
				ch.buff(Talent.FollowupStrikeTracker.class).detach();
			}
			if (ch.buff(Talent.DeadlyFollowupTracker.class) != null
					&& ch.buff(Talent.DeadlyFollowupTracker.class).object == id()){
				ch.buff(Talent.DeadlyFollowupTracker.class).detach();
			}
		}
	}
	
	public void die( Object src ) {
		destroy();
		if (src != Chasm.class) {
			sprite.die();
			if (!flying && Dungeon.level != null && sprite instanceof MobSprite && Dungeon.level.map[pos] == Terrain.CHASM){
				((MobSprite) sprite).fall();
			}
		}
	}

	//we cache this info to prevent having to call buff(...) in isAlive.
	//This is relevant because we call isAlive during drawing, which has both performance
	//and thread coordination implications
	public boolean deathMarked = false;
	private int hitMissIcon = -1; // 用于在 attack 和 damage 之间传递命中/闪避图标信息
	
	public boolean isAlive() {
		return HP > 0 || deathMarked;
	}

	public boolean isActive() {
		return isAlive();
	}

	@Override
	protected void spendConstant(float time) {
		TimekeepersHourglass.timeFreeze freeze = buff(TimekeepersHourglass.timeFreeze.class);
		if (freeze != null) {
			freeze.processTime(time);
			return;
		}

		Swiftthistle.TimeBubble bubble = buff(Swiftthistle.TimeBubble.class);
		if (bubble != null){
			bubble.processTime(time);
			return;
		}

		super.spendConstant(time);
	}

	@Override
	protected void spend( float time ) {

		float timeScale = 1f;
		if (buff( Slow.class ) != null) {
			timeScale *= 0.5f;
			//slowed and chilled do not stack
		} else if (buff( Chill.class ) != null) {
			timeScale *= buff( Chill.class ).speedFactor();
		}
		if (buff( Speed.class ) != null) {
			timeScale *= 2.0f;
		}
		
		super.spend( time / timeScale );
	}
	
	public synchronized LinkedHashSet<Buff> buffs() {
		return new LinkedHashSet<>(buffs);
	}
	
	@SuppressWarnings("unchecked")
	//returns all buffs assignable from the given buff class
	public synchronized <T extends Buff> HashSet<T> buffs( Class<T> c ) {
		HashSet<T> filtered = new HashSet<>();
		for (Buff b : buffs) {
			if (c.isInstance( b )) {
				filtered.add( (T)b );
			}
		}
		return filtered;
	}

	@SuppressWarnings("unchecked")
	//returns an instance of the specific buff class, if it exists. Not just assignable
	public synchronized  <T extends Buff> T buff( Class<T> c ) {
		for (Buff b : buffs) {
			if (b.getClass() == c) {
				return (T)b;
			}
		}
		return null;
	}

	public synchronized boolean isCharmedBy( Char ch ) {
		int chID = ch.id();
		for (Buff b : buffs) {
			if (b instanceof Charm && ((Charm)b).object == chID) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean add( Buff buff ) {

		if (buff(PotionOfCleansing.Cleanse.class) != null) { //cleansing buff
			if (buff.type == Buff.buffType.NEGATIVE
					&& !(buff instanceof AllyBuff)
					&& !(buff instanceof LostInventory)){
				return false;
			}
		}

		if (sprite != null && buff(Challenge.SpectatorFreeze.class) != null){
			return false; //can't add buffs while frozen and game is loaded
		}

		buffs.add( buff );
		if (Actor.chars().contains(this)) Actor.add( buff );

		if (sprite != null && buff.announced) {
			switch (buff.type) {
				case POSITIVE:
					sprite.showStatus(CharSprite.POSITIVE, Messages.titleCase(buff.name()));
					break;
				case NEGATIVE:
					sprite.showStatus(CharSprite.WARNING, Messages.titleCase(buff.name()));
					break;
				case NEUTRAL:
				default:
					sprite.showStatus(CharSprite.NEUTRAL, Messages.titleCase(buff.name()));
					break;
			}
		}

		return true;

	}
	
	public synchronized boolean remove( Buff buff ) {
		
		buffs.remove( buff );
		Actor.remove( buff );

		return true;
	}
	
	public synchronized void remove( Class<? extends Buff> buffClass ) {
		for (Buff buff : buffs( buffClass )) {
			remove( buff );
		}
	}
	
	@Override
	protected synchronized void onRemove() {
		for (Buff buff : buffs.toArray(new Buff[buffs.size()])) {
			buff.detach();
		}
	}
	
	public synchronized void updateSpriteState() {
		for (Buff buff:buffs) {
			buff.fx( true );
		}
	}
	
	public float stealth() {
		float stealth = 0;

		stealth += Obfuscation.stealthBoost(this, glyphLevel(Obfuscation.class));

		return stealth;
	}

	public final void move( int step ) {
		move( step, true );
	}

	//travelling may be false when a character is moving instantaneously, such as via teleportation
	public void move( int step, boolean travelling ) {

		if (travelling && Dungeon.level.adjacent( step, pos ) && buff( Vertigo.class ) != null) {
			sprite.interruptMotion();
			int newPos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			if (!(Dungeon.level.passable[newPos] || Dungeon.level.avoid[newPos])
					|| (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[newPos])
					|| Actor.findChar( newPos ) != null)
				return;
			else {
				sprite.move(pos, newPos);
				step = newPos;
			}
		}

		if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
			Door.leave( pos );
		}

		pos = step;
		
		if (this != Dungeon.hero) {
			sprite.visible = Dungeon.level.heroFOV[pos];
		}
		
		Dungeon.level.occupyCell(this );
	}
	
	public int distance( Char other ) {
		return Dungeon.level.distance( pos, other.pos );
	}

	public boolean[] modifyPassable( boolean[] passable){
		//do nothing by default, but some chars can pass over terrain that others can't
		return passable;
	}
	
	public void onMotionComplete() {
		//Does nothing by default
		//The main actor thread already accounts for motion,
		// so calling next() here isn't necessary (see Actor.process)
	}
	
	public void onAttackComplete() {
		next();
	}
	
	public void onOperateComplete() {
		next();
	}
	
	protected final HashSet<Class> resistances = new HashSet<>();
	
	//returns percent effectiveness after resistances
	//TODO currently resistances reduce effectiveness by a static 50%, and do not stack.
	public float resist( Class effect ){
		HashSet<Class> resists = new HashSet<>(resistances);
		for (Property p : properties()){
			resists.addAll(p.resistances());
		}
		for (Buff b : buffs()){
			resists.addAll(b.resistances());
		}
		
		float result = 1f;
		for (Class c : resists){
			if (c.isAssignableFrom(effect)){
				result *= 0.5f;
			}
		}
		return result * RingOfElements.resist(this, effect);
	}
	
	protected final HashSet<Class> immunities = new HashSet<>();
	
	public boolean isImmune(Class effect ){
		HashSet<Class> immunes = new HashSet<>(immunities);
		for (Property p : properties()){
			immunes.addAll(p.immunities());
		}
		for (Buff b : buffs()){
			immunes.addAll(b.immunities());
		}
		if (glyphLevel(Brimstone.class) >= 0){
			immunes.add(Burning.class);
		}
		
		for (Class c : immunes){
			if (c.isAssignableFrom(effect)){
				return true;
			}
		}
		return false;
	}

	//similar to isImmune, but only factors in damage.
	//Is used in AI decision-making
	public boolean isInvulnerable( Class effect ){
		return buff(Challenge.SpectatorFreeze.class) != null || buff(Invulnerability.class) != null;
	}

	protected HashSet<Property> properties = new HashSet<>();

	public HashSet<Property> properties() {
		HashSet<Property> props = new HashSet<>(properties);
		//TODO any more of these and we should make it a property of the buff, like with resistances/immunities
		if (buff(ChampionEnemy.Giant.class) != null) {
			props.add(Property.LARGE);
		}
		return props;
	}

	public enum Property{
		BOSS ( new HashSet<Class>( Arrays.asList(Grim.class, GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class)),
				new HashSet<Class>( Arrays.asList(AllyBuff.class, Dread.class) )),
		MINIBOSS ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(AllyBuff.class, Dread.class) )),
		BOSS_MINION,
		UNDEAD,
		DEMONIC,
		INORGANIC ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Bleeding.class, ToxicGas.class, Poison.class) )),
		FIERY ( new HashSet<Class>( Arrays.asList(WandOfFireblast.class, Elemental.FireElemental.class)),
				new HashSet<Class>( Arrays.asList(Burning.class, Blazing.class))),
		ICY ( new HashSet<Class>( Arrays.asList(WandOfFrost.class, Elemental.FrostElemental.class)),
				new HashSet<Class>( Arrays.asList(Frost.class, Chill.class))),
		ACIDIC ( new HashSet<Class>( Arrays.asList(Corrosion.class)),
				new HashSet<Class>( Arrays.asList(Ooze.class))),
		ELECTRIC ( new HashSet<Class>( Arrays.asList(WandOfLightning.class, Shocking.class, Potential.class,
										Electricity.class, ShockingDart.class, Elemental.ShockElemental.class )),
				new HashSet<Class>()),
		LARGE,
		IMMOVABLE ( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(Vertigo.class) )),
		//A character that acts in an unchanging manner. immune to AI state debuffs or stuns/slows
		STATIC( new HashSet<Class>(),
				new HashSet<Class>( Arrays.asList(AllyBuff.class, Dread.class, Terror.class, Amok.class, Charm.class, Sleep.class,
									Paralysis.class, Frost.class, Chill.class, Slow.class, Speed.class) ));

		private HashSet<Class> resistances;
		private HashSet<Class> immunities;
		
		Property(){
			this(new HashSet<Class>(), new HashSet<Class>());
		}
		
		Property( HashSet<Class> resistances, HashSet<Class> immunities){
			this.resistances = resistances;
			this.immunities = immunities;
		}
		
		public HashSet<Class> resistances(){
			return new HashSet<>(resistances);
		}
		
		public HashSet<Class> immunities(){
			return new HashSet<>(immunities);
		}

	}

	public static boolean hasProp( Char ch, Property p){
		return (ch != null && ch.properties().contains(p));
	}
	
	//标记角色进入战斗状态
	private void markCombat(Char chara) {
		if (chara instanceof Hero) {
			CombatStateTracker tracker = chara.buff(CombatStateTracker.class);
			if (tracker != null) {
				tracker.enterCombat();
			}
		}
	}

	/**
	 * 攻击伤害计算的上下文数据载体
	 * 用于在各计算阶段之间传递数据，为未来事件化做准备
	 */
	public static class DamageContext {
		// === 输入参数 ===
		public final Char attacker;
		public final Char defender;
		public final float damageMultiplier;
		public final float damageBonus;
		public final float accuracyMultiplier;

		// === 阶段1: 命中判定结果 ===
		public boolean isHit;

		// === 阶段2: 基础伤害 ===
		public float baseDamage;
		public int defenseRoll;

		// === 阶段6: 防御处理后结果 ===
		public int effectiveDamage;
		public boolean defenseProcRejected; // 防御Proc是否拒绝此次攻击

		// === 辅助信息 ===
		public final boolean visibleFight;
		public Preparation preparation; // 刺客准备状态

		public DamageContext(Char attacker, Char defender,
							float dmgMulti, float dmgBonus, float accMulti) {
			this.attacker = attacker;
			this.defender = defender;
			this.damageMultiplier = dmgMulti;
			this.damageBonus = dmgBonus;
			this.accuracyMultiplier = accMulti;
			this.visibleFight = Dungeon.level.heroFOV[attacker.pos]
					|| Dungeon.level.heroFOV[defender.pos];
		}
	}

	// ==================== attack系统 ====================
	/**
	 * 阶段1: 检查目标是否无敌
	 * @return true表示无敌，攻击无效
	 */
	private boolean handleInvulnerability(DamageContext ctx) {
		if (ctx.defender.isInvulnerable(getClass())) {
			if (ctx.visibleFight) {
				ctx.defender.sprite.showStatus(CharSprite.POSITIVE,
					Messages.get(this, "invulnerable"));
				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1f,
					Random.Float(0.96f, 1.05f));
			}
			return true;
		}
		return false;
	}

	/**
	 * 阶段2: 执行命中判定，记录结果到ctx
	 */
	private void calculateHit(DamageContext ctx) {
		ctx.isHit = hit(ctx.attacker, ctx.defender, ctx.accuracyMultiplier, false);
		// tuftDodged 已在 hit 静态方法中设置
	}

	/**
	 * 阶段3: 计算基础伤害和防御骰
	 */
	private void calculateBaseDamage(DamageContext ctx) {
		// 防御骰计算
		ctx.defenseRoll = Math.round(
			ctx.defender.drRoll() * AscensionChallenge.statModifier(ctx.defender)
		);

		// 特殊职业破防逻辑
		if (ctx.attacker instanceof Hero) {
			Hero h = (Hero) ctx.attacker;
			if (h.belongings.attackingWeapon() instanceof MissileWeapon
					&& h.subClass == HeroSubClass.SNIPER
					&& !Dungeon.level.adjacent(h.pos, ctx.defender.pos)) {
				ctx.defenseRoll = 0;
			}
			if (h.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
				ctx.defenseRoll = 0;
			}
		}

		// 基础伤害骰
		ctx.preparation = ctx.attacker.buff(Preparation.class);
		if (ctx.preparation != null) {
			ctx.baseDamage = ctx.preparation.damageRoll(ctx.attacker);
			if (ctx.attacker == Dungeon.hero
					&& Dungeon.hero.hasTalent(Talent.BOUNTY_HUNTER)) {
				Buff.affect(Dungeon.hero, Talent.BountyHunterTracker.class, 0.0f);
			}
		} else {
			ctx.baseDamage = ctx.attacker.damageRoll();
		}

		// 应用传入的倍率和加成
		ctx.baseDamage = ctx.baseDamage * ctx.damageMultiplier + ctx.damageBonus;
	}

	/**
	 * 阶段4: 应用攻击者侧的所有增伤/减伤因子
	 */
	private void applyAttackerModifiers(DamageContext ctx) {
		float dmg = ctx.baseDamage;

		// 光照易伤
		if (ctx.defender.buff(GuidingLight.Illuminated.class) != null) {
			ctx.defender.buff(GuidingLight.Illuminated.class).detach();
			if (ctx.attacker == Dungeon.hero
					&& Dungeon.hero.hasTalent(Talent.SEARING_LIGHT)) {
				dmg += 1 + 2 * Dungeon.hero.pointsInTalent(Talent.SEARING_LIGHT);
			}
			if (ctx.attacker != Dungeon.hero
					&& Dungeon.hero.subClass == HeroSubClass.PRIEST) {
				ctx.defender.damage(5 + Dungeon.hero.lvl, GuidingLight.INSTANCE);
			}
		}

		// 狂战士怒意
		Berserk berserk = ctx.attacker.buff(Berserk.class);
		if (berserk != null) dmg = berserk.damageFactor(dmg);

		// 狂怒
		if (ctx.attacker.buff(Fury.class) != null) {
			dmg *= 1.5f;
		}

		// 万众之力
		if (ctx.attacker.buff(PowerOfMany.PowerBuff.class) != null) {
			if (ctx.attacker.buff(BeamingRay.BeamingRayBoost.class) != null
					&& ctx.attacker.buff(BeamingRay.BeamingRayBoost.class).object
					== ctx.defender.id()) {
				dmg *= 1.3f + 0.05f * Dungeon.hero.pointsInTalent(Talent.BEAMING_RAY);
			} else {
				dmg *= 1.25f;
			}
		}

		// 冠军敌人增伤
		for (ChampionEnemy buff : ctx.attacker.buffs(ChampionEnemy.class)) {
			dmg *= buff.meleeDamageFactor();
		}

		// 升天挑战修正
		dmg *= AscensionChallenge.statModifier(ctx.attacker);

		// 友方坚韧
		Endure.EndureTracker endure = ctx.attacker.buff(Endure.EndureTracker.class);
		if (endure != null) dmg = endure.damageFactor(dmg);

		// 虚弱减伤
		if (ctx.attacker.buff(Weakness.class) != null) {
			dmg *= 0.67f;
		}

		// 爆裂药水增伤
		if (Dungeon.hero.buff(PotionOfBurst.BurstMini.class) != null) {
			dmg *= 1.3f;
		}

		// Boss挑衅减伤
		if (ctx.defender.buff(StoneOfAggression.Aggression.class) != null
				&& ctx.defender.alignment == ctx.attacker.alignment
				&& (Char.hasProp(ctx.defender, Property.BOSS)
				|| Char.hasProp(ctx.defender, Property.MINIBOSS))) {
			dmg *= 0.5f;
			if (ctx.defender instanceof YogDzewa) {
				dmg *= 0.5f;
			}
		}

		ctx.baseDamage = dmg; // 保存阶段性结果
	}

	/**
	 * 阶段5: 应用防御者侧的所有减伤因子
	 */
	private void applyDefenderModifiers(DamageContext ctx) {
		float dmg = ctx.baseDamage;

		// 敌方坚韧
		Endure.EndureTracker endure = ctx.defender.buff(Endure.EndureTracker.class);
		if (endure != null) {
			dmg = endure.adjustDamageTaken(dmg);
		}

		// 挑战竞技场
		if (ctx.defender.buff(ScrollOfChallenge.ChallengeArena.class) != null) {
			dmg *= 0.67f;
		}

		// 守护光环
		if (Dungeon.hero.alignment == ctx.defender.alignment
				&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
				&& (Dungeon.level.distance(ctx.defender.pos, Dungeon.hero.pos) <= 2
				|| ctx.defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
			dmg *= 0.9f - 0.1f * Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
		}

		// 冥想抵抗
		if (ctx.defender.buff(MonkEnergy.MonkAbility.Meditate.MeditateResistance.class) != null) {
			dmg *= 0.2f;
		}

		ctx.baseDamage = dmg;
	}

	/**
	 * 阶段6: 防御Proc、护甲减免、脆弱等
	 */
	private void processDefenseAndArmor(DamageContext ctx) {
		// 防御Proc（可能拒绝攻击）
		int damageAfterProc = ctx.defender.defenseProc(
			ctx.attacker, Math.round(ctx.baseDamage)
		);

		if (damageAfterProc < 0) {
			ctx.defenseProcRejected = true;
			ctx.effectiveDamage = damageAfterProc;
			return;
		}

		ctx.defenseProcRejected = false;

		// 护甲减免
		int effectiveDamage = Math.max(damageAfterProc - ctx.defenseRoll, 0);

		// 粘稠护甲延迟伤害
		if (ctx.defender.buff(Viscosity.ViscosityTracker.class) != null) {
			effectiveDamage = ctx.defender.buff(Viscosity.ViscosityTracker.class)
				.deferDamage(effectiveDamage);
			ctx.defender.buff(Viscosity.ViscosityTracker.class).detach();
		}

		// 脆弱（在护甲后应用）
		if (ctx.defender.buff(Vulnerable.class) != null) {
			effectiveDamage *= 1.33f;
		}

		// 攻击者Proc（冠军敌人等）
		effectiveDamage = ctx.attacker.attackProc(ctx.defender, effectiveDamage);

		ctx.effectiveDamage = effectiveDamage;
	}

	/**
	 * 阶段7: 命中音效、特殊斩杀、视觉反馈、战斗日志
	 */
	private void applyPostAttackEffects(DamageContext ctx) {
		// 命中音效
		if (ctx.visibleFight) {
			if (ctx.effectiveDamage > 0
					|| !ctx.defender.blockSound(Random.Float(0.96f, 1.05f))) {
				ctx.attacker.hitSound(Random.Float(0.87f, 1.15f));
			}
		}

		// 防御Proc可能已致死
		if (!ctx.defender.isAlive()) return;

		// 施加基础伤害
		ctx.defender.damage(ctx.effectiveDamage, ctx.attacker);

		// 火焰/冰霜附魔
		if (ctx.attacker.buff(FireImbue.class) != null)
			ctx.attacker.buff(FireImbue.class).proc(ctx.defender);
		if (ctx.attacker.buff(FrostImbue.class) != null)
			ctx.attacker.buff(FrostImbue.class).proc(ctx.defender);

		// 刺客准备斩杀
		handleAssassinationKill(ctx);

		// 组合杀伤斩杀
		handleCombinedLethalityKill(ctx);

		// 视觉反馈
		if (ctx.defender.sprite != null) {
			ctx.defender.sprite.bloodBurstA(
				ctx.attacker.sprite.center(), ctx.effectiveDamage
			);
			ctx.defender.sprite.flash();
		}

		// 战斗日志
		logCombatResult(ctx);
	}

	/**
	 * 处理刺客准备的斩杀效果
	 */
	private void handleAssassinationKill(DamageContext ctx) {
		if (ctx.defender.isAlive()
				&& ctx.defender.alignment != ctx.attacker.alignment
				&& ctx.preparation != null
				&& ctx.preparation.canKO(ctx.defender)) {

			ctx.defender.HP = 0;
			if (ctx.defender.buff(Brute.BruteRage.class) != null) {
				ctx.defender.buff(Brute.BruteRage.class).detach();
			}
			if (!ctx.defender.isAlive()) {
				ctx.defender.die(ctx.attacker);
			} else {
				ctx.defender.damage(-1, ctx.attacker);
				DeathMark.processFearTheReaper(ctx.defender);
			}
			if (ctx.defender.sprite != null) {
				ctx.defender.sprite.showStatus(CharSprite.NEGATIVE,
					Messages.get(Preparation.class, "assassinated"));
			}
		}
	}

	/**
	 * 处理组合杀伤的斩杀效果
	 */
	private void handleCombinedLethalityKill(DamageContext ctx) {
		Talent.CombinedLethalityAbilityTracker combinedLethality =
			ctx.attacker.buff(Talent.CombinedLethalityAbilityTracker.class);

		if (combinedLethality != null
				&& ctx.attacker instanceof Hero
				&& ((Hero) ctx.attacker).belongings.attackingWeapon() instanceof MeleeWeapon
				&& combinedLethality.weapon != ((Hero) ctx.attacker).belongings.attackingWeapon()) {

			if (ctx.defender.isAlive()
					&& ctx.defender.alignment != ctx.attacker.alignment
					&& !Char.hasProp(ctx.defender, Property.BOSS)
					&& !Char.hasProp(ctx.defender, Property.MINIBOSS)
					&& (ctx.defender.HP / (float) ctx.defender.HT)
					<= 0.4f * ((Hero) ctx.attacker).pointsInTalent(
						Talent.COMBINED_LETHALITY) / 3f) {

				ctx.defender.HP = 0;
				if (ctx.defender.buff(Brute.BruteRage.class) != null) {
					ctx.defender.buff(Brute.BruteRage.class).detach();
				}
				if (!ctx.defender.isAlive()) {
					ctx.defender.die(ctx.attacker);
				} else {
					ctx.defender.damage(-1, ctx.attacker);
					DeathMark.processFearTheReaper(ctx.defender);
				}
				if (ctx.defender.sprite != null) {
					ctx.defender.sprite.showStatus(CharSprite.NEGATIVE,
						Messages.get(Talent.CombinedLethalityAbilityTracker.class, "executed"));
				}
			}
			combinedLethality.detach();
		}
	}

	/**
	 * 记录战斗结果到日志
	 */
	private void logCombatResult(DamageContext ctx) {
		if (!ctx.defender.isAlive() && ctx.visibleFight) {
			if (ctx.defender == Dungeon.hero) {
				if (ctx.attacker == Dungeon.hero) return;

				if (ctx.attacker instanceof WandOfLivingEarth.EarthGuardian
						|| ctx.attacker instanceof MirrorImage
						|| ctx.attacker instanceof PrismaticImage) {
					Badges.validateDeathFromFriendlyMagic();
				}
				Dungeon.fail(ctx.attacker);
				GLog.n(Messages.capitalize(
					Messages.get(Char.class, "kill", ctx.attacker.name())));
			} else if (ctx.attacker == Dungeon.hero) {
				GLog.i(Messages.capitalize(
					Messages.get(Char.class, "defeat", ctx.defender.name())));
			}
		}
	}

	/**
	 * 显示未命中的视觉和听觉反馈
	 */
	private void showMissFeedback(DamageContext ctx) {
		if (ctx.defender.sprite != null) {
			// 检查是否是雪貂绒球闪避
			if (tuftDodged) {
				if (Messages.lang() == Languages.ENGLISH && Random.Int(10) == 0) {
					ctx.defender.sprite.showStatusWithIcon(
						CharSprite.NEUTRAL, "dooked", FloatingText.TUFT);
				} else {
					ctx.defender.sprite.showStatusWithIcon(
						CharSprite.NEUTRAL, ctx.defender.defenseVerb(), FloatingText.TUFT);
				}
			} else {
				ctx.defender.sprite.showStatus(
					CharSprite.NEUTRAL, ctx.defender.defenseVerb());
			}
		}
		tuftDodged = false; // 重要：重置状态

		if (ctx.visibleFight) {
			Sample.INSTANCE.play(Assets.Sounds.MISS);
		}

		// 湿婆手镯闪避回调
		if (ctx.defender.buff(ShivaBangle.MultiArmBlows.class) != null) {
			ctx.defender.buff(ShivaBangle.MultiArmBlows.class).onEvade();
		}
	}

	// ==================== Damage 系统重构 ====================

	private void handleHarvestBleed(int dmg, Object src) {
		buff(Sickle.HarvestBleedTracker.class).detach();
		if (!isImmune(Bleeding.class)) {
			Bleeding b = buff(Bleeding.class);
			if (b == null) b = new Bleeding();
			b.announced = false;
			b.set(dmg, Sickle.HarvestBleedTracker.class);
			b.attachTo(this);
			sprite.showStatus(CharSprite.WARNING, Messages.titleCase(b.name()) + " " + (int)b.level());
		}
	}

	private boolean handleDispellingMagic(Object src, DamageType type, int dmg) {
		PotionOfDispelling.DispellingMini buff = buff(PotionOfDispelling.DispellingMini.class);
		return buff != null && buff.handleMagicDamage(src, dmg);
	}

	private int distributeLifeLinkDamage(int dmg, Object src, DamageType type) {
		if (src instanceof LifeLink || src instanceof Hunger || buff(LifeLink.class) == null) return dmg;
		HashSet<LifeLink> links = buffs(LifeLink.class);
		for (LifeLink link : links.toArray(new LifeLink[0])) {
			if (Actor.findById(link.object) == null) { links.remove(link); link.detach(); }
		}
		int perTarget = (int) Math.ceil(dmg / (float) (links.size() + 1));
		for (LifeLink link : links) {
			Char ch = (Char) Actor.findById(link.object);
			if (ch != null) {
				ch.damage(perTarget, link, type);
				if (!ch.isAlive()) {
					link.detach();
					if (ch == Dungeon.hero) {
						Badges.validateDeathFromFriendlyMagic();
						Dungeon.fail(src);
						GLog.n(Messages.get(LifeLink.class, "ondeath"));
					}
				}
			}
		}
		return perTarget;
	}

	private float applyDamageReductions(float damage, Object src, DamageType type) {
		// 全局百分比减伤
		if (!(src instanceof Char) && Dungeon.hero.alignment == alignment
				&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
				&& (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2 || buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
			damage *= 0.9f - 0.1f * Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
		}
		if (buff(PowerOfMany.PowerBuff.class) != null) {
			damage *= (buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) 
				? 0.70f - 0.05f * Dungeon.hero.pointsInTalent(Talent.LIFE_LINK) : 0.75f;
		}

		// 状态清理与修正
		if (buff(Terror.class) != null) buff(Terror.class).recover();
		if (buff(Dread.class) != null) buff(Dread.class).recover();
		if (buff(Charm.class) != null) buff(Charm.class).recover(src);
		if (buff(Frost.class) != null) Buff.detach(this, Frost.class);
		if (buff(MagicalSleep.class) != null) Buff.detach(this, MagicalSleep.class);

		// 增伤因子
		if (buff(Doom.class) != null && !isImmune(Doom.class)) damage *= 1.67f;
		if (alignment != Alignment.ALLY && buff(DeathMark.DeathMarkTracker.class) != null) damage *= 1.25f;
		return damage;
	}

	private float applyResistancesAndChamps(float damage, DamageType type) {
		Class<?> effectClass = getClassForDamageType(type);
		damage = isImmune(effectClass) ? 0 : damage * resist(effectClass);
		// 冠军敌人减免
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)) {
			damage = (int) Math.ceil(damage * buff.damageTakenFactor());
		}
		return damage;
	}

	private Class<?> getClassForDamageType(DamageType type) {
		switch (type) {
			case BURNING: return Burning.class;
			case FROST: return Frost.class;
			case SHOCKING: return Electricity.class;
			case BLEEDING: return Bleeding.class;
			case TOXIC: return ToxicGas.class;
			case CORROSION: return Corrosion.class;
			case POISON: return Poison.class;
			case OOZE: return Ooze.class;
			case DEFERRED: return Viscosity.DeferedDamage.class;
			case CORRUPTION: return Corruption.class;
			case AMULET: return AscensionChallenge.class;
			default: return Object.class;
		}
	}

	private int applySpecialDefense(int dmg, DamageType type) {
		// 魔法伤害特殊减免
		if (type == DamageType.MAGICAL || type == DamageType.MAGICAL_DISPELLING) {
			dmg -= AntiMagic.drRoll(this, glyphLevel(AntiMagic.class));
			if (buff(ArcaneArmor.class) != null) dmg -= Random.NormalIntRange(0, buff(ArcaneArmor.class).level());
			if (dmg < 0) dmg = 0;
		}
		// 麻痹处理
		if (buff(Paralysis.class) != null) buff(Paralysis.class).processDamage(dmg);
		return dmg;
	}

	private void applyFinalDamageAndDeath(int dmg, Object src, DamageType type) {
		// 护盾激活逻辑
		BrokenSeal.WarriorShield shield = buff(BrokenSeal.WarriorShield.class);
		if (!(src instanceof Hunger) && dmg > 0 && (HP <= HT / 2 || HP + shielding() - dmg <= HT / 2)
				&& shield != null && !shield.coolingDown()) {
			sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shield.maxShield()), FloatingText.SHIELDING);
			shield.activate();
		}

		int shielded = dmg;
		if (!(src instanceof Hunger)) {
			for (ShieldBuff s : buffs(ShieldBuff.class)) {
				dmg = s.absorbDamage(dmg);
				if (dmg == 0) break;
			}
		}
		shielded -= dmg;
		HP -= dmg;

		// Grim 附魔特效
		if (HP > 0 && buff(Grim.GrimTracker.class) != null) {
			float finalChance = buff(Grim.GrimTracker.class).maxChance * (float) Math.pow(((HT - HP) / (float) HT), 2);
			if (Random.Float() < finalChance) {
				int extraDmg = Math.round(HP * resist(Grim.class));
				dmg += extraDmg; HP -= extraDmg;
				sprite.emitter().burst(ShadowParticle.UP, 5);
				if (!isAlive() && buff(Grim.GrimTracker.class).qualifiesForBadge) Badges.validateGrimWeapon();
			}
		}

		// Kinetic 储存
		if (HP < 0 && src instanceof Char && alignment == Alignment.ENEMY) {
			Kinetic.KineticTracker tracker = ((Char) src).buff(Kinetic.KineticTracker.class);
			if (tracker != null) {
				int dmgToAdd = -HP - tracker.conservedDamage;
				dmgToAdd = Math.round(dmgToAdd * Weapon.Enchantment.genericProcChanceMultiplier((Char) src));
				if (dmgToAdd > 0) Buff.affect((Char) src, Kinetic.ConservedDamage.class).setBonus(dmgToAdd);
				tracker.detach();
			}
		}

		showDamageIcon(dmg + shielded, type);
		if (HP < 0) HP = 0;
		if (!isAlive()) die(src);
		else if (HP == 0 && buff(DeathMark.DeathMarkTracker.class) != null) DeathMark.processFearTheReaper(this);
	}

	private void showDamageIcon(int totalDmg, DamageType type) {
		if (sprite == null) return;
		int icon;
		switch (type) {
			case PHYSICAL:
				icon = FloatingText.PHYS_DMG;
				break;
			case PHYSICAL_NO_BLOCK:
				icon = FloatingText.PHYS_DMG_NO_BLOCK;
				break;
			case MAGICAL:
			case MAGICAL_DISPELLING:
				icon = FloatingText.MAGIC_DMG;
				break;
			case PICK:
				icon = FloatingText.PICK_DMG;
				break;
			case HUNGER:
				icon = FloatingText.HUNGER;
				break;
			case BURNING:
				icon = FloatingText.BURNING;
				break;
			case FROST:
				icon = FloatingText.FROST;
				break;
			case WATER:
				icon = FloatingText.WATER;
				break;
			case SHOCKING:
				icon = FloatingText.SHOCKING;
				break;
			case BLEEDING:
				icon = FloatingText.BLEEDING;
				break;
			case TOXIC:
				icon = FloatingText.TOXIC;
				break;
			case CORROSION:
				icon = FloatingText.CORROSION;
				break;
			case POISON:
				icon = FloatingText.POISON;
				break;
			case OOZE:
				icon = FloatingText.OOZE;
				break;
			case DEFERRED:
				icon = FloatingText.DEFERRED;
				break;
			case CORRUPTION:
				icon = FloatingText.CORRUPTION;
				break;
			case AMULET:
				icon = FloatingText.AMULET;
				break;
			default:
				icon = FloatingText.PHYS_DMG;
				break;
		}
		if ((icon == FloatingText.PHYS_DMG || icon == FloatingText.PHYS_DMG_NO_BLOCK) && hitMissIcon != -1) {
			if (icon == FloatingText.PHYS_DMG_NO_BLOCK) hitMissIcon += 18;
			icon = hitMissIcon;
			hitMissIcon = -1;
		}
		sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(totalDmg), icon);
	}

	// ==================== Damage 系统重构 ====================
}
