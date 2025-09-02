package com.shatteredpixel.shatteredpixeldungeon.items.potions.mini;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

public class PotionOfBurning extends MiniPotion {
    //炎蚀试剂
    {
        icon = ItemSpriteSheet.Icons.POTION_BURNING;

    }

    @Override
    public void shatter( int cell ) {

        splash( cell );
        Char ch = Actor.findChar( cell );

        if (ch != null) {
            if (Char.hasProp(ch, Char.Property.BOSS) || Char.hasProp(ch, Char.Property.MINIBOSS)) {
                Buff.prolong(ch, StoneOfAggression.Aggression.class, StoneOfAggression.Aggression.DURATION / 4f);
            } else {
                Buff.affect(ch, Burning.class).setCount(1);
            }
            ch.sprite.emitter().burst( FlameParticle.FACTORY, 5 );
        }
        Sample.INSTANCE.play( Assets.Sounds.SHATTER );
        Sample.INSTANCE.play( Assets.Sounds.BURNING );
    }



    public static class Burning extends Buff {
        {
            type = buffType.NEGATIVE;
            announced = true;
        }

        @Override
        public int icon() { return BuffIndicator.MINIPOTION; }

        @Override
        public void tintIcon(Image icon) { icon.hardlight(0.80f, 0, 0.13f); }

        @Override
        public String desc() {
            return Messages.get(this, "desc", lvl, count);
        }

        public int lvl = 0;
        public int count = 0;

        public void setCount(int str){
            lvl = Math.min(5, lvl + str);
            count = Math.max(0, lvl + 2);
        }

        public int getLvl(){
            return lvl;
        }
        public int getCount(){
            return count;
        }

        public void lossCount(int ct){
            count = Math.max(0, count - ct);
        }

        private static final String BURNING_LVL	= "burning_lvl";
        private static final String BURNING_COUNT	= "burning_count";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( BURNING_LVL, lvl );
            bundle.put( BURNING_COUNT, count );
        }
        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle(bundle);
            lvl = bundle.getInt( BURNING_LVL );
            count = bundle.getInt( BURNING_COUNT );
        }

        @Override
        public void detach() {
            //don't trigger when killed by being knocked into a pit
            if (!target.isAlive() && !target.flying || !Dungeon.level.pit[target.pos]) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    if (!Dungeon.level.solid[target.pos + i] && !Dungeon.level.water[target.pos + i]) {
                        GameScene.add(Blob.seed(target.pos + i, 2, Fire.class));
                    }
                }
            }
            super.detach();
        }
    }
}
