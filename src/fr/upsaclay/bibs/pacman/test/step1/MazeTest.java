package fr.upsaclay.bibs.pacman.test.step1;

import static org.junit.jupiter.api.Assertions.*;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.Tile;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class MazeTest {

    @Test
    public void testEmptyMaze() {
        /** this only test that the creation goes without errors **/
        Maze maze = Maze.emptyMaze(10,10);
    }

    @Test
    public void testWidthHeight() {
        Maze maze = Maze.emptyMaze(10,15);
        assertEquals(maze.getWidth(), 10);
        assertEquals(maze.getHeight(), 15);
    }

    @Test
    public void testGetPixelWidthHeight() {
        Maze maze = Maze.emptyMaze(10,15);
        assertEquals(maze.getPixelWidth(), Maze.TILE_WIDTH * maze.getWidth());
        assertEquals(maze.getPixelHeight(), Maze.TILE_HEIGHT * maze.getHeight());
    }

    @Test
    public void testSetGetTile() {
        Maze maze = Maze.emptyMaze(10,15);
        /** by default, all tile of empty maze should be empty **/
        assertEquals(Tile.EE, maze.getTile(0,0));
        /* set / get using line and col */
        maze.setTile(3,4, Tile.WB);
        assertEquals(Tile.WB, maze.getTile(3,4));
        /* set / get using TilePosition */
        TilePosition pos = new TilePosition(3,4);
        maze.setTile(pos, Tile.WL);
        assertEquals(Tile.WL, maze.getTile(pos));
    }

    @Test
    public void testGetNeighbourTilePosition() {
        Maze maze = Maze.emptyMaze(10,15);

        /** Testing directions inside the grid **/
        assertEquals(new TilePosition(0,1), maze.getNeighbourTilePosition(0,0, Direction.RIGHT));
        assertEquals(new TilePosition(0,1), maze.getNeighbourTilePosition(new TilePosition(0,0), Direction.RIGHT));
        assertEquals(new TilePosition(1,0), maze.getNeighbourTilePosition(0,0, Direction.DOWN));
        assertEquals(new TilePosition(1,0), maze.getNeighbourTilePosition(new TilePosition(0,0), Direction.DOWN));
        assertEquals(new TilePosition(0,1), maze.getNeighbourTilePosition(1,1, Direction.UP));
        assertEquals(new TilePosition(0,1), maze.getNeighbourTilePosition(new TilePosition(1,1), Direction.UP));
        assertEquals(new TilePosition(1,0), maze.getNeighbourTilePosition(1,1, Direction.LEFT));
        assertEquals(new TilePosition(1,0), maze.getNeighbourTilePosition(new TilePosition(1,1), Direction.LEFT));
        /** testing with circularity  when you get out of the grid **/
        assertEquals(new TilePosition(0,9), maze.getNeighbourTilePosition(0,0, Direction.LEFT));
        assertEquals(new TilePosition(14,0), maze.getNeighbourTilePosition(0,0, Direction.UP));
        assertEquals(new TilePosition(0,0), maze.getNeighbourTilePosition(0,9, Direction.RIGHT));
        assertEquals(new TilePosition(0,0), maze.getNeighbourTilePosition(14,0, Direction.DOWN));
    }

    @Test
    public void testGetNeighbourTile() {
        Maze maze = Maze.emptyMaze(10,15);
        maze.setTile(0,0, Tile.SD);
        maze.setTile(0, 1, Tile.WR);
        maze.setTile(0, 9, Tile.WL);
        maze.setTile(1, 0, Tile.WB);
        maze.setTile(14,0, Tile.WT);
        /** Testing directions inside the grid **/
        assertEquals(Tile.WR, maze.getNeighbourTile(0,0, Direction.RIGHT));
        assertEquals(Tile.WR, maze.getNeighbourTile(new TilePosition(0,0), Direction.RIGHT));
        assertEquals(Tile.WB, maze.getNeighbourTile(0,0, Direction.DOWN));
        assertEquals(Tile.WB, maze.getNeighbourTile(new TilePosition(0,0), Direction.DOWN));
        assertEquals(Tile.WR, maze.getNeighbourTile(1,1, Direction.UP));
        assertEquals(Tile.WR, maze.getNeighbourTile(new TilePosition(1,1), Direction.UP));
        assertEquals(Tile.WB, maze.getNeighbourTile(1,1, Direction.LEFT));
        assertEquals(Tile.WB, maze.getNeighbourTile(new TilePosition(1,1), Direction.LEFT));
        /** testing with circularity  when you get out of the grid **/
        assertEquals(Tile.WL, maze.getNeighbourTile(0,0, Direction.LEFT));
        assertEquals(Tile.WT, maze.getNeighbourTile(0,0, Direction.UP));
        assertEquals(Tile.SD, maze.getNeighbourTile(0,9, Direction.RIGHT));
        assertEquals(Tile.SD, maze.getNeighbourTile(14,0, Direction.DOWN));
    }

    @Test
    public void testGetTilePosition() {
        Maze maze = Maze.emptyMaze(10,15);
        assertEquals(new TilePosition(0,0), maze.getTilePosition(0,0));
        assertEquals(new TilePosition(0,0), maze.getTilePosition(1,1));
        assertEquals(new TilePosition(2,3), maze.getTilePosition(3*Maze.TILE_WIDTH + 2, 2*Maze.TILE_HEIGHT));
        assertEquals(new TilePosition(14,9), maze.getTilePosition(10*Maze.TILE_WIDTH - 1, 15*Maze.TILE_HEIGHT - 1));
    }

    @Test
    public void testLoadFromFile1() throws FileNotFoundException {
        Maze testMaze = Maze.loadFromFile("resources/test.txt");
        assertEquals(testMaze.getWidth(), 9);
        assertEquals(testMaze.getHeight(), 17);
        assertEquals(testMaze.getTile(0,0), Tile.EE);
        assertEquals(testMaze.getTile(2,3), Tile.WT);
        assertEquals(testMaze.getTile(2,4), Tile.WT);
        assertEquals(testMaze.getTile(2,5), Tile.WT);
        assertEquals(testMaze.getTile(3,3), Tile.SD);
        assertEquals(testMaze.getTile(3,4), Tile.SD);
        assertEquals(testMaze.getTile(3,6), Tile.WR);
        assertEquals(testMaze.getTile(4,2), Tile.C7);
        assertEquals(testMaze.getTile(4,4), Tile.BD);
        assertEquals(testMaze.getTile(4,6), Tile.C8);
        assertTrue(testMaze.getTile(3,4).hasDot());
        assertFalse(testMaze.getTile(3,4).hasBigDot());
        assertTrue(testMaze.getTile(4,4).hasDot());
        assertTrue(testMaze.getTile(4,4).hasBigDot());
    }

    @Test
    public void testLoadFromFile2() throws FileNotFoundException {
        Maze maze = Maze.loadFromFile("resources/maze.txt");
        assertEquals(maze.getWidth(), 28);
        assertEquals(maze.getHeight(), 36);
        assertEquals(maze.getTile(0,0), Tile.EE);
        assertEquals(maze.getTile(3,0), Tile.D1);
        for(int j =0; j < 28; j++) {
            assertTrue(maze.getTile(3,j).isWall());
        }
        assertTrue(maze.getTile(4,0).isWall());
        assertTrue(maze.getTile(4,27).isWall());
        assertTrue(maze.getTile(4,13).isWall());
        assertTrue(maze.getTile(4,14).isWall());
        for(int j =1; j < 13; j++) {
            assertFalse(maze.getTile(4,j).isWall());
            assertTrue(maze.getTile(4,j).hasDot());
        }
        for(int j =15; j < 27; j++) {
            assertFalse(maze.getTile(4,j).isWall());
            assertTrue(maze.getTile(4,j).hasDot());
        }
        assertEquals(maze.getTile(15,13), Tile.GD);
        assertEquals(maze.getTile(14,13), Tile.EE);
        assertFalse(maze.getTile(14,13).hasDot());
        assertFalse(maze.getTile(14,12).hasDot());
        assertFalse(maze.getTile(14,12).isWall());
        assertTrue(maze.getTile(26,12).hasDot());
        assertFalse(maze.getTile(26,12).isWall());
        assertEquals(maze.getTile(6,1), Tile.BD);
        assertTrue(maze.getTile(6,1).hasDot());
        assertTrue(maze.getTile(6,1).hasBigDot());
    }
}
