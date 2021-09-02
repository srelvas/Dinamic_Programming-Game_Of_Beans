/***
 * Joao Arvana 54982
 * Sara Relvas 55596
 */

package gameBeansII;

import java.util.*;

public class GameBeansIIDinamica {

    private boolean jabaFirst;
    private List<Integer> beans;
    private int P;
    private int D;

    private int pietonTable[][];
    private int jabaTable[][];

    /***
     * A class that represents a single game of the Game of Beans.
     * 
     * @param P         - the number of piles ate the start of the game
     * @param D         - the max number o piles that can be choosen at once
     * @param l         - a list with the piles in order from left to right
     * @param jabaFirst - a boolean that when true means that Jaba plays first, when
     *                  false pieton plays first
     */
    public GameBeansIIDinamica(int P, int D, ArrayList<Integer> l, boolean jabaFirst) {
        this.P = P;
        this.D = D;
        this.jabaFirst = jabaFirst;
        this.beans = l;
        this.pietonTable = new int[this.P][this.P];
        this.jabaTable = new int[this.P][this.P];
    }

    /**
     * This is the function that fills in the dynamic programming tables and returns
     * the final result. It starts by filling in the base cases, which are the
     * diagonals in which the row index (i) matches the collumn index (j). In Jaba's
     * table these base cases are equal to the value of the pile at that index (i),
     * in Pieton's table these base case are zero, thats why we don't fill anything
     * in in Pieton's table, since in Java int arrays are intialized with zero. Next
     * we proceed to fill in every diagonal after the base case one, while taking
     * turns on which table to fill (first fill Jaba's table next diagonal, then fill
     * that same diagonal in Pieton's table), we do this because each consecutive
     * diagonal uses the results from the previous diagonals but from the other
     * player's table. When we reach the upper right corner of each player's table
     * we got ourselfs our final result, being the upper right corner of each
     * players table the result if they were the ones playing first, so we return
     * the upper right corner of the first player's table.
     * 
     * @return the upper right corner of Jaba's dynamic programming table if she's
     *         the first one to play, or frome Pieton's table if he's the first one
     */
    public int resolve() {
        for (int i = 0; i < P; i++) {
            jabaTable[i][i] = scoreJaba(i, i);
        }

        for (int a = 1; a < P; a++) {
            for (int i = 0, j = a; j < P; i++, j++) {
                jabaTable[i][j] = scoreJaba(i, j);
            }
            for (int i = 0, j = a; j < P; i++, j++) {
                pietonTable[i][j] = scorePieton(i, j);
            }
        }

        if (!this.jabaFirst) {
            return pietonTable[0][P - 1];
        } else {
            return jabaTable[0][P - 1];
        }
    }

    /**
     * Given the current i and j bounds for the list of piles, this function will
     * try all possible scores at the end of the game if the given list was only
     * between i and j, and pick the best score. It does this by calculating all the
     * options of piles that can be chosen between 1 and D from each side of
     * the list plus the respective score from Pieton's table on the remaining i and
     * j after choosing each pile, and then choosing the maximum option as a result.
     * 
     * @param i - the index of the first pile on the left that still hasn't been
     *          picked
     * @param j - the index of the first pile on the right that still hasn't been
     *          picked
     * @return the score Jaba would have if the game was only played with piles from
     *         i to j, and she played first
     */
    private int scoreJaba(int i, int j) {
        if (i > j)
            return 0;
        else if (i == j)
            return beans.get(i);
        else {
            int maxLeft = Integer.MIN_VALUE;
            int maxRight = Integer.MIN_VALUE;

            // Left increases which each step until the smallest limit (the one more on the
            // left), either it being because we have covered the full depth of the game or
            // we reached the end of the piles.
            for (int l = i; l < Math.min(i + D, j + 1); l++) {
                int value = 0;
                for (int m = i; m <= l; m++) {
                    value += beans.get(m);
                }
                if (l + 1 >= P) {
                    value += 0;
                } else {
                    value += pietonTable[l + 1][j];
                }
                if (value >= maxLeft) {
                    maxLeft = value;
                }
            }
            // Right decreases which each step until the biggest limit (the one more on the
            // right), either it being because we have covered the full depth of the game or
            // we reached the end of the piles.
            for (int r = j; r > Math.max(j - D, i - 1); r--) {
                int value = 0;
                for (int m = j; m >= r; m--) {
                    value += beans.get(m);
                }
                if (r - 1 < 0) {
                    value += 0;
                } else {
                    value += pietonTable[i][r - 1];
                }
                if (value >= maxRight) {
                    maxRight = value;
                }
            }
            if (maxLeft > maxRight) {
                return maxLeft;
            } else {
                return maxRight;
            }
        }
    }

