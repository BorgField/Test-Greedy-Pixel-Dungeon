package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class ScrollOfBlank extends Scroll {

    {
        image = ItemSpriteSheet.SCROLL_BLANK;
    }

    @Override
    public void doRead() {
        // 空白卷轴不可直接阅读
        GLog.i(Messages.get(this, "nothing"));
    }

    @Override
    public String name() {
        return isKnown() ? Messages.get(this, "name") : super.name();
    }

    @Override
    public String info() {
        return isKnown() ? Messages.get(this, "desc") : super.info();
    }

    @Override
    public int value() {return 10 * quantity;}

    @Override
    public int energyVal() {
        return 1 * quantity;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

}
