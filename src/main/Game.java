package main;

import javax.swing.*;

class Game
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Tetris");
		frame.setContentPane(new GamePanel());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
