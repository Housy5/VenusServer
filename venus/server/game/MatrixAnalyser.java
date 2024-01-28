package venus.server.game;

import java.util.Arrays;

public final class MatrixAnalyser {
    
    private final int[][] matrix;
    
    private final int moveLeft = -1;
    private final int moveRight = 1;
    private final int moveUp = -1;
    private final int moveDown = 1;
    private final int noMove = 0;
    
    private final int sequenceSize = 4;
    
    private volatile int winner = 0;
    
    public MatrixAnalyser(final int[][] mtrx) {
        matrix = mtrx;
    }
    
    public boolean hasWinner() {
        return winner != 0;
    }
    
    public boolean hasTie() {
        return !Arrays.stream(matrix).flatMapToInt(Arrays::stream).anyMatch(x -> x == 0);
    }
    
    public int getWinner() {
        return winner;
    }
    
    public void analyse() {
        if (hasWinner()) {
            return;
        }
        
        boolean win = false;
                
        outer:
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                win = scanLeft(i, j) 
                        || scanRight(i, j) 
                        || scanUp(i, j) 
                        || scanDown(i, j)
                        || scanPrimaryDown(i, j)
                        || scanPrimaryUp(i, j)
                        || scanSecondaryDown(i, j)
                        || scanSecondaryUp(i, j);
                
                if (win) {
                    winner = matrix[i][j];
                    break outer;
                }
            }
        }
    }
    
    private boolean scan(int i, int j, int im, int jm, int count, int target) {
        if (count == 0) {
            return true;
        }
        
        if (target == 0) {
            return false;
        }
        
        if (i >= matrix.length || i == -1) {
            return false;
        }
        
        if (j >= matrix[0].length || j == -1) {
            return false;
        }
        
        if (matrix[i][j] != target) {
            return false;
        }
        
        return scan(i + im, j + jm, im, jm, count - 1, target);
    }
    
    private boolean scan(int i, int j, int im, int jm) {
        int target = matrix[i][j];
        
        return scan(i, j, im, jm, sequenceSize, target);
    }
    
    private boolean scanLeft(int i, int j) {
        return scan(i, j, noMove, moveLeft);
    }
    
    private boolean scanRight(int i, int j) {
        return scan(i, j, noMove, moveLeft);
    }
    
    private boolean scanUp(int i, int j) {
        return scan(i, j, moveUp, noMove);
    }
    
    private boolean scanDown(int i, int j) {
        return scan(i, j, moveDown, noMove);
    }
    
    private boolean scanPrimaryDown(int i, int j) {
        return scan(i, j, moveDown, moveLeft);
    }
    
    private boolean scanPrimaryUp(int i, int j) {
        return scan(i, j, moveUp, moveRight);
    }
    
    private boolean scanSecondaryDown(int i, int j) {
        return scan(i, j, moveDown, moveRight);
    }
    
    private boolean scanSecondaryUp(int i, int j) {
        return scan(i, j, moveUp, moveLeft);
    }
}
