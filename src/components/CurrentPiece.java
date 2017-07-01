package components;

import java.awt.*;

class CurrentPiece
{
	private Piece piece;

	private int column;
	private int row;

	private int tileLength;

	private boolean[][] pieceHitbox;
	private int level;

	private boolean dropping;

	CurrentPiece(Piece piece, int tileLength, int level)
	{
		this.piece = piece;
		this.tileLength = tileLength;

		pieceHitbox = piece.getArray();

		column = 5 - (pieceHitbox[0].length / 2);
		row = -2;

		this.level = level;
	}

	int update(int frame)
	{
		if(!dropping)
		{
			int dropCounter = 46 - level * 2;
			if(dropCounter <= 0) dropCounter = 1;
			if(frame % dropCounter == 0) this.row++;
		}
		else
		{
			if(frame % 2 == 0)
			{
				this.row++;
				return 1;
			}
		}

		return 0;
	}

	void draw(Graphics2D g2d)
	{
		for(int row = 0; row < pieceHitbox.length; row++)
		{
			for(int column = 0; column < pieceHitbox[0].length; column++)
			{
				g2d.setColor(piece.getColor());
				if(pieceHitbox[row][column])
				{
					g2d.fillRect((this.column + column) * tileLength,
							(this.row + row) * tileLength, tileLength, tileLength);
				}
			}
		}
	}

	void drawGhost(Graphics2D g2d)
	{
		for(int row = 0; row < pieceHitbox.length; row++)
		{
			for(int column = 0; column < pieceHitbox[0].length; column++)
			{
				g2d.setColor(new Color(piece.getColor().getRed(), piece.getColor().getGreen(),
						piece.getColor().getBlue(), 100));
				if(pieceHitbox[row][column])
				{
					g2d.fillRect((this.column + column) * tileLength,
							(this.row + row) * tileLength, tileLength, tileLength);
				}
			}
		}
	}

	void move(int direction, int[][] board) //- for Left, + for Right
	{
		boolean moveFlag = true;
		for(int row = 0; row < pieceHitbox.length; row++)
		{
			for(int column = 0; column < pieceHitbox[0].length; column++)
			{
				int resultColumn = this.column + column + direction;
				if(this.row + row < board[0].length && resultColumn >= 0
						&& resultColumn < board.length)
				{
					if(this.row + row >= 0)
					{
						if(pieceHitbox[row][column] && board[resultColumn][this.row + row] != 0)
						{
							moveFlag = false;
							break;
						}
					}
				}
				else
				{
					moveFlag = true;
					break;
				}
			}
			if(!moveFlag) break;
		}
		if(moveFlag) this.column += direction;
	}

	int hardDrop(int[][] board)
	{
		boolean stopFlag = false;
		int points = 0;

		while(!stopFlag)
		{
			for(int row = 0; row < pieceHitbox.length; row++)
			{
				for(int column = 0; column < pieceHitbox[0].length; column++)
				{
					if(this.row + row < board[0].length &&
							this.column + column >= 0 && this.column + column < board.length)
					{
						if(this.row + row + 1 >= 0)
						{
							if(pieceHitbox[row][column] && (this.row + row + 1 >= board[0].length ||
									board[this.column + column][this.row + row + 1] != 0))
							{
								stopFlag = true;
								break;
							}
						}
					}
				}
				if(stopFlag) break;
			}
			if(!stopFlag)
			{
				this.row++;
				points += 2;
			}
		}

		return points;
	}

	void rotatePiece(int direction, int[][] board) //-1 for Counter-CW, 1 for CW
	{
		this.rotateArray(direction);

		if(checkRotation(board))
		{
			//1
			move(1, board);
			if(!checkRotation(board)) return;

			//-1
			move(-2, board);
			if(!checkRotation(board)) return;

			//2
			move(3, board);
			if(!checkRotation(board)) return;

			//-2
			move(-4, board);
			if(!checkRotation(board)) return;

			//0
			move(2, board);
			this.rotateArray(-direction);
		}
	}

