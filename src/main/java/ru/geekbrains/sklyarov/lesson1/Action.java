package ru.geekbrains.sklyarov.lesson1;

public interface Action {
    void run();

    void jump();

    String getName();

    float getLimitTrack();

    float getLimitWall();

}
