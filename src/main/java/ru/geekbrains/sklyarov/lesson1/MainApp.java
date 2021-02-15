package ru.geekbrains.sklyarov.lesson1;

public class MainApp {
    public static void main(String[] args) {
        Participant[] participants = {
                new Human("John", 1000f, 1.56f),
                new Cat("Barsik", 55f, 2.07f),
                new Human("Bob", 47f, 2.0f),
                new Robot("Verter", 6000f, 4.0f)
        };
        Obstacle[] obstacles = {
                new Track(100),
                new Wall(2.2f),
                new Track(500),
                new Wall(3.8f)
        };
        boolean breakDistance=false;
        for (Participant participant : participants) {
            for (Obstacle obstacle : obstacles) {
                obstacle.overcome(participant);
                if (!participant.isAtDistance()) {
                    breakDistance = true;
                    break;
                }
            }
            if (!breakDistance) {
                System.out.println(participant.getName() + " прошел всю дистанцию!");
            } else {
                System.out.println(participant.getName() + " не смог пройти всю дистанцию!");
                breakDistance = false;
            }
            System.out.println();
        }

    }
}
