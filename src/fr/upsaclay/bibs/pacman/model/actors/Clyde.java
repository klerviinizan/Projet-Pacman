package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.board.Counter;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public class Clyde extends  AbstractGhost {

    public Clyde(Board board, int x, int y) {
        super(board, x, y); // Clyde's starting coordinates
        setDirection(Direction.UP); // Bouncing up and down in the pen
        setSpeed(board.getTunnelGhostSpeed());
        setGhostPenState(GhostPenState.IN); // Trapped in the pen
        this.dotCounter = new Counter(60);
    }

    @Override
    public GhostType getGhostType() {
        return GhostType.CLYDE;
    }

    @Override
    public TilePosition getTarget() {
        TilePosition scatterTarget = new TilePosition(34, 0);
        if (getGhostState() == GhostState.CHASE) {
            Actor pacMan = getBoard().getPacMan();
            TilePosition pacManTile = pacMan.getCurrentTile();
            TilePosition clydeTile = this.getCurrentTile();

            int dx = pacManTile.getCol() - clydeTile.getCol();
            int dy  = pacManTile.getLine() - clydeTile.getLine();

            double distance = Math.sqrt((dx * dx) + (dy * dy));

            if (distance > 8) {
                return pacManTile;
            }
            else {
                return scatterTarget;
            }
        }
        else if (getGhostState() == GhostState.SCATTER) {
            return scatterTarget;
        }
        else if (getGhostState() == GhostState.FRIGHTENED || getGhostState() == GhostState.FRIGHTENED_END) {
            return null;
        }
        else if (getGhostState() == GhostState.DEAD) {
            return getBoard().penEntry();
        }
        else {
            return scatterTarget;
        }
    }
}
