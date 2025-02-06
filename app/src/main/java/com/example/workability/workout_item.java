package com.example.workability;

public class workout_item {
    public String name;
    private String benefits;
    private String message;
    private int workout_pic;

    public workout_item(String name, String benefits, String message) {
        this.name = name;
        this.benefits = benefits;
        this.message = message;;
    }

    public String getName() {
        return name;
    }

    public String getBenefits() {
        return benefits;
    }

    public String getMessage() {
        return message;
    }
    public int getWorkout_pic(){
        return workout_pic;
    }
}
