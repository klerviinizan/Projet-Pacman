package fr.upsaclay.bibs.pacman.view;

import fr.upsaclay.bibs.pacman.control.Controller;
import fr.upsaclay.bibs.pacman.model.board.Board;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * The Interface of a PacMan visual user interface
 */
public interface PacManView {

    /**
     * Sets the current board
     * the view should never perform any action on the board
     * the board is set only so that the view has the information to show the game
     * it should NOT be modified by the view
     * @param board a PacMan board
     */
    void setBoard(Board board);

    /**
     * Sets the controller
     * this is used to send action to the controller when the user interacts
     * with the view
     * @param controller, the controller of the application
     */
    void setController(Controller controller);

    /**
     * View initialization
     */
    void initialize();

    /**
     * Sets the current layout
     * layouts correspond to game phases
     * when a new layout is set, the view should take all necessary actions
     * for updating the user interface
     * @param layout, the game layout to show
     */
    void setLayout(PacManLayout layout);

    /**
     * Update the view (for example, because the something has changed on the board)
     */
    void update();

    /**
     * Methjod to import and read the start sound
     */

    public default void playStartSound() {
        try {

            File file = new File("resources\\Presentation\\caoupacmanson(1).wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Methhod to import and read the pause sound
     */
    public default void playPauseSound() {
        try {
            File file = new File("resources\\Presentation\\pacpause.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public default void playWinSound() {
        try {
            File file = new File("resources\\Presentation\\piece.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public default void playLooserSound() {
        try {
            File file = new File("resources\\Presentation\\perdu_lustucru.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public default void playGameSound() {
        try {
            File file = new File("resources\\Presentation\\single_lady.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public default void playDotsSound() {
        try {
            File file = new File("resources\\Presentation\\gloup.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
