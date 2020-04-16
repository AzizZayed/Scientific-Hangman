package com.codingheaven.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * class to handle drawings and input
 * 
 * @author Zayed
 *
 */
public class ScientificHangman extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 800, HEIGHT = 600; // width and height of panel

	private HangmanBoard board;

	/**
	 * Constructor
	 */
	public ScientificHangman() {
		canvasSetup();
		init();
	}

	private void init() {
		board = new HangmanBoard();
	}

	/**
	 * setup canvas size, settings and events
	 */
	private void canvasSetup() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));

		this.setFocusable(true);

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				char key = Character.toUpperCase(e.getKeyChar());

				if (key >= 'A' && key <= 'Z') {
					board.checkChar(key, ScientificHangman.this);
				} else if (key == ' ') {
					board.restart();
				}

				repaint();
			}

		});
	}

	@Override
	public void paintComponent(Graphics g) {

		// background
		g.setColor(Color.WHITE);
		g.fillRect(board.getImageWidth(), 0, WIDTH, HEIGHT);

		// hang man
		g.setColor(Color.BLACK);
		board.draw(g, WIDTH, HEIGHT, this);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Scientific Hangman");
		ScientificHangman game = new ScientificHangman();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(game);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.setDoubleBuffered(true);

		game.repaint();
	}

}
