package ru.albemuth.analysis.population.v2;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author vovan
 * Класс, моделирующий человеческую популяцию
 */
public class Population {

    private static final Logger LOGGER = Logger.getLogger(Population.class);

    public static final int INIT_POPULATION_SIZE = 100000;//начальный объем популяции
    public static final int AGE_LIMIT = 70;//предельный срок жизни

    private int year;
    private List<Man> mankind;
    private List<Woman> womankind;
    private int ageLimit;
    private int debugFactor;

    public int getYear() {
        return year;
    }

    public List<Man> getMankind() {
        return mankind;
    }

    public List<Woman> getWomankind() {
        return womankind;
    }

    public void init(int initPopulationSize, int ageLimit, int debugFactor) {
        this.ageLimit = ageLimit;
        this.debugFactor = debugFactor;
        this.mankind = new LinkedList<Man>();//мужчины - половина исходной популяции. Т.к. из этого списка будет много удалений, лучше использовать LinkedList
        for (int i = 0; i < initPopulationSize / 2; i++) {
            Man man = new Man(ageLimit, calculateInitFirstLaborDeathP());
            man.setAge(calculateInitAge());
            this.mankind.add(man);
        }

        this.womankind = new LinkedList<Woman>();//женщины - половина исходной популяции. Т.к. из этого списка будет много удалений, лучше использовать LinkedList
        for (int i = 0; i < initPopulationSize / 2; i++) {
            Woman woman = new Woman(ageLimit, calculateInitFirstLaborDeathP());
            woman.setAge(calculateInitAge());
            this.womankind.add(woman);
        }
    }

    public void processNextYear() {
        year++;
        processAges(year, mankind, womankind);
        processLabors(year, mankind, womankind);
        processOverPopulationDeaths(year, mankind, womankind);
    }

    private void processAges(int year, List<Man> mankind, List<Woman> womankind) {
        int mankindDeathsCounter = processAges(mankind);
        int womankindDeathsCounter = processAges(womankind);
        if (year % debugFactor == 0) {LOGGER.debug("Year: " + year + ", age deaths: " + (mankindDeathsCounter + womankindDeathsCounter) + "(" + mankindDeathsCounter + "/" + womankindDeathsCounter + ")");}
    }

    private int calculateInitAge() {
        return (int)(70 * Math.random());//возраста в исходной популяции равномерно распределены на интервале 0(новорожденные) - 69(старики, по достижении 70 лет умирают)
    }

    private int processAges(List<? extends Human> humans) {
        int deathsCounter = 0;
        for (Iterator<? extends Human> it = humans.iterator(); it.hasNext(); ) {
            Human human = it.next();
            human.incAge();
            if (human.isDead()) {//смерть по достижении предельного возраста
                it.remove();
                deathsCounter++;
            }
        }
        return deathsCounter;
    }

    private void processLabors(int year, List<Man> mankind, List<Woman> womankind) {
        List<Man> fathers = calculateFathers(mankind);
        List<Human> children = new ArrayList<Human>();
        for (Iterator<Woman> it = womankind.iterator(); it.hasNext(); ) {
            Woman woman = it.next();
            if (woman.isLaborAccepted()) {
                Man father = findFather(fathers, woman);//подбор потенциального отца
                if (father != null) {
                    Human child = processLabor(father, woman);
                    if (child == null) {//смерть во время родов
                        it.remove();
                    } else {
                        children.add(child);//сразу в mankind или womankind добавлять нельзя, т.к. получим ConcurrentModificationException при итерации по womankind
                    }
                }
            }
        }
        int mans = 0;
        int womans = 0;
        for (Human child: children) {
            if (child.getSex() == Human.Sex.MALE) {
                mankind.add((Man)child);
                mans++;
            } else if (child.getSex() == Human.Sex.FEMALE) {
                womankind.add((Woman)child);
                womans++;
            }
        }
        if (year % debugFactor == 0) {LOGGER.debug("Year: " + year + ", newborns: " + (mans + womans) + "(" + mans + "/" + womans + ")");}
    }

