package fr.upsaclay.bibs.pacman.view;

import fr.upsaclay.bibs.pacman.control.Controller;
import fr.upsaclay.bibs.pacman.model.actors.ActorType;
import fr.upsaclay.bibs.pacman.model.actors.GhostState;
import fr.upsaclay.bibs.pacman.model.actors.GhostType;
import fr.upsaclay.bibs.pacman.model.board.Board;
import fr.upsaclay.bibs.pacman.model.board.ColorBoard;
import fr.upsaclay.bibs.pacman.model.maze.Maze;
import fr.upsaclay.bibs.pacman.control.GameAction;
import fr.upsaclay.bibs.pacman.model.maze.Tile;
import fr.upsaclay.bibs.pacman.model.maze.TilePosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


import static fr.upsaclay.bibs.pacman.control.GameAction.NEXT_FRAME;

public class LesYeux implements PacManView {

    private Board board;
    private Controller controller;
    private Timer timer;
    private PacManLayout currentLayout;
    private JPanel gamePanel;
    private JFrame frame;
    private int frameCount = 0;

    private Image welcomeImage;
    private final String IMAGE_PATH = "resources\\Presentation\\Cap ou PacMan.png";
    private Image pauseImage;
    private final String PAUSE_IMAGE_PATH = "resources\\Presentation\\UNE_PAUSE.png";
    private Image winImage;
    private final String WIN_IMAGE_PATH = "resources\\Presentation\\yeeah_man.png";
    private Image looseImage;
    private final String LOOSE_IMAGE_PATH = "resources\\Presentation\\looser.png";
    private Image game_overImage;
    private final String GAME_OVER_IMAGE_PATH = "resources\\Presentation\\game_over.png";

    private final int SCALE = 20;

    private Image deadGhostImage;
    private final String DEAD_INKY_IMAGE_PATH = "resources/ghosts/stefan_ghost.jpeg";
    private final String DEAD_PINKY_IMAGE_PATH = "resources/ghosts/klervi_ghost.png";
    private final String DEAD_CLYDE_IMAGE_PATH = "resources/ghosts/eloise_ghost.jpeg";
    private final String DEAD_BLINKY_IMAGE_PATH = "resources/ghosts/viviane_ghost.jpg";

    private Image backgroundImage;
    private boolean isBackgroundLoaded = false;
    private Image pacmanImage;

    private Image deadInkyGhostImage;
    private Image deadPinkyGhostImage;
    private Image deadBlinkyGhostImage;
    private Image deadClydeGhostImage;

    private Map<Integer, Image> levelImages;

    private Map<String, Map<GhostType, Image>> ghostImages;
    private Map<Integer, Image> lifeImages;

