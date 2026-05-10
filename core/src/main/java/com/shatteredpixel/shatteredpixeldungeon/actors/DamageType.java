package com.shatteredpixel.shatteredpixeldungeon.actors;

/**
 * 显式的伤害类型枚举，用于替代基于 instanceof 的伤害判断。
 */
public enum DamageType {
    PHYSICAL,           // 普通物理（受护甲减免）
    PHYSICAL_NO_BLOCK,  // 无视护甲的物理伤害（狙击手远程、武僧、特定环境伤害等）
    MAGICAL,            // 魔法伤害（受反魔场/奥术护甲减免）
    PICK,               // 镐子
    HUNGER,             // 饥饿
    BURNING,            // 燃烧
    FROST,              // 冰霜
    WATER,              // 蒸汽/热水
    SHOCKING,           // 电击
    BLEEDING,           // 流血
    TOXIC,              // 毒气
    CORROSION,          // 腐蚀
    POISON,             // 毒素
    OOZE,               // 黏液
    DEFERRED,           // 粘稠护甲延迟伤害
    CORRUPTION,         // 腐败
    AMULET,             // 飞升挑战
    // 以下为模组新增类型
    MAGICAL_DISPELLING, // 驱散药水反转的魔法伤害
    BURST_BURNING,      // 烈焰药水反伤
    SHIVA_BANGLE,       // 湿婆手镯（如有独立伤害）
    COMBAT_MODIFIER,    // 战斗修饰器特殊伤害
    OTHER               // 兜底类型
}
