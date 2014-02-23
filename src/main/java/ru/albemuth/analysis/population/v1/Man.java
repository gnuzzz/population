package ru.albemuth.analysis.population.v1;

/**
 * @author vovan
 * Класс, моделирующий мужчину
 */
public class Man extends Human {

    public Man(int ageLimit, double firstLaborDeathP) {
        super(ageLimit, firstLaborDeathP);
    }

    @Override
    public Sex getSex() {
        return Sex.MALE;
    }

    public boolean isFatherhoodAccepted() {
        return 17 <= age && age <= 33;//отцовство возможно для мужчин от 17 до 33-х лет
    }

}
