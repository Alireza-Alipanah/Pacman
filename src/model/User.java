package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class User {

    private static final ArrayList<User> ALL_USERS;
    private static final DateTimeFormatter DATE_TIME_FORMATTER;
    private static final ArrayList<int[][]> defaultMap;
    private static User loggedIn = null;

    static {
        ALL_USERS = new ArrayList<>();
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        defaultMap = new ArrayList<>();
    }

    private final String USERNAME;
    private final ArrayList<int[][]> MAZES;
    private int score;
    private LocalDateTime lastScoreTime;
    private String password;
    private int rank;
    private Game currentGame;

    public User(String USERNAME, String password) {
        this.lastScoreTime = LocalDateTime.now();
        this.score = 0;
        this.USERNAME = USERNAME;
        this.password = password;
        this.MAZES = new ArrayList<>(defaultMap);
        if (!USERNAME.equals(""))
            ALL_USERS.add(this);
    }

    public static ArrayList<int[][]> getDefaultMap() {
        return defaultMap;
    }

    public static void setLoggedIn(User loggedIn) {
        User.loggedIn = loggedIn;
    }

    public static User getLoggedIn() {
        return loggedIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLastScoreTime() {
        return lastScoreTime.format(DATE_TIME_FORMATTER);
    }

    public LocalDateTime getLastScoreTimeWithPrecision() {
        return lastScoreTime;
    }

    public void setLastScoreTime(LocalDateTime lastScoreTime) {
        this.lastScoreTime = lastScoreTime;
    }

    public String getUsername() {
        return USERNAME;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public ArrayList<int[][]> getMazes() {
        return MAZES;
    }

    public void setCurrentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public static ArrayList<User> getAllUsers() {
        return ALL_USERS;
    }

    public void addNewMaze(int[][] maze) {
        MAZES.add(maze);
    }
}
