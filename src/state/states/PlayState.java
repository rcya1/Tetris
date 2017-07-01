package state.states;

import components.Board;
import components.Piece;
import main.GamePanel;
import state.State;
import state.StateManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

public class PlayState extends State
{
	private StateManager stateManager;

	private Board board;

	private boolean paused;

	public PlayState(StateManager stateManager)
	{
		this.stateManager = stateManager;
		init();
	}

	public void init()
	{
		board = new Board();
		board.init();

		paused = false;
	}

	public void update()
	{
		if(!paused) board.update();
	}

	public void draw(Graphics2D g2d)
	{
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		AffineTransform transform = g2d.getTransform();
		g2d.translate(100, 0);
		board.draw(g2d);
		g2d.setTransform(transform);

		g2d.setFont(new Font("Default", Font.PLAIN, 24));
		g2d.setColor(Color.WHITE);


		g2d.drawString("Level", 22, 300);
		int width = g2d.getFontMetrics().stringWidth(Integer.toString(board.getLevel()));
		g2d.drawString(Integer.toString(board.getLevel()), (104 - width) / 2, 330);

		g2d.drawString("Points", 16, 380);
		width = g2d.getFontMetrics().stringWidth(Integer.toString(board.getPoints()));
		g2d.drawString(Integer.toString(board.getPoints()), (104 - width) / 2, 410);

		g2d.drawString("Lines", 20, 460);
		g2d.drawString("Cleared", 8, 490);
		width = g2d.getFontMetrics().stringWidth(Integer.toString(board.getLinesCleared()));
		g2d.drawString(Integer.toString(board.getLinesCleared()), (104 - width) / 2, 520);

		g2d.drawString("Hold", 24, 60);
		g2d.drawString("Piece", 20, 84);
		if(board.getHoldPiece() != null)
		{
			boolean[][] pieceHitbox = board.getHoldPiece().getArray();
			for(int row = 0; row < pieceHitbox.length; row++)
			{
				for(int column = 0; column < pieceHitbox[0].length; column++)
				{
					if(pieceHitbox[row][column])
					{
						g2d.setColor(board.getHoldPiece().getColor());
						g2d.fillRect(2 + column * board.getTileLength(),
								90 + row * board.getTileLength(), board.getTileLength(),
								board.getTileLength());
						g2d.setColor(Color.DARK_GRAY);
						g2d.drawRect(2 + column * board.getTileLength(),
								90 + row * board.getTileLength(), board.getTileLength(),
								board.getTileLength());
					}
				}
			}
		}

		g2d.setColor(Color.WHITE);
		g2d.drawString("Next", 446, 60);
		g2d.drawString("Pieces", 434, 84);
		for(int i = 0; i < 3; i++)
		{
			Piece piece = board.getNextPieces()[i];
			boolean[][] pieceHitbox = piece.getArray();
			for(int row = 0; row < pieceHitbox.length; row++)
			{
				for(int column = 0; column < pieceHitbox[0].length; column++)
				{
					if(pieceHitbox[row][column])
					{
						g2d.setColor(piece.getColor());
						g2d.fillRect(420 + column * board.getTileLength(),
								100 + row * board.getTileLength() + 140 * i,
								board.getTileLength(),
								board.getTileLength());
						g2d.setColor(Color.DARK_GRAY);
						g2d.drawRect(420 + column * board.getTileLength(),
								100 + row * board.getTileLength() + 140 * i,
								board.getTileLength(),
								board.getTileLength());
					}
				}
			}
		}

		if(paused)
		{
			g2d.setColor(new Color(255, 255, 255, 100));
			g2d.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
	}

	public void keyPressed(int key)
	{
		board.keyPressed(key);
		if(key == KeyEvent.VK_ESCAPE) paused = !paused;
	}

	public void keyReleased(int key)
	{
		board.keyReleased(key);
	}
}
