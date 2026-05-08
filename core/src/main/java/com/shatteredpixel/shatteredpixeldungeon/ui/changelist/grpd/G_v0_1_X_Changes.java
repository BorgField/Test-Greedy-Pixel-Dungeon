package com.shatteredpixel.shatteredpixeldungeon.ui.changelist.grpd;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
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
                        "麻痹-->抵抗试剂\n" +
                        "隐身-->拟态试剂\n" +
                        "净化-->破魔试剂\n" +
                        "灵视-->明目试剂\n" +
                        "浮空-->腾跃试剂\n" +
                        "毒气-->毒雾试剂"));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_BLANK), "空白卷轴",
                "空白卷轴：有些卷轴的用料意外的好，或许是精品？总之这张卷轴被残留的一点魔力维持着外形。\n" +
                        "\n" +
                        "空白卷轴+任意卷轴+6能量→两张*拓印*任意卷轴\n" +
                        "-拓印过的卷轴不能制作为秘卷。\n" +
                        "-拓印过的卷轴分解为符石时只获得1颗指定符石\n" +
                        "-拓印过的升级卷轴至多只能将物品提升到3级，然后不能被升级。\n" +
                        "\n" +
                        "空白卷轴：拆解卷轴为符石时有1/12的概率获得一张\n" +
                        "\n" +
                        "空白卷轴可以拆解为2颗空白符石\n" +
                        "空白符石：好像只是一颗普通的黑色石子？\n" +
                        "空白符石+3能量→随机一种符石。"));

        changes.addButton(new ChangeButton(Icons.ABILITY_STR.get(), "图形化属性栏",
                "在设置里可以调整属性栏为图形化或者文本类型，材质切换也在设置调整"));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.STEWED), "魔法炖肉",
                "制作炖肉时有1/3概率使其转化为魔法炖肉！食用时触发随机药水、合剂甚至是力量药水效果！"));
    }
}
