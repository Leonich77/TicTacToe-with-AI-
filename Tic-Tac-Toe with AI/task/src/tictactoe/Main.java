package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {     //aka controller in term MVC

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //get user input of initial game field configuration
        String inStr = scanner.nextLine();
        if (inStr.length() < 9) {   //there need more complete check user input correctness
            inStr = "         ";
        }
        GameModel gameModel = new GameModel(inStr); // aka Model
        GameField field = new GameField();          // aka View

        field.printField(gameModel.getCurentState());
        boolean isStop = false;
        int column;
        int row;
        int moveCount = 0;
        String strRes;

        while (!isStop) {
            System.out.print("Enter the coordinates: ");
            String str = scanner.nextLine();
            char[] chArr = str.toCharArray();
            if (Character.isDigit(chArr[0]) && Character.isWhitespace(chArr[1]) && Character.isDigit(chArr[2])) {
                row = Character.digit(chArr[0], 10);
                column = Character.digit(chArr[2], 10);
            } else {
                System.out.print("You should enter numbers!\n");
                continue;
            }
            if ((column < 1 || column > 3) || (row < 1 || row > 3)) {
                System.out.print("Coordinates should be from 1 to 3!\n");
                continue;
            }
            if (gameModel.isOccupied(column, row)) {
                System.out.print("This cell is occupied! Choose another one!\n");
                continue;
            }
            /*if (!(++moveCount % 2 == 0)) {            //TODO: not deleted this!
                gameModel.setUserMove(column, row, 'X');
            } else {
                gameModel.setUserMove(column, row, 'O');
            }*/
            gameModel.makeNextMove(column, row);
            field.printField(gameModel.getCurentState());
            strRes = gameModel.analyzeField(gameModel.getCurentState());
            /*if (strRes.equals("X wins") || strRes.equals("O wins") || strRes.equals("Draw")) {
                isStop = true;
                System.out.print(strRes);
            }*/
            isStop = true;
            System.out.print(strRes);
        }
    }

    private static class GameField {

        public void printField(String in) {
            char[] inChArr = in.toCharArray();
            System.out.println("---------");
            System.out.print("| ");
            for (int i = 0; i < 9; i++) {
                System.out.print(inChArr[i] + " ");
                if (i == 2 || i == 5) {
                    System.out.print("|" + "\n" + "| ");
                }
            }
            System.out.print("|" + "\n" + "---------" + "\n");
        }
    }

    private static class GameModel {
        //private static final String inStr = "         ";
        //private static String currStr = inStr;
        private static String currStr;

        public GameModel(String instr) {
            currStr = instr.replaceAll("_", " ");
            //currStr = instr;
        }

        public String getCurentState () {
            return currStr;
        }

        public void makeNextMove(int column, int row) {
            PreparedFieldData pfd = prepareFieldData(currStr);
            if (pfd.numX == pfd.numO) {
                setUserMove(column, row, 'X');
            } else {
                setUserMove(column, row, 'O');
            }
        }

        static class PreparedFieldData {
            int numX;
            int numO;
            int num_;
            Character [][] inArr;
        }

        private static PreparedFieldData prepareFieldData(String inData) {
            int numX = 0;
            int numO = 0;
            int num_ = 0;
            Character [][] inArr = new Character[3][3];
            PreparedFieldData pfd = new PreparedFieldData();
            char[] inChArr = inData.toCharArray();
            for (int i = 0; i < inChArr.length; i++) {
                if (inChArr[i] == 'X') {
                    numX++;
                } else if (inChArr[i] == 'O') {
                    numO++;
                } else if (inChArr[i] == ' ') {
                    num_++;
                }

                if (i < 3) {
                    inArr[0][i] = inChArr[i];
                } else if (i < 6) {
                    inArr[1][i-3] = inChArr[i];
                } else if (i < 9) {
                    inArr[2][i-6] = inChArr[i];
                }
            }
            pfd.inArr = inArr;
            pfd.numX = numX;
            pfd.numO = numO;
            pfd.num_ = num_;
            return pfd;
        }

        private static int transformColumnCoordinate(int column) {
            int c = column;
            if (column == 1) {
                c = 3;
            } else if (column == 3) {
                c = 1;
            }
            return c;
        }

        public String analyzeField(String in) {
            String out = "";
            PreparedFieldData pfd = prepareFieldData(in);
            LinesChars cl = getLinesChars(pfd.inArr);
            if (cl.lines.size() == 1 ) {
                out = cl.chars.get(0) + " wins";
            } else if (cl.lines.size() == 0 && pfd.num_ == 0) {
                out = "Draw";
            } else if (pfd.num_ != 0 && cl.lines.size() == 0 && Math.abs(pfd.numO - pfd.numX) < 2) {
                out = "Game not finished";
            } else if (cl.lines.size() > 1) {
                out = "Impossible";
            } else if (Math.abs(pfd.numO - pfd.numX) > 1) {
                out = "Impossible";
            }

            return  out;
        }

        static class LinesChars {
            List<Integer> lines = new ArrayList<>();
            List<Character> chars  = new ArrayList<>();
        }

        private static LinesChars getLinesChars(Character[][] inArr) {
            LinesChars out = new LinesChars();
            if (!inArr[0][0].equals(' ') && inArr[0][0].equals(inArr[0][1]) && inArr[0][0].equals(inArr[0][2])) {
                out.lines.add(1);
                out.chars.add(inArr[0][0]);
            }
            if (!inArr[1][0].equals(' ') && inArr[1][0].equals(inArr[1][1]) && inArr[1][0].equals(inArr[1][2])) {
                out.lines.add(2);
                out.chars.add(inArr[1][0]);
            }
            if (!inArr[2][0].equals(' ') && inArr[2][0].equals(inArr[2][1]) && inArr[2][0].equals(inArr[2][2])) {
                out.lines.add(3);
                out.chars.add(inArr[2][0]);
            }
            if (!inArr[0][0].equals(' ') && inArr[0][0].equals(inArr[1][0]) && inArr[0][0].equals(inArr[2][0])) {
                out.lines.add(4);
                out.chars.add(inArr[0][0]);
            }
            if (!inArr[0][1].equals(' ') && inArr[0][1].equals(inArr[1][1]) && inArr[0][1].equals(inArr[2][1])) {
                out.lines.add(5);
                out.chars.add(inArr[0][1]);
            }
            if (!inArr[0][2].equals(' ') && inArr[0][2].equals(inArr[1][2]) && inArr[0][2].equals(inArr[2][2])) {
                out.lines.add(6);
                out.chars.add(inArr[0][2]);
            }
            if (!inArr[0][0].equals(' ') && inArr[0][0].equals(inArr[1][1]) && inArr[0][0].equals(inArr[2][2])) {
                out.lines.add(7);
                out.chars.add(inArr[0][0]);
            }
            if (!inArr[2][0].equals(' ') && inArr[2][0].equals(inArr[1][1]) && inArr[2][0].equals(inArr[0][2])) {
                out.lines.add(8);
                out.chars.add(inArr[2][0]);
            }
            return out;
        }

        public boolean isOccupied(int column, int row) {
            PreparedFieldData pfd;
            boolean out = true;
            pfd = prepareFieldData(currStr);
            int c = transformColumnCoordinate(column);
            if (pfd.inArr[c - 1][row - 1] == ' ') {
                out = false;
            }
            return out;
        }

        public void setUserMove(int column, int row, char userSig) {
            PreparedFieldData pfd;
            pfd = prepareFieldData(currStr);
            int c = transformColumnCoordinate(column);
            if (column > 0 && column < 4 && row > 0 && row < 4) {
                if (pfd.inArr[c - 1][row - 1] == ' ') {
                    pfd.inArr[c - 1][row - 1] = userSig;
                }
            }
            currStr = packFieldData(pfd.inArr);
        }

        private static String packFieldData(Character[][] currArr) {
            String out = "";
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    out = out.concat(currArr[i][j].toString());
                }
            }
            return out;
        }
    }
}