package ru.geekbrains.sklyarov.lesson1;

public class Wall implements Obstacle {
    private float height;

    public Wall(float height) {
        this.height = height;
    }

    @Override
    public boolean overcome(Action action) {
        System.out.printf("Высота стены %f м.\n", height);
        action.jump();
        if (height < action.getLimitWall()) {
            System.out.println(action.getName() + " перепрыгнул стену!");
            return true;
        } else {
            System.out.println(action.getName() + " не смог перепрыгнуть стену!");
        }
        return false;
    }
}