	//Returns if there is a problem with the rotation
	private boolean checkRotation(int[][] board)
	{
		boolean collisionFlag = getHitboxLeftColumn() < 0
				|| getHitboxRightColumn() > board.length - 1;
		for(int row = 0; row < pieceHitbox.length; row++)
		{
			for(int column = 0; column < pieceHitbox[0].length; column++)
			{
				if(this.row + row < board[0].length &&
						this.column + column >= 0 && this.column + column < board.length)
				{
					if(this.row + row >= 0)
					{
						if((pieceHitbox[row][column] && (board[this.column + column][this.row + row] != 0)
								|| this.row + row + 1 >= board[0].length))
						{
							collisionFlag = true;
							break;
						}
					}
				}
				else
				{
					collisionFlag = true;
					break;
				}
			}
			if(collisionFlag) break;
		}

		return collisionFlag;
	}

	private void rotateArray(int direction) //-1 for Counter-CW, 1 for CW
	{
		boolean[][] tempArray = new boolean[pieceHitbox.length][pieceHitbox[0].length];

		for(int row = 0; row < pieceHitbox.length; row++)
		{
			for(int column = 0; column < pieceHitbox[0].length; column++)
			{
				if(direction < 0) tempArray[row][column] =
						pieceHitbox[pieceHitbox[0].length - 1 - column][row];
				else if(direction > 0) tempArray[row][column] =
						pieceHitbox[column][pieceHitbox.length - 1 - row];

				if(piece == Piece.PIECE_I) //Special case for the I-Piece since it only has 2 rotations
				{
					if(pieceHitbox[1][0]) //Check if it is in its original rotation
					{
						//Do a normal clockwise rotation if the piece is in its original state
						tempArray[row][column] = pieceHitbox[column]
								[pieceHitbox.length - 1 - row];
					}
					else
					{
						//If it is not in its original state, then do a counter clockwise rotation
						tempArray[row][column] =
								pieceHitbox[pieceHitbox[0].length - 1 - column][row];
					}
				}
			}
		}

		pieceHitbox = tempArray;
	}

	int getHitboxLeftColumn()
	{
		int leftMostColumn = pieceHitbox.length - 1;
		for(int column = pieceHitbox.length - 1; column >= 0; column--)
		{
			for(boolean[] row : pieceHitbox)
			{
				if(row[column]) leftMostColumn = column;
			}
		}

		return leftMostColumn + this.column;
	}

	int getHitboxRightColumn()
	{
		return getHitboxLeftColumn() + getWidth() - 1;
	}

	boolean detectTSpin(int[][] board)
	{
		if(getPiece() == Piece.PIECE_T)
		{
			boolean cannotMoveLeft = this.column + column - 1 < 0;
			boolean cannotMoveRight = this.column + column + 1 >= board.length;
			boolean cannotMoveUp = this.row + row - 1 < 0;

			for(int row = 0; row < pieceHitbox.length; row++)
			{
				for(int column = 0; column < pieceHitbox[0].length; column++)
				{
					cannotMoveLeft = this.column + column - 1 < 0;
					cannotMoveRight = this.column + column + 1 >= board.length;
					cannotMoveUp = this.row + row - 1 < 0;

					if(pieceHitbox[row][column])
					{
						if(!cannotMoveLeft)
						{
							if(board[this.column + column - 1][this.row + row] != 0)
								cannotMoveLeft = true;
						}
						if(!cannotMoveRight)
						{
							if(board[this.column + column + 1][this.row + row] != 0)
								cannotMoveRight = true;
						}
						if(!cannotMoveUp)
						{
							if(board[this.column + column][this.row + row - 1] != 0)
								cannotMoveUp = true;
						}
					}
				}
			}

			return cannotMoveLeft && cannotMoveRight && cannotMoveUp;
		}
		else return false;
	}

	private int getWidth()
	{
		int width = 0;
		for(int column = 0; column < pieceHitbox[0].length; column++)
		{
			for(int row = 0; row < pieceHitbox.length; row++)
			{
				if(pieceHitbox[row][column])
				{
					width++;
					break;
				}
			}
		}

		return width;
	}

	Piece getPiece()
	{
		return piece;
	}

	boolean[][] getPieceHitbox()
	{
		return pieceHitbox;
	}

	//Yes I know that this only copies a reference to the other array. IT DOESN'T MATTER!!!
	void setPieceHitbox(boolean[][] pieceHitbox)
	{
		this.pieceHitbox = pieceHitbox;
	}

	int getColumn()
	{
		return column;
	}

	void setColumn(int column)
	{
		this.column = column;
	}

	int getRow()
	{
		return row;
	}

	void setRow(int row)
	{
		this.row = row;
	}

	int getPieceID()
	{
		return piece.getID();
	}

	void setDropping(boolean dropping)
	{
		this.dropping = dropping;
	}
}
