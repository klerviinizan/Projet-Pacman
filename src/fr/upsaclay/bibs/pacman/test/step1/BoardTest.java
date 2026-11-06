package fr.upsaclay.bibs.pacman.test.step1;

import static org.junit.jupiter.api.Assertions.*;

import fr.upsaclay.bibs.pacman.GameType;
import fr.upsaclay.bibs.pacman.PacManException;
import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.actors.Actor;
import fr.upsaclay.bibs.pacman.model.actors.ActorType;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;
import org.junit.jupiter.api.Test;


public class BoardTest {

    @Test
    public void testTestBoardCreation() {
        Board testBoard = Board.createBoard(GameType.TEST);
        assertEquals(GameType.TEST, testBoard.getGameType());
    }

    @Test
    public void testClassicBoardCreation() {
        Board classicBoard = Board.createBoard(GameType.CLASSIC);
        assertEquals(GameType.CLASSIC, classicBoard.getGameType());
    }

    @Test
    public void testTestBoardInitialization() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        /** test that PacMan is not null after initialization **/
        assertNotNull(testBoard.getPacMan());
        /** test that PacMan is an initial position **/
        assertEquals(35, testBoard.getPacMan().getX());
        assertEquals(75, testBoard.getPacMan().getY());
        assertEquals(new TilePosition(9,4), testBoard.getPacMan().getCurrentTile());
        /** test that the maze is not null after initialization **/
        assertNotNull(testBoard.getMaze());
        assertEquals(9, testBoard.getMaze().getWidth());
        assertEquals(17, testBoard.getMaze().getHeight());
    }

    @Test
    public void testClassicBoardInitialization() throws PacManException {
        Board classicBoard = Board.createBoard(GameType.CLASSIC);
        classicBoard.initialize();
        /** test that PacMan is not null after initialization **/
        assertNotNull(classicBoard.getPacMan());
        assertEquals(ActorType.PACMAN, classicBoard.getPacMan().getType());
        /** test that PacMan is an initial position **/
        assertEquals(112, classicBoard.getPacMan().getX());
        assertEquals(211, classicBoard.getPacMan().getY());
        assertEquals(new TilePosition(26, 14), classicBoard.getPacMan().getCurrentTile());
        /** test that the maze is not null after initialization **/
        assertNotNull(classicBoard.getMaze());
        assertEquals(28, classicBoard.getMaze().getWidth());
        assertEquals(36, classicBoard.getMaze().getHeight());
    }

    @Test
    public void testStartActor() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        /** test that PacMan has a direction when the game starts **/
        assertEquals(Direction.LEFT, testBoard.getPacMan().getDirection());
        Board classicBoard = Board.createBoard(GameType.CLASSIC);
        classicBoard.initialize();
        classicBoard.startActors();
        assertEquals(Direction.LEFT, classicBoard.getPacMan().getDirection());
        assertNull(classicBoard.getPacMan().getIntention());
    }

    @Test
    public void testNextFrameMoving() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        TilePosition pos = pacman.getCurrentTile(); // initial tile position
        testBoard.nextFrame(); // PacMan moves one step to the left
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        assertEquals(pos, pacman.getCurrentTile()); // the tile is still the same
        // We move 3 more pixels to the left
        testBoard.nextFrame();
        testBoard.nextFrame();
        testBoard.nextFrame();
        TilePosition newPos = new TilePosition(pos.getLine(), pos.getCol() - 1);
        assertEquals(x-4, pacman.getX());
        assertEquals(y, pacman.getY());
        assertEquals(newPos, pacman.getCurrentTile()); // new tile
    }

}
