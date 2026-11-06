package fr.upsaclay.bibs.pacman.model.board;

import fr.upsaclay.bibs.pacman.GameType;
import fr.upsaclay.bibs.pacman.PacManException;
import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.actors.*;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.model.maze.Tile;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractBoard implements Board {

    protected Maze maze;
    protected Actor pacMan;
    protected GameType type;
    private BoardState state; // L'état interne du jeu
    protected int score;
    protected Ghost blinky;
    protected Ghost pinky;
    protected Ghost inky;
    protected Ghost clyde;
    protected int level = 1;
    protected int nbLives = 2;
    protected int stateTimer = 0;
    protected int waveIndex = 0;
    protected boolean stateTimeDisabled = false;
    protected GhostState currentGlobalState = GhostState.SCATTER;
    protected final int[] WAVE_DURATIONS = {420, 1200, 420, 1200, 300, 1200, 300, Integer.MAX_VALUE};
    Random random = new Random();
    protected Counter noDot = new Counter(0);
    protected Counter specialDot = new Counter(0);
    protected boolean noBlinky;
    protected boolean noPinky;
    protected boolean noInky;
    protected boolean noClyde;
    protected List<BoardEvent> currentEvents = new ArrayList<>();
    protected int frightTimer = 0;
    protected int ghostsEatenInStreak = 0;
    protected boolean useSpecialDotCounter = false;

    public AbstractBoard(GameType gameType) {
        this.state = BoardState.INITIAL;
        this.type = gameType;
    }

    @Override
    public GameType getGameType() {
        return this.type;
    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }

    /**
     * Fait passer l'état du plateau à STARTED.
     * C'est cette méthode qui permet au test du Controller de passer.
     */
    @Override
    public void startActors() {
        this.state = BoardState.STARTED;
    }

    @Override
    public Actor getPacMan() {
        return this.pacMan;
    }

    @Override
    public void nextFrame() {
        this.currentEvents.clear();
        this.noDot.inc();

        if (pacMan != null) {
            pacMan.nextFrame();
            TilePosition tile_pos = pacMan.getCurrentTile();
            if (tile_pos.getLine() < 0 || tile_pos.getLine() >= maze.getHeight()
                    || tile_pos.getCol() < 0 || tile_pos.getCol() >= maze.getWidth()) {
                return;
            }

            Tile current_tile=maze.getTile(tile_pos);
            if (current_tile.hasDot()) {
                if (current_tile.hasBigDot()) {
                    this.score += 50;
                    pacMan.setStopTime(3);

                    if (this.type != GameType.TEST) {
                        pacMan.setSpeed(getFrightPacManSpeed());
                    }

                    this.currentEvents.add(new BoardEvent(BoardEventType.EAT_BIG_DOT, tile_pos, 50));
                    this.ghostsEatenInStreak = 0;

                    int frightenTime = getFrightenTime();
                    if (frightenTime > 0) {
                        this.frightTimer = frightenTime + getFrightenEndTime();
                        // Frighten living ghosts
                        for (Ghost ghost : getGhosts()) {
                            if (ghost.getGhostState() != GhostState.DEAD) {
                                ghost.changeGhostState(GhostState.FRIGHTENED);
                            }
                        }
                    }
                }
                else {
                    this.score += 10;
                    pacMan.setStopTime(1);
                    this.currentEvents.add(new BoardEvent(BoardEventType.EAT_SMALL_DOT, tile_pos, 10));
                }

                // Pen release and dot logic
                this.noDot.reset();

                if (this.useSpecialDotCounter) {
                    this.specialDot.inc();
                    int specialCount = this.specialDot.getValue();

                    if (specialCount == 7 && pinky != null && pinky.getGhostPenState() == GhostPenState.IN) {
                        pinky.setGhostPenState(GhostPenState.GET_OUT);
                    } else if (specialCount == 17 && inky != null && inky.getGhostPenState() == GhostPenState.IN) {
                        inky.setGhostPenState(GhostPenState.GET_OUT);
                    } else if (specialCount == 32) {
                        this.useSpecialDotCounter = false;
                    }
                }
                else {
                    if (pinky != null && pinky.getGhostPenState() == GhostPenState.IN) {
                        pinky.getDotCounter().inc();
                        if (pinky.getDotCounter().getValue() >= pinky.getDotCounter().getLimit()) {
                            pinky.setGhostPenState(GhostPenState.GET_OUT);
                        }
                    } else if (inky != null && inky.getGhostPenState() == GhostPenState.IN) {
                        inky.getDotCounter().inc();
                        if (inky.getDotCounter().getValue() >= inky.getDotCounter().getLimit()) {
                            inky.setGhostPenState(GhostPenState.GET_OUT);
                        }
                    } else if (clyde != null && clyde.getGhostPenState() == GhostPenState.IN) {
                        clyde.getDotCounter().inc();
                        if (clyde.getDotCounter().getValue() >= clyde.getDotCounter().getLimit()) {
                            clyde.setGhostPenState(GhostPenState.GET_OUT);
                        }
                    }
                }
            }

            maze.setTile(pacMan.getCurrentTile(), current_tile.clearDot());

            // Force ghost release
            if (this.noDot.getValue() >= this.noDot.getLimit()) {
                this.noDot.reset();
                if (pinky != null && pinky.getGhostPenState() == GhostPenState.IN) {
                    pinky.setGhostPenState(GhostPenState.GET_OUT);
                } else if (inky != null && inky.getGhostPenState() == GhostPenState.IN) {
                    inky.setGhostPenState(GhostPenState.GET_OUT);
                } else if (clyde != null && clyde.getGhostPenState() == GhostPenState.IN) {
                    clyde.setGhostPenState(GhostPenState.GET_OUT);
                }
            }
        }

        if (maze.getNumberOfDots() == 0) {
            this.state = BoardState.LEVEL_OVER;
            this.level++;
        }

        // Time management
        if (this.frightTimer > 0) {

            // Increment down fright timer
            this.frightTimer--;

            // Tick white
            if (this.frightTimer == getFrightenEndTime()) {
                for (Ghost ghost : getGhosts()) {
                    if (ghost.getGhostState() == GhostState.FRIGHTENED) {
                        ghost.changeGhostState(GhostState.FRIGHTENED_END);
                    }
                }
            } else if (this.frightTimer == 0) {

                // Revert to normal
                for (Ghost ghost : getGhosts()) {
                    if (ghost.getGhostState() == GhostState.FRIGHTENED_END) {
                        ghost.changeGhostState(this.currentGlobalState);
                    }
                }
            }
        }
        // Normal time waver (if ghosts are not frightened)
        else if (!stateTimeDisabled) {
            this.stateTimer++;
            if (stateTimer >= WAVE_DURATIONS[this.waveIndex]) {
                this.stateTimer = 0;
                if (this.waveIndex < WAVE_DURATIONS.length - 1) {
                    this.waveIndex++;
                }
                if (this.currentGlobalState == GhostState.SCATTER) {
                    this.currentGlobalState = GhostState.CHASE;
                }
                else {
                    this.currentGlobalState = GhostState.SCATTER;
                }
                for (Ghost ghost : getGhosts()) {
                    ghost.changeGhostState(this.currentGlobalState);
                }
            }
        }

        for (Ghost ghost : getGhosts()) {
            ghost.nextFrame();
            TilePosition ghostTile = ghost.getCurrentTile();
            if (ghostTile.getLine() < 0 || ghostTile.getLine() >= maze.getHeight()
                    || ghostTile.getCol() < 0 || ghostTile.getCol() >= maze.getWidth()) {
                continue;
            }

            Tile currentGhostTile = maze.getTile(ghostTile);

            // Ghost speed override hierarchy
            if (ghost.getGhostPenState() != GhostPenState.OUT) {
                // Pen takes absolute priority
                ghost.setSpeed(getTunnelGhostSpeed());
            }
            else if (ghost.getGhostState() == GhostState.DEAD) {
                // Dead ghosts
                ghost.setSpeed(getDeadGhostSpeed());
            }
            else if (ghost.getGhostState() == GhostState.FRIGHTENED || ghost.getGhostState() == GhostState.FRIGHTENED_END) {
                // Frightened speed
                ghost.setSpeed(getFrightGhostSpeed());
            }
            else if (currentGhostTile == Tile.SL) {
                // Tunnel tiles
                ghost.setSpeed(getTunnelGhostSpeed());
            }
            else {
                // Normal speed
                ghost.setSpeed(getLevelGhostSpeed());
            }

            // Collision logic
            if (pacMan.getCurrentTile().equals(ghostTile)) {
                if (ghost.getGhostState() == GhostState.FRIGHTENED || ghost.getGhostState() == GhostState.FRIGHTENED_END) {
                    this.ghostsEatenInStreak++;
                    int points = 200 * (int)Math.pow(2, this.ghostsEatenInStreak - 1);
                    this.score += points;
                    this.currentEvents.add(new BoardEvent(BoardEventType.EAT_GHOST, ghostTile, points));
                    ghost.changeGhostState(GhostState.DEAD);
                    pacMan.setStopTime(3);
                }
                else if (ghost.getGhostState() != GhostState.DEAD) {
                    if (this.nbLives > 0) {
                        this.state = BoardState.LIFE_OVER;
                    } else {
                        this.state = BoardState.GAME_OVER;
                    }
                }
            }
        }
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public BoardState getBoardState() {
        return this.state;
    }

    @Override
    public Ghost getGhost(GhostType ghostType) {
        return switch(ghostType) {
            case BLINKY -> blinky;
            case PINKY -> pinky;
            case INKY -> inky;
            case CLYDE -> clyde;
        };
    }

    @Override
    public List<Ghost> getGhosts() {
        List<Ghost> ghosts = new ArrayList<>();
        if (blinky != null) ghosts.add(blinky);
        if (pinky  != null) ghosts.add(pinky);
        if (inky   != null) ghosts.add(inky);
        if (clyde  != null) ghosts.add(clyde);
        return ghosts;
    }

    @Override
    public int getLevel() {
        return this.level; }

    @Override
    public void initializeNewLevel(int level) throws PacManException {
        this.level = level;
        this.state = BoardState.INITIAL;
    }

    @Override
    public void setNumberOfLives(int nbLives) {
        this.nbLives = nbLives;
    }

    @Override
    public int getNumberOfLives() {
        return this.nbLives;
    }

    @Override
    public void initializeNewLife() {
        this.nbLives--;
        if (nbLives < 0) {
            state = BoardState.GAME_OVER;
        } else {
            state = BoardState.INITIAL;
            useSpecialDotCounter = true;
            specialDot = new Counter(0);
            this.noDot.reset();
            this.stateTimer = 0;
            this.waveIndex = 0;
            this.currentGlobalState = GhostState.SCATTER;
        }
    }


    @Override
    public boolean hasGhost(GhostType ghostType) {
        return getGhost(ghostType) != null;
    }

    @Override
    public void disableGhost(GhostType ghostType) {
        switch(ghostType) {
            case BLINKY: this.noBlinky = true; break;
            case PINKY: this.noPinky = true; break;
            case INKY: this.noInky = true; break;
            case CLYDE: this.noClyde = true; break;
        }
    }

    @Override
    public void disableStateTime() {
        this.stateTimeDisabled = true;
    }

    @Override
    public Direction getRandomDirection() {
        int nb = random.nextInt(4);
        return switch (nb) {
            case 0 -> Direction.UP;
            case 1 -> Direction.DOWN;
            case 2 -> Direction.LEFT;
            case 3 -> Direction.RIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + nb);
        };
    }

    @Override
    public TilePosition penEntry() {
        return new TilePosition(14, 13);
    }

    @Override
    public int minYPen() { return 134; }

    @Override
    public int maxYPen() { return 144; }

    @Override
    public int penGhostXPosition(GhostType type) {
        return switch(type) {
            case BLINKY -> 112;
            case PINKY -> 112;
            case INKY -> 96;
            case CLYDE -> 128;
        };
    }

    @Override
    public int penGhostYPosition(GhostType type) {
        return 139;
    }

    @Override
    public int outPenXPosition() { return 112; }

    @Override
    public int outPenYPosition() { return 115; }

    @Override
    public Counter noDotCounter() {
        return this.noDot;
    }

    @Override
    public Counter specialDotCounter() {
        return this.specialDot;
    }

    @Override
    public double getLevelPacManSpeed() {
        if (this.level == 1) {
            return 1.0;
        }
        else if (this.level >= 2 && this.level <= 4) {
            return 1.14;
        }
        else if (this.level >= 5 && this.level <= 20) {
            return 1.26;
        }
        else {
            return 1.14;
        }
    }

    @Override
    public double getFrightPacManSpeed() {
        if (this.level == 1) {
            return 1.14;
        }
        else if (this.level >= 2 && this.level <= 4) {
            return 1.2;
        }
        else {
            return 1.26;
        }
    }

    @Override
    public double getLevelGhostSpeed() {
        if (this.level == 1) {
            return 0.94;
        }
        else if (this.level >= 2 && this.level <= 4) {
            return 1.07;
        }
        else {
            return 1.20;
        }
    }

    @Override
    public double getTunnelGhostSpeed() {
        if (this.level == 1) {
            return 0.50;
        }
        else if (this.level >= 2 && this.level <= 4) {
            return 0.57;
        }
        else {
            return 0.63;
        }
    }

    @Override
    public double getFrightGhostSpeed() {
        if (this.level == 1) {
            return 0.63;
        }
        else if (this.level >= 2 && this.level <= 4) {
            return 0.69;
        }
        else {
            return 0.75;
        }
    }

    @Override
    public double getDeadGhostSpeed() {
        return 1.26;
    }

    /*
     * Ajout
     * Fonction pour avoir le temps de frighten des fantômes
     */
    public int getFrightenTime() {
        return switch (this.level) {
            case 1 -> 360;
            case 2 -> 300;
            case 3 -> 240;
            case 4 -> 180;
            case 5 -> 120;
            case 6 -> 360;
            case 7, 8 -> 120;
            case 9 -> 60;
            case 10 -> 300;
            case 11 -> 120;
            case 12, 13 -> 60;
            case 14 -> 180;
            case 15, 16 -> 60;
            case 17 -> -1;
            case 18 -> 60;
            default -> -1;
        };
    }

    /*
     * Ajout
     * Fonction pour avoir le temps de frighten_end des fantômes
     */
    public int getFrightenEndTime() {
        return switch (this.level) {
            case 9, 12, 13, 15, 16, 18 -> 90;
            default -> 150;
        };
    }

    @Override
    public void setExtraLifeScore(int score) { }

    @Override
    public int getExtraLifeScore() { return 0; }

    @Override
    public Bonus getCurrentBonus() { return null; }

    @Override
    public BonusType getLevelBonusType(int level) { return null; }

    @Override
    public void setBonusOnBoard() { }

    @Override
    public double getElroyGhostSpeed(int elroyNumber) { return 0; }

    @Override
    public int getElroyDotValue(int elroyNumber) { return 0; }

    @Override
    public List<BoardEvent> getCurrentEvents() {
        return this.currentEvents;
    }
}