    /**
     * Given the current i and j bounds for the list of piles, we will call upon the
     * pietonChoices function to know what would be his choice in this situation,
     * and then calculate the new i and j bounds that would remain after his choice.
     * Using these new bounds we check on Jaba's table what would her score be if
     * she played with these bounds, because this score will be Jaba's score if
     * Pieton played with the old bounds. We then return the score we got from
     * Jaba's table.
     * 
     * @param i - the index of the first pile on the left that still hasn't been
     *          picked
     * @param j - the index of the first pile on the right that still hasn't been
     *          picked
     * @return the score Jaba would have if the game was only played with piles from
     *         i to j, and Pieton played first
     */
    private int scorePieton(int i, int j) {
        if (i > j) {
            return 0;
        } else if (i == j) {
            return 0;
        } else {
            int[] choice = pietonChoices(i, j);
            int newI = i + choice[0];
            int newJ = j - choice[1];
            if (newI >= P || newJ < 0)
                return 0;
            else
                return jabaTable[newI][newJ];
        }
    }

    /**
     * Given the current i and j bounds for the list of piles, Pieton will search
     * between i and j for the best score he can get this round by picking between 1
     * and D piles from one of the sides of the list. He does this by trying
     * all of the possible combinations of choices of piles between 1 and D
     * from each end and finding the maximum result, with the least amount of piles,
     * and if possible from the left side of the list. This function then returns an
     * array that represents his choice.
     * 
     * @param i - the index of the first pile on the left that still hasn't been
     *          picked
     * @param j - the index of the first pile on the right that still hasn't been
     *          picked
     * @return an int array with two positions, on which, given i and j, the first
     *         position is the number of piles from the left that Pieton would
     *         choose in this situation, and the second position is the number of
     *         piles from the right Pieton would choose in this situation (Pieton
     *         can only chose piles from one side at a time so when one position of
     *         the array isn't zero, the other one will be zero)
     */
    private int[] pietonChoices(int i, int j) {
        int[] result = new int[2];

        if (i > j) {
            result[0] = 0;
            result[1] = 0;
        } else if (i == j) {
            result[0] = 1;
            result[1] = 0;
        } else {
            int maxLeft = Integer.MIN_VALUE;
            int maxRight = Integer.MIN_VALUE;
            int nLeft = 0;
            int nRight = 0;

            // Left increases which each step until the smallest limit (the one more on the
            // left), either it being because we have covered the full depth of the game or
            // we reached the end of the piles.
            for (int l = i; l < Math.min(i + D, j + 1); l++) {
                int curr = 0;
                for (int m = i; m <= l; m++) {
                    curr += beans.get(m);
                }
                if (curr > maxLeft) {
                    maxLeft = curr;
                    nLeft = l - i + 1;
                }
            }
            // Right decreases which each step until the biggest limit (the one more on the
            // right), either it being because we have covered the full depth of the game or
            // we reached the end of the piles.
            for (int r = j; r > Math.max(j - D, i - 1); r--) {
                int curr = 0;
                for (int m = j; m >= r; m--) {
                    curr += beans.get(m);
                }
                if (curr > maxRight) {
                    maxRight = curr;
                    nRight = j - r + 1;
                }
            }
            if (maxLeft >= maxRight) {
                result[0] = nLeft;
                result[1] = 0;
            } else {
                result[0] = 0;
                result[1] = nRight;
            }
        }
        return result;
    }
}