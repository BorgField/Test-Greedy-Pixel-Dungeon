package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class HeroLongSword extends MeleeWeapon {
    public int lvl = 0;
    public int exp = 0;
    public int expMax = 10;
    private int augments = 0;

    {
        image = ItemSpriteSheet.LONGSWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        tier = 1;
        ACC = 1.0f;
    }

//    @Override//提升武器价值
//    public int value() {
//        int gold;
//        switch (Dungeon.depth) {
//            default:
//            case 6:
//                gold = 50 + ((lvl / 100) * lvl < 100 ? 0 : 100);
//                return gold;
//            case 11:
//                gold = 110 + ((lvl / 100) * lvl < 100 ? 0 : 100);
//                return gold;
//            case 16:
//                gold = 142 + ((lvl / 100) * lvl < 100 ? 0 : 100);
//                return gold;
//            case 20:
//                gold = 234 + ((lvl / 100) * lvl < 100 ? 0 : 100);
//                return gold;
//        }
//    }

    public void updateAugment() {
        augments++;
        while (augments >= tier) {
            augments -= tier; // 扣除所需次数
            tier++;          // 提升 tier
            GLog.p(Messages.get(this, "tierUp", tier));
//            curUser.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
            Enchanting.show( curUser, this );
            ScrollOfUpgrade.upgrade(curUser); // 触发升级效果
        }
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc",exp,expMax,augments) ;
    }


    @Override
    public boolean isUpgradable() {
        return false;
    }

    public void gainExp(float levelPortion) {
        if (cursed || levelPortion == 0) return;

        exp += Math.round(levelPortion * 1);

        int levelCap = 100; // 假设武器等级上限为10
        while (exp >= expMax && level() < levelCap) {
            exp -= expMax;

            Catalog.countUses(HeroLongSword.class, 2);
            upgrade();

            expMax = 5*level() *level()  + 15*level()  + 10;
            GLog.p(Messages.get(this, "levelup", level()));
            Enchanting.show( curUser, this );
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        lvl = bundle.getInt("lvl");
        exp = bundle.getInt("exp");
        expMax = bundle.getInt("expMax");
        tier = bundle.getInt("tier");
        augments = bundle.getInt("augments");
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("lvl", lvl);
        bundle.put("exp", exp);
        bundle.put("expMax", expMax);
        bundle.put("tier", tier);
        bundle.put("augments", augments);
    }
}
