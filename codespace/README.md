# CSC 8840 - Homework 2: Sheep Grass System

The homework source is in `Homework2026/PredatorPrey/CKillariGiribabu`.
The `framework` directory contains the provided DEVSJAVA source code. It is included here only so the project can compile in a new GitHub Codespace.

## Running in GitHub Codespaces

1. Upload/extract this folder into a GitHub repository, then create a Codespace on that repository.
2. Run `./setup-visual.sh` in the Codespaces terminal. (This installs a virtual Linux display and browser VNC viewer.)
3. Open forwarded **port 6080** in the Codespaces **Ports** tab, then open `vnc.html` if necessary.
4. In another terminal, run `DISPLAY=:99 ./run.sh 5 3000`.
5. Switch scenarios by changing the first argument: `DISPLAY=:99 ./run.sh 1 300` through `DISPLAY=:99 ./run.sh 6 3000`.

The first argument is the scenario (1 to 6). The second argument is the number of simulator events. Change the simulation time parameters in `GlobalRef.java`. Every run starts from the same random seed. Stop the running simulation with Ctrl+C if desired. The grid is white for empty, green for grass, and red for sheep.

## Scenarios

1. One grass, no sheep
2. Four grass cells, no sheep
3. One sheep, no grass
4. Four sheep, no grass
5. Two adjacent grass cells and a neighboring sheep
6. Randomly seeded grass and sheep populations

## Files to submit to iCollege

Submit the separate `CSC8840_HW2_Submission.zip`, **not** the Codespaces workspace ZIP. The submission archive contains just the required Java package and two-page design PDF.

## Notes

The original DEVSJAVA toolkit is old; warnings about deprecated Java methods may appear. Check `compile.log` for warnings. If Java compilation fails, the shell script stops before running the simulation.
