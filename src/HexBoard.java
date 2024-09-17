/****************************************************************************
 *  This class manages an N-by-N hex game board .
 ****************************************************************************/

public class HexBoard {
    int [][] board; //2d array to make the board
    private int N; //size of board
    int boardSize;
    QuickFindUF unionBoard;

    private int topVNode;
    private int bottomVNode;
    private int rightSideVNode;
    private int leftSideVNode;

    int winningUnion;



    public HexBoard(int N) {
        /*the constructor that creates N-by-N grid with all tiles unset*/
        if(N<=0) throw new java.lang.IllegalArgumentException("BOARD SIZE TOO SMALL");

        this.N = N; //
        this.board = new int[N][N]; //initialize board to have NxN dimensions.
        unionBoard = new QuickFindUF((N*N) + 4); //needs to account for size of virtual nodes
        boardSize = N;

        this.topVNode = N * N; //give index 1 bigger than any on board
        this.bottomVNode = (N * N) + 1; //give index 1 bigger than top v node;
        this.leftSideVNode = (N * N) + 2; //give index 1 bigger than bottom v node;
        this.rightSideVNode = (N * N) + 3;//give index 1 bigger than left v node;

    }


    public int getPlayer(int row, int col) {
        /*returns which player has set the tile.*/
        return board[row][col]; //return the player who set the tile and if not set return 0
    }
    
    public boolean isSet(int row, int col) {
        /*returns whether tile in row "row" and column "col" has been set by either
        player (false means the tile is unset)*/
        return board[row][col] !=0;
    }


    public int nodeIndex(int row, int col) {
        //give tile a node index
        return col + (row * boardSize);
    }
    public int[] indexToNode(int index){
        int col = index % boardSize;
        int row = (index-col)/boardSize;
        return new int[] {row,col}; //return row and col as an array
    }
    boolean boundaryChecker(int row, int col){
        //true if in bounds false if not in bounds
        return (!( row<0 || col<0 || row>(boardSize -1) || col > (boardSize -1) ));
    }

    public void setTile(int row, int col, int player) {
        /*sets tile to specified player.*/

        //making sure it is in bounds
        if (!boundaryChecker(row,col)){
            throw new java.lang.IndexOutOfBoundsException(row+", "+col+" IS OUT OF BOUNDS");
        }
        if (!isSet(row, col)) { //making sure tile is unset
            board[row][col] = player;
            int index = nodeIndex(row, col);
            checkSurroundingTile(row, col, player);
        } else {
            throw new java.lang.IllegalArgumentException(row+", "+col+" HAS ALREADY BEEN SET");
        }
    }

    public void checkSurroundingTile(int row, int col, int player){
        checkVirtualUnion(row, col, player);

        checkUnion(row, col, row-1, col, player);
        checkUnion(row, col, row+1, col, player);
        checkUnion(row, col, row, col-1, player);
        checkUnion(row, col, row, col+1, player);
        checkUnion(row, col, row-1, col+1, player);
        checkUnion(row, col, row+1, col-1, player);
    }

    public void checkVirtualUnion(int row, int col, int player){
        if (player ==1){ //red
            if (col ==0 ){
                unionBoard.union(topVNode, nodeIndex(row,col));
            } else if (col == boardSize -1){
                unionBoard.union(bottomVNode,nodeIndex(row,col));
            }
        }else if (player ==2){ //blue
            if (row == 0){
                unionBoard.union(leftSideVNode, nodeIndex(row,col));
            } else if (row == boardSize -1){
                unionBoard.union(rightSideVNode, nodeIndex(row,col));
            }
        }
    }

    public void checkUnion(int row, int col,int nRow, int nCol, int player){
        int index = nodeIndex(row, col);
        int nIndex = nodeIndex(nRow,nCol);

        //check out of bounds
        if ( boundaryChecker(row, col) && boundaryChecker(nRow,nCol) ) { //checks if out of bounds
            //check players tile color
            if (board[nRow][nCol] == player) { //check if its the players color
                if (!unionBoard.connected(index, nIndex)) { //check already unioned
                    unionBoard.union(index, nIndex); //union them together
                    /*StdOut.println("the union is " + unionBoard.find(index));// telling us the union for checking purposes*/
                }
            }
        }
    }

    public boolean hasPlayer1Won() {
        //red
        //if vnode is unioned to that union node then return true
        if (unionBoard.connected(topVNode,bottomVNode)) {
            winningUnion = unionBoard.find(bottomVNode);
            return true;
        }
        return false;
    }

    public boolean hasPlayer2Won() {
        //if vnode is unioned to that union node then return true
       if (unionBoard.connected( leftSideVNode,rightSideVNode)) {
            winningUnion = unionBoard.find(leftSideVNode);
            return true;
        }
        return false;
    }


    public boolean isOnWinningPath(int row, int col) {
        /* returns whether the given tile is on the winning path.*/
        int position = nodeIndex(row,col);
        return winningUnion == unionBoard.find(position);
    }

    public int numberOfUnsetTiles() {
        /*returns number of unset tiles*/
        int unsetTileCount = (boardSize * boardSize) ;
            for (int row = 0; row < boardSize; row ++){
              for (int col = 0; col< boardSize; col ++){
                  if (isSet(row,col)){
                      unsetTileCount --;
                  }
              }
            }
        StdOut.println("the number of unset tiles is " + unsetTileCount);
        return unsetTileCount; // returns number of unset tiles
    }


    public void clearBoard(){
        for (int row = 0; row < boardSize; row++){
            for (int col=0;col < boardSize; col++) {
                board[row][col] = 0;
            }
        }
    }
}
