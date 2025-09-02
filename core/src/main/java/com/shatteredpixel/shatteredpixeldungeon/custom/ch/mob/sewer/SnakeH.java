package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.sewer;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob.MobHard;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SnakeSprite;
import com.watabou.utils.Random;

public class SnakeH extends MobHard {
    {
        HT = HP = 4;

        defenseSkill = 9999;
    }

    {
        spriteClass = SnakeSprite.class;

        EXP = 2;
        maxLvl = 7;

        loot = Generator.Category.SEED;
        lootChance = 0.3f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 10;
    }

}
