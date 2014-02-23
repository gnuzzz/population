package ru.albemuth.analysis.population.v2;

import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author VKornyshev
 */
public class TestPopulation {

    private static final Logger LOGGER = Logger.getLogger(TestPopulation.class);

    @Test
    public void test() {
        try {
            int debugFactor = 10;
            int maxYear = 1000;
            Population population = new Population();
            population.init(Population.INIT_POPULATION_SIZE, Population.AGE_LIMIT, debugFactor);
            for (int year = 1; year <= maxYear; year++) {
                population.processNextYear();
                if (year % debugFactor == 0) {
                    double avgFirstLaborDeathP = 0;
                    for (Man man: population.getMankind()) {avgFirstLaborDeathP += man.getFirstLaborDeathP();}
                    for (Woman woman: population.getWomankind()) {avgFirstLaborDeathP += woman.getFirstLaborDeathP();}
                    avgFirstLaborDeathP /= population.getMankind().size() + population.getWomankind().size();
                    LOGGER.debug(population.getYear() + ": " + (population.getMankind().size() + population.getWomankind().size()) + ", " + avgFirstLaborDeathP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
