package ru.albemuth.analysis.population;

/**
 * @author VKornyshev
 * Класс, моделирующий женщину
 */
public class Woman extends Human {

    protected int laborCounter = 0;//кол-во родов

    public Woman(int ageLimit, double firstLaborDeathP) {
        super(ageLimit, firstLaborDeathP);
    }

    @Override
    public Sex getSex() {
        return Sex.FEMALE;
    }

    public int getLaborCounter() {
        return laborCounter;
    }

    public void incLaborCounter() {
        laborCounter++;
    }

    public boolean isLaborAccepted() {
        //женщина рожает на 17, 21, 25, 29, 33 год
        return age == 17 || age == 21 || age == 25 || age == 29 || age == 33;
    }

    public double calculateLaborDeathP(int laborCounter, double firstLaborDeathP) {
        double laborDeathP;
        switch (laborCounter) {
            case 1:
                laborDeathP = firstLaborDeathP;
                break;
            case 2:
                laborDeathP = 0.7 * firstLaborDeathP;
                break;
            case 3:
                laborDeathP = 0.5 * firstLaborDeathP;
                break;
            case 4:
                laborDeathP = 0.7 * firstLaborDeathP;
                break;
            case 5:
                laborDeathP = firstLaborDeathP;
                break;
            default:
                laborDeathP = 1;
        }
        return laborDeathP;
    }

}
