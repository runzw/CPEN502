import robocode.*;

import java.awt.*;

import java.util.Random;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class RLRobot extends AdvancedRobot {
    private String saveClassname =  getClass().getSimpleName() + ".txt";

    public enum enumEnergy {zero, dying, low, medium, high}

    public enum enumDistance {exClose, close, near, far, exFar}

    public enum enumAction {circle, retreat, advance, fire, toCenter}
    public enum enumOptionalMode {scan, performanceAction}

    static private RLRobotLUT q = new RLRobotLUT(
            enumEnergy.values().length,
            enumDistance.values().length,
            enumEnergy.values().length,
            enumDistance.values().length,
            enumAction.values().length
    );

    static int totalNumRounds = 0;
    static int numRoundsTo100 = 0;
    static int numWins = 0;

    private enumEnergy myCurrentEnergy = enumEnergy.high;
    private enumEnergy enemyCurrentEnergy = enumEnergy.high;
    private enumDistance distanceToEnemy = enumDistance.near;
    private enumDistance distanceToCenter = enumDistance.near;
    private enumAction currentAction = enumAction.circle;


    private enumEnergy prevMyEnergy = enumEnergy.high;
    private enumEnergy prevEnemyEnergy = enumEnergy.high;
    private enumDistance prevDistanceToEnemy = enumDistance.near;
    private enumDistance prevDistanceToCenter = enumDistance.near;
    private enumAction prevAction = enumAction.circle;

    private enumOptionalMode optionalMode = enumOptionalMode.scan;

 
    private double gamma = 0.9;
    private double alpha = 0.9;
    private final double e_initial = 0.35;
    private double e = e_initial;
    private boolean decaye = false;

    //previous and current Q
    private double currentQ = 0.0;
    private double previousQ = 0.0;

    // Rewards
    private final double goodReward = 1.0;
    private final double badReward = -0.25;
    private final double goodTerminalReward = 2.0;
    private final double badTerminalReward = -0.5;

    private double currentReward = 0.0;

    // Initialize states
    double myX = 0.0;
    double myY = 0.0;
    double myEnergy = 0.0;
    double enemyBearing = 0.0;
    double enemyDistance = 0.0;
    double enemyEnergy = 0.0;

    double totalReward = 0.0;

    int direction = 1;


    // Logging
    static String logFilename = "robotLUT.log";
    static LogFile log = null;

    // get center of board
    int xMid = 0;
    int yMid = 0;

    public void run() {
        setBodyColor(Color.red);
        setGunColor(Color.black);
        setRadarColor(Color.yellow);
        setBulletColor(Color.green);
        setScanColor(Color.green);

        // get coordinate of the board center
        int xMid = (int) getBattleFieldWidth() / 2;
        int yMid = (int) getBattleFieldHeight() / 2;

        // Create log file
        if (log == null) {
            System.out.print("!!!*********************!!!");
            System.out.print(logFilename);
            log = new LogFile(getDataFile(logFilename));
            log.stream.printf("Start writing log\n");
            log.stream.printf("gamma,   %2.2f\n", gamma);
            log.stream.printf("alpha,   %2.2f\n", alpha);
            log.stream.printf("e, %2.2f\n", e);
            log.stream.printf("badInstantReward, %2.2f\n", badReward);
            log.stream.printf("badTerminalReward, %2.2f\n", badTerminalReward);
            log.stream.printf("goodInstantReward, %2.2f\n", goodReward);
            log.stream.printf("goodTerminalReward, %2.2f\n\n", goodTerminalReward);
        }

        while (true) {

            // set e to 0 after 8000 round
            if (totalNumRounds > 5000) e = 0;

            System.out.println("Flag 1");

            robotMovement();
            radarMovement();

            if (getGunHeat() == 0)
                execute();

            // Update previous Q
            double[] x = new double[]{
                    prevMyEnergy.ordinal(),
                    prevDistanceToEnemy.ordinal(),
                    prevEnemyEnergy.ordinal(),
                    prevDistanceToCenter.ordinal(),
                    prevAction.ordinal()};

            q.train(x, computeQ(currentReward));

            optionalMode = enumOptionalMode.scan;
            execute();
        }
    }

    private void robotMovement() {
        if (Math.random() < e)
            // exploit
            currentAction = selectRandomAction();
        else
            currentAction = selectBestAction(
                    myEnergy,
                    enemyDistance,
                    enemyEnergy,
                    distanceToCenter(myX, myY, xMid, yMid)
            );

        switch (currentAction) {
            case circle: {
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
            case toCenter: {
                double bearing = getBearingToCenter(getX(), getY(), xMid, yMid, getHeadingRadians());
                setTurnRight(bearing);
                setAhead(100);
                break;
            }
        }
    }

    private void radarMovement() {
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        myX = getX();
        myY = getY();
        enemyBearing = e.getBearing();
        enemyDistance = e.getDistance();
        enemyEnergy = e.getEnergy();
        myEnergy = getEnergy();

        // Update states
        prevMyEnergy = myCurrentEnergy;
        prevDistanceToCenter = distanceToCenter;
        prevDistanceToEnemy = distanceToEnemy;
        prevEnemyEnergy = enemyCurrentEnergy;
        prevAction = currentAction;

        myCurrentEnergy = enumEnergyOf(getEnergy());
        distanceToCenter = enumDistanceOf(distanceToCenter(myX, myY, xMid, yMid));
        distanceToEnemy = enumDistanceOf(e.getDistance());
        enemyCurrentEnergy = enumEnergyOf(e.getEnergy());
        optionalMode = enumOptionalMode.performanceAction;
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        currentReward = goodReward; totalReward += currentReward;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        currentReward = badReward; totalReward += currentReward;
    }

    @Override
    public void onDeath(DeathEvent e) {
        currentReward = badTerminalReward;
        totalReward += currentReward;
        totalReward = 0;

        // Update Q, otherwise it won't be updated at the last round
        double[] x = new double[]{
                prevMyEnergy.ordinal(),
                prevDistanceToEnemy.ordinal(),
                prevEnemyEnergy.ordinal(),
                prevDistanceToCenter.ordinal(),
                prevAction.ordinal()};

        q.train(x, computeQ(currentReward));

        // stats
        if (numRoundsTo100 < 100) {
            numRoundsTo100++;
            totalNumRounds++;
        } else {
            log.stream.printf("%d - %d  win rate, %2.1f\n", totalNumRounds - 100, totalNumRounds, 100.0 * numWins / numRoundsTo100);
            log.stream.flush();
            numRoundsTo100 = 0;
            numWins = 0;
        }

        q.save(getDataFile(saveClassname));
    }

    @Override
    public void onWin(WinEvent e) {
        currentReward = goodTerminalReward;
        totalReward += currentReward;
        totalReward = 0;

        // Update Q, otherwise it won't be updated at the last round
        double[] x = new double[]{
                prevMyEnergy.ordinal(),
                prevDistanceToEnemy.ordinal(),
                prevEnemyEnergy.ordinal(),
                prevDistanceToCenter.ordinal(),
                prevAction.ordinal()};

        q.train(x, computeQ(currentReward));

        // stats
        if (numRoundsTo100 < 100) {
            numRoundsTo100++;
            totalNumRounds++;
            numWins++;
        } else {
            log.stream.printf("%d - %d  win rate, %2.1f\n", totalNumRounds - 100, totalNumRounds, 100.0 * numWins / numRoundsTo100);
            log.stream.flush();
            numRoundsTo100 = 0;
            numWins = 0;
        }

        q.save(getDataFile(saveClassname));
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        super.onHitWall(e);
        avoidObstacle();
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        super.onHitRobot(e);
//        if (e.isMyFault()) {
//            currentReward = badReward;
//            totalReward += currentReward;
//        }
        avoidObstacle();
    }

    public void avoidObstacle() {
        switch (currentAction) {
            case circle: {
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
        enumAction maxA = selectBestAction(
                myCurrentEnergy.ordinal(),
                distanceToEnemy.ordinal(),
                enemyCurrentEnergy.ordinal(),
                distanceToCenter.ordinal());

        double[] prevStateAction = new double[]{
                prevMyEnergy.ordinal(),
                prevDistanceToEnemy.ordinal(),
                prevEnemyEnergy.ordinal(),
                prevDistanceToCenter.ordinal(),
                prevAction.ordinal()};

        double[] currentStateAction = new double[]{
                myCurrentEnergy.ordinal(),
                distanceToEnemy.ordinal(),
                enemyCurrentEnergy.ordinal(),
                distanceToCenter.ordinal(),
                maxA.ordinal()};

        double prevQ = q.outputFor(prevStateAction);
        double currentQ = q.outputFor(currentStateAction);

        double updatedQ = prevQ + alpha * (r + gamma * currentQ - prevQ);
        return updatedQ;
    }

    public enumAction selectRandomAction() {
        Random rand = new Random();
        int r = rand.nextInt(enumAction.values().length);
        return enumAction.values()[r];
    }

    public enumAction selectBestAction(double e, double d, double e2, double d2) {
        int energy = enumEnergyOf(e).ordinal();
        int distance = enumDistanceOf(d).ordinal();
        int enemyEnergy = enumEnergyOf(e2).ordinal();
        int distanceToCenter = enumDistanceOf(e2).ordinal();
        double bestQ = -Double.MAX_VALUE;
        enumAction bestAction = null;

        for (int a = enumAction.circle.ordinal(); a < enumAction.values().length; a++) {
            double[] x = new double[]{energy, distance, enemyEnergy, distanceToCenter, a};
            if (q.outputFor(x) > bestQ) {
                bestQ = q.outputFor(x);
                bestAction = enumAction.values()[a];
            }
        }
        return bestAction;
    }

    public enumDistance enumDistanceOf(double distance) {
        enumDistance d = null;
        if (distance < 50) d = enumDistance.exClose;
        else if (distance >= 50 && distance < 250) d = enumDistance.close;
        else if (distance >= 250 && distance < 500) d = enumDistance.near;
        else if (distance >= 500 && distance < 750) d = enumDistance.far;
        else if (distance >= 750) d = enumDistance.exFar;
        return d;
    }

    public enumEnergy enumEnergyOf(double energy) {
        enumEnergy e = null;
        if (energy == 0) e = enumEnergy.zero;
        else if (energy > 0 && energy < 20) e = enumEnergy.dying;
        else if (energy >= 20 && energy < 40) e = enumEnergy.low;
        else if (energy >= 40 && energy < 60) e = enumEnergy.medium;
        else if (energy >= 60) e = enumEnergy.high;
        return e;
    }

    public double distanceToCenter(double fromX, double fromY, double toX, double toY) {
        double distance = Math.sqrt(Math.pow((fromX - toX), 2) + Math.pow((fromY - toY), 2));
        return distance;
    }

    // convert an angle to [-Pi, Pi]
    public double norm(double a) {
        while (a <= -Math.PI) a += 2 * Math.PI;
        while (a > Math.PI) a -= 2 * Math.PI;
        return a;
    }

    public double getBearingToCenter(double fromX, double fromY, double toX, double toY, double currentHeadingRadians) {
        double b = Math.PI / 2 - Math.atan2(toY - fromY, toX - fromX);
        return norm(b - currentHeadingRadians);
    }

}