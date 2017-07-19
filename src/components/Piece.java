package components;

import java.awt.*;

public enum Piece
{
	PIECE_I,
	PIECE_J,
	PIECE_L,
	PIECE_O,
	PIECE_S,
	PIECE_T,
	PIECE_Z;

	public Color getColor()
	{
		switch(this)
		{
			case PIECE_I:
				return Color.CYAN;
			case PIECE_J:
				return Color.BLUE;
			case PIECE_L:
				return Color.ORANGE;
			case PIECE_O:
				return Color.YELLOW;
			case PIECE_S:
				return Color.GREEN;
			case PIECE_T:
				return new Color(100, 0, 255);
			case PIECE_Z:
				return Color.RED;
			default:
				return Color.BLACK;
		}
	}

	public static Color getColor(int id)
	{
		switch(id)
		{
			case 1:
				return Color.CYAN;
			case 2:
				return Color.BLUE;
			case 3:
				return Color.ORANGE;
			case 4:
				return Color.YELLOW;
			case 5:
				return Color.GREEN;
			case 6:
				return new Color(100, 0, 255);
			case 7:
				return Color.RED;
			case -1:
				return Color.GRAY;
		default:
			return Color.BLACK;
		}
	}

	public int getID()
	{
		switch(this)
		{
			case PIECE_I:
				return 1;
			case PIECE_J:
				return 2;
			case PIECE_L:
				return 3;
			case PIECE_O:
				return 4;
			case PIECE_S:
				return 5;
			case PIECE_T:
				return 6;
			case PIECE_Z:
				return 7;
			default:
				return 0;
		}
	}

	public static Piece parseID(int id)
	{
		switch(id)
		{
			case 0:
				return PIECE_I;
			case 1:
				return PIECE_J;
			case 2:
				return PIECE_L;
			case 3:
				return PIECE_O;
			case 4:
				return PIECE_S;
			case 5:
				return PIECE_T;
			case 6:
				return PIECE_Z;
			default:
				System.out.println("yeah, we messed up: " + id);
				return PIECE_I;
		}
	}

	public boolean[][] getArray()
	{
		switch(this)
		{
		case PIECE_I:
			return new boolean[][]
					{
						{false, true, false, false},
						{false, true, false, false},
						{false, true, false, false},
						{false, true, false, false}
					};
		case PIECE_J:
				return new boolean[][]
						{
								{true, false, false},
								{true, true, true},
								{false, false, false}
						};
			case PIECE_L:
				return new boolean[][]
						{
								{false, false, true},
								{true, true, true},
								{false, false, false}
						};
			case PIECE_O:
				return new boolean[][]
						{
								{true, true},
								{true, true},
						};
			case PIECE_S:
				return new boolean[][]
						{
								{false, true, true},
								{true, true, false},
								{false, false, false}
						};
			case PIECE_T:
				return new boolean[][]
						{
								{false, true, false},
								{true, true, true},
								{false, false, false}
						};
			case PIECE_Z:
				return new boolean[][]
						{
								{true, true, false},
								{false, true, true},
								{false, false, false}
						};
			default:
				return new boolean[][]
						{
								{false, false, false, false},
								{false, false, false, false},
								{false, false, false, false},
								{false, false, false, false}
						};
		}
	}
}
