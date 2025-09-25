package com.shatteredpixel.shatteredpixeldungeon.custom.buffs.modifier;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.modifier.testmode.MobModifier;
import com.shatteredpixel.shatteredpixeldungeon.custom.buffs.modifier.testmode.TestMode;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

//Interface for attributes/damage/effects modifying and processing.
//Each module has its own modifier so the code would keep clean
//if there are multiple modules
public enum CombatModifier {
    INSTANCE;

    public static ArrayList<ModifierTemplate> modifiers = new ArrayList<>();
    static {
        modifiers.add(new MobModifier());
        modifiers.add(new TestMode());
    }

    public float attackModify(Char attacker, Char defender, float atk){
        for(ModifierTemplate mf: modifiers){
            if(mf.affectAtk) {
                atk = mf.attackModify(attacker, defender, atk);
            }
        }
        return atk;
    }

    //modify dr. Happens after defProc but before armor reduces damage.
    public int defenseModify(Char attacker, Char defender, int dr, int damage){
        for(ModifierTemplate mf: modifiers){
            if(mf.affectDef){
                dr = mf.defenseModify(attacker, defender, dr, damage);
            }
        }
        return dr;
    }

    public float accuracyModify(Char attacker, Char defender, float accuracy){
        for(ModifierTemplate mf: modifiers){
            if(mf.affectAcc){
               accuracy = mf.accuracyModify(attacker, defender, accuracy);
            }
        }
        return accuracy;
    }

    public float evasionModify(Char attacker, Char defender, float evasion){
        for(ModifierTemplate mf: modifiers){
            if(mf.affectEva){
                 evasion= mf.evasionModify(attacker, defender, evasion);
            }
        }
        return evasion;
    }


    //modify what happens in atkProc.
    public int attackProc(Char attacker, Char defender, int damage) {
        for(ModifierTemplate mf: modifiers){
            if(mf.atkProc){
                damage = mf.attackProc(attacker, defender, damage);
            }
        }
        return damage;
    }

    //modify what happens in defProc
    public int defenseProc(Char attacker, Char defender, int damage){
        for(ModifierTemplate mf: modifiers) {
            if(mf.defProc){
                damage = mf.defenseProc(attacker, defender, damage);
            }
        }
        return damage;
    }

    //should have a tracker on who caused the damage
    //modify what happens before taking final damage
    public int damage(Char ch, int damage, Object src) {
        for(ModifierTemplate mf: modifiers) {
            if(mf.affectDmg){
                damage = mf.damage(ch, damage, src);
            }
        }
        return damage;
    }

    public boolean preAddBuff(Char ch, Buff b){
        boolean result = true;
        for(ModifierTemplate mf: modifiers) {
            if(mf.preBuff){
                result = mf.preAddBuff(ch, b);
            }

        }
        return result;
    }

    public void postAddBuff(Char ch, Buff buff){
        for(ModifierTemplate mf: modifiers) {
            if(mf.postBuff){
                mf.postAddBuff(ch, buff);
            }
        }
    }

}
