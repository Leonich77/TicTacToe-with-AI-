package tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {     //aka controller in term MVC

    static Scanner scanner = new Scanner(System.in);
    static GameModel gameModel = new GameModel("         "); // aka Model
    static GameField field = new GameField();          // aka View
    static GamePlayer playerX;
    static GamePlayer playerO;

    public static void main(String[] args) {
        boolean isExit = false;
        String inStr;
        String[] inCommands;
        while (!isExit) {
            System.out.print("Input command: ");
            inStr = scanner.nextLine();
            inCommands = inStr.split(" ");
            if (inCommands[0].equals("exit")) {
                isExit = true;
                continue;
            }
            if (!inCommands[0].equals("start")) {
                System.out.println("Bad parameters!");
            } else if (inCommands.length != 3){
                System.out.println("Bad parameters!");
            } else if ((inCommands[1].equals("easy") || inCommands[1].equals("medium") ||
                        inCommands[1].equals("hard") || inCommands[1].equals("user")) &&
                        (inCommands[2].equals("easy") || inCommands[2].equals("medium") ||
                         inCommands[2].equals("hard") || inCommands[2].equals("user"))) {
                playerX = new GamePlayer(inCommands[1]);
                playerO = new GamePlayer(inCommands[2]);
                goGame();
            } else {
                System.out.println("Bad parameters!");
            }
        }
    }

    private static void goGame() {
        GameModel.ArrayCoordinates ac;
        boolean isStop = false;
        String strRes;
        gameModel = new GameModel("         ");
        field.printField(gameModel.getCurentState());
        while (!isStop) {
            ac = playerX.setNextMoveCoordinates();
            gameModel.makeNextMove(ac.column, ac.row);
            field.printField(gameModel.getCurentState());
            strRes = gameModel.analyzeField(gameModel.getCurentState());
            if (checkGameResult(strRes)) {
                isStop = true;
            } else {
                ac = playerO.setNextMoveCoordinates();
                gameModel.makeNextMove(ac.column, ac.row);
                field.printField(gameModel.getCurentState());
                strRes = gameModel.analyzeField(gameModel.getCurentState());
                if (checkGameResult(strRes)) {
                    isStop = true;
                }
            }
            if (!strRes.equals("")) {
                System.out.println(strRes);
            }
        }
    }

    private static boolean checkGameResult(String strRes) {
        return strRes.equals("X wins") || strRes.equals("O wins") || strRes.equals("Draw");
    }

    private static class GamePlayer {
        Random random = new Random();
        String level;

        GamePlayer(String level) {
            this.level = level;
        }

        public GameModel.ArrayCoordinates setNextMoveCoordinates() {
            GameModel.ArrayCoordinates ac;
            if (level.equals("user")) {
                ac = getUserInputCoordinates();
            } else {
                System.out.println("Making move level \"".concat(level).concat("\""));
                ac = getAiBotInputCoordinates();
            }
            return ac;
        }

        private GameModel.ArrayCoordinates getUserInputCoordinates() {
            GameModel.ArrayCoordinates ac = new GameModel.ArrayCoordinates();
            int column = -1;
            int row = -1;
            boolean isCorrect = false;

            while (!isCorrect) {
                System.out.print("Enter the coordinates: ");
                String str = scanner.nextLine();
                char[] chArr = str.toCharArray();
                if (Character.isDigit(chArr[0]) && Character.isWhitespace(chArr[1]) && Character.isDigit(chArr[2])) {
                    column = Character.digit(chArr[0], 10);
                    row = Character.digit(chArr[2], 10);
                } else {
                    System.out.print("You should enter numbers!\n");
                    continue;
                }
                if ((column < 1 || column > 3) || (row < 1 || row > 3)) {
                    System.out.print("Coordinates should be from 1 to 3!\n");
                    continue;
                }
                if (GameModel.isOccupied(column, row)) {
                    System.out.print("This cell is occupied! Choose another one!\n");
                    continue;
                }
                isCorrect = true;
            }
            ac.row = row;
            ac.column = column;
            return ac;
        }

        private GameModel.ArrayCoordinates getAiBotInputCoordinates() {
            GameModel.ArrayCoordinates ac = new GameModel.ArrayCoordinates();
            boolean isCorrect = false;
            while (!isCorrect) {
                int rndCell = random.nextInt(10 - 1) + 1;
                ac = transformFromStrToGameCoordinates(rndCell);
                if (!GameModel.isOccupied(ac.column, ac.row)) {
                    isCorrect = true;
                }
            }
            return  ac;
        }

        private static GameModel.ArrayCoordinates transformFromStrToGameCoordinates (int strPosition) {
            GameModel.ArrayCoordinates ret = new GameModel.ArrayCoordinates();
            if (strPosition < 4) {
                ret.row = 3;
                ret.column = strPosition;
            } else if (strPosition < 7) {
                ret.row = 2;
                ret.column = strPosition - 3;
            } else if (strPosition < 10) {
                ret.row = 1;
                ret.column = strPosition - 6;
            }
            return ret;
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
        private static String currStr;

        public GameModel(String instr) {
            currStr = instr.replaceAll("_", " ");
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

                ArrayCoordinates ac = transformFromStrToArrCoordinates(i);
                inArr[ac.row][ac.column] = inChArr[i];
            }
            pfd.inArr = inArr;
            pfd.numX = numX;
            pfd.numO = numO;
            pfd.num_ = num_;
            return pfd;
        }

        private static class ArrayCoordinates {
            int row;
            int column;

            ArrayCoordinates() {
                row = -1;
                column = -1;
            }
        }

        private static ArrayCoordinates transformFromStrToArrCoordinates (int strPosition) {
            ArrayCoordinates ret = new ArrayCoordinates();
            if (strPosition < 3) {
                ret.row = 0;
                ret.column = strPosition;
            } else if (strPosition < 6) {
                ret.row = 1;
                ret.column = strPosition - 3;
            } else if (strPosition < 9) {
                ret.row = 2;
                ret.column = strPosition - 6;
            }
            return ret;
        }

        private static int transformRowCoordinate(int row) {
            int r = row;
            if (row == 1) {
                r = 3;
            } else if (row == 3) {
                r = 1;
            }
            return r;
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
                //out = "Game not finished";
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

        public static boolean isOccupied(int column, int row) {
            PreparedFieldData pfd;
            boolean out = true;
            pfd = prepareFieldData(currStr);
            int r = transformRowCoordinate(row);
            if (pfd.inArr[r - 1][column - 1] == ' ') {
                out = false;
            }
            return out;
        }

        public void setUserMove(int column, int row, char userSig) {
            PreparedFieldData pfd;
            pfd = prepareFieldData(currStr);
            int r = transformRowCoordinate(row);
            if (column > 0 && column < 4 && row > 0 && row < 4) {
                if (pfd.inArr[r - 1][column - 1] == ' ') {
                    pfd.inArr[r - 1][column - 1] = userSig;
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