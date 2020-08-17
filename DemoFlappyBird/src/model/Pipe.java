/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;
import java.util.Random;
import javax.swing.JButton;

/**
 *
 * @author conqu
 */
public class Pipe {

    private final int pipeWidth = 50;
    // Better be higher than player height
    private final int pipeMinHeight = 70;

    // Distance between each pipes
    private int distance;
    private final int distanceMin = pipeWidth * 2 + 50;
    private final int distanceMax;

    // Must be equal or larger than Character size, default is frog height
    private final int holeSizeMin = 471 * 15 / 100 + 30;
    private final int speedMin = 2;

    private int screenHeight;
    private int screenWidth;

    private JButton upperPipe;
    private JButton lowerPipe;

    private int level;
    private int maxHoleSize = holeSizeMin + 100;
    private final int holeSizePerLevel = 40;

    private boolean calculatedPoint;

    //Constructor
    public Pipe(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.level = 1;
        this.generatePipes();

        // distance to the next pipe
        this.distanceMax = screenWidth / 3;
        this.distance = randomNumberInRange(distanceMin, distanceMax);
    }

    // Ulti
    public boolean isCalculatedPoint() {
        return calculatedPoint;
    }

    public void setCalculatedPoint(boolean calculatedPoint) {
        this.calculatedPoint = calculatedPoint;
    }

    public int getX() {
        return upperPipe.getX();
    }

    public void setScreenWidthAndHeight(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    private int getFirstAppearX() {
        return this.screenWidth + 10;
    }

    private int getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        int holeSize = lowerPipe.getY() - upperPipe.getHeight();
        return upperPipe.getX() + ","
                + upperPipe.getHeight() + ","
                + holeSize + ","
                + distance + ","
                + (calculatedPoint ? "1" : "0");
    }

    private int randomNumberInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min + 1)) + min;
    }

    //LEVEL
    public void updateLevel(int level) {
        this.level = level;
        this.maxHoleSize = Math.max(this.maxHoleSize - this.holeSizePerLevel * (level - 1), this.holeSizeMin);;
    }

    public void levelUp() {
        ++this.level;
        this.maxHoleSize = Math.max(this.maxHoleSize - this.holeSizePerLevel, this.holeSizeMin);
    }

    public void updateLevelByScore(int score) {
        // Level up each 10 points
        int level = score / 10 + 1;
        if (level > this.level) {
            levelUp();
        }
    }

    private int getMoveSpeed() {
        return this.level * speedMin;
    }

    private int getHoleSize() {
        return this.randomNumberInRange(holeSizeMin, maxHoleSize);
    }

    //GENERATE PIPE
    public void generatePipes(int xPosition, int upperPipeHeight, int holeSize, int distance, int calculatedPoint) {

        // Upper Pipe
        upperPipe = this.constructPipe();
        upperPipe.setBounds(
                xPosition,
                0,
                pipeWidth,
                upperPipeHeight);

        // Lower Pipe
        int lowerPipeY = upperPipeHeight + holeSize;
        int lowerPipeHeight = this.screenHeight - lowerPipeY;
        lowerPipe = this.constructPipe();
        lowerPipe.setBounds(
                xPosition,
                lowerPipeY,
                pipeWidth,
                lowerPipeHeight);

        this.maxHoleSize = holeSize;
        this.distance = randomNumberInRange(distanceMin, distanceMax);
        this.calculatedPoint = calculatedPoint > 0;
    }

    private void generatePipes() {
        // Hole Size
        int holeSize = this.getHoleSize();

        // Upper Pipe
        int upperPipeHeight = this.randomNumberInRange(this.pipeMinHeight,
                this.screenHeight - this.pipeMinHeight - this.maxHoleSize);
        upperPipe = this.constructPipe();
        upperPipe.setBounds(
                this.getFirstAppearX(),
                0,
                pipeWidth,
                upperPipeHeight);

        // Lower Pipe
        int lowerPipeY = upperPipeHeight + holeSize;
        int lowerPipeHeight = this.screenHeight - lowerPipeY;
        lowerPipe = this.constructPipe();
        lowerPipe.setBounds(
                this.getFirstAppearX(),
                lowerPipeY,
                pipeWidth,
                lowerPipeHeight);
    }

    private JButton constructPipe() {
        JButton pipe = new JButton();
        pipe.setFocusable(false);
        return pipe;
    }

    public boolean isGenerateNewPipe() {
        int x = upperPipe.getX();
        return x <= this.screenWidth - this.distance;
    }

    public boolean isOutOfScreen() {
        int x = upperPipe.getX();
        return x < 0 - this.pipeWidth;
    }

    //GET PIPES
    public JButton getUpperPipe() {
        return upperPipe;
    }

    public JButton getLowerPipe() {
        return lowerPipe;
    }

    //PIPE MOVEMENT
    public void movePipes() {
        this.movePipe(upperPipe);
        this.movePipe(lowerPipe);
    }

    private void movePipe(JButton pipe) {
        Point point = pipe.getLocation();
        pipe.setLocation(point.x - this.getMoveSpeed(), point.y);
    }
}
