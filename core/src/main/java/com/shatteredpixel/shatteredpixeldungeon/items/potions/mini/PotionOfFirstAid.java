package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfFirstAid extends MiniPotion {
    //急救试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_FIRSTAID;

        bones = true;
    }

    @Override
    public void apply( Hero hero ) {
        identify();
        cure( hero );
        heal( hero );
    }

    public static void heal( Char ch ){
        if (ch == Dungeon.hero && Dungeon.isChallenged(Challenges.NO_HEALING)){
            pharmacophobiaProc(Dungeon.hero);
        } else {
            int healAmount = 20;
            if (ch.HP <= 0.3f * ch.HT) {
                // if character's HP is less than 30%, heal an additional 20 hp
                healAmount += 20;
            }
            Healing healing = Buff.affect(ch, Healing.class);
            healing.setHeal(healAmount, 0.5f, 0);
            healing.applyVialEffect();
            if (ch == Dungeon.hero){
                GLog.p( Messages.get(PotionOfHealing.class, "heal") );
            }
        }
    }

    public static void pharmacophobiaProc( Hero hero ){
        // harms the hero for ~40% of their max HP in poison
        Buff.affect( hero, Poison.class).set(2 + hero.lvl/4);
    }

    public static void cure( Char ch ) {
        Buff.detach( ch, Poison.class );
        Buff.detach( ch, Bleeding.class );
        Buff.reduce( ch, Weakness.class, 0.5f);
    }

    @Override
    public int value() {
        return isKnown() ? 30 * quantity : super.value();
    }
}
