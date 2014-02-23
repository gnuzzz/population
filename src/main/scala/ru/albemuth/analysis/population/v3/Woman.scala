package ru.albemuth.analysis.population.v3

import ru.albemuth.analysis.population.v3.Human.Sex

/**
 * @author vovan
 * Класс, моделирующий женщину
 */
object Woman {

    def calculateLaborDeathP(d: Double): Double = {
        import scala.math.exp
        val r = 16.606082245039158
        Population.p(0.001 * exp(r * d) / (1 + 0.001 * (exp(r * d) - 1)))
    }
    
    def calculateLaborDeathP(laborDeathP: Double, laborCounter: Int): Double = {
        var deathP: Double = 0

        laborCounter match {
            case 1 => deathP = laborDeathP
            case 2 => deathP = 0.7 * laborDeathP
            case 3 => deathP = 0.5 * laborDeathP
            case 4 => deathP = 0.7 * laborDeathP
            case 5 => deathP = laborDeathP
            case _ => deathP = laborDeathP
        }

        deathP
    }

}

class Woman(aCranialDiameter: Double, anAge: Int, anAgeLimit: Int) extends Human(aCranialDiameter, anAge, anAgeLimit) {

    val sex = Sex.FEMALE

    private var laborCounter_ = 0

    def laborCounter = laborCounter_
    def incLaborCounter() { laborCounter_ += 1}

    def isLaborAccepted: Boolean = {
        //женщина рожает на 17, 21, 25, 29, 33 год
        age == 17 || age == 21 || age == 25 || age == 29 || age == 33
    }

    def calculateLaborDeathP(childCranialDiameter: Double): Double = {Woman.calculateLaborDeathP(Woman.calculateLaborDeathP(childCranialDiameter), laborCounter)}

}
