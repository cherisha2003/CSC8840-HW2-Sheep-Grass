package Homework2026.PredatorPrey.CKillariGiribabu;

import GenCol.entity;

public class SheepMessage extends entity {
    public final String kind;
    public final double moveLeft;
    public final double lifeLeft;
    public final double reproduceLeft;

    public SheepMessage(String kind, double move, double life, double reproduce) {
        super(kind);
        this.kind = kind;
        moveLeft = move;
        lifeLeft = life;
        reproduceLeft = reproduce;
    }
}
