/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scene;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import model.Frog;
import model.Pipe;

/**
 *
 * @author conqu
 */
public class GameScene extends javax.swing.JPanel implements KeyListener {

    private final String saveFilePath = "SaveFile.txt";

    private final int fixedUpdateTime = 20;

    private LinkedList<Pipe> pipes;
    private Frog frog;
    private int frogVelocity;

    private Rectangle gameSceneCollider;

    private boolean gamePlaying;
    private boolean isPaused;

    private int score;
    private JLabel scoreLabel;
    private JLabel readyLabel;

    private Thread pipeThread;
    private Thread frogThread;

    //============================INITIALIZATION================================
    public void addCharacter() {
        addCharacter(this.getFrogCenterPosition());
    }

    public void addCharacter(Point location) {
        this.removeAll();
        // Set up gamescene
        gamePlaying = false;
        this.setFocusable(true);
        this.setLayout(null);

        // Add frog to gamescene
        this.frog = new Frog();
        frog.setBounds(new Rectangle(
                location, frog.getPreferredSize()));
        this.add(frog);

        // Set collider
        gameSceneCollider = this.getBounds();

        // Make ready
        this.add(readyLabel);
        readyLabel.setBounds(new Rectangle(getReadyLabelPostion(), readyLabel.getPreferredSize()));
        readyLabel.setHorizontalAlignment(JLabel.RIGHT);

        // Redraw the scene
        this.revalidate();
        this.repaint();

    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        this.isPaused = false;
    }

    private void startTheGame() {
        // Set up component
        this.remove(readyLabel);
        scoreLabel.setText("0");
        gamePlaying = true;

        // Make the frog fall
        this.makeFrogFall();

        // Make pipes generate and move
        this.addNewPipesToScreen();
        this.makePipeMove();
    }

    private Point getReadyLabelPostion() {
        int x = 0;
        int y = this.getSize().height / 6 * 5;
        return new Point(x, y);
    }

    private Point getFrogCenterPosition() {
        int x = this.getSize().width / 15;
        int y = this.getSize().height / 2;
        return new Point(x, y);
    }

    //==============================MOVEMENT====================================
    /**
     * Detect Jump
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Start game
        if (!gamePlaying) {
            startTheGame();
            return;

        }

        this.frogVelocity = Math.max(this.frogVelocity - frog.getJumpSpeed(), -frog.getJumpSpeed());
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void makeFrogFall() {
        frogThread = new Thread(() -> {
            Rectangle frogNextPosition;
            while (this.gamePlaying) {

                if (!isPaused) {
                    frogVelocity += frog.getFallSpeed();

                    Point frogLocation = frog.getLocation();
                    frogNextPosition = frog.getBounds();

                    frogNextPosition.setLocation(
                            frogLocation.x,
                            frogLocation.y + frogVelocity);
                    if (isInsideScene(frogNextPosition)) {
                        frog.setBounds(frogNextPosition);
                    }
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameScene.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        frogThread.start();
    }

    //==========================END MOVEMENT====================================
    //
    //==========================PIPE MANAGEMENT=================================
    private synchronized void addNewPipesToScreen() {
        try {
            Pipe pipe = new Pipe(this.getWidth(), this.getHeight());
            pipes.add(pipe);
            this.add(pipe.getLowerPipe());
            this.add(pipe.getUpperPipe());

            this.revalidate();
            this.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void removePipeFromScreen(Pipe pipe) {
        try {
            super.remove(pipe.getLowerPipe());
            super.remove(pipe.getUpperPipe());
            pipes.removeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makePipeMove() {
        pipeThread = new Thread(() -> {
            synchronized (this) {
            while (this.gamePlaying) {

                if (!isPaused) {
                    // Move pipe
                    try {
                        pipes.forEach((pipe) -> {
                            pipe.updateLevelByScore(score);
                            pipe.movePipes();
                            if (calculatePoint(pipe)) {
                                gamePlaying = false;
                            }
                        });
                    } catch (Exception e) {
                    }

                    if (!gamePlaying) {
                        return;
                    }

                    // If last generated pipe is far enough from the starting point
                    if (pipes.getLast().isGenerateNewPipe()) {
                        this.addNewPipesToScreen();
                    }

                    // Remove out of screen pipe
                    Pipe pipe = pipes.getFirst();
                    if (pipe.isOutOfScreen()) {
                        removePipeFromScreen(pipe);
                    }
                }

                // Slow down the thread
                try {
                    TimeUnit.MILLISECONDS.sleep(this.fixedUpdateTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            }
        });

        pipeThread.start();
    }

//==========================END PIPE MANAGEMENT=============================
//
//==========================POINTS MANAGEMENT===============================
    /**
     *
     * @param pipe
     * @return true when game is over
     */
    private boolean calculatePoint(Pipe pipe) {
        if (pipe.isCalculatedPoint()) {
            return false;
        }

        int frogX = this.frog.getX();
        int pipeX = pipe.getX();

        if (isCollide(this.frog.getCollider(), pipe.getLowerPipe().getBounds())
                || isCollide(this.frog.getCollider(), pipe.getUpperPipe().getBounds())) {
            this.makeGameOver();
            return true;
        }

        if (pipeX < frogX) {
            increasePoint();
            pipe.setCalculatedPoint(true);
        }

        return false;
    }

    private void increasePoint() {
        this.scoreLabel.setText(String.valueOf(++this.score));
    }

