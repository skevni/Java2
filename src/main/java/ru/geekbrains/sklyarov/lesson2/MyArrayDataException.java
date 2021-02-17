package ru.geekbrains.sklyarov.lesson2;

public class MyArrayDataException extends RuntimeException {
    private int firstIndex;
    private int secondIndex;
    private  String value;

    public MyArrayDataException(int firstIndex, int secondIndex, String value) {
        super("В позиции " +  firstIndex + "," + secondIndex +  " находится значение " + value + ", которое нельзя преобразовать в число");
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        this.value = value;
    }
}
