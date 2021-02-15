package ru.geekbrains.sklyarov.lesson1;

public class Human implements Participant {
    private String name;
    private float limitTrack;
    private float limitWall;
    private boolean atDistance;

    public Human(String name, float limitTrack, float limitWall) {
        this.name = name;
        this.limitTrack = limitTrack;
        this.limitWall = limitWall;
        this.atDistance=true;
    }
    @Override
    public boolean isAtDistance(){
        return atDistance;
    }
    @Override
    public String getName(){
        return name;
    }

    @Override
    public void run(float length) {
        System.out.printf("Человек по имени %s побежал\n", name);
        if (length < limitTrack){
            System.out.printf("%s пробежал дистанцию.\n", name);
        } else {
            System.out.printf("%s не смог пробежать дистанцию.\n", name);
            atDistance = false;
        }
    }

    @Override
    public void jump(float height ) {
        System.out.printf("Человек по имени %s прыгает\n", name);
        if (height < limitWall){
            System.out.printf("%s перепрыгнул препятствие.\n", name);
        } else {
            System.out.printf("%s не смог перепрыгнуть дистанцию.\n", name);
            atDistance = false;
        }

    }

}
