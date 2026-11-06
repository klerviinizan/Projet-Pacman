package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public class Pinky extends AbstractGhost{

    public Pinky(Board board, int x, int y) {
        super(board, x, y);
        setDirection(Direction.UP);
        setSpeed(board.getTunnelGhostSpeed());
        setGhostPenState(GhostPenState.IN);
    }

    @Override
    public GhostType getGhostType() {
        return GhostType.PINKY;
    }

    @Override
    public TilePosition getTarget() {
        TilePosition scatterTarget = new TilePosition(0, 2);
        if (getGhostState() == GhostState.CHASE) {
            Actor pacMan = getBoard().getPacMan();
            Direction pacManDirection = pacMan.getDirection();
            TilePosition pacManTile = pacMan.getCurrentTile();

            int chaseTargetLine = pacManTile.getLine() + (pacManDirection.getDy() * 4);
            int chaseTargetCol  = pacManTile.getCol()  + (pacManDirection.getDx() * 4);

            if (pacManDirection == Direction.UP) {
                chaseTargetCol = chaseTargetCol - 4;
            }

            TilePosition chaseTarget = new TilePosition(chaseTargetLine, chaseTargetCol);

            return chaseTarget;
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
