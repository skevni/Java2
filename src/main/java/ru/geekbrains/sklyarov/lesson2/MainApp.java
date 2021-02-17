package ru.geekbrains.sklyarov.lesson2;

public class MainApp {
    public static void main(String[] args) {
        String[][] s = {{"1","2g","3","4"},{"5","6","7","8"},{"9","10","11","12"},{"13","14","15","16"}};
        System.out.println("Сумма элементов массива: " + getArray(s));
    }

    public static int getArray(String[][] strings) {
        int sum = 0;
        try {
            if (strings.length > 4 || strings[0].length > 4) {
                try {
                    throw new MyArraySizeException("Передан неверный размер массива. Допустим двумерный массив 4 х 4");
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
            int i=-1, j=-1;
            try {

                for (i=0;i < strings.length; i++) {
                    for (j=0; j < strings[i].length; j++) {
                        sum += Integer.parseInt(strings[i][j]);
                    }
                }
            } catch (NumberFormatException e){
                try {
                    throw new MyArrayDataException(i,j,strings[i][j]);
                } catch (MyArrayDataException e2){
                    e2.printStackTrace();
                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return sum;
    }
}
