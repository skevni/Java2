package ru.geekbrains.sklyarov.lesson2;

public class MainApp {
    public static void main(String[] args) {
        String[][] s = {{"1", "2", "3", "4"}, {"5", "6", "7", "8"}, {"9", "10", "11", "12"}, {"13", "14", "15", "16"}};
        try {
            System.out.println("Сумма элементов массива: " + calcElementsSum(s));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static int calcElementsSum(String[][] strings) throws RuntimeException {
        int sum = 0;

        for (int i = 0; i < strings.length; i++) {
            if (strings.length != 4 || strings[i].length != 4) {
//                    try {
                throw new MyArraySizeException("Передан неверный размер массива. Допустим двумерный массив 4 х 4");
//                    } catch (ArrayIndexOutOfBoundsException e) {
//                        e.printStackTrace();
//                        sum = 0;
//                        break;
//                    }
            }
            for (int j = 0; j < strings.length; j++) {
                try {
                    sum += Integer.parseInt(strings[i][j]);
                } catch (NumberFormatException e) {
                    throw new MyArrayDataException(i, j, strings[i][j]);
                }
            }
        }
        return sum;
    }
}
