package components;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class Board
{
	private int[][] board;
	private int numOfColumns;
	private int numOfRows;

	private int tileLength;

	private CurrentPiece currentPiece;
	private CurrentPiece ghostPiece;
	private Piece holdPiece;
	private boolean switched;

	private Piece[] nextPieces;

	private int frame;
	private int lockTimer;
	private int lineClearTimer;

	private double level;
	private int linesCleared;
	private int points;

	private boolean tSpin;

	private ArrayList<Integer> linesToBeCleared;

	private String state;

	private boolean moveLeft;
	private boolean moveRight;
	private boolean moveDown;
	private boolean rotateClock;
	private boolean rotateCounter;
	private boolean hardDrop;
	private boolean pieceSwitch;

	private int movementTimer;
	private int rotateTimer;
	private int hardDropTimer;

	public void init()
	{
		numOfColumns = 10;
		numOfRows = 22;

		board = new int[numOfColumns][numOfRows];

		tileLength = 32;

		currentPiece = new CurrentPiece(generateRandomPiece(null), tileLength, getLevel());
		ghostPiece = new CurrentPiece(currentPiece.getPiece(), tileLength, getLevel());

		nextPieces = new Piece[] {generateRandomPiece(currentPiece.getPiece()), null, null};
		nextPieces[1] = generateRandomPiece(nextPieces[0]);
		nextPieces[2] = generateRandomPiece(nextPieces[1]);

		switched = false;

		lockTimer = 0;
		lineClearTimer = 0;
		frame = 0;

		level = 1;
		linesCleared = 0;
		points = 0;

		linesToBeCleared = new ArrayList<>();

		state = "PLAY";

		movementTimer = 0;
		rotateTimer = 0;
		hardDropTimer = 0;
	}

	public void update()
	{
		if(state.equals("PLAY"))
		{
			this.points += currentPiece.update(frame);
		}

		tSpin = false;

		handleKeyActions();

		ghostPiece.setRow(currentPiece.getRow());
		ghostPiece.setColumn(currentPiece.getColumn());
		ghostPiece.setPieceHitbox(currentPiece.getPieceHitbox());
		ghostPiece.hardDrop(board);

		checkBounds();

		handleCollisions();
		handleLineClearing();

		level = 1 + (linesCleared / 10.0);

		if(tSpin) System.out.println("T-Spin Detected");

		frame++;

		if(movementTimer > 0) movementTimer--;
		if(rotateTimer > 0) rotateTimer--;
		if(hardDropTimer > 0) hardDropTimer--;
	}

	private void handleCollisions()
	{
		if(checkCollision()) lockPiece(true);
		else state = "PLAY";

		if(lockTimer > 0 && state.equals("PLAY")) lockTimer = 0;
	}

	private boolean checkCollision()
	{
		boolean generateFlag = false; //Triggered if need to generate a new piece
		for(int hbColumn = 0; hbColumn < currentPiece.getPieceHitbox()[0].length; hbColumn++)
		{
			for(int hbRow = 0; hbRow < currentPiece.getPieceHitbox().length; hbRow++)
			{
				if(currentPiece.getRow() + hbRow + 1 > numOfRows - 1
						&& currentPiece.getPieceHitbox()[hbRow][hbColumn])
				{
					generateFlag = true;
					break;
				}

				if(currentPiece.getColumn() + hbColumn >= 0 && currentPiece.getColumn() + hbColumn < board.length &&
						currentPiece.getRow() + hbRow + 1 >= 0 && currentPiece.getRow() + hbRow + 1 < board[0].length)
				{
					if(currentPiece.getPieceHitbox()[hbRow][hbColumn] &&
							board[currentPiece.getColumn() + hbColumn][currentPiece.getRow() + hbRow + 1] != 0)
					{
						generateFlag = true;
						break;
					}
				}
			}
			if(generateFlag) break;
		}

		return generateFlag;
	}

	private void lockPiece(boolean useLockDelay)
	{
		if(useLockDelay)
		{
			if(lockTimer == 0)
			{
				state = "LOCK";
				lockTimer = 40 - getLevel();
				if(lockTimer < 20) lockTimer = 20;
			}
			else lockTimer--;
		}

		if(lockTimer == 0)
		{
			state = "PLAY";

			for(int hbColumn = 0; hbColumn < currentPiece.getPieceHitbox()[0].length; hbColumn++)
			{
				for(int hbRow = 0; hbRow < currentPiece.getPieceHitbox().length; hbRow++)
				{
					if(currentPiece.getPieceHitbox()[hbRow][hbColumn])
					{
						board[currentPiece.getColumn() + hbColumn][currentPiece.getRow()
								+ hbRow] = currentPiece.getPieceID();
					}
				}
			}

			tSpin = currentPiece.detectTSpin(board);

			generateNewCurrentPiece();

			switched = false;
		}
	}

	private void handleLineClearing()
	{
		Iterator iterator = linesToBeCleared.iterator();

		if(iterator.hasNext())
		{
			state = "CLEAR";
			if(lineClearTimer == 0) lineClearTimer = 30;
			else lineClearTimer--;
		}

		clearKnownLines(iterator);
		checkForLines();
	}

	private void clearKnownLines(Iterator linesToBeClearedIterator)
	{
		if(lineClearTimer == 0)
		{
			handleScoring();

			while(linesToBeClearedIterator.hasNext())
			{
				Integer lineToBeCleared = (Integer) linesToBeClearedIterator.next();

				int[][] tempBoard = new int[numOfColumns][numOfRows];
				for(int i = 0; i < board.length; i++)
				{
					System.arraycopy(board[i], 0, tempBoard[i], 0, numOfRows);
				}

				for(int row = 0; row < lineToBeCleared; row++)
				{
					for(int column = 0; column < board.length; column++)
					{
						tempBoard[column][row + 1] = board[column][row];
					}
				}

				board = tempBoard;

				linesToBeClearedIterator.remove();
				linesCleared++;

				state = "PLAY";
			}
		}
	}

	private void checkForLines()
	{
		if(state.equals("PLAY"))
		{
			for(int row = 0; row < board[0].length; row++)
			{
				boolean lineFlag = true;
				for(int[] column : board)
				{
					if(column[row] == 0) lineFlag = false;
				}
				if(lineFlag)
				{
					linesToBeCleared.add(row);
				}
			}
		}
	}

	private void handleScoring()
	{
		int levelPoints = 0;
		switch(linesToBeCleared.size())
		{
		case 0:
			levelPoints = 0;
			break;
		case 1:
			levelPoints = 100;
			if(tSpin) levelPoints = 800;
			break;
		case 2:
			levelPoints = 300;
			if(tSpin) levelPoints = 1200;
			break;
		case 3:
			levelPoints = 400;
			if(tSpin) levelPoints = 1600;
			break;
		case 4:
			levelPoints = 800;
			break;
		}

		points += (int) level * levelPoints;
	}

	public void draw(Graphics2D g2d)
	{
		g2d.setStroke(new BasicStroke(1));
		for(int column = 0; column < numOfColumns; column++)
		{
			for(int row = 0; row < numOfRows; row++)
			{
				g2d.setColor(Piece.getColor(board[column][row]));
				if(linesToBeCleared.contains(row)) g2d.setColor(Color.WHITE);
				g2d.fillRect(column * tileLength, (row - 2) * tileLength, tileLength, tileLength);
			}
		}

		ghostPiece.drawGhost(g2d);
		currentPiece.draw(g2d);

		//Draw the Grid
		for(int column = 0; column < numOfColumns + 1; column++)
		{
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawLine(column * tileLength, 0, column * tileLength, numOfRows * tileLength);
		}

		for(int row = 0; row < numOfRows; row++)
		{
			g2d.setColor(Color.DARK_GRAY);
			g2d.drawLine(0, row * tileLength, numOfColumns * tileLength, row * tileLength);
		}
	}

	//Check to make sure the currentPiece is within bounds
	private void checkBounds()
	{
		if(currentPiece.getHitboxLeftColumn() < 0)
		{
			int offset = 0 - currentPiece.getHitboxLeftColumn();
			currentPiece.setColumn(currentPiece.getColumn() + offset);
		}
		if(currentPiece.getHitboxRightColumn() > numOfColumns - 1)
		{
			int offset = currentPiece.getHitboxRightColumn() - (numOfColumns - 1);
			currentPiece.setColumn(currentPiece.getColumn() - offset);
		}

		if(ghostPiece.getHitboxLeftColumn() < 0)
		{
			int offset = 0 - ghostPiece.getHitboxLeftColumn();
			ghostPiece.setColumn(ghostPiece.getColumn() + offset);
		}
		if(ghostPiece.getHitboxRightColumn() > numOfColumns - 1)
		{
			int offset = ghostPiece.getHitboxRightColumn() - (numOfColumns - 1);
			ghostPiece.setColumn(ghostPiece.getColumn() - offset);
		}
	}

	private Piece generateRandomPiece(Piece piece)
	{
		Piece random = Piece.parseID((int) (Math.random() * 7));
		if(random.equals(piece))
		{
			random = Piece.parseID((int) (Math.random() * 7));
		}

		return random;
	}

	private void handleKeyActions()
	{
		if(movementTimer == 0)
		{
			if(moveLeft)
			{
				currentPiece.move(-1, board);

				checkBounds();
			}
			if(moveRight)
			{
				currentPiece.move(1, board);

				checkBounds();
			}
			if(moveDown)
			{
				currentPiece.setDropping(true);
			}

			movementTimer = 5;
		}

		if(rotateTimer == 0)
		{
			if(rotateClock)
			{
				currentPiece.rotatePiece(1, board);

				checkBounds();
			}
			if(rotateCounter)
			{
				currentPiece.rotatePiece(-1, board);

				checkBounds();
			}

			rotateTimer = 7;
		}

		if(hardDrop && hardDropTimer == 0)
		{
			currentPiece.setDropping(true);
			points += currentPiece.hardDrop(board);
			checkBounds();
			lockPiece(false);

			rotateTimer = 0;
			movementTimer = 0;
			hardDropTimer = 10;
		}
		if(pieceSwitch)
		{
			if(!switched)
			{
				switchPiece();
			}
			checkBounds();
		}
	}

	private void switchPiece()
	{
		Piece hold = holdPiece;
		holdPiece = currentPiece.getPiece();
		if(hold != null) currentPiece = new CurrentPiece(hold,
				tileLength, getLevel());
		else
			generateNewCurrentPiece();
		switched = true;

		ghostPiece = new CurrentPiece(currentPiece.getPiece(), tileLength, getLevel());
	}

	private void generateNewCurrentPiece()
	{
		currentPiece = new CurrentPiece(nextPieces[0], tileLength, getLevel());
		nextPieces[0] = nextPieces[1];
		nextPieces[1] = nextPieces[2];
		nextPieces[2] = generateRandomPiece(nextPieces[1]);

		ghostPiece = new CurrentPiece(currentPiece.getPiece(), tileLength, getLevel());
	}

	public void keyPressed(int key)
	{
		switch(key)
		{
		case KeyEvent.VK_LEFT:
			moveLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = true;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = true;
			break;
		case KeyEvent.VK_Z:
			rotateCounter = true;
			break;
		case KeyEvent.VK_UP:
			rotateClock = true;
			break;
		case KeyEvent.VK_SPACE:
			hardDrop = true;
			break;
		case KeyEvent.VK_C:
			pieceSwitch = true;
			break;
		}
	}

	public void keyReleased(int key)
	{
		switch(key)
		{
		case KeyEvent.VK_LEFT:
			moveLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = false;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = false;
			currentPiece.setDropping(false);
			break;
		case KeyEvent.VK_Z:
			rotateCounter = false;
			break;
		case KeyEvent.VK_UP:
			rotateClock = false;
			break;
		case KeyEvent.VK_SPACE:
			hardDrop = false;
			break;
		case KeyEvent.VK_C:
			pieceSwitch = false;
			break;
		}
	}

	public int getTileLength()
	{
		return tileLength;
	}

	public int getLevel()
	{
		return (int) level;
	}

	public int getPoints()
	{
		return points;
	}

	public Piece getHoldPiece()
	{
		return holdPiece;
	}

	public int getLinesCleared()
	{
		return linesCleared;
	}

	public Piece[] getNextPieces()
	{
		return nextPieces;
	}
}
