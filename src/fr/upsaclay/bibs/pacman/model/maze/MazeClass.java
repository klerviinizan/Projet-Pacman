package fr.upsaclay.bibs.pacman.model.maze;

import fr.upsaclay.bibs.pacman.model.Direction;

public class MazeClass implements Maze{

    private Tile[][] tiles;
    private int width;
    private int height;

    public MazeClass(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];

        for(int i=0;i<this.width;i++){
            for (int j=0;j<this.height;j++){
                setTile(j,i,Tile.EE);
            }
        }
    }

    /**
     * Return the maze width
     * i.e. the number of tiles horizontally
     *
     * @return the width
     */
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * Return the width in number of pixels
     *
     * @return the number of horizontal pixels
     */
    @Override
    public int getPixelWidth() {
        return TILE_WIDTH*this.width;
    }

    /**
     * Return the maze height
     * i.e. the number of tiles vertically
     *
     * @return the number of vertical pixels
     */
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * Return the maze height in number of pixels
     * @return the pixel height
     */
    @Override
    public int getPixelHeight() {
        return TILE_HEIGHT*this.height;
    }

    /**
     * Put a given tile at the position
     *
     * @param line
     * @param col
     * @param tile
     */
    @Override
    public void setTile(int line, int col, Tile tile) {
        this.tiles[col][line] = tile;
    }

    /**
     * Put a given tile at the position
     *
     * @param pos
     * @param tile
     */
    @Override
    public void setTile(TilePosition pos, Tile tile) {
        this.tiles[pos.getCol()][pos.getLine()] = tile;
    }

    /**
     * Get the tile at a given position
     *
     * @param pos@return the corresponding tile
     */
    @Override
    public Tile getTile(TilePosition pos) {
        return this.tiles[pos.getCol()][pos.getLine()];
    }

    /**
     * Get the tile at given position
     *
     * @param line
     * @param col
     * @return the corresponding tile
     */
    @Override
    public Tile getTile(int line, int col) {
        return this.tiles[col][line];
    }

    /**
     * Return the next tile from a position in
     * a given direction
     *
     * @param line
     * @param col
     * @param dir
     * @return the corresponding tile
     */
    @Override
    public TilePosition getNeighbourTilePosition(int line, int col, Direction dir) {
        switch (dir) {
            case UP    -> line = line - 1 + this.height;
            case DOWN  -> line++;
            case LEFT  -> col = col - 1 + this.width;
            case RIGHT -> col++;
        }

        /* Circularity of the maze */
        line = line % this.height;
        col = col % this.width;

        return new TilePosition(line, col);
    }


    /**
     * Return the next tile from a position in
     * a given direction
     *
     * @param pos
     * @param dir
     * @return the corresponding tile
     */
    @Override
    public TilePosition getNeighbourTilePosition(TilePosition pos, Direction dir) {
        int line=pos.getLine();
        int col =pos.getCol();

        switch (dir) {
            case UP    -> line = line - 1 + this.height;
            case DOWN  -> line++;
            case LEFT  -> col = col - 1 + this.width;
            case RIGHT -> col++;
        }

        /* Circularity of the maze */
        line = line % this.height;
        col = col % this.width;

        return new TilePosition(line, col);
    }

    /**
     * Return the next tile from a position in
     * a given direction
     *
     * @param line
     * @param col
     * @param dir
     * @return the corresponding tile
     */
    @Override
    public Tile getNeighbourTile(int line, int col, Direction dir) {
        switch (dir) {
            case UP    -> line = line - 1 + this.height;
            case DOWN  -> line++;
            case LEFT  -> col = col - 1 + this.width;
            case RIGHT -> col++;
        }

        /* Circularity of the maze */
        line = line % this.height;
        col = col % this.width;

        return this.tiles[col][line];
    }

    /**
     * Return the next tile from a position in
     * a given direction
     *
     * @param pos
     * @param dir
     * @return the corresponding tile
     */
    @Override
    public Tile getNeighbourTile(TilePosition pos, Direction dir) {
        int line = pos.getLine();
        int col = pos.getCol();

        switch (dir) {
            case UP    -> line = line - 1 + this.height;
            case DOWN  -> line++;
            case LEFT  -> col = col - 1 + this.width;
            case RIGHT -> col++;
        }

        /* Circularity of the maze */
        line = line % this.height;
        col = col % this.width;

        return this.tiles[col][line];
    }

    /**
     * Return the tile position of a given pixel
     *
     * @param x
     * @param y
     * @return a tile position
     */
    @Override
    public TilePosition getTilePosition(int x, int y) {
        int line = y / TILE_HEIGHT;
        int col = x / TILE_WIDTH;

        return new TilePosition(line, col);
    }

    /**
     * Return the current number of dots on the maze (big and small)
     *
     * @return the number of dots
     */
    @Override
    public int getNumberOfDots() {
        int dots = 0;
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if ((getTile(j, i).hasDot())) {
                    dots ++;
                }
            }
        }
        return dots;
    }
}
