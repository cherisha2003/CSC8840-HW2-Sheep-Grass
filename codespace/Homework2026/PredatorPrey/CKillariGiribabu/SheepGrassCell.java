package Homework2026.PredatorPrey.CKillariGiribabu;

import GenCol.*;
import genDevs.modeling.*;
import genDevs.plots.newCellGridView;
import twoDCellSpace.TwoDimCell;
import java.awt.Color;
import java.util.ArrayList;

public class SheepGrassCell extends TwoDimCell {
    private static final String[] OUT = {"outN", "outNE", "outE", "outSE", "outS", "outSW", "outW", "outNW"};
    private static final String[] IN = {"inN", "inNE", "inE", "inSE", "inS", "inSW", "inW", "inNW"};
    private final GlobalRef global = GlobalRef.getInstance();
    private final int x, y;
    public String initial = "empty";
    private String cellType = "empty";
    private double moveLeft, lifeLeft, reproduceLeft;
    private int chosen = -1;
    private int targetX, targetY;
    private newCellGridView grid;

    public SheepGrassCell(int x, int y) {
        super(new Pair(Integer.valueOf(x), Integer.valueOf(y)));
        this.x = x;
        this.y = y;
    }

    public void initialize() {
        grid = ((SheepGrassCellSpace) getParent()).plot.getCellGridView();
        cellType = initial;
        global.change(x, y, cellType);
        draw();
        if (cellType.equals("grass")) holdIn("grass", global.grassReproduceT);
        else if (cellType.equals("sheep")) {
            moveLeft = global.sheepMoveT;
            lifeLeft = global.sheepLifeT;
            reproduceLeft = global.sheepReproduceT;
            scheduleSheep();
        } else passivateIn("empty");
    }

    private void draw() {
        if (grid == null) return;
        Color color = cellType.equals("grass") ? Color.green :
                      cellType.equals("sheep") ? Color.red : Color.white;
        grid.drawCell(x, 29 - y, color);
    }

    private void setType(String next) {
        cellType = next;
        global.change(x, y, next);
        draw();
    }

    private void scheduleSheep() {
        // Dying wins ties with moving or reproducing.
        double smallest = Math.min(moveLeft, Math.min(lifeLeft, reproduceLeft));
        if (lifeLeft <= smallest + 1e-9) holdIn("die", Math.max(0, lifeLeft));
        else if (moveLeft <= smallest + 1e-9) holdIn("move", Math.max(0, moveLeft));
        else holdIn("reproduce", Math.max(0, reproduceLeft));
    }

    private void subtractTime(double amount) {
        moveLeft = Math.max(0, moveLeft - amount);
        lifeLeft = Math.max(0, lifeLeft - amount);
        reproduceLeft = Math.max(0, reproduceLeft - amount);
    }

    private int[] neighbor(int direction) {
        return ((SheepGrassCellSpace) getParent()).getNeighborXYCoord(this, direction);
    }

    private int pickTarget(String type) {
        ArrayList<Integer> choices = new ArrayList<Integer>();
        for (int i = 0; i < 8; i++) {
            int[] where = neighbor(i);
            if (type.equals(global.state[where[0]][where[1]])) choices.add(i);
        }
        while (!choices.isEmpty()) {
            int index = global.random.nextInt(choices.size());
            int direction = choices.remove(index);
            int[] where = neighbor(direction);
            if (global.claim(where[0], where[1])) {
                targetX = where[0];
                targetY = where[1];
                return direction;
            }
        }
        return -1;
    }

    public message out() {
        message result = super.out();
        chosen = -1;
        if (cellType.equals("grass") && phaseIs("grass")) {
            chosen = pickTarget("empty");
            if (chosen >= 0) result.add(makeContent(OUT[chosen], new SheepMessage("grass", 0, 0, 0)));
        } else if (cellType.equals("sheep")) {
            if (phaseIs("move")) {
                chosen = pickTarget("grass");
                if (chosen < 0) chosen = pickTarget("empty");
                if (chosen >= 0) {
                    double remainingLife = Math.max(0, lifeLeft - sigma);
                    double remainingRepro = Math.max(0, reproduceLeft - sigma);
                    result.add(makeContent(OUT[chosen], new SheepMessage("move", global.sheepMoveT,
                                                    remainingLife, remainingRepro)));
                }
            } else if (phaseIs("reproduce")) {
                chosen = pickTarget("grass");
                if (chosen < 0) chosen = pickTarget("empty");
                if (chosen >= 0) result.add(makeContent(OUT[chosen], new SheepMessage("birth", 0, 0, 0)));
            }
        }
        return result;
    }

    public void deltint() {
        if (cellType.equals("grass")) {
            holdIn("grass", global.grassReproduceT);
            return;
        }
        if (!cellType.equals("sheep")) { passivateIn("empty"); return; }
        double elapsed = sigma;
        subtractTime(elapsed);
        if (phaseIs("die")) {
            setType("empty");
            passivateIn("empty");
        } else if (phaseIs("move")) {
            if (chosen >= 0) {
                setType("empty");
                passivateIn("empty");
            } else {
                moveLeft = global.sheepMoveT;
                scheduleSheep();
            }
        } else if (phaseIs("reproduce")) {
            reproduceLeft = global.sheepReproduceT;
            scheduleSheep();
        }
    }

    public void deltext(double e, message input) {
        if (cellType.equals("sheep")) subtractTime(e);
        else Continue(e);
        for (int i = 0; i < 8; i++) {
            if (!somethingOnPort(input, IN[i])) continue;
            entity value = getEntityOnPort(input, IN[i]);
            if (!(value instanceof SheepMessage)) continue;
            SheepMessage arriving = (SheepMessage) value;
            if (arriving.kind.equals("grass")) {
                if (cellType.equals("empty")) {
                    setType("grass");
                    holdIn("grass", global.grassReproduceT);
                }
            } else if (!cellType.equals("sheep")) {
                boolean ateGrass = cellType.equals("grass");
                setType("sheep");
                if (arriving.kind.equals("birth")) {
                    moveLeft = global.sheepMoveT;
                    lifeLeft = global.sheepLifeT;
                    reproduceLeft = global.sheepReproduceT;
                } else {
                    moveLeft = arriving.moveLeft;
                    lifeLeft = ateGrass ? global.sheepLifeT : arriving.lifeLeft;
                    reproduceLeft = arriving.reproduceLeft;
                }
                scheduleSheep();
            }
        }
        global.release(x, y);
        if (cellType.equals("sheep") && !phaseIs("move") && !phaseIs("die") && !phaseIs("reproduce")) scheduleSheep();
        else if (cellType.equals("sheep")) scheduleSheep();
    }

    // Complete the current event before handling arrivals at the same time.
    public void deltcon(double e, message input) {
        deltint();
        deltext(0, input);
    }
}
