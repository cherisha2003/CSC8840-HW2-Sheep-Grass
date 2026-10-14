package Homework2026.PredatorPrey.CKillariGiribabu;

import genDevs.modeling.IODevs;
import java.util.Random;

public class GlobalRef {
    private static final GlobalRef instance = new GlobalRef();
    public String[][] state;
    public IODevs[][] cell_ref;
    private boolean[][] reserved;

    public double grassReproduceT = 1.0;
public double sheepMoveT = 1.5;
public double sheepLifeT = 3.0;
public double sheepReproduceT = 8.0;
    public Random random = new Random(8840);
    public int grassCount = 0;
    public int sheepCount = 0;

    private GlobalRef() {}
    public static GlobalRef getInstance() { return instance; }

    public void setDim(int x, int y) {
        state = new String[x][y];
        cell_ref = new IODevs[x][y];
        reserved = new boolean[x][y];
        grassCount = 0;
        sheepCount = 0;
        random.setSeed(8840);
    }

    // A target is reserved only when a movement/reproduction event finishes.
    // This avoids two simultaneous events choosing the same destination.
    public boolean claim(int x, int y) {
        if (reserved[x][y] || "sheep".equals(state[x][y])) return false;
        reserved[x][y] = true;
        return true;
    }

    public void release(int x, int y) { reserved[x][y] = false; }

    public void change(int x, int y, String next) {
        String old = state[x][y];
        if (next.equals(old)) return;
        if ("grass".equals(old)) grassCount--;
        if ("sheep".equals(old)) sheepCount--;
        state[x][y] = next;
        if ("grass".equals(next)) grassCount++;
        if ("sheep".equals(next)) sheepCount++;
    }
}
