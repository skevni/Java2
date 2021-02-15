package ru.geekbrains.sklyarov.lesson1;

public interface Participant {
    void run(float length);

    void jump(float height);

    boolean isAtDistance();

    String getName();

}
