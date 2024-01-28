import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    // Обозначение размера игровой сетки 3х3
    private static final int ROW_COUNT = 3;
    private static final int COL_COUNT = 3;

    // Состояние игровых клеток - пустая, крестик, нолик
    private static final String CELL_STATE_EMPTY = " ";
    private static final String CELL_STATE_X = "X";
    private static final String CELL_STATE_O = "O";

    // Состояние игры - победа крестиков, победа ноликов, ничья, игра не закончена
    private static String GAME_STATE_X_WON = "X победили";
    private static String GAME_STATE_O_WON = "O победили";
    private static String GAME_STATE_DRAW = "Ничья";
    private static String GAME_STATE_NOT_FINISHED = "Игра не закончена";

    // Сканер для считывания данных из консоли
    private static Scanner scanner = new Scanner(System.in);
    // Генерация случайных значений для использвания в выборе клетки для ноликов
    private static Random random = new Random();

    // Параметры состояния игры
    private static String APP_STATE_EXIT = "E";
    private static String APP_STATE_PLAY = "P";

    public static void main(String[] args) {
        while (getAppState()) {
            startGameRound();
        }
    }

    // Функция в который выполняется отрисовка игровой области  вызов игрового цикла
    public static void startGameRound() {
        System.out.println("Начало нового раунда");

        String[][] board = createBoard();
        startGameLoop(board);
    }

    // Функция для создания игровой области
    public static String[][] createBoard() {
        String[][] board = new String[ROW_COUNT][COL_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                board[row][col] = CELL_STATE_EMPTY;
            }
        }
        return board;
    }

    // Функция игрового цикла
    public static void startGameLoop(String[][] board) {
        // Переменная хода игрока
        boolean playerTurn = true;

        // После каждого хода игрока или бота отрисывывается игровая область с текущим состоянием игры
        // Также после каждого хода выполняется проверка состояния чьей либо победы
        do {
            if (playerTurn) {
                makePlayerTurn(board);
                printBoard(board);
            } else {
                makeBotTurn(board);
                printBoard(board);
            }
            playerTurn = !playerTurn;
            System.out.println();

            String gameState = checkGameState(board);
            if (!gameState.equals(GAME_STATE_NOT_FINISHED)) {
                System.out.println(gameState);
                return;
            }
        } while (true);
    }

    // Функция обработки хода игрока
    public static void makePlayerTurn(String[][] board) {
        int[] coordinates = inputCellCoordinates(board);
        board[coordinates[0]][coordinates[1]] = CELL_STATE_X;
    }

    // Функция обработки считывания ввода координат, введенных игроком
    public static int[] inputCellCoordinates(String[][] board) {
        System.out.println("Введите координаты Х (0...2) через пробел:");
        // Допущение, проверка некорректного ввода не выполняется
        do {
            // Считывание введенного значения, разделение через пробел
            String[] input = scanner.nextLine().split(" ");
            int row = Integer.parseInt(input[0]);
            int col = Integer.parseInt(input[1]);

            // Минимальные проверка некорректного ввода
            if ((row < 0) || (row >= ROW_COUNT) || (col < 0) || (col >= COL_COUNT)) {
                System.out.println("Некорректный ввод! Введите значения от 0 до 2:");
            } else if (!Objects.equals(board[row][col], CELL_STATE_EMPTY)) {
                System.out.println("Данная ячейка уже занята. Введите новые координаты (0...2):");
            } else {
                return new int[]{row, col};
            }
        } while (true);
    }

    // Функция обработки хода бота
    public static void makeBotTurn(String[][] board) {
        System.out.println("Ход бота:");
        int[] coordinates = getRandomEmptyCellCoordinates(board);
        board[coordinates[0]][coordinates[1]] = CELL_STATE_O;
    }

    // Функция генерации случайной свободной координаты для хода бота
    public static int[] getRandomEmptyCellCoordinates(String[][] board) {
        do {
            // Генерация случайных чисел в диапазоне размерности игровой сетки, если ячейка свободная - добавляем
            // ее кординаты для хода бота
            int row = random.nextInt(ROW_COUNT);
            int col = random.nextInt(COL_COUNT);

            if (Objects.equals(board[row][col], CELL_STATE_EMPTY)) {
                return new int[]{row, col};
            }
        } while (true);
    }

    // Функция обработки проверки состояния игры
    public static String checkGameState(String[][] board) {
        // Коллекция хранения сумм ячеек
        ArrayList<Integer> sums = new ArrayList<Integer>();

        // Проверка сумм по строкам
        for (int row = 0; row < ROW_COUNT; row++) {
            int rowSum = 0;
            for (int col = 0; col < COL_COUNT; col++) {
                rowSum += calculateNumValue(board[row][col]);
            }
            sums.add(rowSum);
        }

        // Проверка сумм по столбцам
        for (int col = 0; col < COL_COUNT; col++) {
            int colSum = 0;
            for (int row = 0; row < ROW_COUNT; row++) {
                colSum += calculateNumValue(board[row][col]);
            }
            sums.add(colSum);
        }

        // Проверка сумм по диагонали слева сверху - справа вниз
        int leftDiagonalSum = 0;
        for (int i = 0; i < ROW_COUNT; i++) {
            leftDiagonalSum += calculateNumValue(board[i][i]);
        }
        sums.add(leftDiagonalSum);

        //Проверка сумм по диагонали справа сверху - слева вниз
        int rightDiagonalSum = 0;
        for (int i = 0; i < ROW_COUNT; i++) {
            rightDiagonalSum += calculateNumValue(board[i][ROW_COUNT - 1 - i]);
        }
        sums.add(rightDiagonalSum);

        // Проверка состония
        // Х - 1
        // О - -1
        // Пустая - 0
        if (sums.contains(3)) {
            return GAME_STATE_X_WON;
        } else if (sums.contains(-3)) {
            return GAME_STATE_O_WON;
        } else if (areAllCellsTaken(board)) {
            return GAME_STATE_DRAW;
        } else {
            return GAME_STATE_NOT_FINISHED;
        }
    }

    // Функция подсчитывания значения в ячейке в соответствие с элементом внутри
    private static int calculateNumValue(String cellState) {
        if (cellState.equals(CELL_STATE_X)) {
            return 1;
        } else if (cellState.equals(CELL_STATE_O)) {
            return -1;
        } else {
            return 0;
        }
    }

    // Функция проверки занятости всех ячеек
    public static boolean areAllCellsTaken(String[][] board) {
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                if (board[row][col].equals(CELL_STATE_EMPTY)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Функция отрисовки игровой сетки
    public static void printBoard(String[][] board) {
        System.out.println("---------");
        for (int row = 0; row < ROW_COUNT; row++) {
            String line = "| ";
            for (int col = 0; col < COL_COUNT; col++) {
                line += board[row][col] + " ";
            }
            line += "|";
            System.out.println(line);
        }
        System.out.println("---------");
    }

    // Функция для считывания состояния приложения - играть или выйти
    public static boolean getAppState() {
        System.out.println("Введите P чтобы играть.");
        System.out.println("Введите Е чтобы выйти.");
        System.out.println("Что делать:");
        do {
            String input = scanner.nextLine();
            if (input.equals("P")) {
                return true;
            } else if (input.equals("E")) {
                return false;
            } else {
                System.out.println("Неверный ввод! Попробуйте еще раз:");
            }
        } while(true);
    }
}