    @Override
    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize() {
        welcomeImage = new ImageIcon(IMAGE_PATH).getImage();
        pauseImage = new ImageIcon(PAUSE_IMAGE_PATH).getImage();
        winImage=new ImageIcon(WIN_IMAGE_PATH).getImage();
        looseImage=new ImageIcon(LOOSE_IMAGE_PATH).getImage();
        game_overImage=new ImageIcon(GAME_OVER_IMAGE_PATH).getImage();

        deadInkyGhostImage = new ImageIcon(DEAD_INKY_IMAGE_PATH).getImage();
        deadPinkyGhostImage = new ImageIcon(DEAD_PINKY_IMAGE_PATH).getImage();
        deadBlinkyGhostImage = new ImageIcon(DEAD_BLINKY_IMAGE_PATH).getImage();
        deadClydeGhostImage = new ImageIcon(DEAD_CLYDE_IMAGE_PATH).getImage();

        frame = new JFrame("PacMan - Les Yeux");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        ColorBoard cb = new ColorBoard();
        int[][] pacmanMatrix = ColorBoard.lireTilesEnMatrice("resources/pacman_new");
        if (pacmanMatrix != null) {
            BufferedImage bImage = new BufferedImage(13, 13, BufferedImage.TYPE_INT_ARGB);
            cb.dessinerPacman(bImage, pacmanMatrix, 0, 0);
            pacmanImage = bImage;
        }
        this.ghostImages = new HashMap<>();
        ColorBoard cb2 = new ColorBoard();
        var matrices = ColorBoard.creerDicoFantomesComplet();

        for (String dir : matrices.keySet()) {
            Map<GhostType, Image> subMap = new HashMap<>();
            for (GhostType type : GhostType.values()) {
                int[][] template = matrices.get(dir).get(type);
                BufferedImage bImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
                cb2.dessinerGhostDepuisTemplate(bImg, template, type, dir);
                subMap.put(type, bImg);
            }
            ghostImages.put(dir, subMap);
        }

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setFocusable(true);
        frame.add(gamePanel);

        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e);
            }
        });

        timer = new Timer(17, e -> {
            if (currentLayout == PacManLayout.GAME_ON) {
                try {
                    controller.receiveAction(NEXT_FRAME);
                    frameCount++;
                    update();
                } catch (Exception ex) {
                    timer.stop();
                    ex.printStackTrace();
                }
            }
        });

        this.lifeImages = new HashMap<>();
        Map<Integer, int[][]> matricesVies = ColorBoard.creerDicoVies();
        for (Integer nb : matricesVies.keySet()) {
            int[][] template = matricesVies.get(nb);
            BufferedImage bImg = new BufferedImage(template[0].length, template.length, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < template.length; y++) {
                for (int x = 0; x < template[y].length; x++) {
                    int val = template[y][x];
                    if (val == 1) {
                        bImg.setRGB(x, y, Color.RED.getRGB());
                    } else {
                        bImg.setRGB(x, y, 0x00000000);
                    }
                }
            }
            lifeImages.put(nb, bImg);
        }

        this.levelImages = new HashMap<>();
        Map<Integer, int[][]> matricesNiveaux = ColorBoard.creerDicoNiveaux();
        for (Integer lvl : matricesNiveaux.keySet()) {
            int[][] template = matricesNiveaux.get(lvl);
            BufferedImage bImg = new BufferedImage(template[0].length, template.length, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < template.length; y++) {
                for (int x = 0; x < template[y].length; x++) {
                    if (template[y][x] == 1) {
                        bImg.setRGB(x, y, Color.WHITE.getRGB());
                    } else {
                        bImg.setRGB(x, y, 0x00000000);
                    }
                }
            }
            levelImages.put(lvl, bImg);
        }

        setLayout(PacManLayout.INIT);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();

    }

    private void handleInput(KeyEvent e) {
        try {
            int key = e.getKeyCode();
            if (currentLayout == PacManLayout.LEVEL_OVER || currentLayout == PacManLayout.LIFE_OVER || currentLayout == PacManLayout.GAME_OVER) {
                if (key == KeyEvent.VK_ENTER) {
                    if (currentLayout == PacManLayout.LEVEL_OVER) {
                        controller.receiveAction(GameAction.NEXT_LEVEL);
                    } else if (currentLayout == PacManLayout.LIFE_OVER) {
                        controller.receiveAction(GameAction.NEW_LIFE);
                    } else if (currentLayout == PacManLayout.GAME_OVER) {
                        controller.receiveAction(GameAction.NEW_GAME);
                    }
                    return;
                }
            }

            if (key == KeyEvent.VK_ENTER && currentLayout == PacManLayout.INIT) {
                controller.receiveAction(GameAction.START);
                setLayout(PacManLayout.GAME_ON);
                return;
            }

            if (key == KeyEvent.VK_P) {
                if (currentLayout == PacManLayout.GAME_ON) {
                    controller.receiveAction(GameAction.PAUSE);
                    setLayout(PacManLayout.PAUSE);
                } else if (currentLayout == PacManLayout.PAUSE) {
                    controller.receiveAction(GameAction.RESUME);
                    setLayout(PacManLayout.GAME_ON);
                }
                return;
            }

            if (currentLayout == PacManLayout.GAME_ON) {
                switch (key) {
                    case KeyEvent.VK_UP -> controller.receiveAction(GameAction.UP);
                    case KeyEvent.VK_DOWN -> controller.receiveAction(GameAction.DOWN);
                    case KeyEvent.VK_LEFT -> controller.receiveAction(GameAction.LEFT);
                    case KeyEvent.VK_RIGHT -> controller.receiveAction(GameAction.RIGHT);
                }
            }


        } catch (Exception ex) {
            System.err.println("Erreur d'action : " + ex.getMessage());
        }
    }

    private void drawGame(Graphics g) {
        Board activeBoard = controller.getBoard(); // récupération du board actif à modifier et pas création d'un autre
        if (activeBoard == null || activeBoard.getMaze() == null) return;
        if (currentLayout == PacManLayout.INIT) {
            drawWelcomeScreen(g);
            return;
        }

        if (!isBackgroundLoaded) {
            var dico = ColorBoard.creerDictionnaire();
            ColorBoard cb = new ColorBoard();
            cb.dessinerLabyrinthe("resources/maze.txt", dico);
            backgroundImage = new ImageIcon("background_jeu.png").getImage();
            isBackgroundLoaded = true;
        }
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0,
                    activeBoard.getMaze().getWidth() *SCALE,
                    activeBoard.getMaze().getHeight()*SCALE , null);
        }

        Maze maze = activeBoard.getMaze();
        for (int i = 0; i < maze.getHeight(); i++) {
            for (int j = 0; j < maze.getWidth(); j++) {
                Tile tile = maze.getTile(i, j);
                if (tile.hasDot()) {
                    g.setColor(Color.MAGENTA);
                    int dotSize = tile.hasBigDot() ? 8 : 4;
                    int dx = j * SCALE + (SCALE / 2) - (dotSize / 2);
                    int dy = i * SCALE + (SCALE / 2) - (dotSize / 2);
                    g.fillOval(dx, dy, dotSize, dotSize);
                }
            }
        }

        if (activeBoard != null) {
            int nbVies = activeBoard.getNumberOfLives();
            Image imgVie = lifeImages.get(nbVies);

            if (imgVie != null) {
                int panelWidth = gamePanel.getWidth();
                int drawWidth = imgVie.getWidth(null) * 2;
                int drawHeight = imgVie.getHeight(null) * 2;
                int vx = (panelWidth / 2) - (drawWidth / 2);
                int vy = 10;
                g.drawImage(imgVie, vx, vy, drawWidth, drawHeight, null);
            }
        }

        if (activeBoard.getPacMan() != null) {
            g.setColor(Color.YELLOW);
            double xModel = activeBoard.getPacMan().getX();
            double yModel = activeBoard.getPacMan().getY();
            int modelTileSize = Maze.TILE_HEIGHT;
            if (modelTileSize <= 0) modelTileSize = Maze.TILE_WIDTH;
            double scaleFactor = SCALE / (double) Maze.TILE_WIDTH;
            int px = (int) Math.round((xModel - Maze.TILE_CENTER_X) * scaleFactor);
            int py = (int) Math.round((yModel - Maze.TILE_CENTER_Y) * scaleFactor);
            g.drawImage(pacmanImage, px, py, SCALE, SCALE, null);
        }
        if (activeBoard != null) {
            int niveauActuel = activeBoard.getLevel();
            Image imgLevel = levelImages.get(niveauActuel);

            if (imgLevel != null) {
                int panelWidth = gamePanel.getWidth();
                int drawWidth = imgLevel.getWidth(null) * 2;
                int drawHeight = imgLevel.getHeight(null) * 2;
                int lx = panelWidth - drawWidth - 20;
                int ly = 10;
                g.drawImage(imgLevel, lx, ly, drawWidth, drawHeight, null);
            }
        }

        if (activeBoard.getGhosts() != null) {
            for (var ghost : activeBoard.getGhosts()) {
                double scaleFactor = SCALE / (double) Maze.TILE_WIDTH;
                int gx = (int) Math.round((ghost.getX() - Maze.TILE_CENTER_X) * scaleFactor);
                int gy = (int) Math.round((ghost.getY() - Maze.TILE_CENTER_Y) * scaleFactor);

                GhostState ghostState = ghost.getGhostState();
                GhostType type = ghost.getGhostType();
                Image imgAffiche = null;
                if (ghostState == GhostState.DEAD) {
                    imgAffiche = switch (type) {
                        case BLINKY -> deadBlinkyGhostImage;
                        case PINKY  -> deadPinkyGhostImage;
                        case INKY   -> deadInkyGhostImage;
                        case CLYDE  -> deadClydeGhostImage;
                    };
                }
                else if (ghostState == GhostState.FRIGHTENED) {
                    String directionKey = "frightened";
                    if (ghostImages.containsKey(directionKey)) {
                        imgAffiche = ghostImages.get(directionKey).get(type);
                    }
                }
                else if (ghostState == GhostState.FRIGHTENED_END) {
                    boolean visible = (frameCount / 15) % 2 == 0;
                    String directionKey = visible ? "frightened" : ghost.getDirection().name();
                    imgAffiche = ghostImages.get(directionKey).get(type);
                }
                else {
                    String directionKey = ghost.getDirection().name();
                    if (ghostImages.containsKey(directionKey)) {
                        imgAffiche = ghostImages.get(directionKey).get(type);
                    }
                }
                if (imgAffiche != null) {
                    g.drawImage(imgAffiche, gx, gy, SCALE, SCALE, null);
                }
            }
        }

        if (this.board != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("DIALOG", Font.BOLD, 18)); // Style arcade
            String scoreTxt = "TuPac Score: " + board.getScore();
            g.drawString(scoreTxt, 20, 30);
        }

        if (currentLayout == PacManLayout.PAUSE) {
            if (pauseImage != null) {
                g.drawImage(pauseImage, 0, 0, gamePanel.getWidth(), gamePanel.getHeight(), null);
            }
        }

        if (currentLayout==PacManLayout.LEVEL_OVER){
            if (winImage!=null){
                g.drawImage(winImage,0,0,gamePanel.getWidth(),gamePanel.getHeight(),null);
            }
        }

        if (currentLayout==PacManLayout.LIFE_OVER && board.getNumberOfLives()!=0){
            if (looseImage!=null){
                g.drawImage(looseImage,0,0,gamePanel.getWidth(),gamePanel.getHeight(),null);
            }
        }
        if (currentLayout==PacManLayout.GAME_OVER){
            if (looseImage!=null){
                g.drawImage(game_overImage,0,0,gamePanel.getWidth(),gamePanel.getHeight(),null);
            }
        }
    }

    @Override
    public void setLayout(PacManLayout layout) {
        this.currentLayout = layout;

        Board activeBoard = controller.getBoard();
        if (activeBoard != null && activeBoard.getMaze() != null) {
            int width = activeBoard.getMaze().getWidth() * SCALE;
            int height = activeBoard.getMaze().getHeight() * SCALE;
            gamePanel.setPreferredSize(new Dimension(width, height));
            frame.pack();
            frame.setLocationRelativeTo(null);
        }

        if (layout == PacManLayout.GAME_ON) {
            timer.start();
            gamePanel.requestFocusInWindow();
        } else {
            timer.stop();
        }
        update();

    }

    @Override
    public void update() {
        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }

    private void drawWelcomeScreen(Graphics g) {
        if (welcomeImage != null) {
            g.drawImage(welcomeImage, 0, 0, gamePanel.getWidth(), gamePanel.getHeight(), null);
        }

    }
}