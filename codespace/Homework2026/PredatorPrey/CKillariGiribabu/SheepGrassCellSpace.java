package Homework2026.PredatorPrey.CKillariGiribabu;

import twoDCellSpace.TwoDimCellSpace;
import genDevs.plots.newCellGridPlot;
import genDevs.simulation.coordinator;
import java.util.ArrayList;

public class SheepGrassCellSpace extends TwoDimCellSpace {
    public newCellGridPlot plot;
    private final GlobalRef global = GlobalRef.getInstance();
    private final int scenario;
    private static final int SIZE = 30;

    public SheepGrassCellSpace(int scenario) {
        super("Sheep and Grass", SIZE, SIZE);
        if (scenario < 1 || scenario > 6) throw new IllegalArgumentException("Scenario must be 1 to 6");
        this.scenario = scenario;
        global.setDim(SIZE, SIZE);
        plot = new newCellGridPlot("Sheep Grass", 0.1, "", 480, "", 480);
        plot.setCellSize(16);
        plot.setCellGridViewLocation(120, 100);
        numCells = SIZE * SIZE;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                SheepGrassCell cell = new SheepGrassCell(x, y);
                global.state[x][y] = "empty";
                global.cell_ref[x][y] = cell;
                addCell(cell, SIZE, SIZE);
            }
        }
        buildScenario();
        hideAll();
        doNeighborToNeighborCoupling();
        DoBoundaryToBoundaryCoupling();
    }

    private void put(int x, int y, String state) {
        ((SheepGrassCell) withId(x, y)).initial = state;
        global.change(x, y, state);
    }

    private void buildScenario() {
        switch (scenario) {
            case 1:
                put(15, 15, "grass");
                break;
            case 2:
                put(5, 5, "grass"); put(10, 20, "grass");
                put(20, 10, "grass"); put(24, 24, "grass");
                break;
            case 3:
                put(15, 15, "sheep");
                break;
            case 4:
                put(5, 5, "sheep"); put(10, 20, "sheep");
                put(20, 10, "sheep"); put(24, 24, "sheep");
                break;
            case 5:
                put(15, 15, "grass"); put(16, 15, "grass");
                put(14, 15, "sheep");
                break;
            case 6:
                // Spread the initial organisms across the grid.
                for (int i = 0; i < 120; i++) {
                    int x = global.random.nextInt(SIZE), y = global.random.nextInt(SIZE);
                    put(x, y, "grass");
                }
                for (int i = 0; i < 35; i++) {
                    int x = global.random.nextInt(SIZE), y = global.random.nextInt(SIZE);
                    put(x, y, "sheep");
                }
                break;
        }
    }

    // Wrap the eight directions around the grid edges.
    public int[] getNeighborXYCoord(twoDCellSpace.TwoDimCell cell, int direction) {
        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};
        return new int[]{(cell.getXcoord() + dx[direction] + SIZE) % SIZE,
                         (cell.getYcoord() + dy[direction] + SIZE) % SIZE};
    }

    private void DoBoundaryToBoundaryCoupling() {
        String[] out = {"outN", "outNE", "outE", "outSE", "outS", "outSW", "outW", "outNW"};
        String[] in = {"inS", "inSW", "inW", "inNW", "inN", "inNE", "inE", "inSE"};
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int d = 0; d < 8; d++) {
                    int nx = (x + new int[]{0,1,1,1,0,-1,-1,-1}[d] + SIZE) % SIZE;
                    int ny = (y + new int[]{1,1,0,-1,-1,-1,0,1}[d] + SIZE) % SIZE;
                    if (Math.abs(nx - x) > 1 || Math.abs(ny - y) > 1)
                        addCoupling(withId(x, y), out[d], withId(nx, ny), in[d]);
                }
            }
        }
    }

    public static void main(String[] args) {
        int scenario = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        int events = args.length > 1 ? Integer.parseInt(args[1]) : 3000;
        SheepGrassCellSpace model = new SheepGrassCellSpace(scenario);
        coordinator simulation = new coordinator(model);
        simulation.initialize();
        simulation.simulate(events);
        System.out.println("Scenario " + scenario + " after " + events + " events: grass="
                           + model.global.grassCount + " sheep=" + model.global.sheepCount);
        try {
    Thread.sleep(60000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
    }
}
