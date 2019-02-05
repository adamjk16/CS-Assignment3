/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	// Dimensions of the canvas, in pixels
	// These should be used when setting up the initial size of the game,
	// but in later calculations you should use getWidth() and getHeight()
	// rather than these constants for accurate size information.
	public static final double CANVAS_WIDTH = 420;
	public static final double CANVAS_HEIGHT = 600;

	// Number of bricks in each row
	public static final int NBRICK_COLUMNS = 10;

	// Number of rows of bricks
	public static final int NBRICK_ROWS = 10;

	// Separation between neighboring bricks, in pixels
	public static final double BRICK_SEP = 4;

	// Width of each brick, in pixels
	public static final double BRICK_WIDTH = Math.floor(
			(CANVAS_WIDTH - (NBRICK_COLUMNS + 1.0) * BRICK_SEP) / NBRICK_COLUMNS);

	// Height of each brick, in pixels
	public static final double BRICK_HEIGHT = 8;

	// Offset of the top brick row from the top, in pixels
	public static final double BRICK_Y_OFFSET = 70;

	// Dimensions of the paddle
	public static final double PADDLE_WIDTH = 60;
	public static final double PADDLE_HEIGHT = 10;

	// Offset of the paddle up from the bottom 
	public static final double PADDLE_Y_OFFSET = 30;

	// Radius of the ball in pixels
	public static final double BALL_RADIUS = 10;

	// The ball's vertical velocity.
	public static final double VELOCITY_Y = 3.0;

	// The ball's minimum and maximum horizontal velocity; the bounds of the
	// initial random velocity that you should choose (randomly +/-).
	public static final double VELOCITY_X_MIN = 1.0;
	public static final double VELOCITY_X_MAX = 3.0;

	// Animation delay or pause time between ball moves (ms)
	public static final double DELAY = 1000.0 / 60.0;

	// Number of turns 
	public static final int NTURNS = 3;

	public void run() {
		// Set the window's title bar text
		setTitle("CS 106A Breakout");

		// Set the canvas size.  In your code, remember to ALWAYS use getWidth()
		// and getHeight() to get the screen dimensions, not these constants!
		setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		
		for (int i = 0; i < NTURNS; i++) {
			setUpGame();
			playGame();
			if (bricks == 0) {
				terminateGame();
			}
		}
		gameOver();
	}

	private void setUpGame() {
		setUpBricks();
		setUpPaddle();
		setUpBall();
	}

	private void setUpBricks() {
		double x = getWidth()/2 - BRICK_SEP/2 - (BRICK_WIDTH * (NBRICK_COLUMNS/2)) - ((NBRICK_COLUMNS/2 - 1) * BRICK_SEP);
		double y = BRICK_Y_OFFSET;
		for (int rowNumber = 1; rowNumber <= NBRICK_ROWS; rowNumber++) {
			for (int layBricks = 0; layBricks < NBRICK_COLUMNS; layBricks++) {
				GRect brick = new GRect (BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (rowNumber == 1 || rowNumber == 2) {
					brick.setColor(Color.RED);
				} else if (rowNumber == 3 || rowNumber == 4) {
					brick.setColor(Color.ORANGE);
				} else if (rowNumber == 5 || rowNumber == 6) {
					brick.setColor(Color.YELLOW);
				} else if (rowNumber == 7 || rowNumber == 8) {
					brick.setColor(Color.GREEN);
				} else if (rowNumber == 9 || rowNumber == 10) {
					brick.setColor(Color.CYAN);
				}
				add (brick, x + (layBricks * (BRICK_SEP + BRICK_WIDTH)), y + (rowNumber * (BRICK_HEIGHT + BRICK_SEP)));	
			}
		}
	}

	private GRect paddle = null; 

	private void setUpPaddle() {
		double x = getWidth()/2 - PADDLE_WIDTH/2;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		paddle = new GRect (PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add (paddle, x, y);
		addMouseListeners();
	}

	public void mouseMoved (MouseEvent e) {
		int x = e.getX();
		if (x < getWidth() - PADDLE_WIDTH/2  && x > PADDLE_WIDTH/2) {
			paddle.setLocation(x - PADDLE_WIDTH/2, getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT/2);
		}
	}

	GOval ball = null;

	private void setUpBall() {
		double x = getWidth()/2 - BALL_RADIUS;
		double y = getHeight()/2 - BALL_RADIUS;
		ball = new GOval (BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add (ball, x, y);
	}

	private void playGame() {
		bounceBall();
	}

	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	private double vx, vy;

	int bricks = 100;

	private void bounceBall() {
		vy = VELOCITY_Y;
		vx = rgen.nextDouble (1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while (true) {
			addMouseListeners();
			ball.move(vx, vy);
			pause (DELAY);
			if (ball.getX() <= 0 || ball.getX() >= getWidth() - (ball.getWidth() / 2)) {
				vx = -vx;
			}
			if (ball.getY() <= 0) {
				vy = -vy;
			}
			if (ball.getY() > getHeight() - (ball.getHeight() / 2)) {
				terminateGame();
				break;
			}
			
			GObject collider = getCollidingObject();
			if (collider == paddle) {
				if (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2) {
				vy = -vy;
				}
			} else if (collider != null) {
				remove (collider);
				vy = -vy;
				bricks--;
			}
		}
	}
	public void mouseClicked (MouseEvent e) {
		
	}

	private GObject getCollidingObject() {

		if (getElementAt (ball.getX(), ball.getY()) != null) {
			return getElementAt (ball.getX(), ball.getY());
		}
		if (getElementAt (ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null) {
			return getElementAt (ball.getX() + (2 * BALL_RADIUS), ball.getY());
		}
		if (getElementAt (ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt (ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		}
		if (getElementAt (ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)) != null) {
			return getElementAt (ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		} else {
			return null;
		}

	}

	private void terminateGame() {
		tryAgain();
		remove (paddle);
		remove (ball);
		startOver();
	}
	private void gameOver() {
		GLabel gameOver = new GLabel ("Game Over!");
		gameOver.setFont ("SansSerif-28");
		double x = getWidth()/2 - gameOver.getWidth()/2;
		double y = getHeight()/2 - gameOver.getAscent()/2;
		add (gameOver, x, y);
	}
	
	private GLabel tryAgain;
	
	private void tryAgain() {
		tryAgain = new GLabel ("Try Again!");
		tryAgain.setFont ("SansSerif-28");
		double x = getWidth()/2 - tryAgain.getWidth()/2;
		double y = getHeight()/2 - tryAgain.getAscent()/2;
		add (tryAgain, x, y);
	}
	
	private void startOver() {
		waitForClick();
		remove (tryAgain);
	}
}

