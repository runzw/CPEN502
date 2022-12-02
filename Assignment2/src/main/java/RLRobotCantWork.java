import robocode.*;
import java.awt.*;
import java.util.Random;

import static robocode.util.Utils.normalRelativeAngleDegrees;

enum Energy {empty, almostEmpty, medium, half, high}
enum Action {retreat, advance, leaveCorner, fire, spin}
enum Distance {veryClose, close, middle, far, veryFar}

public class RLRobotCantWork extends AdvancedRobot {
    private final Energy energies[] = new Energy[]{
            Energy.empty,
            Energy.almostEmpty,
            Energy.medium,
            Energy.half,
            Energy.high
    };

    private final Distance distances[] = new Distance[]{
            Distance.veryClose,
            Distance.close,
            Distance.middle,
            Distance.far,
            Distance.veryFar
    };

    private final double hitReward = 1.0;
    private final double beHitReward = -0.25;
    private final double winReward = 2.0;
    private final double lossReward = -0.5;

    private final double gamma = 0.9;
    private final double alpha = 0.9;
    private final double epsilon = 0.35;

    private final boolean onPolicy = false;

    private RLRobotLUT RLLUT = new RLRobotLUT(
            Energy.values().length,
            Distance.values().length,
            Energy.values().length,
            Distance.values().length,
            Action.values().length
    );
    RLLUT.initialise();

    static int totalNumRounds = 0;
    static int numRoundsTo100 = 0;
    static int numWins = 0;

    private Energy curMyEnergy = Energy.high;
    private Energy curEnemyEnergy = Energy.high;
    private Distance curDistanceToEnemy = Distance.middle;
    private Distance curDistanceleaveCorner = Distance.middle;
    private Action curAction = Action.spin;

    private Energy prevMyEnergy = Energy.high;
    private Energy prevEnemyEnergy = Energy.high;
    private Distance prevDistanceToEnemy = Distance.middle;
    private Distance prevDistanceleaveCorner = Distance.middle;
    private Action prevAction = Action.spin;

    // Initialize states
    double myX = 0.0;
    double myY = 0.0;
    double myEnergy = 0.0;
    double enemyBearing = 0.0;
    double enemyDistance = 0.0;
    double enemyEnergy = 0.0;

    double totalReward = 0.0;
    private double curReward = 0.0;
    int direction = 1;

    int xMid = 0;
    int yMid = 0;

    private void robotMovement() {
        if (Math.random() < epsilon)
            curAction = selectRandomAction();
        else
            curAction = selectBestAction(
                    myEnergy,
                    enemyDistance,
                    enemyEnergy,
                    distanceleaveCorner(myX, myY, xMid, yMid)
            );

        switch (curAction) {
            case spin: {
                setTurnRight(enemyBearing + 90);
                setAhead(50 * direction);
                break;
            }
            case fire: {
                turnGunRight(normalRelativeAngleDegrees(getHeading() - getGunHeading() + enemyBearing));
                setFire(3);
                break;
            }
            case advance: {
                setTurnRight(enemyBearing);
                setAhead(100);
                break;
            }
            case retreat: {
                setTurnRight(enemyBearing + 180);
                setAhead(100);
                break;
            }
            case leaveCorner: {
                double bearing = getBearingleaveCorner(getX(), getY(), xMid, yMid, getHeadingRadians());
                setTurnRight(bearing);
                setAhead(100);
                break;
            }
        }
    }

