package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.board.Counter;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public class Inky extends AbstractGhost {

    public Inky(Board board, int x, int y) {
        super(board, x, y);
        setDirection(Direction.UP);
        setSpeed(board.getTunnelGhostSpeed());
        setGhostPenState(GhostPenState.IN);
        this.dotCounter = new Counter(30);
    }

    @Override
    public GhostType getGhostType() {
        return GhostType.INKY;
    }

    @Override
    public TilePosition getTarget() {
        TilePosition scatterTarget = new TilePosition(34, 27);
        if (getGhostState() == GhostState.CHASE) {
            Actor pacMan = getBoard().getPacMan();
            Direction pacManDirection = pacMan.getDirection();
            TilePosition pacManTile = pacMan.getCurrentTile();

            Ghost blinky = getBoard().getGhost(GhostType.BLINKY);
            TilePosition blinkyTile = blinky.getCurrentTile();

            int pacManTileAheadLine = pacManTile.getLine() + (pacManDirection.getDy() * 2);
            int pacManTileAheadCol = pacManTile.getCol()  + (pacManDirection.getDx() * 2);

            if (pacManDirection == Direction.UP) {
                pacManTileAheadCol = pacManTileAheadCol -2;
            }

            int vectorLine = pacManTileAheadLine - blinkyTile.getLine();
            int vectorCol  = pacManTileAheadCol - blinkyTile.getCol();

            int chaseTargetLine = pacManTileAheadLine + vectorLine;
            int chaseTargetCol  = pacManTileAheadCol + vectorCol;
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
