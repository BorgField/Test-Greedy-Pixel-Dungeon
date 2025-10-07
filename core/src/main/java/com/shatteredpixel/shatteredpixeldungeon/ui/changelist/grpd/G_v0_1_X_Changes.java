package com.shatteredpixel.shatteredpixeldungeon.ui.changelist.grpd;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;

import java.util.ArrayList;

public class G_v0_1_X_Changes {
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        add_v01_1_changes( changeInfos );
    }

    private static void add_v01_1_changes( ArrayList<ChangeInfo> changeInfos ){
        ChangeInfo changes = new ChangeInfo("v0.0.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SEAL), "小型试剂",
                "目前已完成的小型试剂：\n" +
                        "火焰-->炎蚀试剂\n" +
                        "力量-->迸发试剂\n" +
                        "经验-->顿悟试剂\n" +
                        "治疗-->急救试剂\n" +
                        "冰霜-->霜冻试剂\n" +
                        "极速-->迅捷试剂\n" +
                        "麻痹-->抵抗试剂"));
    }
}
