package state;

import state.states.MenuState;
import state.states.PlayState;

import java.awt.*;

public class StateManager
{
	private static final int NUMBER_OF_STATES = 2;

	public static final int MENU_STATE = 0;
	public static final int PLAY_STATE = 1;

	private final State[] states;
	private int currentState;

	public boolean PAUSED;

	public StateManager()
	{
		states = new State[NUMBER_OF_STATES];
		currentState = MENU_STATE;
		loadState(currentState);

		PAUSED = false;
	}

	public void update()
	{
		if(states[currentState] != null) states[currentState].update();
	}

	public void draw(Graphics2D g2d)
	{
		if(states[currentState] != null) states[currentState].draw(g2d);
	}

	private void loadState(int state)
	{
		if(state == MENU_STATE) states[state] = new MenuState(this);
		if(state == PLAY_STATE) states[state] = new PlayState(this);
	}

	public void setState(int state)
	{
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}

	private void unloadState(int state)
	{
		states[state] = null;
	}

	public void keyPressed(int key)
	{
		states[currentState].keyPressed(key);
	}

	public void keyReleased(int key)
	{
		states[currentState].keyReleased(key);
	}
}