    private void updatePoint() {
        System.out.println(scoreLabel);
        this.scoreLabel.setText(String.valueOf(this.score));
    }

    //==========================END POINTS MANAGEMENT===========================
    //
    //==========================COLLIDER========================================
    /**
     * Check collision
     *
     * @param colliderA
     * @param colliderB
     * @return true if two objects collide
     */
    private boolean isCollide(Rectangle colliderA, Rectangle colliderB) {
        return colliderA.intersects(colliderB);
    }

    private boolean isInsideScene(Rectangle object) {
        return gameSceneCollider.contains(object);
    }

    //==========================END COLLIDER====================================
    //
    //==========================GAME OVER=======================================
    private void makeGameOver() {
        gamePlaying = false;

        this.setFocusable(false);

        // Get Parent frame
        Window parentWindow = SwingUtilities.windowForComponent(this);
        OpenUI parentFrame = (OpenUI) parentWindow;

        // Tell frame that the game is over
        parentFrame.isGameover(score);
    }

    //==========================END GAME OVER===================================
    //
    //==========================GAME OVER=======================================
    public void saveGame() {
        File saveFile = null;
        FileWriter saveFileWriter = null;
        try {
            saveFile = new File(this.saveFilePath);
            saveFile.createNewFile();

            saveFileWriter = new FileWriter(saveFile);
            saveFileWriter.write(frog.toString() + "\n");
            saveFileWriter.write(score + "\n");
            for (Pipe pipe : pipes) {
                saveFileWriter.write(pipe.toString() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (saveFileWriter != null) {
                try {
                    saveFileWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public void deleteSaveGame() {
        File saveFile = new File(saveFilePath);
        saveFile.delete();
    }

    public boolean isSavedGame() {
        File saveFile = new File(this.saveFilePath);
        return saveFile.exists();
    }

    public boolean recoverSave() {
        Point frogPositon;
        String line;
        int score;
        LinkedList<Pipe> pipes = new LinkedList<>();
        File saveFile = null;
        InputStream is = null;
        BufferedReader buf = null;
        try {
            saveFile = new File(this.saveFilePath);
            is = new FileInputStream(saveFile);
            buf = new BufferedReader(new InputStreamReader(is));

            //frog position
            line = buf.readLine();
            System.out.println(line);
            frogPositon = recoverFrogPosistion(line);

            //score
            line = buf.readLine();
            score = recoverScore(line);

            while (line != null) {
                line = buf.readLine();
                if (line != null)
                pipes.addFirst(recoverPipe(line));
            }

            // Recover Success
            addCharacter(frogPositon);
            this.pipes = pipes;
            this.addPipesToScreen();
            this.updatePoint();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (buf != null) {
                    buf.close();
                }
                this.deleteSaveGame();
            } catch (Exception e) {
            }
        }
        return false;
    }

    private Point recoverFrogPosistion(String frogPosition) throws Exception {
        String[] position = frogPosition.split(",");

        System.out.println(Arrays.toString(position));
        if (position.length > 2) { // Wrong format
            throw new Exception();
        }

        try {
            int x = Integer.parseInt(position[0]);
            int y = Integer.parseInt(position[1]);
            return new Point(x, y);

        } catch (Exception e) {
            throw e;
        }
    }

    private int recoverScore(String score) throws Exception {
        try {
            return Integer.parseInt(score);
        } catch (Exception e) {
            throw e;
        }
    }

    private Pipe recoverPipe(String pipeInfo) throws Exception {
        Pipe pipe = new Pipe(this.getWidth(), this.getHeight());

        String[] pipeInfoSplitted = pipeInfo.split(",");
        try {
            if (pipeInfoSplitted.length > 5) {
                throw new Exception();
            }

            int x = Integer.parseInt(pipeInfoSplitted[0]);
            int upperPipeHeight = Integer.parseInt(pipeInfoSplitted[1]);
            int holeSize = Integer.parseInt(pipeInfoSplitted[2]);
            int distance = Integer.parseInt(pipeInfoSplitted[3]);
            int calculatedPoint = Integer.parseInt(pipeInfoSplitted[4]);
            pipe.generatePipes(x, upperPipeHeight, holeSize, distance, calculatedPoint);

            return pipe;
        } catch (Exception e) {
            throw e;
        }
    }

    private void addPipesToScreen() {
        for (Pipe pipe : pipes) {
            this.add(pipe.getLowerPipe());
            this.add(pipe.getUpperPipe());
            pipe.updateLevel(this.score / 10 + 1);
            System.out.println("Added: " + pipe);
        }

        this.revalidate();
        this.repaint();
    }

    //==========================END GAME OVER===================================
    /**
     * Creates new form GameSceneGenerated
     * @param scoreLabel
     */
    public GameScene(JLabel scoreLabel) {
        initComponents();

        score = 0;

        // score label
        this.scoreLabel = scoreLabel;

        // ready Label
        readyLabel = new JLabel("Press anykey to start playing.");
        readyLabel.setFont(new Font("Consolas", Font.BOLD, 34));
        readyLabel.setBounds(new Rectangle(getReadyLabelPostion(), readyLabel.getPreferredSize()));
        readyLabel.setHorizontalAlignment(JLabel.RIGHT);

        // Pipes
        pipes = new LinkedList<>();

    }

    public void setScorePanel(JLabel scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
