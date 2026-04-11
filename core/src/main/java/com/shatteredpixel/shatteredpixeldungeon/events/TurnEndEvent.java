package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.Statistics;

/**
 * 回合结束事件
 * 当游戏回合结束时触发
 */
public class TurnEndEvent extends GameEvent {
    private final int turnNumber;
    private final float duration;
    private final float now;

    public TurnEndEvent(int turnNumber, float duration, float now) {
        this.turnNumber = turnNumber;
        this.duration = duration;
        this.now = now;
    }

    /**
     * 获取当前回合数
     * @return 回合编号
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * 获取累计的整数回合数（通过 fixTime 累积）
     * @return Statistics.duration
     */
    public float getDuration() {
        return duration;
    }

    /**
     * 获取当前全局时间
     * @return Actor.now()
     */
    public float getNow() {
        return now;
    }

    /**
     * 获取完整的回合计数（duration + now 的整数部分）
     * @return 完整回合数
     */
    public int getTotalTurn() {
        return (int)(duration + now);
    }
}
