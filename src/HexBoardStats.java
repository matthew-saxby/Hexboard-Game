/****************************************************************************
 *  Command: HexBoardStats N0 N1 T
 *
 *  This program takes the board sizes N0,N1 and game count T as a command-line
 *  arguments. Then, the program runs T games for each board size N where
 *  N0 <= N <= N1 and where each play randomly chooses an unset tile to set in
 *  order to estimate the probability that player 1 will win.
 ****************************************************************************/

public class HexBoardStats {
    static int lowerNumber;
    static int upperNumber;
    static int numOfGames;
    static HexBoard hexBoard;

    static int playerOneWins = 0;
    static int playerTwoWins = 0;
    static int draws = 0;

    private int[] availableMoves;
    private int moveCount;

    public int getN0() {
        return lowerNumber;
    }

    public int getN1() {
        return upperNumber;
    }

    public int getT() {
        return numOfGames;
    }

    public HexBoardStats(int N0, int N1, int T) {
        lowerNumber = N0;
        upperNumber = N1;
        numOfGames = T; //change HERE for T variation for readme info
        //check validity
        if (lowerNumber <= 0 || upperNumber < lowerNumber || numOfGames <= 0) {
            throw new java.lang.IllegalArgumentException("invalid input");
        }
        StdOut.println("working...");
    }

    public int simulateGame ( int boardSize){
        hexBoard = new HexBoard(boardSize);
        //2d array to keep track of the unset tiles
        availableMoves= new int[boardSize * boardSize];
        moveCount  = boardSize * boardSize;

        //make tiles available and unset
        for (int i=0; i< moveCount; i++){
            availableMoves[i]=i; //value equals the index of the position
        }
        //initialize variables
        int currentPlayer = 1;
        //player 1 wins when board size is 1
        if (boardSize ==1){
            playerOneWins+=1;
            return 1;
        }
        //stop when there are no more tiles, or a player has won
        while (moveCount > 0 && !hexBoard.hasPlayer1Won() && !hexBoard.hasPlayer2Won()) {
            //MAKE MOVE
            makeMove(currentPlayer);
            //check if player1 has won
            if (hexBoard.hasPlayer1Won()){
                return 1;
            }
            //check if player 2 has won
            if (hexBoard.hasPlayer2Won()) {
                return 2;
            }
            //switch players
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
        // all tiles have been filled and nobody won, so draw
        return 3;
    }

    //function that makes the move
    public void makeMove(int player){

        if (moveCount <= 0){
            throw new IllegalStateException("no unset tiles available");
        }
        //pick a random unset tile
        int randomIndex = StdRandom.uniform(moveCount);
        int move = availableMoves[randomIndex];

        //get row and col of unset tile
        int[] position = hexBoard.indexToNode(move);
        int row = position[0];
        int col = position[1];

        //set that tile to the current player
        hexBoard.setTile(row,col,player);

        //creates new array without the just made move. not efficient, but need working code.
        int[] newAvailableMoves = new int[moveCount-1];
        int j=0; //for new array index
        for (int i=0; i< moveCount; i++){
            if (i!= randomIndex){
                newAvailableMoves[j++]=availableMoves[i];
            }
        }
        availableMoves = newAvailableMoves;
        moveCount--;
    }

    public double getP1WinProbabilityEstimate() {
        return (double) playerOneWins / numOfGames;
    }

    public double getP2WinProbabilityEstimate() {
        return (double) playerTwoWins / numOfGames;
    }

    public static void main(String[] args) {
        if(args.length !=3){
            throw new IllegalArgumentException("N0,N1,and T need to be provided as arguments");
        }
        Stopwatch totalWatch = new Stopwatch();

        //take arguments from list to make them integers and arguable lol
        int N0 = Integer.parseInt(args[0]);
        int N1 = Integer.parseInt(args[1]);
        int T = Integer.parseInt(args[2]);

        HexBoardStats hexBoardStats = new HexBoardStats(N0,N1,T);

        for (int n = lowerNumber; n<=upperNumber;n++ ) {

            playerOneWins = 0;
            playerTwoWins = 0;
            draws = 0;

            //run simulation T times
            for (int simNum = 0; simNum <= T; simNum++) {

                int boardSize = n;

                //simulate game and record winner
                int winner = hexBoardStats.simulateGame(boardSize);

                if (winner == 1) {
                    playerOneWins += 1;
                } else if (winner == 2) {
                    playerTwoWins += 1;
                } else if (winner == 3) {
                    draws += 1;
                }
            }
            //printing p1 win and p2 win probabilities
            double p1WinProb = hexBoardStats.getP1WinProbabilityEstimate();
            double p2WinProb = hexBoardStats.getP2WinProbabilityEstimate();

            StdOut.println("");
            StdOut.println("-----------------------Board Size: "+n+"-----------------------------");
            StdOut.println("");
            StdOut.println("Total P1 Wins: "+playerOneWins+"  --- P1 Win Probability: "+p1WinProb);
            StdOut.println("Total P2 Wins: "+playerTwoWins+"  --- P1 Win Probability: "+p2WinProb);
            StdOut.println("Draws: "+draws);

            double totalTime = totalWatch.elapsedTime();
            StdOut.println("Total Elapsed Time Calculating Board Size "+n+": "+totalTime+" Seconds");
        }

        double totalTime = totalWatch.elapsedTime();
        StdOut.println("");
        StdOut.println("");
        StdOut.println("----------------Completed Simulation for 100000 games on Board Sizes 2-15 ------------");
        StdOut.println("Total Elapsed Time: "+totalTime+" Seconds");

    }
}
