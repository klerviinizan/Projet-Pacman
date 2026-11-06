package fr.upsaclay.bibs.pacman.model.board;

import fr.upsaclay.bibs.pacman.GameType;
import fr.upsaclay.bibs.pacman.PacManException;
import fr.upsaclay.bibs.pacman.model.actors.Actor;
import fr.upsaclay.bibs.pacman.model.actors.PacMan;
import fr.upsaclay.bibs.pacman.model.maze.Maze;

import java.io.FileNotFoundException;

public class TestBoard extends AbstractBoard {

    public TestBoard() {
        super(GameType.TEST);
    }

    /**
     * Initialization of the board
     * (loads the maze, create and place the actors, etc.)
     * @throws PacManException in case something went wrong
     */
    @Override
    public void initialize() throws PacManException {
        try {
            this.maze = Maze.loadFromFile("resources/test.txt");
            this.pacMan = new PacMan(this, 35, 75);
        } catch (Exception e) {
            throw new BoardInitialisationException(e);
        }
    }
}
