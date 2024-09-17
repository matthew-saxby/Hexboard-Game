/****************************************************************************
 *  Command: InteractiveHexBoardVisualizer N
 *
 *  This program takes the board size N as a command-line argument.
 *  Then, the user repeatedly clicks tiles to set with the mouse.
 *  After each tile is set, it redraws the game board (see comments in
 *  HexBoardVisualizer.java file for details).
 *
 ****************************************************************************/

public class InteractiveHexBoardVisualizer {
    private final static int DELAY = 20;

    public static void main(String[] args) {
        // N-by-N hex board game (read from command-line, default = 5)
        int N = 5;
        if (args.length == 1) N = Integer.parseInt(args[0]);

        int player = 1;

        // turn on animation mode
        StdDraw.show(0);

        StdOut.println(N);

        HexBoard board = new HexBoard(N);

        HexBoardVisualizer.drawBoardAndStatus(player, board, N);
        StdDraw.show(DELAY);

        while (true) {

            // detected mouse click
            if(StdDraw.mousePressed() && !board.hasPlayer1Won() && !board.hasPlayer2Won()) {

                // screen coordinates
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                // find row,col of click
                double r = (x + 1.73 * y) / 3.00;
                double c = x / 1.5 - r;
                int i = (int)Math.round(r);
                int j = (int)Math.round(c);

                // make sure player clicks close enough to center of tile
                if(Math.abs(r - i) < 0.4 && Math.abs(c - j) < 0.4) {
                    // is tile a valid position on board?
                    if (i >= 0 && i < N && j >= 0 && j < N) {
                        // is board tile already set?
                        if (!board.isSet(i, j)) {
                            StdOut.println(i + " " + j + " " + player);
                            // set board tile
                            board.setTile(i, j, player);

                            // switch player
                            player = (player==1) ? 2 : 1;

                            // redraw board
                            HexBoardVisualizer.drawBoardAndStatus(player, board, N);
                        }
                    }
                }
            }

            StdDraw.show(DELAY);
        }
    }
}
