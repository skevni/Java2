package ru.geekbrains.sklyarov.lesson1;

public class Track implements Obstacle {
    private float length;

    public Track(float length) {
        this.length = length;
    }

    @Override
    public boolean overcome(Action action) {
        System.out.printf("Длина дорожки %f м.\n", length);
        action.run();
        if (action.getLimitTrack() > length) {
            System.out.println(action.getName() + " пробежал");
            return true;
        } else {
            System.out.println(action.getName() + " не смог пробежать");
        }
        return false;
    }

}
