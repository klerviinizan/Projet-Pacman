package fr.upsaclay.bibs.pacman.model.board;

import fr.upsaclay.bibs.pacman.model.actors.GhostType;
import fr.upsaclay.bibs.pacman.model.maze.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ColorBoard {
    private static final int TILE_SIZE = 8;

    /**
     * Lire les fichiers de resources/tiles en matrice
     */
    public static int[][] lireTilesEnMatrice(String filename) {
        try {
            File fichier = new File(filename);
            Scanner scanner = new Scanner(fichier);
            List<int[]> listeLignes = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] cases = ligne.split("\\s+");
                int[] ligneInt = new int[cases.length];

                for (int i = 0; i < cases.length; i++) {
                    ligneInt[i] = Integer.parseInt(cases[i]);
                }
                listeLignes.add(ligneInt);
            }
            scanner.close();
            return listeLignes.toArray(new int[0][]);

        } catch (FileNotFoundException e) {
            System.err.println("Le fichier n'existe pas : " + filename);
            return null;
        }
    }

    /**
     * Crée un dico (Map) de toutes les matrices
     */
    public static Map<Tile, int[][]> creerDictionnaire() {
        Map<Tile, int[][]> dictionnaire = new HashMap<>();

        for (Tile t : Tile.values()) {
            String nomFichier = "resources/tiles/" + t.name() + ".txt";
            int[][] matrice = lireTilesEnMatrice(nomFichier);

            if (matrice != null) {
                dictionnaire.put(t, matrice);
            }
        }
        return dictionnaire;
    }


    public void dessinerLabyrinthe(String mazeFile, Map<Tile, int[][]> dictionnaire) {
        try {
            int nbLignesMaze = 36;
            int nbColonnesMaze = 28;

            BufferedImage imageFinale = new BufferedImage(
                    nbColonnesMaze * TILE_SIZE,
                    nbLignesMaze * TILE_SIZE,
                    BufferedImage.TYPE_INT_RGB
            );
            Scanner scanner = new Scanner(new File(mazeFile));
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            int ligneActuelle = 0;

            while (scanner.hasNextLine() && ligneActuelle < nbLignesMaze) {
                String ligneTxt = scanner.nextLine().trim();
                if (ligneTxt.isEmpty()) continue;

                String[] nomsTiles = ligneTxt.split("\\s+");

                for (int colActuelle = 0; colActuelle < nomsTiles.length; colActuelle++) {
                    Tile typeTile = Tile.valueOf(nomsTiles[colActuelle]);
                    int[][] matricePixels = dictionnaire.get(typeTile);
                    dessinerTileSpecifique(imageFinale, typeTile, matricePixels, colActuelle, ligneActuelle);
                }
                ligneActuelle++;
            }
            scanner.close();

            javax.imageio.ImageIO.write(imageFinale, "png", new File("background_jeu.png"));
            File result = new File("background_jeu.png");

            System.out.println("Labyrinthe généré à partir des fichiers de tiles !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dessinerTileSpecifique(BufferedImage image, Tile type, int[][] pixels, int col, int lig) {
        if (pixels == null) return;
        Color couleurPrincipale;
        if (type.isWall()) {
            couleurPrincipale = Color.BLUE; // Murs en bleu
        } else if (type.hasDot()) {
            couleurPrincipale = Color.BLACK;
        } else {
            couleurPrincipale = Color.BLACK;
        }

        for (int y = 0; y < TILE_SIZE; y++) {
            for (int x = 0; x < TILE_SIZE; x++) {
                int couleurFinale = (pixels[y][x] == 1) ? couleurPrincipale.getRGB() : Color.BLACK.getRGB();

                int posX = (col * TILE_SIZE) + x;
                int posY = (lig * TILE_SIZE) + y;

                image.setRGB(posX, posY, couleurFinale);
            }
        }
    }

    public void dessinerPacman(BufferedImage image, int[][] pixels, int col, int lig) {
        if (pixels == null) return;

        for (int y = 0; y < pixels.length; y++) {
            for (int x = 0; x < pixels[y].length; x++) {
                if (pixels[y][x] == 1) {
                    image.setRGB(x, y, Color.YELLOW.getRGB());
                } else {
                    image.setRGB(x, y, 0);
                }
            }
        }
    }

    public static Map<String, Map<GhostType, int[][]>> creerDicoFantomesComplet() {
        Map<String, Map<GhostType, int[][]>> grandDico = new HashMap<>();
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT", "frightened"};

        for (String dir : directions) {
            String nomFichier = "resources/ghosts/" + dir + ".txt";
            int[][] matrice = lireTilesEnMatrice(nomFichier);

            if (matrice != null) {
                Map<GhostType, int[][]> sousDico = new HashMap<>();
                for (GhostType type : GhostType.values()) {
                    sousDico.put(type, matrice);
                }
                grandDico.put(dir, sousDico);
            }
        }
        return grandDico;
    }

    public void dessinerGhostDepuisTemplate(BufferedImage image, int[][] template, GhostType type, String direction) {
        if (template == null) return;
        int couleurCorps;
        if (direction.equalsIgnoreCase("frightened")) {
            couleurCorps = Color.BLUE.getRGB();
        } else {
            couleurCorps = switch (type) {
                case BLINKY -> Color.RED.getRGB();
                case INKY   -> Color.CYAN.getRGB();
                case PINKY  -> Color.PINK.getRGB();
                case CLYDE  -> Color.ORANGE.getRGB();
                default     -> Color.WHITE.getRGB();
            };
        }
        for (int y = 0; y < template.length; y++) {
            for (int x = 0; x < template[y].length; x++) {
                int valeur = template[y][x];
                int pixelColor = 0;
                switch (valeur) {
                    case 1 -> pixelColor = couleurCorps;
                    case 2 -> pixelColor = Color.WHITE.getRGB();
                    case 3 -> pixelColor = Color.BLACK.getRGB();
                    default -> pixelColor = 0;
                }
                image.setRGB(x, y, pixelColor);
            }
        }
    }

    public static Map<Integer, int[][]> creerDicoVies() {
        Map<Integer, int[][]> dicoVies = new HashMap<>();
        int[][] matrice1 = lireTilesEnMatrice("resources/Presentation/1life");
        if (matrice1 != null) {
            dicoVies.put(0, matrice1);
        }
        int[][] matrice2 = lireTilesEnMatrice("resources/Presentation/2lifes");
        if (matrice2 != null) {
            dicoVies.put(1, matrice2);
        }
        int[][] matrice3 = lireTilesEnMatrice("resources/Presentation/3lifes.txt");
        if (matrice3 != null) {
            dicoVies.put(2, matrice3);
        }
        return dicoVies;
    }

    public static Map<Integer, int[][]> creerDicoNiveaux() {
        Map<Integer, int[][]> dicoNiveaux = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            String nomFichier = "resources/level/level" + i ;
            int[][] matrice = lireTilesEnMatrice(nomFichier);
            if (matrice != null) {
                dicoNiveaux.put(i, matrice);
            }
        }
        return dicoNiveaux;
    }
}
