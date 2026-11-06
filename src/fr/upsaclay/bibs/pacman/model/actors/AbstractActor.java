package fr.upsaclay.bibs.pacman.model.actors;

import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.Tile;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

public abstract class AbstractActor implements Actor {

    private Board board;
    private int x;
    private int y;
    double realX; // Position précise
    double realY;
    private Direction direction;
    private Direction intention;
    double speed=1.0;
    protected boolean blocked = false;
    private int stopTime = 0;
    private int score=0;

    public AbstractActor(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.direction = Direction.LEFT;
        this.intention = null;
        this.setPosition(x, y);
    }

    @Override
    public Board getBoard() { return this.board; }

    @Override
    public ActorType getType() { return ActorType.PACMAN; }

    @Override
    public int getX() { return this.x; }

    @Override
    public int getY() { return this.y; }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if ((realX % 1) == 0) {
            this.realX = x;
        }
        if ((realY % 1) == 0) {
            this.realY = y;
        }
    }

    @Override
    public TilePosition getCurrentTile() {
        // Calcul de la tuile basé sur les pixels
        return new TilePosition(y / Maze.TILE_HEIGHT, x / Maze.TILE_WIDTH);
    }

    @Override
    public void start() {
        this.direction = Direction.LEFT;
        this.intention = null;
        this.blocked = false;
    }

    @Override
    public Direction getDirection() { return this.direction; }

    @Override
    public void setDirection(Direction direction) { this.direction = direction; }

    @Override
    public void setIntention(Direction direction) { this.intention = direction; }

    @Override
    public Direction getIntention() { return this.intention; }

    @Override
    public boolean isBlocked() { return this.blocked; }

    @Override
    public void nextFrame() {
        if (stopTime > 0) {
            stopTime--;
        } else {
            this.nextMove();
        }
    }

    @Override
    public void setSpeed(double speed) { this.speed = speed; }

    @Override
    public double getSpeed() { return this.speed; }

    @Override
    public double getRealX() { return this.realX; }

    @Override
    public double getRealY() { return this.realY;}

    @Override
    public void setStopTime(int nbFrames) {
        this.stopTime = nbFrames;
    }
}