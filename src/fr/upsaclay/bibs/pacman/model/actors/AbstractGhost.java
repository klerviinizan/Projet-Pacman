package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.board.Counter;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.Tile;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public abstract class AbstractGhost extends AbstractActor implements Ghost {

    protected GhostState state;
    protected GhostPenState penState = GhostPenState.OUT;
    protected Direction outOfPenDirection;
    protected int elroy = 0;
    protected Counter dotCounter;
    private double moveAccumulator = 0.5;

    public AbstractGhost(Board board, int x, int y) {
        super(board, x, y);
        this.state =  GhostState.SCATTER;
        this.dotCounter = new Counter(0);
    }

    @Override
    public ActorType getType() {
        return ActorType.GHOST;
    }

    @Override
    public void nextFrame() {
        moveAccumulator += getSpeed();
        if (moveAccumulator >= 1.0) {
            moveAccumulator -= 1.0;
            nextMove();
        }
    }

    @Override
    public void nextMove() {

        // Get maze
        Maze maze = getBoard().getMaze();

        // If ghost is at the door, go inside
        if (this.penState == GhostPenState.GET_IN) {
            int targetX = getBoard().penGhostXPosition(getGhostType());
            int targetY = getBoard().penGhostYPosition(getGhostType());
            int dx = Integer.signum(targetX - getX());
            int dy = Integer.signum(targetY - getY());
            if (dx != 0) setPosition(getX() + dx, getY());
            else if (dy != 0) setPosition(getX(), getY() + dy);
            if (getX() == targetX && getY() == targetY) {
                setGhostPenState(GhostPenState.IN);
                this.moveAccumulator = 1.0;
            }
            return;
        }

        // If ghost is inside pen and dead, leave (scatter)
        if (this.penState == GhostPenState.IN) {

            // Dead ghosts revive and leave
            if (this.state == GhostState.DEAD) {
                setGhostPenState(GhostPenState.GET_OUT);
                setGhostState(GhostState.SCATTER);
                return;
            }

            // Otherwise, they wait in the pen
            this.moveAccumulator = 1.0;
            return;
        }

        // If ghost pen state is leaving, set direction and intention for the door
        if (this.penState == GhostPenState.GET_OUT) {
            int targetX = getBoard().outPenXPosition();
            int targetY = getBoard().outPenYPosition();
            int dx = Integer.signum(targetX - getX());
            int dy = Integer.signum(targetY - getY());
            if (dx != 0) setPosition(getX() + dx, getY());
            else if (dy != 0) setPosition(getX(), getY() + dy);
            if (getX() == targetX && getY() == targetY) {
                setGhostPenState(GhostPenState.OUT);
                setDirection(this.outOfPenDirection);
                setIntention(this.outOfPenDirection);
            }
            return;
        }

       // Fail safe
        if (getDirection() == null) return;

        // Move 1 pixel
        if (!isBlocked()) {
            if (getDirection() == null) return;
            int next_x = getX() + getDirection().getDx();
            int next_y = getY() + getDirection().getDy();
            setPosition(next_x, next_y);
        }

        // Handle wrap around logic for side tunnels
        if (getX() < 0) {
            setPosition(maze.getPixelWidth() - 1, getY());
        } else if (getX() >= maze.getPixelWidth()) {
            setPosition(0, getY());
        }

        // Get current ghost pixel positions
        int currentX = getX();
        int currentY = getY();

        // Test if ghosts is at the center pixel of tile
        boolean at_center = (currentX % Maze.TILE_WIDTH == Maze.TILE_CENTER_X
                && currentY % Maze.TILE_HEIGHT == Maze.TILE_CENTER_Y);

        // If yes, do the following
        if (at_center) {

            // Set direction to intention
            if (getIntention() != null) {
                setDirection(getIntention());
            }

            // If ghost is dead and at the door, put them in
            if (this.state == GhostState.DEAD && this.penState == GhostPenState.OUT) {
                TilePosition penEntry = getBoard().penEntry();
                if (getCurrentTile().equals(penEntry)) {
                    setGhostPenState(GhostPenState.GET_IN);
                    setPosition(getBoard().outPenXPosition(), getBoard().outPenYPosition());
                    setIntention(Direction.DOWN);
                    return;
                }
            }

            // Create current, next, and target tiles
            TilePosition current_tile_position = getCurrentTile();
            TilePosition next_tile_position = maze.getNeighbourTilePosition(current_tile_position, getDirection());
            TilePosition target_tile_position = getTarget();

            // Initialize direction variables
            Direction best_direction = null;
            int shortest_square_distance = Integer.MAX_VALUE;
            Direction[] directions = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};

            // Find best direction
            for (Direction test_direction : directions) {

                // Exception for tiles for ghosts who cannot go up
                if (test_direction == Direction.UP) {
                    Tile deciding_tile = maze.getTile(next_tile_position.getLine(), next_tile_position.getCol());
                    if (deciding_tile != null && !deciding_tile.ghostCanGoUp()) {
                        continue;
                    }
                }

                // Calculate candidate tile
                Tile candidate_tile = maze.getNeighbourTile(next_tile_position, test_direction);
                TilePosition candidate_tile_position = maze.getNeighbourTilePosition(next_tile_position, test_direction);

                // Calculate best direction
                if (test_direction != getDirection().reverse() && candidate_tile != null && !candidate_tile.isWall()) {
                    if (this.state == GhostState.FRIGHTENED || this.state == GhostState.FRIGHTENED_END) {
                        if (best_direction == null) {
                            best_direction = test_direction;
                        }
                    }
                    else if (target_tile_position != null) {
                        int dx = target_tile_position.getCol() - candidate_tile_position.getCol();
                        int dy = target_tile_position.getLine() - candidate_tile_position.getLine();
                        int square_distance = (dx * dx) + (dy * dy);
                        if (square_distance < shortest_square_distance) {
                            shortest_square_distance = square_distance;
                            best_direction = test_direction;
                        }
                    }
                }
            }

            // Set intention to best direction
            setIntention(best_direction);
        }
    }

    @Override
    public void setGhostState(GhostState state) {
        this.state = state;
    }

    @Override
    public void changeGhostState(GhostState state) {
        GhostState oldState = this.state;
        this.state = state;
        if (state == GhostState.FRIGHTENED) {
            this.reverseDirectionIntention();
        }
        else if ((state == GhostState.CHASE || state == GhostState.SCATTER)
                && (oldState != GhostState.FRIGHTENED && oldState != GhostState.FRIGHTENED_END)) {
            this.reverseDirectionIntention();
        }
    }

    @Override
    public GhostState getGhostState() {
        return this.state;
    }

    @Override
    public void setGhostPenState(GhostPenState state) {
        this.penState = state;
    }

    @Override
    public GhostPenState getGhostPenState() {
        return this.penState;
    }

    @Override
    public void setOutOfPenDirection(Direction dir) {
        this.outOfPenDirection = dir;
    }

    @Override
    public Direction getOutOfPenDirection() {
        return this.outOfPenDirection;
    }

    @Override
    public void reverseDirectionIntention() {
        if (this.penState == GhostPenState.OUT) {
            if (getDirection() != null) {
                setIntention(getDirection().reverse());
            }
        }
        else {
            this.outOfPenDirection = Direction.RIGHT;
        }
    }

    @Override
    public boolean hasDotCounter() {
        if (this.getGhostType() != GhostType.BLINKY) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Counter getDotCounter() {
        if (this.hasDotCounter()) {
            return this.dotCounter;
        }
        else {
            return null;
        }
    }

    @Override
    public void setElroy(int elroy) {
        this.elroy = elroy;
    }

    @Override
    public int getElroy() {
        return this.elroy;
    }


}
