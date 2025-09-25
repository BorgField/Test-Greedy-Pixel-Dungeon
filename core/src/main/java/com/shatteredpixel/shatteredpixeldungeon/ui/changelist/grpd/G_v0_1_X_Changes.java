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
        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SEAL), "测试标题",
                "在游戏玩法方面，勇士在进行小型重做后表现良好。我可能会在 v3.2 中做出一些更改，但现在我只是对早期天赋进行一个增益：\n\n" +
                        "**-丰盛的大餐**生命值阈值从 30% 增加到 33%，治疗量从 +1/+2 的 3/5 增加到 +1/+2 的 4/6。\n\n" +
                        "根据反馈，战士的新水花也进行了一些调整：\n" +
                        "**-** 显着提亮了破损的密封件，以便更容易看到新的细节\n" +
                        "**-** 战士手臂上增加了疤痕和一些肌肉轮廓\n" +
                        "**-** 沿着战士的身影加深\n" +
                        "**-** 添加了各种小细节，使盔甲显得更破旧，战士更粗犷\n"));
    }
}
