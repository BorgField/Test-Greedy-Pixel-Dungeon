package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ShivaBangle extends Artifact {

    {
        image = ItemSpriteSheet.GAUNTLETS;
        levelCap = 10;
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            // 在这里添加装备时的触发逻辑
            GLog.i(Messages.get(this, "equip_message"));
            activate(hero);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void activate(Char target) {
        super.activate(target);
        // 在这里添加激活时的逻辑
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            // 在这里添加卸下时的逻辑
            if (activeBuff != null) {
                activeBuff.detach();
                activeBuff = null;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String info() {
        StringBuilder info = new StringBuilder(super.info());

        if (isIdentified()) {
            info.append("\n\n");
            info.append(Messages.get(this, "desc"));
        }

        return info.toString();
    }

    @Override
    public String status() {
        return super.status();
    }

    @Override
    public int value() {
        int price = 300;
        if (level() > 0)
            price += 50*visiblyUpgraded();
        if (cursed && cursedKnown) {
            price /= 2;
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap) {
            partialCharge += amount;
            if (partialCharge >= 1) {
                charge++;
                partialCharge -= 1;
            }
            // 你可以在这里添加充电时的特殊效果
        }
    }

    protected ArtifactBuff passiveBuff() {
        return new ShivaBangleBuff();
    }

    public class ShivaBangleBuff extends ArtifactBuff {

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                // 在这里添加附加效果时的逻辑
                return true;
            }
            return false;
        }

        @Override
        public void detach() {
            super.detach();
            // 在这里添加分离效果时的逻辑
        }

        @Override
        public boolean act() {
            // 在这里添加每回合的逻辑
            spend(TICK);
            return true;
        }
    }

    private static final String CHARGE = "charge";
    private static final String PARTIALCHARGE = "partialcharge";

}
