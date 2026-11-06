package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.board.Counter;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public class Blinky extends AbstractGhost {

    GhostState ghostState=getGhostState();

    public Blinky(Board board, int x, int y) {
        super(board, x, y);
        setDirection(Direction.LEFT);
        setSpeed(board.getLevelGhostSpeed());
    }

    @Override
    public GhostType getGhostType() {
        return GhostType.BLINKY;
    }

    @Override
    public TilePosition getTarget() {
        return switch (this.state) {

            case CHASE -> getBoard().getPacMan().getCurrentTile();
            case SCATTER -> new TilePosition(0, 25);
            case FRIGHTENED, FRIGHTENED_END -> null;
            case DEAD -> getBoard().penEntry();
        };
    }
}
