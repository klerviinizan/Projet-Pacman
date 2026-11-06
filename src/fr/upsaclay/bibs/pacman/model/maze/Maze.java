package fr.upsaclay.bibs.pacman.model.maze;


import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.maze.MazeClass;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The interface corresponding to the maze where PacMan evolves
 * It is made by a grid of tiles,
 * each tile corresponding to a certain number of pixels
 *
 * @author Viviane Pons
 */
public interface Maze {

    /* tile default width in pixels */
    int TILE_WIDTH = 8;

    /* tile default height in pixels */
    int TILE_HEIGHT = 8;

    /* The x position of the central pixel inside a tile */
    int TILE_CENTER_X = 3;

    /* The y position of the central pixel inside a tile */
    int TILE_CENTER_Y = 3;

    /**
     * Return the maze width
     * i.e. the number of tiles horizontally
     * @return the width
     */
    int getWidth();

    /**
     * Return the width in number of pixels
     * @return the number of horizontal pixels
     */
    int getPixelWidth();

    /**
     * Return the maze height
     * i.e. the number of tiles vertically
     * @return the number of vertical pixels
     */
    int getHeight();

    /**
     * Return the maze height in number of pixels
     * @return the pixel height
     */
    int getPixelHeight();

    /**
     * Get the tile at given position
     * @param line, the line number
     * @param col, the col number
     * @return the corresponding tile
     */
    Tile getTile(int line, int col);

    /**
     * Get the tile at a given position
     * @param pos, a tile position
     * @return the corresponding tile
     */
    Tile getTile(TilePosition pos);

    /**
     * Return the next tile from a position in
     * a given direction
     * @param line, the line number
     * @param col, the col number
     * @param dir, the direction
     * @return the corresponding tile
     */
    TilePosition getNeighbourTilePosition(int line, int col, Direction dir);

    /**
     * Return the next tile from a position in
     * a given direction
     * @param pos, a tile position
     * @param dir, a direction
     * @return the corresponding tile
     */
    TilePosition getNeighbourTilePosition(TilePosition pos, Direction dir);

    /**
     * Return the next tile from a position in
     * a given direction
     * @param line, the line number
     * @param col, the col number
     * @param dir, the direction
     * @return the corresponding tile
     */
    Tile getNeighbourTile(int line, int col, Direction dir);

    /**
     * Return the next tile from a position in
     * a given direction
     * @param pos, a tile position
     * @param dir, a direction
     * @return the corresponding tile
     */
    Tile getNeighbourTile(TilePosition pos, Direction dir);

    /**
     * Put a given tile at the position
     * @param line, the line number
     * @param col, the col number
     * @param tile, the tile
     */
    void setTile(int line, int col, Tile tile);

    /**
     * Put a given tile at the position
     * @param pos, the tile position
     * @param tile, the tile
     */
    void setTile(TilePosition pos, Tile tile);

    /**
     * Return the tile position of a given pixel
     * @param x, the x coordinate of the pixel
     * @param y, the y coordinate of the pixel
     * @return a tile position
     */
    TilePosition getTilePosition(int x, int y);

    /**
     * Create an empty Maze
     * Create a maze with given dimension and only empty tiles
     *
     * @param width  the number of tiles horizontally
     * @param height the number of tiles vertically
     * @return an empty PacMan Maze
     */
    static Maze emptyMaze(int width, int height) {
        return new MazeClass(width, height);
    }

    /**
     * Load a Maze from a file
     * @param fileName the file name
     * @return a PacMan Maze
     */
    static Maze loadFromFile(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
        Scanner scan = new Scanner(file);

        Scanner data = new Scanner(scan.nextLine());
        int width = data.nextInt();
        int height = data.nextInt();

        Maze maze = new MazeClass(width,height);

        for (int i = 0; i<height; i++) {
            data = new Scanner(scan.nextLine());
            for (int j = 0; j<width; j++) {
                String word = data.next();
                maze.setTile(i, j, Tile.valueOf(word));
            }
        }

        return maze;
    }

    // Step 2
    // The methods below won't be used / tested before step 2

    /**
     * Return the current number of dots on the maze (big and small)
     * @return the number of dots
     */
    int getNumberOfDots();
}
