package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.Tile;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public class PacMan extends AbstractActor{

    private double speed=1.0;


    public PacMan(Board board, int x, int y) {
        super(board, x, y);
        setSpeed(board.getLevelPacManSpeed());
    }

    @Override
    public void nextMove() {
        Maze maze = getBoard().getMaze();

        // Calculate x and y coordinates of current position
        double currentX = getRealX();
        double currentY = getRealY();

        // Define at_center boolean to test if PacMan is at the center of the tile
        boolean at_center = (currentX % Maze.TILE_WIDTH == Maze.TILE_CENTER_X
                && currentY % Maze.TILE_HEIGHT == Maze.TILE_CENTER_Y);

        // U-turns and cornering
        if (getIntention() != null) {

            // Allow u-turns anywhere
            if (getIntention() == super.getDirection().reverse()) {
                this.setDirection(getIntention());
                this.setIntention(null);
                super.blocked = false;
            }

            // Allow cornering only when Pac-Man is at the center of his tile
            else if (at_center) {
                Tile intendedTile = maze.getNeighbourTile(getCurrentTile(), getIntention());
                if (intendedTile != null && !intendedTile.isWall()) {
                    this.setDirection(getIntention());
                    super.blocked = false;
                }
                this.setIntention(null);
            }
        }

        // Stop at center if PacMan reaches a wall
        if (at_center) {
            Tile nextTile = maze.getNeighbourTile(getCurrentTile(), getDirection());
            if (nextTile == null || nextTile.isWall()) {
                this.blocked = true;
                super.realX = getRealX();
                super.realY = getRealY();
                return;
            }
        }

        // Try to move if not blocked
        super.blocked = false;
        super.realX = super.realX + getDirection().getDx() * super.speed;
        super.realY = super.realY + getDirection().getDy() * super.speed;

        // Calculate center of current tile
        double centerX = (getCurrentTile().getCol() * Maze.TILE_WIDTH) + Maze.TILE_CENTER_X;
        double centerY = (getCurrentTile().getLine() * Maze.TILE_HEIGHT) + Maze.TILE_CENTER_Y;

        // Check if we moved across center of tile (anti-tunneling patch)
        boolean crossedCenter = false;
        if (getDirection() == Direction.RIGHT && currentX < centerX && super.realX >= centerX) {
            crossedCenter = true;
        }
        if (getDirection() == Direction.LEFT && currentX > centerX && super.realX <= centerX) {
            crossedCenter = true;
        }
        if (getDirection() == Direction.DOWN && currentY < centerY && super.realY >= centerY) {
            crossedCenter = true;
        }
        if (getDirection() == Direction.UP && currentY > centerY && super.realY <= centerY) {
            crossedCenter = true;
        }

        // If PacMan crosses center of tile during this frame, snap to center
        if (crossedCenter) {
            super.realX = centerX;
            super.realY = centerY;
        }

        this.setPosition((int)super.realX, (int)super.realY);

        // Handle tunnel wrapping
        if (this.realX < 0) {
            super.realX = maze.getPixelWidth() - 1;
            this.setPosition((int)super.realX, getY());
        }
        else if (this.realX >= maze.getPixelWidth()) {
            super.realX = 0;
            this.setPosition((int)super.realX, getY());
        }

        // Re-evaluate center for intentions after moving/snapping
        at_center = (getX() % Maze.TILE_WIDTH == Maze.TILE_CENTER_X
                && getY() % Maze.TILE_HEIGHT == Maze.TILE_CENTER_Y);

        if (at_center) {
            if (getIntention() != null) {
                Tile intendedTile = maze.getNeighbourTile(getCurrentTile(), getIntention());
                if (intendedTile != null && !intendedTile.isWall()) {
                    this.setDirection(getIntention());
                    super.blocked = false;
                }
                this.setIntention(null);
            }
        }
    }

}
