package ru.geekbrains.sklyarov.lesson1;

public class Track implements Obstacle {
    private float length;

    public Track(float length) {
        this.length = length;
    }

    @Override
    public void overcome(Participant participant) {
        participant.run(length);
    }

}
