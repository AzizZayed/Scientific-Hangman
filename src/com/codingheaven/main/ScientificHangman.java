package com.codingheaven.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ScientificHangman extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 800, HEIGHT = 600; // width and height of panel

	private static final int kSTATES = 7; // number of chances (states), basically number of images
	private static BufferedImage[] states = new BufferedImage[kSTATES]; // all images
	private int state = 1; // current state

	private static String[] elements; // all the 118 elements of the periodic table
	private String word, used = ""; // current words, and used letters

	private int goodGuesses = 0; // keep count of the number of good guesses
	private boolean[] guessed; // keep track of which letters from the word were guessed

	/**
	 * Constructor
	 */
	public ScientificHangman() {
		canvasSetup();
		loadImages();
		loadElements();
		chooseWord();
	}

	/**
	 * Load all the 118 periodic elements from a text file
	 */
	private void loadElements() {
		Scanner scanner;
		int count = 0;
		Path path = Paths.get("res/elements.txt");

		try {
			scanner = new Scanner(path);
			count = (int) Files.lines(path).count();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		elements = new String[count];

		int i = 0;
		while (scanner.hasNextLine()) {
			elements[i] = scanner.nextLine();
			i++;
		}
		scanner.close();
	}

	/**
	 * Load all images from the res folder
	 */
	private void loadImages() {

		for (int i = 0; i < kSTATES; i++) {
			try {
				states[i] = ImageIO.read(new File("res/pictures/pic" + (i + 1) + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
					checkChar(key);
				} else if (key == ' ') {
					restart();
					repaint();
				}
			}

		});
	}

	/**
	 * Chose a word randomly from the list
	 */
	private void chooseWord() {
		int randomIndex = (int) (Math.random() * elements.length);

		word = elements[randomIndex];

		guessed = new boolean[word.length()];

	}

	/**
	 * Restart the game
	 */
	private void restart() {
		chooseWord();

		state = 1;
		used = "";
		goodGuesses = 0;
	}

	/**
	 * Checks if character entered is part of the word, and check if the player won
	 * or lost too and act accordingly 
	 * 
	 * @param keyChar, the entered character
	 */
	private void checkChar(char keyChar) {

		if (used.contains(Character.toString(keyChar)))
			return;

		used += (keyChar + ", ");

		boolean contains = false;

		// check if letter is in word
		for (int i = 0; i < word.length(); i++) {
			char letter = word.charAt(i);

			if (letter == keyChar) {
				guessed[i] = true;
				contains = true;
				goodGuesses++;

				// win check
				if (goodGuesses == word.length()) {
					repaint();

					int triesLeft = kSTATES - state;

					JOptionPane.showMessageDialog(this, "You got it! You had " + triesLeft + " "
							+ (triesLeft == 1 ? "try" : "tries") + " before you died.", "Hang Man",
							JOptionPane.INFORMATION_MESSAGE);
					restart();
				}

			}

		}

		if (!contains) {
			if (state < kSTATES)
				state++;

			repaint();

			if (state == kSTATES) {
				JOptionPane.showMessageDialog(this,
						"You Lost. It was element " + (linearSearch(elements, word) + 1) + ", " + word, "Hang Man",
						JOptionPane.INFORMATION_MESSAGE);
				restart();
			}
		}

		repaint();
	}

	/**
	 * regular linear search algorithm, searches for the index of the element in the element array
	 * @param arr, the array we wish to search in
	 * @param elementToSearch, the element we wish to find
	 * @return the index of the element we wish to find, returns -1 if doesn't exist
	 */
	private int linearSearch(String arr[], String elementToSearch) {

		for (int index = 0; index < arr.length; index++) {
			if (arr[index].equals(elementToSearch))
				return index;
		}
		return -1;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		// hang man
		g2d.drawImage(states[state - 1], 0, 0, null);

		// text
		g.setColor(Color.BLACK);

		Font font;
		int txtSize;

		// draw the lines for the game and guessed letters

		int space = 5;
		int x = states[0].getWidth() - 50;
		int y = 150;
		int lineSize = (WIDTH - x - space * 4) / word.length() - space;

		txtSize = lineSize;
		font = new Font("Times New Roman", Font.BOLD, txtSize);
		g.setFont(font);

		for (int i = 0; i < guessed.length; i++) {
			// draw line
			g.drawLine(x, y, x + lineSize, y);

			// draw letters or whole words if needed
			if (state == kSTATES)
				for (int j = 0; j < guessed.length; j++)
					guessed[i] = true;

			String letter = word.substring(i, i + 1);
			int strWidth = g.getFontMetrics(font).stringWidth(letter);

			if (guessed[i])
				g.drawString(letter, x + lineSize / 2 - strWidth / 2, y - 2);

			x += lineSize + space;
		}

		// draw info
		txtSize = 15;
		x = states[0].getWidth() - 50;
		font = new Font("Calibri", Font.PLAIN, txtSize);
		g.setFont(font);

		g.drawString("Space bar to change word.", x, y + txtSize + 5);

		// draw used letters
		txtSize = 20;
		font = new Font("Times New Roman", Font.ITALIC | Font.BOLD, txtSize);
		g.setFont(font);
		int max = 39;

		if (used.length() < max) {
			g.drawString("Used Letters: " + used, x, HEIGHT - txtSize * 3);
		} else {
			g.drawString("Used Letters: " + used.substring(0, max), x, HEIGHT - txtSize * 3);
			g.drawString("              " + used.substring(max), x, HEIGHT - txtSize * 2);
		}
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
