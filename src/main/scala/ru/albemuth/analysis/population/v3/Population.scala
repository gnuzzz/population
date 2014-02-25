package ru.albemuth.analysis.population.v3

import scala.math.random
import scala.math.pow
import scala.collection.mutable
import Human.Sex._
import scala.collection.mutable.ArrayBuffer

/**
 * @author vovan
 */
object Population {

    import org.apache.log4j.Logger
    private val LOGGER = Logger.getLogger(Population.getClass)

    final val INIT_POPULATION_SIZE: Int = 100000//начальный объем популяции
    final val AGE_LIMIT: Int = 70//предельный срок жизни

    def calculateInitCranialDiameter() = {
        0.7 * random//диаметр черепа в исходной популяции равномерно распределен на интервале 0 - 0.7. Не очень реалистично)
    }

    def calculateInitAge() = {
        (70 * random).asInstanceOf[Int]//возраста в исходной популяции равномерно распределены на интервале 0(новорожденные) - 69(старики, по достижении 70 лет умирают)
    }

    def p(p: Double) = { if (p < 0) 0.0 else if (p > 1) 1.0 else p }

    def mutationsFactor = /*0.01*/0.25
}

class Population(val initPopulationSize: Int, val ageLimit: Int, val debugFactor: Int) {
    import Population._

    private var year_ = 0
    def year = year_

    //мужчины - половина исходной популяции. Т.к. из этого списка будет много удалений, лучше использовать LinkedList
    var mankind = mutable.ListBuffer[Man]()
    for (i <- 0 to initPopulationSize / 2) {
        val man = new Man(calculateInitCranialDiameter(), calculateInitAge(), ageLimit)
        mankind += man
    }

    //женщины - половина исходной популяции. Т.к. из этого списка будет много удалений, лучше использовать LinkedList
    var womankind = mutable.ListBuffer[Woman]()
    for (i <- 0 to initPopulationSize / 2) {
        val woman = new Woman(calculateInitCranialDiameter(), calculateInitAge(), ageLimit)
        womankind += woman
    }

    def processNextYear() {
        year_ += 1
        processAges()
        processLabors()
        processOverPopulationDeaths()
    }

    private def processAges() {
        val mr = processAges(year, mankind)
        mankind = mr._1
        val wmr = processAges(year, womankind)
        womankind = wmr._1
        if (year % debugFactor == 0) { LOGGER.debug("Year: " + year + ", age deaths: " + (mr._2 + wmr._2) + "(" + mr._2 + "/" + wmr._2 + ")") }
    }
    
    private def processAges[H <: Human](year: Int, humans: mutable.ListBuffer[H]): (mutable.ListBuffer[H], Int) = {
        val ret = humans.filter(human => {human.incAge(); !human.isDead})
        (ret, humans.size - ret.size)
    }

    private def processLabors() {
        val fathers = calculateFathers(mankind)
        val children = mutable.ListBuffer[Human]()

        womankind = womankind.filter(woman => {
            if (woman.isLaborAccepted) {
                val father = findFather(fathers, woman)
                if (father != null) {
                    val child = processLabor(father, woman)
                    if (child == null) {//смерть во время родов
                        false
                    } else {
                        children += child
                    }
                }
            }
            true
        })

        var mans = 0
        var womans = 0
        for (child <- children) {
            if (child.sex == Human.Sex.MALE) {
                mankind += child.asInstanceOf[Man]
                mans += 1
            } else if (child.sex == Human.Sex.FEMALE) {
                womankind += child.asInstanceOf[Woman]
                womans += 1
            }
        }

        if (year % debugFactor == 0) { LOGGER.debug("Year: " + year + ", newborns: " + (mans + womans) + "(" + mans + "/" + womans + ")") }
    }

    private def calculateFathers(mankind: mutable.ListBuffer[Man]): mutable.ArrayBuffer[Man] = {
        val fathers = new ArrayBuffer[Man](mankind.size / 2)
        for (man <- mankind if man.isFatherhoodAccepted) {fathers += man}
        fathers
    }

    private def findFather(fathers: mutable.Buffer[Man], woman: Woman): Man = {
        if (fathers.isEmpty) null else fathers((fathers.size * Math.random).asInstanceOf[Int])
    }

    private def processLabor(father: Man, mother: Woman): Human = {
        mother.incLaborCounter()

        val childCranialDiameter = calculateChildCranialDiameter(father, mother)
        val laborDeathP = mother.calculateLaborDeathP(childCranialDiameter)

        var child: Human = null

        val death = Math.random < laborDeathP

        if (!death) {
            val childSex = calculateChildSex
            if (childSex == Human.Sex.MALE) {
                child = new Man(childCranialDiameter, 0, ageLimit)
            } else if (childSex == Human.Sex.FEMALE) {
                child = new Woman(childCranialDiameter, 0, ageLimit)
            }
        }

        child
    }

    private def calculateChildCranialDiameter(father: Man, mother: Woman): Double = {
        //диаметр черепа передается от одного из родителей (50/50) с изменением +/- 1% на мутации
        var diameter = (if (Math.random < 0.5) mother.cranialDiameter else father.cranialDiameter) * (if (Math.random < 0.5) (1 + mutationsFactor) else (1 - mutationsFactor))
        if (diameter < 0) {diameter = 0}
        diameter
    }

    private def calculateChildSex: Sex = {
        //пол ребенка - 50/50
        if (Math.random < 0.5) Human.Sex.MALE else Human.Sex.FEMALE}

    private def processOverPopulationDeaths() {
        val projectedPopulationSize: Int = (initPopulationSize * pow(pow(2, 1 / 200.0), year)).asInstanceOf[Int]
        val overPopulatedSize: Int = mankind.size + womankind.size - projectedPopulationSize

        if (year % debugFactor == 0) { LOGGER.debug("Year: " + year + ", projected size: " + projectedPopulationSize + ", current size: " + (mankind.size + womankind.size) + "(" + mankind.size + "/" + womankind.size + "), overpopulated: " + overPopulatedSize) }

        if (overPopulatedSize > 0) {
            val overPopulatedMansSize = (overPopulatedSize * mankind.size / (mankind.size + womankind.size).asInstanceOf[Double]).asInstanceOf[Int]
            mankind = deleteHumans(mankind, overPopulatedMansSize)
            val overPopulatedWomansSize = overPopulatedSize - overPopulatedMansSize
            womankind = deleteHumans(womankind, overPopulatedWomansSize)
        }
    }

    private def deleteHumans[H <: Human](humans: mutable.ListBuffer[H], number: Int):mutable.ListBuffer[H] = {
        var n = number
        var size = humans.size
        humans.filter(human => {
            if (random < number / size.asInstanceOf[Double]) {
                n -= 1; size -= 1; false
            } else {
                true
            }
        })
    }

}
