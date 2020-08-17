/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;
import java.util.LinkedList;

/**
 *
 * @author conqu
 */
public class SaveGameModel {
    private Point frogLocation;
    private int score;
    private LinkedList<Pipe> pipes;

    public SaveGameModel(Point frogLocation, int score, LinkedList<Pipe> pipes) {
        this.frogLocation = frogLocation;
        this.score = score;
        this.pipes = pipes;
    }

    public Point getFrogLocation() {
        return frogLocation;
    }

    public void setFrogLocation(Point frogLocation) {
        this.frogLocation = frogLocation;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LinkedList<Pipe> getPipes() {
        return pipes;
    }

    public void setPipes(LinkedList<Pipe> pipes) {
        this.pipes = pipes;
    }
}
