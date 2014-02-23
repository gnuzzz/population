package ru.albemuth.analysis.population.v3

import scala.math.random

/**
 * @author vovan
 * Класс, моделирующий человека
 */
object Human {

    object Sex extends Enumeration {
        type Sex = Value

        val MALE = Value("male")
        val FEMALE = Value("female")
    }

    def firstYearDeathP(d: Double): Double = {
        import scala.math.sqrt
        import scala.math.Pi
        import scala.math.exp
        val m = 0.35
        val s = 0.12930984560277864
        val c = 0.32639851212114895

        1 - Population.p(c / (s * sqrt(2 * Pi)) * exp(- (d - m) * (d - m) / (2 * s * s)))
    }

}

abstract class Human(val cranialDiameter: Double, var age: Int, val ageLimit: Int) {

    import Human._
    import Human.Sex._
    def sex: Sex
    private var dead = false

    def incAge(): Int = {
        age += 1
        dead = age == 1 && random < firstYearDeathP(cranialDiameter) || age >= ageLimit
        age
    }

    def isDead: Boolean = dead

}
