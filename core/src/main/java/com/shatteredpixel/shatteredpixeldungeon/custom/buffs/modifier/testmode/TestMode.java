package com.shatteredpixel.shatteredpixeldungeon.custom.buffs.modifier.testmode;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.custom.testmode.ImmortalShieldAffecter;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.modifier.ModifierTemplate;

public class TestMode extends ModifierTemplate {
    {
        affectDmg = true;
    }

    @Override
    public int damage(Char ch, int damage, Object src) {
        if (ch.buff(ImmortalShieldAffecter.ImmortalShield.class) != null) {
            ch.sprite.showStatus(0x00FFFF, "%d", damage);
            damage = 0;
        }
        return damage;
    }
}
