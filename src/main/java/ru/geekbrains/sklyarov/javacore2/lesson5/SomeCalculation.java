package ru.geekbrains.sklyarov.javacore2.lesson5;

public class SomeCalculation {

    public void calc(float[] sourceArr) {
        for (int i = 0; i < sourceArr.length; i++) {
            sourceArr[i] = (float) (sourceArr[i] * Math.sin(0.2f + (float)(i / 5)) * Math.cos(0.2f + (float)(i / 5)) * Math.cos(0.4f + (float)(i / 2)));
        }
    }
}
