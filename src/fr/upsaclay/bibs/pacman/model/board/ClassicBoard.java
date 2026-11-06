package fr.upsaclay.bibs.pacman.model.board;

import fr.upsaclay.bibs.pacman.GameType;
import fr.upsaclay.bibs.pacman.PacManException;
import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.actors.*;
import fr.upsaclay.bibs.pacman.model.maze.Maze;

import java.io.FileNotFoundException;

public class ClassicBoard extends AbstractBoard {


    public ClassicBoard() {
        super(GameType.CLASSIC);
    }


    /**
     * Initialization of the board
     * (loads the maze, create and place the actors, etc.)
     * @throws PacManException in case something went wrong
     */
    @Override
    public void initialize() throws PacManException {
        try {
            this.maze = Maze.loadFromFile("resources/maze.txt");
            this.pacMan = new PacMan(this, 112, 211);
            if (!this.noBlinky) {
                this.blinky = new Blinky(this, 112, 115);

            }
            if  (!this.noPinky) {
                this.pinky = new Pinky(this, 112, 139);
            }
            if (!this.noInky) {
                this.inky = new Inky(this, 96, 139);
            }
            if (!this.noClyde) {
                this.clyde = new Clyde(this, 128, 139);
            }
        } catch (Exception e) {
            throw new BoardInitialisationException(e);
        }
        for (Ghost ghost : getGhosts()) {
            ghost.setOutOfPenDirection(Direction.LEFT);
            ghost.setGhostState(GhostState.SCATTER);
        }
        this.noDotCounter().setLimit(240);
    }

    @Override
    public void initializeNewLife() {

        // Sets state board state to initial
        super.initializeNewLife();

        // Re-create all actors
        this.pacMan.setPosition(112, 211);
        this.pacMan.setDirection(Direction.LEFT);
        this.pacMan.setSpeed(getLevelPacManSpeed());

        if (!this.noBlinky) {
            this.blinky.setPosition(112, 115);
            this.blinky.setGhostPenState(GhostPenState.OUT);
        }

        if (!this.noPinky) {
            this.pinky.setPosition(112, 139);
            this.pinky.setGhostPenState(GhostPenState.IN);
        }

        if (!this.noInky) {
            this.inky.setPosition(96, 139);
            this.inky.setGhostPenState(GhostPenState.IN);
        }

        if (!this.noClyde) {
            this.clyde.setPosition(128, 139);
            this.clyde.setGhostPenState(GhostPenState.IN);
        }

        // Initialize directions and states
        for (Ghost ghost : getGhosts()) {
            ghost.setOutOfPenDirection(Direction.LEFT);
            ghost.setGhostState(GhostState.SCATTER);

            if (ghost.getGhostType() != GhostType.BLINKY) {
                ghost.setDirection(Direction.UP);
            }
            else {
                ghost.setDirection(Direction.LEFT);
            }

            if (ghost.getGhostPenState() == GhostPenState.IN) {
                ghost.setSpeed(getTunnelGhostSpeed());
            }
            else {
                ghost.setSpeed(getLevelGhostSpeed());
            }

            // Reset les dot counters après utilisation de special dot counter
            if (ghost.hasDotCounter() && ghost.getDotCounter() != null) {
                ghost.getDotCounter().reset();
            }
        }
    }

    @Override
    public void initializeNewLevel(int level) throws PacManException {
        // Updates level in AbstractBoard
        super.initializeNewLevel(level);

        try {
            this.maze = Maze.loadFromFile("resources/maze.txt");
        } catch (Exception e) {
            throw new PacManException(e);
        }

        // Reset actors
        this.initializeNewLife();

        // Reset dot counters
        for (Ghost ghost : getGhosts()) {
            if (ghost.hasDotCounter() && ghost.getDotCounter() != null) {
                Counter c = ghost.getDotCounter();
                c.reset();

                if (ghost.getGhostType() == GhostType.INKY) {
                    if (this.level == 1) {
                        c.setLimit(30);
                    }
                    else if (this.level >= 2) {
                        c.setLimit(0);
                    }
                }
                else if (ghost.getGhostType() == GhostType.CLYDE) {
                    if (this.level == 1) {
                        c.setLimit(60);
                    }
                    else if (this.level == 2) {
                        c.setLimit(50);
                    }
                    else if (this.level >= 3) {
                        c.setLimit(0);
                    }
                }

            }
        }

        // No dot counter limits
        if (this.level <= 4) {
            this.noDotCounter().setLimit(240);
        }
        else {
            this.noDotCounter().setLimit(180);
        }
    }
}
