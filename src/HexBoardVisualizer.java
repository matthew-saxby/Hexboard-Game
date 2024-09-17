/****************************************************************************
 *  Command: HexBoardVisualizer input.txt
 *
 *  This program takes the name of a file as a command-line argument.
 *  From that file, it
 *
 *    - Reads the board size N of the Hex game board.
 *    - Creates an N-by-N grid of tiles (initially all unset)
 *    - Reads in a sequence of triplets (r, c, p) to set tile row r, col c by player p.
 *
 *  After each tile is set, it redraws the game board.
 *  Unset tiles are drawn in gray, tiles set by player 1 in red,
 *  tiles set by player 2 in blue, and tiles along game-winning path
 *  are drawn in lighter version of the player color.
 *  Tile (0, 0) is the leftmost corner tile.
 *
 ****************************************************************************/

import java.awt.*;

public class HexBoardVisualizer {

    // delay in milliseconds (controls animation speed)
    private final static int DELAY = 100;

    // colors of board when unset or set by player 1 or player 2
    private final static Color cUnset      = new Color(192, 192, 192);
    private final static Color cPlayer1    = new Color(240,  32,  32);
    private final static Color cWinPath1   = new Color(255, 144, 128);
    private final static Color cEdge1      = new Color(160,  80,  80);
    private final static Color cPlayer2    = new Color( 32,  32, 240);
    private final static Color cWinPath2   = new Color(128, 144, 255);
    private final static Color cEdge2      = new Color( 80,  80, 160);
    private final static Color cBackground = new Color( 96,  96,  96);
    private final static Color cStatusBG   = new Color(255, 255, 255, 128);

    private static void drawHexXY(double cx, double cy, Color color, double r) {
        double px[] = { cx + 1.00*r, cx + 0.50*r, cx - 0.50*r, cx - 1.00*r, cx - 0.50*r, cx + 0.50*r };
        double py[] = { cy + 0.00*r, cy - 0.87*r, cy - 0.87*r, cy + 0.00*r, cy + 0.87*r, cy + 0.87*r };
        StdDraw.setPenColor(color);
        StdDraw.filledPolygon(px, py);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.polygon(px, py);
    }

    private static void drawHexRowCol(int row, int col, Color color) {
        double cx = row * 1.50 + col * 1.50;
        double cy = row * 0.87 - col * 0.87;
        drawHexXY(cx, cy, color, 1.0);
    }

    private static void setScale(int N) {
        StdDraw.setXscale(-1.0, (N-1)*3.0+1);
        StdDraw.setYscale(-N * 1.5, N * 1.5);
    }

    public static void drawStatus(int player, HexBoard board, int N) {
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 12));
        StdDraw.setXscale(0, 200);
        StdDraw.setYscale(0, 200);

        // draw background of status
        StdDraw.setPenColor(cStatusBG);
        StdDraw.filledRectangle(0,0,64,36);

        // report number of unset tiles
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.textLeft(5, 12, board.numberOfUnsetTiles() + " unset tiles");

        if(board.hasPlayer1Won()) {
            StdDraw.textLeft(5, 24, "Player 1 has won!");
        } else if(board.hasPlayer2Won()) {
            StdDraw.textLeft(5, 24, "Player 2 has won!");
        } else if(player == 1 || player == 2){
            double hx = 29.0; //2.25*N;
            double hy = 24.0;

            HexBoardVisualizer.drawHexXY(hx, hy, player == 1 ? cPlayer1 : cPlayer2, 6.0);
            StdDraw.setPenColor(StdDraw.WHITE); //(player == 1 ? StdDraw.WHITE : StdDraw.BLACK);
            StdDraw.text(hx, hy, "" + player);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.textLeft(5, hy, "Player");
        } else {
            // a special player status for playback
            StdDraw.textLeft(5, 24, "No winner detected...");
        }

        setScale(N);
    }

    // draw N-by-N hex board
    public static void drawBoard(HexBoard board, int N) {
        StdDraw.clear();
        StdDraw.clear(cBackground);
        setScale(N);

        int won = board.hasPlayer1Won() ? 1 : (board.hasPlayer2Won() ? 2 : 0);

        for(int row = 0; row < N; row++) {
            for(int col = 0; col < N; col++) {
                Color color;
                if(!board.isSet(row, col)) {
                    color = cUnset;
                } else if(won != 0 && board.isOnWinningPath(row, col)) {
                    if(won == 1) {
                        color = cWinPath1;
                    } else {
                        color = cWinPath2;
                    }
                } else if(board.getPlayer(row, col) == 1) {
                    color = cPlayer1;
                } else {
                    color = cPlayer2;
                }
                drawHexRowCol(row, col, color);
            }
        }

        // draw player 1 sides of board
        for(int row = 0; row < N-1; row++) {
            drawHexRowCol(row + 1, -1, cEdge1);
            drawHexRowCol(row, N, cEdge1);
        }

        // draw player 2 sides of board
        for(int col = 0; col < N-1; col++) {
            drawHexRowCol(-1, col + 1, cEdge2);
            drawHexRowCol(N, col, cEdge2);
        }
    }

    public static void drawBoardAndStatus(int player, HexBoard board, int N) {
        drawBoard(board, N);
        drawStatus(player, board, N);
    }

    private static void simulateFromFile(String filename) {
        In in = new In(filename);
        int N = in.readInt();
        HexBoard board = new HexBoard(N);

        // turn on animation mode
        StdDraw.show(0); 

        // repeatedly read in tiles to open and drawBoard resulting system
        drawBoardAndStatus(0, board, N);
        StdDraw.show(DELAY);
        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            int p = in.readInt();
            board.setTile(i, j, p);
            drawBoardAndStatus(0, board, N);
            StdDraw.show(DELAY);
            if(board.hasPlayer1Won() || board.hasPlayer2Won()) break;
        }
    }

    public static void main(String[] args) {
        String filename = args[0];
        simulateFromFile(filename);
    }
}
