package fr.upsaclay.bibs.pacman.test.step1;

import fr.upsaclay.bibs.pacman.GameType;
import fr.upsaclay.bibs.pacman.PacManException;
import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.actors.Actor;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PacManTest {

    @Test
    public void testSetGetXY() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setPosition(3,3);
        assertEquals(3, pacman.getX());
        assertEquals(3, pacman.getY());
    }

    @Test
    public void testGetCurrentTile() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setPosition(3,3);
        assertEquals(new TilePosition(0,0), pacman.getCurrentTile());
        pacman.setPosition(35, 75);
        assertEquals(new TilePosition(9,4), pacman.getCurrentTile());
    }
    @Test
    public void testSetGetDirection() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.UP);
        assertEquals(Direction.UP, pacman.getDirection());
    }

    /**
     * We test : PacMan starts going left then goes right then left again
     * @throws PacManException
     */
    @Test
    public void testSetIntentionTurnAroundLeftRight() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();
        // PacMan directio is to the left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved one pixel to the left
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        // We ask Pacman to go right
        pacman.setIntention(Direction.RIGHT);
        pacman.nextMove();
        // PacMan direction should now be right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // The intention should be back to null
        assertNull(pacman.getIntention());
        // Pacman has moved one pixel the right
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // We move one more time
        pacman.nextMove();
        assertEquals(Direction.RIGHT, pacman.getDirection());
        assertEquals(x+1, pacman.getX());
        assertEquals(y, pacman.getY());
        // Now we give the order to go left
        pacman.setIntention(Direction.LEFT);
        pacman.nextMove();
        // PacMan direction should now be left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // The intention should be back to null
        assertNull(pacman.getIntention());
        // Pacman has moved one pixel the left
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // We move one more time
        pacman.nextMove();
        assertEquals(Direction.LEFT, pacman.getDirection());
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
    }

    /**
     * We test : PacMan starts up then goes down then up again
     * @throws PacManException
     */
    @Test
    public void testSetIntentionTurnAroundUpDown() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.UP); // We set the direction up before starting the actor
        testBoard.startActors();

        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();
        // PacMan direction is up
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved one pixel up
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
        // We ask Pacman to go down
        pacman.setIntention(Direction.DOWN);
        pacman.nextMove();
        // PacMan direction should now be down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // The intention should be back to null
        assertNull(pacman.getIntention());
        // Pacman has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // We move one more time
        pacman.nextMove();
        assertEquals(Direction.DOWN, pacman.getDirection());
        assertEquals(x, pacman.getX());
        assertEquals(y+1, pacman.getY());
        // Now we give the order to go UP
        pacman.setIntention(Direction.UP);
        pacman.nextMove();
        // PacMan direction should now be up
        assertEquals(Direction.UP, pacman.getDirection());
        // The intention should be back to null
        assertNull(pacman.getIntention());
        // Pacman has moved one pixel up
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // We move one more time
        pacman.nextMove();
        assertEquals(Direction.UP, pacman.getDirection());
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
    }

    /**
     * We test when we ask pacman to turn but it's not possible due to a wall
     * Pacman starts to the left, moves one pixel then is asked to up
     * It cannot go up and continues left
     * Then is asked to go down
     * It cannot go down and continues left
     * @throws PacManException
     */
    @Test
    public void testSetIntentionNotTurningLeftUpDown() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();
        // PacMan direction is to the left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved one pixel to the left
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        // We ask pacman to go up
        pacman.setIntention(Direction.UP);
        // Pacman has started at the tile center and has gone one pixel left
        // We move enough so that it reaches the next tile and tries its intention
        for(int i =0; i < 8; i++) {
            pacman.nextMove();;
        }
        // Pacman should have tried to go up, failed and kept going left
        assertEquals(Direction.LEFT, pacman.getDirection());
        assertEquals(x-9, pacman.getX());
        assertEquals(y, pacman.getY());
        // The intention should be back to null
        assertNull(pacman.getIntention());
        // We ask pacman to go down
        pacman.setIntention(Direction.DOWN);
        // We move enough so that it reaches the next tile and tries its intention
        for(int i =0; i < 8; i++) {
            pacman.nextMove();;
        }
        // Pacman should have tried to go down, failed and kept going left
        assertEquals(Direction.LEFT, pacman.getDirection());
        assertEquals(x-17, pacman.getX());
        assertEquals(y, pacman.getY());
        // The intention should be back to null
        assertNull(pacman.getIntention());
    }

    /**
     * We test when we ask pacman to turn but it's not possible due to a wall
     * Pacman starts up, moves one pixel then is asked to go left
     * It cannot go left and continues up
     * Then is asked to go right
     * It cannot go right and continues up
     * @throws PacManException
     */
    @Test
    public void testSetIntentionNotTurningUpLeftRight() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.UP); // We set the direction up before starting the actor
        testBoard.startActors();

        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();;
        // PacMan direction is up
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved one pixel up
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
        // We ask pacman to go left
        pacman.setIntention(Direction.LEFT);
        // Pacman has started at the tile center and has gone one pixel up
        // We move enough so that it reaches the next tile and tries its intention
        for(int i =0; i < 8; i++) {
            pacman.nextMove();;
        }
        // Pacman should have tried to go left, failed and kept going up
        assertEquals(Direction.UP, pacman.getDirection());
        assertEquals(x, pacman.getX());
        assertEquals(y-9, pacman.getY());
        // The intention should be back to null
        assertNull(pacman.getIntention());
        // We ask pacman to go right
        pacman.setIntention(Direction.RIGHT);
        // We move enough so that it reaches the next tile and tries its intention
        for(int i =0; i < 8; i++) {
            pacman.nextMove();;
        }
        // Pacman should have tried to go right, failed and kept going up
        assertEquals(Direction.UP, pacman.getDirection());
        assertEquals(x, pacman.getX());
        assertEquals(y-17, pacman.getY());
        // The intention should be back to null
        assertNull(pacman.getIntention());
    }

    /**
     * We test an actual turn on pacman
     * Initially pacman goes some pixels left
     * We then ask pacman to turn around
     * Then we ask to go up and check that the intention is kept until reaching the tile center.
     * When reaching the tile center, pacman's direction changes to up
     * @throws PacManException
     */
    @Test
    public void testSetIntentionTurnUp() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();;
        // PacMan direction is to the left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved one pixel to the left
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        // We move 4 more pixels to the left to go to the next tile
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        // PacMan direction is to the left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved 4 pixels to the left
        assertEquals(x-5, pacman.getX());
        assertEquals(y, pacman.getY());
        // We turn around and go right
        pacman.setIntention(Direction.RIGHT);
        pacman.nextMove();;
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-4, pacman.getX());
        assertEquals(y, pacman.getY());
        // We give an intention to go up
        // The intention should be kept until we reach the next tile center
        // (the original x position)
        pacman.setIntention(Direction.UP);
        pacman.nextMove();;
        assertEquals(Direction.UP, pacman.getIntention());
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-3, pacman.getX());
        assertEquals(y, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.UP, pacman.getIntention());
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-2, pacman.getX());
        assertEquals(y, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.UP, pacman.getIntention());
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        // At the next move, pacman reaches the tile center and change its direction
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is Up
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved one pixel to the right (previous direction)
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // Next move : pacman moves up
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is Up
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved one pixel up
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
    }

    /**
     * We test an actual turn on pacman
     * Initially pacman goes some pixels left
     * We then ask pacman to turn around
     * Then we ask to go down and check that the intention is kept until reaching the tile center.
     * When reaching the tile center, pacman's direction changes to down
     * @throws PacManException
     */
    @Test
    public void testSetIntentionTurnDown() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();;
        // PacMan direction is to the left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved one pixel to the left
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        // We move 4 more pixels to the left to go to the next tile
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        // PacMan direction is to the left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved 4 pixels to the left
        assertEquals(x-5, pacman.getX());
        assertEquals(y, pacman.getY());
        // We turn around and go right
        pacman.setIntention(Direction.RIGHT);
        pacman.nextMove();;
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-4, pacman.getX());
        assertEquals(y, pacman.getY());
        // We give an intention to go down
        // The intention should be kept until we reach the next tile center
        // (the original x position)
        pacman.setIntention(Direction.DOWN);
        pacman.nextMove();;
        assertEquals(Direction.DOWN, pacman.getIntention());
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-3, pacman.getX());
        assertEquals(y, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.DOWN, pacman.getIntention());
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-2, pacman.getX());
        assertEquals(y, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.DOWN, pacman.getIntention());
        // PacMan direction is to the Right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel to the right
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        // At the next move, pacman reaches the tile center and change its direction
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel to the right (previous direction)
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // Next move : pacman moves down
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y+1, pacman.getY());
    }

    /**
     * We test an actual turn on pacman
     * Initially pacman goes some pixels up
     * We then ask pacman to turn around
     * Then we ask to go left and check that the intention is kept until reaching the tile center.
     * When reaching the tile center, pacman's direction changes to left
     * @throws PacManException
     */
    @Test
    public void testSetIntentionTurnLeft() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.UP); // We set the direction up before starting the actor
        testBoard.startActors();

        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();;
        // PacMan direction is up
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved one pixel up
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
        // We move 4 more pixels up to go to the next tile
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        // PacMan direction is ip
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved 4 pixels up
        assertEquals(x, pacman.getX());
        assertEquals(y-5, pacman.getY());
        // We turn around and go down
        pacman.setIntention(Direction.DOWN);
        pacman.nextMove();;
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-4, pacman.getY());
        // We give an intention to go left
        // The intention should be kept until we reach the next tile center
        // (the original y position)
        pacman.setIntention(Direction.LEFT);
        pacman.nextMove();;
        assertEquals(Direction.LEFT, pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-3, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.LEFT, pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-2, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.LEFT, pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
        // At the next move, pacman reaches the tile center and change its direction
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved one pixel down (previous direction)
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // Next move : pacman moves left
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is left
        assertEquals(Direction.LEFT, pacman.getDirection());
        // It has moved one pixel left
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
    }

    /**
     * We test an actual turn on pacman
     * Initially pacman goes some pixels up
     * We then ask pacman to turn around
     * Then we ask to go right and check that the intention is kept until reaching the tile center.
     * When reaching the tile center, pacman's direction changes to left
     * @throws PacManException
     */
    @Test
    public void testSetIntentionTurnRight() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.UP); // We set the direction up before starting the actor
        testBoard.startActors();

        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        pacman.nextMove();;
        // PacMan direction is up
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved one pixel up
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
        // We move 4 more pixels up to go to the next tile
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        pacman.nextMove();;
        // PacMan direction is ip
        assertEquals(Direction.UP, pacman.getDirection());
        // It has moved 4 pixels up
        assertEquals(x, pacman.getX());
        assertEquals(y-5, pacman.getY());
        // We turn around and go down
        pacman.setIntention(Direction.DOWN);
        pacman.nextMove();;
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-4, pacman.getY());
        // We give an intention to go right
        // The intention should be kept until we reach the next tile center
        // (the original y position)
        pacman.setIntention(Direction.RIGHT);
        pacman.nextMove();;
        assertEquals(Direction.RIGHT, pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-3, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.RIGHT, pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-2, pacman.getY());
        pacman.nextMove();;
        assertEquals(Direction.RIGHT, pacman.getIntention());
        // PacMan direction is down
        assertEquals(Direction.DOWN, pacman.getDirection());
        // It has moved one pixel down
        assertEquals(x, pacman.getX());
        assertEquals(y-1, pacman.getY());
        // At the next move, pacman reaches the tile center and change its direction
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel down (previous direction)
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // Next move : pacman moves right
        pacman.nextMove();;
        // The intention should be empty
        assertNull(pacman.getIntention());
        // PacMan direction is right
        assertEquals(Direction.RIGHT, pacman.getDirection());
        // It has moved one pixel right
        assertEquals(x+1, pacman.getX());
        assertEquals(y, pacman.getY());
    }

    /**
     * We test that if pacman reaches the end of the screen,
     * it arrives on the other side
     * @throws PacManException
     */
    @Test
    public void testMovesLeftCircularScreen() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        testBoard.startActors();
        Actor pacman = testBoard.getPacMan();
        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        // We move 4 tiles to the left
        for(int i = 0; i < 4*Maze.TILE_WIDTH; i ++) {
            pacman.nextMove();;
        }
        assertEquals(x - 4*Maze.TILE_WIDTH, pacman.getX());
        assertEquals(y, pacman.getY());
        // We are at the center of the leftmost tile
        assertEquals(0, pacman.getCurrentTile().getCol());
        x = pacman.getX();
        assertEquals(3, x);
        // We should still be able to move left
        pacman.nextMove();;
        assertEquals(2, pacman.getX());
        pacman.nextMove();;
        assertEquals(1, pacman.getX());
        pacman.nextMove();;
        assertEquals(0, pacman.getX());
        // When we move one more pixel to the left, we go to the right end of the board
        pacman.nextMove();;
        assertEquals(maze.getPixelWidth() - 1, pacman.getX());
        // the tile is the last tile of the line
        assertEquals(maze.getWidth() - 1, pacman.getCurrentTile().getCol());
    }

    /**
     * We test that if pacman reaches the end of the screen from the right,
     * it arrives on the other side
     * @throws PacManException
     */
    @Test
    public void testMovesRightCircularScreen() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.RIGHT);
        testBoard.startActors();
        Maze maze = testBoard.getMaze();
        int x = pacman.getX(); // x position at start
        int y = pacman.getY();
        // We move 4 tiles to the left
        for(int i = 0; i < 4*Maze.TILE_WIDTH; i ++) {
            pacman.nextMove();;
        }
        assertEquals(x + 4*Maze.TILE_WIDTH, pacman.getX());
        assertEquals(y, pacman.getY());
        // We are at the center of the rightmost tile
        assertEquals(maze.getWidth() - 1, pacman.getCurrentTile().getCol());
        x = pacman.getX();
        assertEquals(maze.getPixelWidth() - 5, x);
        // We should still be able to move right
        pacman.nextMove();;
        assertEquals(maze.getPixelWidth() - 4, pacman.getX());
        pacman.nextMove();;
        assertEquals(maze.getPixelWidth() - 3, pacman.getX());
        pacman.nextMove();;
        assertEquals(maze.getPixelWidth() - 2, pacman.getX());
        pacman.nextMove();;
        assertEquals(maze.getPixelWidth() - 1, pacman.getX());
        // When we move one more pixel to the right, we go to the left end of the board
        pacman.nextMove();;
        assertEquals(0, pacman.getX());
        // the tile is the last tile of the line
        assertEquals(0, pacman.getCurrentTile().getCol());
    }

    @Test
    public void testMoveAndStopAtWall() throws PacManException {
        Board testBoard = Board.createBoard(GameType.TEST);
        testBoard.initialize();
        Actor pacman = testBoard.getPacMan();
        pacman.setDirection(Direction.UP); // We set the direction up before starting the actor
        testBoard.startActors();
        assertFalse(pacman.isBlocked());
        Maze maze = testBoard.getMaze();
        // we move 6 tiles up
        for(int i =0; i < Maze.TILE_HEIGHT * 6; i++) {
            pacman.nextMove();;
        }
        int x = pacman.getX();
        int y = pacman.getY();
        assertFalse(pacman.isBlocked()); // not blocked yet
        // On the next move, we are stuck because of the wall
        pacman.nextMove();;
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        assertTrue(pacman.isBlocked());
        // still stuck
        pacman.nextMove();;
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        assertTrue(pacman.isBlocked());
        // We ask pacman to go up, and it's still stuck
        pacman.setIntention(Direction.UP);
        pacman.nextMove();;
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        assertTrue(pacman.isBlocked());
        // Now let's ask him to go down and unstuck
        pacman.setIntention(Direction.DOWN);
        pacman.nextMove();;
        assertEquals(x, pacman.getX());
        assertEquals(y+1, pacman.getY());
        assertFalse(pacman.isBlocked());
        // Let's ask him to go up again
        pacman.setIntention(Direction.UP);
        pacman.nextMove();;
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        // Now we're again stuck
        pacman.nextMove();;
        assertEquals(x, pacman.getX());
        assertEquals(y, pacman.getY());
        assertTrue(pacman.isBlocked());
        // Let's ask him to go left -- that should unstuck him
        pacman.setIntention(Direction.LEFT);
        pacman.nextMove();;
        assertEquals(x-1, pacman.getX());
        assertEquals(y, pacman.getY());
        assertFalse(pacman.isBlocked());
        // Now let's move to the left wall and get stuck again
        for(int i = 0; i < Maze.TILE_WIDTH; i++) {
            pacman.nextMove();;
        }
        assertEquals(x-8, pacman.getX());
        assertEquals(y, pacman.getY());
        assertTrue(pacman.isBlocked());
        // If we tell pacman to g right, it gets unstuck
        pacman.setIntention(Direction.RIGHT);
        pacman.nextMove();;
        assertEquals(x-7, pacman.getX());
        assertEquals(y, pacman.getY());
        assertFalse(pacman.isBlocked());
    }
}
