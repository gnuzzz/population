package ru.albemuth.analysis.population;

/**
 * @author VKornyshev
 * Класс, моделирующий мужчины
 */
public class Man extends Human {

    //protected boolean fatherhoodAccepted;

    public Man(int ageLimit, double firstLaborDeathP) {
        super(ageLimit, firstLaborDeathP);
    }

    @Override
    public Sex getSex() {
        return Sex.MALE;
    }

    /*@Override
    public void incAge() {
        super.incAge();
        fatherhoodAccepted = 17 <= age && age <= 33;//отцовство возможно для мужчин от 17 до 33-х лет
    }*/

    public boolean isFatherhoodAccepted() {
        return 17 <= age && age <= 33;//отцовство возможно для мужчин от 17 до 33-х лет
        //return fatherhoodAccepted;
    }

}
