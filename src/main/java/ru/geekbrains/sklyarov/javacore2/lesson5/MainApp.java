package ru.geekbrains.sklyarov.javacore2.lesson5;

public class MainApp {
    static final int SIZE = 10_000_000;
    static final int HALF = SIZE / 2;

    public static void main(String[] args) {

        System.out.println("Первый метод:");
        firstMethod();
        System.out.println("Второй метод:");
        try {
            secondMethod();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        /*
        Processor:          Intel Core i5 - 6200U @ 2.3GHz
        Sockets:            1
        Cores:              2
        Logical processor:  4

        Есть смысл в распараллеливании, если размер массива более 4млн
         */
    }

    public static void firstMethod() {
        float[] arr = new float[SIZE];

        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1;
        }

        long time = System.currentTimeMillis();

        calc(arr);

        System.out.println("Время выполнения:" + (System.currentTimeMillis() - time));
    }

    public static void secondMethod() throws InterruptedException {
        float[] arr = new float[SIZE];

        for (int i = 0; i < SIZE; i++) {
            arr[i] = 1;

        }
        long timer = System.currentTimeMillis();
        long time = System.currentTimeMillis();

        float[] arr2 = new float[HALF];
        float[] arr3 = new float[HALF];

        System.arraycopy(arr, 0, arr2, 0, HALF);
        System.arraycopy(arr, HALF, arr3, 0, HALF);

        System.out.println("Время на деление массива: " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        Thread tr1 = new Thread(() -> calc(arr2));
        Thread tr2 = new Thread(() -> calc(arr3));

        tr1.start();
        tr2.start();

        tr1.join();
        System.out.printf("Поток %s выполни операции за %d\n", tr1.getName(), System.currentTimeMillis() - time);
        tr2.join();
        System.out.printf("Поток %s выполни операции за %d\n", tr2.getName(), System.currentTimeMillis() - time);

        time = System.currentTimeMillis();

        System.arraycopy(arr, 0, arr2, 0, HALF);
        System.arraycopy(arr, HALF, arr3, 0, HALF);

        System.out.println("Времяя на склеивание массива:" + (System.currentTimeMillis() - time));

        System.out.println("\nВсего времени прошло: " + (System.currentTimeMillis() - timer));
    }
    private static void calc(float[] sourceArr) {
        for (int i = 0; i < sourceArr.length; i++) {
            sourceArr[i] = (float) (sourceArr[i] * Math.sin(0.2f + (float)(i / 5)) * Math.cos(0.2f + (float)(i / 5)) * Math.cos(0.4f + (float)(i / 2)));
        }
    }
}
