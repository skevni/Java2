package ru.geekbrains.sklyarov.lesson1;

public class Wall implements Obstacle {
    private float height;

    public Wall(float height) {
        this.height = height;
    }

    @Override
    public void overcome(Participant participant) {
        participant.jump(height);
    }
}
