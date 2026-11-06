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
import static fr.upsaclay.bibs.pacman.model.board.BoardState.LEVEL_OVER;

public class ControllerClass implements Controller {

    private GameType gameType;
    private Board board;
    private boolean start=false;
    private boolean pause=false;

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
        if (this.gameType == null) {
            this.gameType = CLASSIC;
        }
        initializeNewGame();
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
        this.board = Board.createBoard(this.gameType);
        this.board.initialize();
        this.start = false;
        this.pause = false;
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
                if (this.board == null) {
                    this.board = Board.createBoard(CLASSIC);
                    this.board.initialize();
                }
                this.board.startActors();
                this.start = true;
                this.pause = false;
                return;

            case NEXT_FRAME:
                if (!this.start || this.pause || this.board.getBoardState() != BoardState.STARTED) {
                    throw new ForbiddenActionException(action);
                }
                board.nextFrame();
                BoardState currentState = board.getBoardState();
                if (currentState == BoardState.LEVEL_OVER) {
                    this.pause = true;
                } else if (currentState == BoardState.LIFE_OVER || currentState == BoardState.GAME_OVER) {
                    this.pause = true;
                }
                break;

            case NEXT_LEVEL:
            case NEW_GAME:
                this.board = Board.createBoard(this.gameType != null ? this.gameType : GameType.CLASSIC);
                this.board.initialize();
                this.start = false;
                this.pause = false;
                return;

            case PAUSE:
                if (!this.start || this.pause || this.board.getBoardState() != BoardState.STARTED)
                    throw new ForbiddenActionException(action);
                this.pause = true;
                return;

            case RESUME:
                if (!this.pause || this.board.getBoardState() != BoardState.STARTED)
                    throw new ForbiddenActionException(action);
                this.pause = false;
                return;

            case UP: case DOWN: case LEFT: case RIGHT:
                if (!this.start || this.pause || this.board.getBoardState() != BoardState.STARTED)
                    throw new ForbiddenActionException(action);

                Direction dir = switch (action) {
                    case UP -> Direction.UP;
                    case DOWN -> Direction.DOWN;
                    case LEFT -> Direction.LEFT;
                    case RIGHT -> Direction.RIGHT;
                    default -> Direction.NONE;
                };

                board.getPacMan().setIntention(dir);
                return;

            case NEW_LIFE:
                if (this.board == null || this.board.getBoardState() != BoardState.LIFE_OVER) {
                    throw new ForbiddenActionException(action);
                }
                this.board.initializeNewLife();
                this.start = false;
                this.pause = false;
                return;

            default:
                throw new ForbiddenActionException(action);
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
