package state.states;

import main.GamePanel;
import state.State;
import state.StateManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MenuState extends State
{
	private String[] options;
	private int selection;

	public MenuState(StateManager stateManager)
	{
		this.stateManager = stateManager;
		init();
	}

	public void init()
	{
		options = new String[] {"Play", "Exit"};
		selection = 0;
	}

	public void update()
	{

	}

	public void draw(Graphics2D g2d)
	{
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setFont(new Font("Default", Font.PLAIN, 48));

		for(int i = 0; i < options.length; i++)
		{
			g2d.setColor(Color.WHITE);
			if(i == selection) g2d.setColor(Color.RED);
			int length = g2d.getFontMetrics().stringWidth(options[i]);
			g2d.drawString(options[i], (GamePanel.WIDTH - length) / 2, 80 + 60 * i);
		}
	}

	public void keyPressed(int key)
	{
		switch(key)
		{
		case KeyEvent.VK_UP:
			if(selection > 0) selection--;
			break;

		case KeyEvent.VK_DOWN:
			if(selection < options.length - 1) selection++;
			break;

		case KeyEvent.VK_ENTER:
			switch(selection)
			{
			case 0: //Play
				stateManager.setState(StateManager.PLAY_STATE);
				break;
			case 1: //Exit
				System.exit(0);
				break;
			}
			break;
		}
	}

	public void keyReleased(int key)
	{

	}
}
