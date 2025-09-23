package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
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
            GLog.w(Messages.get(this, "equip_message"));
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
            KindOfWeapon.unequipSlotGroup(hero, KindOfWeapon.WeaponSlot.PRIMARY_3, KindOfWeapon.WeaponSlot.PRIMARY_4);

            KindOfWeapon.setWeaponInSlot(hero, KindOfWeapon.WeaponSlot.PRIMARY_3, null);
            KindOfWeapon.setWeaponInSlot(hero, KindOfWeapon.WeaponSlot.PRIMARY_4, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped( hero )){
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else
                desc += Messages.get(this, "desc_equipped");
        }
        return desc;
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
        }
    }

    protected ArtifactBuff passiveBuff() {
        return new MultiArmBlows();
    }

    public class MultiArmBlows extends ArtifactBuff {

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

        }

        @Override
        public boolean act() {
            // 在这里添加每回合的逻辑
            spend(TICK);
            return true;
        }
    }

}
