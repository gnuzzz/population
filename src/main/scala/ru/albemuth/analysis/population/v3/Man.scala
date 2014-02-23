package ru.albemuth.analysis.population.v3

import ru.albemuth.analysis.population.v3.Human.Sex

/**
 * @author vovan
 * Класс, моделирующий мужчины
 */
class Man(aCranialDiameter: Double, anAge: Int, anAgeLimit: Int) extends Human(aCranialDiameter, anAge, anAgeLimit) {

    val sex = Sex.MALE

    def isFatherhoodAccepted: Boolean = {
        //отцовство возможно для мужчин от 17 до 33-х лет
        17 <= age && age <= 33
    }

}