    private void radarMovement() {
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    private void trainLUT(){
        double[] index = new double[]{
                prevMyEnergy.ordinal(),
                prevDistanceToEnemy.ordinal(),
                prevEnemyEnergy.ordinal(),
                prevDistanceleaveCorner.ordinal(),
                prevAction.ordinal()};
        double Q = computeQ(curReward);

        RLLUT.train(index, Q);
    }

    public void run() {
        while (true) {
            robotMovement();
            radarMovement();

            if (getGunHeat() == 0)
                execute();

            trainLUT();
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        myX = getX();
        myY = getY();
        enemyBearing = e.getBearing();
        enemyDistance = e.getDistance();
        enemyEnergy = e.getEnergy();
        myEnergy = getEnergy();

        prevMyEnergy = curMyEnergy;
        prevDistanceleaveCorner = curDistanceleaveCorner;
        prevDistanceToEnemy = curDistanceToEnemy;
        prevEnemyEnergy = curEnemyEnergy;
        prevAction = curAction;

        curMyEnergy = EnergyOf(getEnergy());
        curDistanceleaveCorner = DistanceOf(distanceleaveCorner(myX, myY, xMid, yMid));
        curDistanceToEnemy = DistanceOf(e.getDistance());
        curEnemyEnergy = EnergyOf(e.getEnergy());
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        totalReward += hitReward;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        totalReward += beHitReward;
    }

    private void updateBattleStats(){
        if (numRoundsTo100 < 100) {
            numRoundsTo100++;
            totalNumRounds++;
        } else {
            numRoundsTo100 = 0;
            numWins = 0;
        }
    }

    @Override
    public void onDeath(DeathEvent e) {
        curReward = lossReward;

        trainLUT();
        execute();

        updateBattleStats();
    }

    @Override
    public void onWin(WinEvent e) {
        curReward = winReward;

        trainLUT();
        execute();

        updateBattleStats();
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        super.onHitWall(e);
        avoidWall();
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        super.onHitRobot(e);
        avoidWall();
    }

    public void avoidWall() {
        switch (curAction) {
            case spin: {
                direction = direction * -1;
                setAhead(50 * direction);
                break;
            }
            case advance: {
                setTurnRight(30);
                setBack(50);
                execute();
                break;
            }
            case retreat: {
                setTurnRight(30);
                setAhead(50);
                execute();
                break;
            }

        }
    }

    public double computeQ(double r) {
        Action candidateAction;
        if(onPolicy){
            if (Math.random() < epsilon)
                curAction = selectRandomAction();
            else {
                curAction = selectBestAction(
                        curMyEnergy.ordinal(),
                        curDistanceToEnemy.ordinal(),
                        curEnemyEnergy.ordinal(),
                        curDistanceleaveCorner.ordinal());
            }
            candidateAction = curAction;
        }
        else {
            candidateAction = selectBestAction(
                    curMyEnergy.ordinal(),
                    curDistanceToEnemy.ordinal(),
                    curEnemyEnergy.ordinal(),
                    curDistanceleaveCorner.ordinal());
        }

        double[] prevStateAction = new double[]{
                prevMyEnergy.ordinal(),
                prevDistanceToEnemy.ordinal(),
                prevEnemyEnergy.ordinal(),
                prevDistanceleaveCorner.ordinal(),
                prevAction.ordinal()};

        double[] curStateAction = new double[]{
                curMyEnergy.ordinal(),
                curDistanceToEnemy.ordinal(),
                curEnemyEnergy.ordinal(),
                curDistanceleaveCorner.ordinal(),
                candidateAction.ordinal()};

        double prevQ = RLLUT.outputFor(prevStateAction);
        double curQ = RLLUT.outputFor(curStateAction);

        double updatedQ = prevQ + alpha * (r + gamma * curQ - prevQ);
        return updatedQ;
    }

    public Action selectRandomAction() {
        Random rand = new Random();
        int r = rand.nextInt(Action.values().length);
        return Action.values()[r];
    }

    public Action selectBestAction(double e, double d, double e2, double d2) {
        int energy = EnergyOf(e).ordinal();
        int distance = DistanceOf(d).ordinal();
        int enemyEnergy = EnergyOf(e2).ordinal();
        int distanceleaveCorner = DistanceOf(e2).ordinal();
        double bestQ = -Double.MAX_VALUE;
        Action bestAction = null;

        for (int a = Action.spin.ordinal(); a < Action.values().length; a++) {
            double[] x = new double[]{energy, distance, enemyEnergy, distanceleaveCorner, a};
            if (RLLUT.outputFor(x) > bestQ) {
                bestQ = RLLUT.outputFor(x);
                bestAction = Action.values()[a];
            }
        }
        return bestAction;
    }

    public Distance DistanceOf(double distance) {
        if (distance < 50) return Distance.veryClose;
        else return distances[(int) ((distance-50) / 200 + 1)];
    }

    public Energy EnergyOf(double energy) {
        if(energy == 0) return Energy.empty;
        return energies[(int) (energy / 20 + 1)];
    }

    public double distanceleaveCorner(double fromX, double fromY, double toX, double toY) {
        double distance = Math.sqrt(Math.pow((fromX - toX), 2) + Math.pow((fromY - toY), 2));
        return distance;
    }

}