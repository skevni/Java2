package ru.geekbrains.sklyarov.lesson1;

public class Cat implements Action {
    private String name;
    private float limitTrack;
    private float limitWall;

    public Cat(String name, float limitTrack, float limitWall) {
        this.name = name;
        this.limitTrack = limitTrack;
        this.limitWall = limitWall;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getLimitTrack() {
        return limitTrack;
    }

    @Override
    public float getLimitWall() {
        return limitWall;
    }

    @Override
    public void run() {
        System.out.printf("Кот по кличке %s побежал\n", name);
    }

    @Override
    public void jump() {
        System.out.printf("Кот по кличке %s прыгнул\n", name);
    }
}
