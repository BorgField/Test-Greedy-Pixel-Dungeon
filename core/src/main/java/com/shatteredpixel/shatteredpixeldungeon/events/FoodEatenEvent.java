package com.shatteredpixel.shatteredpixeldungeon.events;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;

/**
 * 食物食用事件
 * 当英雄食用食物时触发
 */
public class FoodEatenEvent extends GameEvent {
    private final Hero hero;
    private final Food food;
    private final float foodValue;

    public FoodEatenEvent(Hero hero, Food food, float foodValue) {
        this.hero = hero;
        this.food = food;
        this.foodValue = foodValue;
    }

    public Hero getHero() {
        return hero;
    }

    public Food getFood() {
        return food;
    }

    public float getFoodValue() {
        return foodValue;
    }
}
