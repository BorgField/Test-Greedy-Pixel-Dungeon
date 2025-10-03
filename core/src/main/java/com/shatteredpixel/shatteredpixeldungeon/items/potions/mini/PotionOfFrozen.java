package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class  PotionOfFrozen extends MiniPotion {
    //霜冻试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_FROZEN;
    }

    @Override
    public void shatter(int cell) {
        splash(cell);
        Char ch = Actor.findChar(cell);
        if (ch != null) {
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );

            if (Dungeon.level.water[cell] && !ch.isImmune(Frost.class)) {
                Buff.prolong(ch, Frost.class, 5f);
            } else {
                Buff.prolong(ch, Chill.class, 10f);
            }
        }
    }
}
