package ru.albemuth.analysis.population;

/**
 * @author VKornyshev
 * Класс, моделирующий человека
 */
public abstract class Human {

    public enum Sex {
        MALE, FEMALE
    }

    protected int ageLimit;//предельный возраст, по достижении которого человек умирает
    protected int age;//возраст
    protected double firstLaborDeathP;//вероятность смерти первыми родами

    public Human(int ageLimit, double firstLaborDeathP) {
        this.ageLimit = ageLimit;
        this.age = 0;
        this.firstLaborDeathP = firstLaborDeathP;
    }

    public abstract Sex getSex();

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void incAge() {
        age++;
    }

    public boolean isDead() {
        return age >= ageLimit;
    }

    public double getFirstLaborDeathP() {
        return firstLaborDeathP;
    }

}
