package ru.geekbrains.sklyarov.lesson1;

public class MainApp {
    public static void main(String[] args) {
        Action[] actions = {
                new Human("John", 1000f, 1.56f),
                new Cat("Barsik", 55f, 2.07f),
                new Human("Bob", 47f, 2.0f),
                new Robot("Verter", 6000f, 4.0f)
        };
        Obstacle[] obstacles = {
                new Track(100),
                new Wall(2.2f),
                new Track(500),
                new Wall(5f)
        };
        boolean breakDistance=false;
        for (Action action : actions) {
            for (Obstacle obstacle : obstacles) {
                if (!obstacle.overcome(action)) {
                    breakDistance = true;
                    break;
                }
            }
            if (!breakDistance) {
                System.out.println(action.getName() + " прошел всю дистанцию!");
            }
            System.out.println();
        }

    }
}