    private List<Man> calculateFathers(List<Man> mankind) {
        List<Man> fathers = new ArrayList<Man>(mankind.size());
        for (Man man: mankind) {
            if (man.isFatherhoodAccepted()) {fathers.add(man);}
        }
        return fathers;
    }

    //возвращает ребенка, если роды успешны, null, если роды окончились смертью
    private Human processLabor(Man father, Woman mother) {
        mother.incLaborCounter();
        double laborDeathP = mother.calculateLaborDeathP(mother.getLaborCounter(), mother.getFirstLaborDeathP());//вероятность смерти при родах
        Human child = null;
        boolean death = Math.random() < laborDeathP;//определяем, закончились роды смертью или нет
        if (!death) {
            double childFirstLaborDeathP = calculateChildFirstLaborDeathP(father, mother); //вероятность смерти первыми родами ребенка
            Human.Sex childSex = calculateChildSex();//определяем пол ребенка
            if (childSex == Human.Sex.MALE) {
                child = new Man(ageLimit, childFirstLaborDeathP);
            } else if (childSex == Human.Sex.FEMALE) {//я в курсе что тут проверка уже не нужна, но влруг полов будет больше 2-х?)
                child = new Woman(ageLimit, childFirstLaborDeathP);
            }
        }
        return child;
    }

    private double calculateInitFirstLaborDeathP() {
        return 0.5 * Math.random();//исходная вероятность смерти первыми родами равномерно распределена на интервале 0 - 0.5
    }

    private double calculateChildFirstLaborDeathP(Man father, Woman mother) {
        //вероятность смерти первыми родами передается от одного из родителей (50/50) с изменением +/- 1% на мутации
        double childFirstLaborDeathP = (Math.random() < 0.5 ? mother.getFirstLaborDeathP() : father.getFirstLaborDeathP()) * (Math.random() < 0.5 ? 1.01 : 0.99);
        if (childFirstLaborDeathP > 1) {childFirstLaborDeathP = 1;}//вероятность больше 1 быть не может (меньше 0 тоже, но при данном способе ее генерации это невозможно)
        return childFirstLaborDeathP;
    }

    private Human.Sex calculateChildSex() {
        return Math.random() < 0.5 ? Human.Sex.MALE : Human.Sex.FEMALE;//пол ребенка - 50/50
    }

    private Man findFather(List<Man> fathers, Woman woman) {
        //потенциальный отец выбирается случайным образом из всего множества потенциальных отцов
        return fathers.isEmpty() ? null : fathers.get((int)(fathers.size() * Math.random()));
    }

    private void processOverPopulationDeaths(int year, List<Man> mankind, List<Woman> womankind) {
        int projectedPopulationSize = (int)(100000 * Math.pow(Math.pow(2, 1/200.0), year));//соответствует удвоению населения каждые 200 лет
        int overPopulatedSize = mankind.size() + womankind.size() - projectedPopulationSize;
        if (year % debugFactor == 0) {LOGGER.debug("Year: " + year + ", projected size: " + projectedPopulationSize + ", current size: " + (mankind.size() + womankind.size()) + "(" + mankind.size() + "/" + womankind.size() + "), overpopulated: " + overPopulatedSize);}
        if (overPopulatedSize > 0) {//если переизбыток населения
            int overPopulatedMansSize = (int)(overPopulatedSize * mankind.size()/(double)(mankind.size() + womankind.size()));
            deleteHumans(mankind, overPopulatedMansSize);
            int overPopulatedWomansSize = overPopulatedSize - overPopulatedMansSize;
            deleteHumans(womankind, overPopulatedWomansSize);
        }
    }

    private void deleteHumans(List<? extends Human> humans, int number) {
        for (Iterator<? extends Human> it = humans.iterator(); it.hasNext(); ) {
            Human human = it.next();
            if (Math.random() < number/(double)humans.size()) {
                it.remove();
                number--;
            }
        }
    }

}
