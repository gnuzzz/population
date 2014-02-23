package ru.albemuth.analysis.population.v3

import org.scalatest.FunSuite
import scala.collection.mutable.ListBuffer
import org.apache.log4j.Logger

/**
 * @author vovan
 */
class TestPopulation extends FunSuite {

    private val LOGGER = Logger.getLogger(getClass)

    /*

    test("test") {
    }

    */

    test("Population") {
        val debugFactor: Int = 10
        val maxYear: Int = 1000
        val population: Population = new Population(Population.INIT_POPULATION_SIZE, Population.AGE_LIMIT, debugFactor)

        def printStats(population: Population) {
            val avgMankind = population.mankind.foldLeft((0.0, 0.0, 0.0)){(avg, m) => {(avg._1 + m.age, avg._2 + m.cranialDiameter, avg._3 + Human.firstYearDeathP(m.cranialDiameter))}}
            val avgWomankind = population.womankind.foldLeft((0.0, 0.0, 0.0, 0.0)){(avg, w) => {(avg._1 + w.age, avg._2 + w.cranialDiameter, avg._3 + Human.firstYearDeathP(w.cranialDiameter), avg._4 + w.calculateLaborDeathP(w.cranialDiameter))}}

            val avgAge = (avgMankind._1 + avgWomankind._1) / (population.mankind.size + population.womankind.size)
            val avgCranialDiameter = (avgMankind._2 + avgWomankind._2) / (population.mankind.size + population.womankind.size)
            val avgFirstYearDeathP= (avgMankind._3 + avgWomankind._3) / (population.mankind.size + population.womankind.size)
            val avgFirstLaborDeathP = avgWomankind._4 / population.womankind.size

            LOGGER.debug("Year " + population.year + ": " + (population.mankind.size + population.womankind.size) + ", " + avgAge + ", " + avgCranialDiameter + ", " + avgFirstYearDeathP + ", " + avgFirstLaborDeathP)
        }

        printStats(population)
        for (year <- 1 to maxYear) {
            population.processNextYear()
            if (year % debugFactor == 0) {
                printStats(population)
            }
        }

    }

    test("Woman.calculateLaborDeathP(d: Double)") {
        assert(0.001 == Woman.calculateLaborDeathP(0))
        assert(0.25074817508041075 == Woman.calculateLaborDeathP(0.35))
        assert(0.9911417013565391 == Woman.calculateLaborDeathP(0.7))
    }

    test("Human.firstYearDeathP(p: Double") {
        assert((1 - 0.02583354590573526) == Human.firstYearDeathP(0))
        assert(0 == Human.firstYearDeathP(0.35))
        assert((1 - 0.02583354590573526) == Human.firstYearDeathP(0.7))
    }



}
