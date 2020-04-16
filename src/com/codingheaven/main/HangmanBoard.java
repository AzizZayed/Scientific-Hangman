package com.codingheaven.main;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * the board that contains the HangMan game
 * 
 * @author Zayed
 *
 */
public class HangmanBoard {

	private final int kSTATES = 7; // number of chances (states), basically number of images
	private Image[] states = new Image[kSTATES]; // all images
	private int imageWidth;
	private int state = 1; // current state

	private String[] elements; // all the 118 elements of the periodic table
	private String word, used = ""; // current word, and used letters
	private int wordIndex; // location of the word in the array

	private int goodGuesses = 0; // keep count of the number of good guesses
	private boolean[] guessed; // keep track of which letters from the word were guessed

	/**
	 * constructor
	 */
	public HangmanBoard() {
		loadElements();
		loadImages();
		chooseWord();
	}

	/**
	 * Load all the 118 periodic elements from the text file
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
		states[0] = new ImageIcon("res/animations/anim0.png").getImage();
		imageWidth = states[0].getWidth(null);

		for (int i = 1; i < kSTATES; i++) {
			states[i] = new ImageIcon("res/animations/anim" + i + ".gif").getImage();
		}
	}

	/**
	 * Chose a word randomly from the list
	 */
	private void chooseWord() {
		wordIndex = (int) (Math.random() * elements.length);
		word = elements[wordIndex];
		guessed = new boolean[word.length()];

	}

	/**
	 * @return the imageWidth
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 * Restart the game
	 */
	public void restart() {

		// reset GIF loops because they are set to loop only once
		for (int i = 1; i < kSTATES; i++) {
			states[i].flush();
		}

		chooseWord();

		state = 1;
		used = "";
		goodGuesses = 0;
	}

	/**
	 * Checks if character entered is part of the word, and check if the player won
	 * or lost too and act accordingly
	 * 
	 * @param keyChar       - the entered character
	 * @param gameReference - reference to the JPanel parent that contains the board
	 */
	public void checkChar(char keyChar, ScientificHangman gameReference) {

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

					int triesLeft = kSTATES - state;
					String tries = (triesLeft == 1 ? " try" : " tries");

					notify(gameReference, "You got it! You had " + triesLeft + tries + " before you died.");// win
					restart();
				}
			}
		}

		if (!contains) {
			if (state < kSTATES)
				state++;

			if (state == kSTATES) {
				notify(gameReference, "You Lost. It was element " + (wordIndex + 1) + ", " + word);// lost
				restart();
			}
		}
	}

	/**
	 * message box with desired message
	 * 
	 * @param gameReference - reference to the JPanel parent that contains the board
	 * @param message       - the desired message to display
	 */
	private void notify(ScientificHangman gameReference, String message) {
		JOptionPane.showMessageDialog(gameReference, message, "Hang Man", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * draw the board
	 * 
	 * @param g      - tool to draw
	 * @param width  - width of board
	 * @param height - height of board
	 * @param ref    - reference to the JPanel parent containing the board
	 */
	public void draw(Graphics g, int width, int height, ScientificHangman ref) {
		((Graphics2D) g).drawImage(states[state - 1], 0, 0, ref);

		// draw the lines for the game and guessed letters
		drawWord(g, width);

		// draw info under word and under letters
		drawInfo(g, height);

	}

	/**
	 * draw the lines for the game and guessed letters of the word
	 * 
	 * @param g     - tool to draw
	 * @param width - width of board
	 */
	private void drawWord(Graphics g, int width) {
		int space = 5;
		int x = imageWidth - 50;
		int y = 150;
		int lineSize = (width - x - space * 4) / word.length() - space;

		g.setFont(new Font("Times New Roman", Font.BOLD, lineSize));

		for (int i = 0; i < guessed.length; i++) {
			// draw line
			g.drawLine(x, y, x + lineSize, y);

			// draw letters or whole words if needed
			if (state == kSTATES)
				for (int j = 0; j < guessed.length; j++)
					guessed[i] = true;

			String letter = word.substring(i, i + 1);
			int strWidth = g.getFontMetrics().stringWidth(letter);

			if (guessed[i])
				g.drawString(letter, x + lineSize / 2 - strWidth / 2, y - 2);

			x += lineSize + space;
		}
	}

	/**
	 * draw info under the word and the used letters
	 * 
	 * @param g      - tool to draw
	 * @param height - height of board
	 */
	private void drawInfo(Graphics g, int height) {
		// draw info under word
		int txtSize = 15;
		int x = imageWidth - 50;
		int y = 150;

		g.setFont(new Font("Calibri", Font.PLAIN, txtSize));

		g.drawString("Space bar to change word.", x, y + txtSize + 5);

		// draw used letters
		txtSize = 20;
		g.setFont(new Font("Times New Roman", Font.ITALIC | Font.BOLD, txtSize));
		int max = 39;

		if (used.length() < max) {
			g.drawString("Used Letters: " + used, x, height - txtSize * 3);
		} else {
			g.drawString("Used Letters: " + used.substring(0, max), x, height - txtSize * 3);
			g.drawString("              " + used.substring(max), x, height - txtSize * 2);
		}
	}

}
