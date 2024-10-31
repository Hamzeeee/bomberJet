package io.hamza.github.utilities;

import org.bukkit.boss.BarColor;

public enum BomberJetRules {
    MAXIMAL_SPEED(33, Integer.MAX_VALUE, BarColor.RED),
    MINIMUM_SPEED(18, 32, BarColor.YELLOW),
    LOW_SPEED(0, 17, BarColor.GREEN);

    private final int minSpeed;
    private final int maxSpeed;
    private final BarColor color;

    BomberJetRules(int minSpeed, int maxSpeed, BarColor color) {
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.color = color;
    }

    public static BarColor setBossBarColorRules(int blocksPerSecond) {
        for (BomberJetRules rules : values()) {

            if (rules.minSpeed <= blocksPerSecond && rules.maxSpeed >= blocksPerSecond) {
                return rules.color;
            }

        }
        return BarColor.WHITE;
    }



}
