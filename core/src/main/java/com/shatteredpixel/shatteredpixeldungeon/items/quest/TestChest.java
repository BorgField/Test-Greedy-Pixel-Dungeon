package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.WheelChair;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ShivaBangle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfBlank;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HeroLongSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.TendonHookSickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TestChest extends Item {

    public static final String AC_AAT	= "KITEM";

    {
        stackable = true;
        image = ItemSpriteSheet.LOCKED_CHEST;
        defaultAction = AC_AAT;
        bones = true;
//        animation = false;
    }

    //    @Override
    public void frames(ItemSprite itemSprite){
        itemSprite.texture(Assets.Sprites.MIMIC);
        TextureFilm frames = new TextureFilm(itemSprite.texture, 16, 16);
        MovieClip.Animation idle = new MovieClip.Animation(5, true);
        idle.frames( frames,2,2,2,3,3);
        itemSprite.play(idle);
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_AAT);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals( AC_AAT )) {
            detach( hero.belongings.backpack );
            GLog.i( Messages.get(this, "look_msg") );
            ArrayList<Item> items = convert();
            for (Item item : items) {
                item.collect();
            }
            hero.sprite.operate( hero.pos );
            hero.busy();
            Sample.INSTANCE.play( Assets.Sounds.DRINK );
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }


    private final int coin = Random.Int(60,75);
    @Override
    public int value() {
        return coin;
    }

    private ArrayList<Item> convert() {
        ArrayList<Item> items = new ArrayList<>();

        //英雄长剑
        items.add(new HeroLongSword().identify());
//        //猫人饼干
//        items.add(new CatCookie().quantity(10));
//        //魔绫随机物资箱子
//        items.add(new RandomChest().quantity(10));
//        items.add(new ScrollOfBlank().quantity(10));
//        //饥饿碎片
//        items.add(new ShardOfHunger().quantity(1));
        items.add(new TendonHookSickle().quantity(1).identify());
        items.add(new ScrollOfUpgrade().quantity(0).identify());
        items.add(new ScrollOfBlank().quantity(20).identify());
        items.add(new PotionOfStrength().quantity(0).identify());
        items.add(new WheelChair().quantity(1).identify());
        items.add(new ShivaBangle().quantity(1).identify());

        return items;
    }
}

