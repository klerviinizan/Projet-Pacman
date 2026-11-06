package fr.upsaclay.bibs.pacman.control;

import fr.upsaclay.bibs.pacman.GameType;
import fr.upsaclay.bibs.pacman.PacManException;
import fr.upsaclay.bibs.pacman.model.Direction;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.board.BoardState;
import fr.upsaclay.bibs.pacman.view.LesYeux;
import fr.upsaclay.bibs.pacman.view.PacManLayout;
import fr.upsaclay.bibs.pacman.view.PacManView;


import static fr.upsaclay.bibs.pacman.GameType.CLASSIC;

public class ControllerView implements Controller{
    private PacManView view;
    private Board board;
    GameType gameType = getGameType();
    private boolean start=false;
    private boolean pause=true;

    /**
     * Initialization of the controller at the beginning
     * of the application.
     * Depending on the developer choices, this could for
     * example initialize a new game by default
     * This would also create and launch the view and user
     * interface
     *
     * @throws PacManException if the initialization fails
     */
    @Override
    public void initialize() throws PacManException {
        this.board = Board.createBoard(this.gameType != null ? this.gameType : GameType.CLASSIC);
        this.board.initialize();
        this.view = new LesYeux();
        this.view.setBoard(this.board);
        this.view.setController(this);

        this.view.initialize();
        this.view.playStartSound();
    }

    /**
     * Initialize a new game
     * The initialization should do necessary steps on the model
     * and the view :
     * for example, creating a new board, update the view etc.
     *
     * @throws PacManException if the initialization fails
     */
    @Override
    public void initializeNewGame() throws PacManException {
        this.board = Board.createBoard(CLASSIC);
        this.view.setBoard(this.board);
    }

    /**
     * Setting the game type
     *
     * @param gameType a game type to be played
     */
    @Override
    public void setGameType(GameType gameType) {
        this.gameType=gameType;
    }

    /**
     * Getting the game type
     *
     * @return the current game type of the controller
     */
    @Override
    public GameType getGameType() {
        return this.gameType;
    }

    /**
     * Receives an action order (typically from the view)
     * and perform necessary actions on model and view
     *
     * @param action a game action
     * @throws ForbiddenActionException if the action is not permitted at this step
     */
    @Override
    public void receiveAction(GameAction action) throws PacManException {

        switch (action) {
            case START:
                if (this.start) throw new ForbiddenActionException(action);
                this.board.initialize();
                this.board.startActors();
                this.start = true;
                this.pause = false;
                view.setLayout(PacManLayout.GAME_ON);
                break;

            case NEXT_FRAME:
                if (!this.start || this.pause) return;
                board.nextFrame();

                BoardState currentState = board.getBoardState();
                if (currentState == BoardState.LEVEL_OVER) {
                    this.pause = true;
                    view.setLayout(PacManLayout.LEVEL_OVER);
                    view.playWinSound();
                } else if (currentState == BoardState.LIFE_OVER) {
                    this.pause = true;
                    view.setLayout(PacManLayout.LIFE_OVER);
                    view.playLooserSound();
                } else if (currentState == BoardState.GAME_OVER) {
                    this.pause = true;
                    view.setLayout(PacManLayout.GAME_OVER);
                    view.playLooserSound();
                }
                break;

            case PAUSE:
                if (!this.start || this.pause) throw new ForbiddenActionException(action);
                this.pause = true;
                view.setLayout(PacManLayout.PAUSE);
                this.view.playPauseSound();
                break;

            case RESUME:
                if (!this.pause) throw new ForbiddenActionException(action);
                this.pause = false;
                view.setLayout(PacManLayout.GAME_ON);
                break;

            case NEXT_LEVEL:
                if (board.getBoardState() == BoardState.LEVEL_OVER) {
                    board.initializeNewLevel(board.getLevel());
                    board.startActors();
                    this.start = true;
                    this.pause = false;
                    view.setLayout(PacManLayout.GAME_ON);
                    view.update();
                }
                break;
            case NEW_GAME:
                this.board = Board.createBoard(this.gameType != null ? this.gameType : GameType.CLASSIC);
                this.board.initialize();
                this.board.startActors();
                this.view.setBoard(this.board);
                this.start = true;
                this.pause = false;
                view.setLayout(PacManLayout.GAME_ON);
                break;

            case UP:
            case DOWN:
            case LEFT:
            case RIGHT:
                if (!this.start || this.pause) return;

                Direction dir = switch (action) {
                    case UP -> Direction.UP;
                    case DOWN -> Direction.DOWN;
                    case LEFT -> Direction.LEFT;
                    case RIGHT -> Direction.RIGHT;
                    default -> null;
                };

                if (dir != null) {
                    board.getPacMan().setIntention(dir);
                }
                break;

            case NEW_LIFE:
                if (this.board == null) throw new ForbiddenActionException(action);
                this.board.initializeNewLife();
                if (this.board.getBoardState() == BoardState.GAME_OVER) {
                    view.setLayout(PacManLayout.GAME_OVER);
                    this.start = false;
                    this.pause = true;
                } else {
                    this.board.startActors();
                    this.start = true;
                    this.pause = false;
                    view.setLayout(PacManLayout.GAME_ON);
                }
                break;
        }
    }

    /**
     * Return the current board
     *
     * @return the board
     */
    @Override
    public Board getBoard() {
        return this.board;
    }
}
