package ru.geekbrains.sklyarov.lesson1;

public class Robot implements Action {
    private String name;
    private float limitTrack;
    private float limitWall;

    public Robot(String name, float limitTrack, float limitWall) {
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
        System.out.printf("Робот по имени %s побежал\n", name);
    }

    @Override
    public void jump() {
        System.out.printf("Робот по имени %s прыгнул\n", name);
    }

}
