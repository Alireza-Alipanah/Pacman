package controller;

import javafx.scene.layout.GridPane;
import model.Game;
import model.Ghost;
import model.GhostColor;
import model.User;
import view.Direction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveController {

    private static SaveController saveController;

    private final MazeController MAZE_CONTROLLER;
    private final UserController USER_CONTROLLER;

    Pattern savesPattern = Pattern.compile("(?<username>[^\n]+)\\s(?<password>[^\n]+)\\s(?<score>\\d+)\\s" +
            "(?<lastScoreTime>[^\\s]+)\\s(?<game>null|[^+]+)(?<mazes>[+\\s\\d]+)?");
    Pattern gamePattern = Pattern.compile("(?<playBeginning>[tf])\\s(?<numberOfMapEntitiesEaten>\\d+)\\s" +
            "(?<running>[tf])\\s(?<healthRemaining>\\d+)\\s(?<eatingGhostMultiplier>\\d+)\\s(?<score>\\d+)" +
            "\\s(?<hardDifficulty>[tf])\\s(?<ghostsArePaused>[tf])\\s\\*\\*\\*(?<ghosts>[\\s\\w\\d.-]+)\\*\\*\\*" +
            "(?<pacmanXPosition>\\d+)\\s(?<pacmanYPosition>\\d+)\\s(?<maze>(?:\\d+\\s)+)");
    Pattern mazeRow = Pattern.compile("(\\d)");
    Pattern extractMazePattern = Pattern.compile("\\+\\+\\+\\s([\\d\\s]+)");
    Pattern ghostsPattern = Pattern.compile("(?<color>[^\\s]+)\\s(?<currentColor>[^\\s)]+)\\s(?<remainingFrightened>\\d+)" +
            "\\s(?<direction>[^\\s]+)\\s(?<xPosition>\\d+)\\s(?<yPosition>\\d+)\\s");

    private SaveController() {
        MAZE_CONTROLLER = MazeController.getInstance();
        USER_CONTROLLER = UserController.getInstance();
    }

    public static SaveController getInstance() {
        if (saveController == null)
            saveController = new SaveController();
        return saveController;
    }

    public void save() throws IOException {
        USER_CONTROLLER.removeDefaultMaps();
        FileWriter save;
        StringBuilder saveText;
        for (User user : User.getAllUsers()) {
            if (user.getUsername().equals(""))
                continue;
            save = new FileWriter("saves/" + user.getUsername() + ".txt");
            saveText = new StringBuilder();
            saveText.append(user.getUsername()).append("\n");
            saveText.append(user.getPassword()).append("\n");
            saveText.append(user.getScore()).append("\n");
            saveText.append(user.getLastScoreTimeWithPrecision()).append("\n");
            Game game = user.getCurrentGame();
            if (game != null) {
                saveGAme(saveText, game);
            } else saveText.append("null");
            ArrayList<int[][]> mazes = user.getMazes();
            for (int[][] maze : mazes) {
                saveText.append("+++").append("\n");
                for (int[] row : maze) {
                    for (int i : row) {
                        saveText.append(i);
                    }
                    saveText.append("\n");
                }
            }
            save.write(saveText.toString());
            save.close();
        }
    }

    private void saveGAme(StringBuilder saveText, Game game) {
        saveText.append(game.getPlayBeginning() ? "t" : "f").append("\n");
        saveText.append(game.getNumberOfMapEntitiesEaten()).append("\n");
        saveText.append(game.getRunning() ? "t" : "f").append("\n");
        saveText.append(game.getHealthRemaining()).append("\n");
        saveText.append(game.getEatingGhostMultiplier()).append("\n");
        saveText.append(game.getScore()).append("\n");
        saveText.append(game.getHardDifficulty() ? "t" : "f").append("\n");
        saveText.append(game.getGhostsArePaused() ? "t" : "f").append("\n");
        int[] pacmanPosition = game.getPacmanPosition();
        boolean[][] visited = game.getVisited();
        int[][] maze = game.getMaze();
        saveGhosts(saveText, game);
        saveText.append(pacmanPosition[0]).append("\n");
        saveText.append(pacmanPosition[1]).append("\n");
        saveArrays(saveText, visited, maze);
    }

    private void saveArrays(StringBuilder saveText, boolean[][] visited, int[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (visited[i][j])
                    saveText.append(maze[i][j] == 2 ? 5 : 6);
                else saveText.append(maze[i][j]);
            }
            saveText.append("\n");
        }
    }

    private void saveGhosts(StringBuilder saveText, Game game) {
        Ghost[] ghosts = game.getGhosts();
        saveText.append("***");
        for (Ghost ghost : ghosts) {
            saveText.append(ghost.getGhostColor().getValue()).append("\n");
            saveText.append(ghost.getCurrentColor().getValue()).append("\n");
            saveText.append(ghost.getTurnsRemainingAsFrightened()).append("\n");
            saveText.append(ghost.getPreviousDirection()).append("\n");
            int[] position = ghost.getPosition();
            saveText.append(position[0]).append("\n");
            saveText.append(position[1]).append("\n");
        }
        saveText.append("***");
    }

    public void load() throws IOException {
        File directory = new File("saves");
        File[] saves = directory.listFiles();
        boolean loadedDefaultMaps = false;
        if (saves != null)
            for (int i = 0; i < saves.length; i++) {
                if (!saves[i].isFile())
                    continue;
                String saveString = new String(Files.readAllBytes(Path.of(saves[i].getPath())));
                if (!loadedDefaultMaps) {
                    if (saves[i].getName().equals("defaultMaps.txt")) {
                        addDefaultMaps(saveString);
                        i = -1;
                        loadedDefaultMaps = true;
                    }
                } else if (!saves[i].getName().equals("defaultMaps.txt")) {
                    Matcher matcher = savesPattern.matcher(saveString);
                    if (matcher.find()) {
                        loadUser(matcher);
                    }
                }
            }
    }

    private void loadUser(Matcher matcher) {
        User user = new User(matcher.group("username"), matcher.group("password"));
        user.setScore(Integer.parseInt(matcher.group("score")));
        user.setLastScoreTime(LocalDateTime.parse(matcher.group("lastScoreTime")));
        Game game = null;
        if (!matcher.group("game").equals("null")) {
            Matcher matcher1 = gamePattern.matcher(matcher.group("game"));
            if (matcher1.find()) {
                game = loadGame(matcher1);
            }
        }
        user.setCurrentGame(game);
        Matcher matcher1 = extractMazePattern.matcher(matcher.group("mazes") != null ? matcher.group("mazes") : "");
        while (matcher1.find()) {
            user.addNewMaze(makeMaze(matcher1.group(1)));
        }
    }

    private Game loadGame(Matcher matcher1) {
        Game game;
        int[][] maze = makeMaze(matcher1.group("maze"));
        boolean[][] visited = new boolean[51][51];
        loadArrays(maze, visited);
        GridPane pane = MAZE_CONTROLLER.makeMazeGrid(maze);
        MAZE_CONTROLLER.removeVisitedFromPane(pane, visited);
        Ghost[] ghosts = loadGhosts(matcher1);
        game = new Game(pane, maze, ghosts,
                matcher1.group("hardDifficulty").equals("t"),
                Integer.parseInt(matcher1.group("healthRemaining")));
        game.setVisited(visited);
        game.setRunning(matcher1.group("running").equals("t"));
        game.setEatingGhostMultiplier(Integer.parseInt(matcher1.group("eatingGhostMultiplier")));
        game.setScore(Integer.parseInt(matcher1.group("score")));
        game.setGhostsArePaused(matcher1.group("ghostsArePaused").equals("t"));
        game.setNumberOfMapEntitiesEaten(Integer.parseInt(matcher1.group("numberOfMapEntitiesEaten")));
        game.setPlayBeginning(matcher1.group("playBeginning").equals("t"));
        int[] pacmanPosition = game.getPacmanPosition();
        pacmanPosition[0] = Integer.parseInt(matcher1.group("pacmanXPosition"));
        pacmanPosition[1] = Integer.parseInt(matcher1.group("pacmanYPosition"));
        int[] pacmanStaticPosition = game.getPacmanStaticPosition();
        pacmanStaticPosition[0] = pacmanPosition[0];
        pacmanStaticPosition[1] = pacmanPosition[1];
        return game;
    }

    private void loadArrays(int[][] maze, boolean[][] visited) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                if (maze[i][j] == 5 || maze[i][j] == 6) {
                    visited[i][j] = true;
                    maze[i][j] = maze[i][j] == 5 ? 2 : 4;
                } else visited[i][j] = false;
            }
        }
    }

    private Ghost[] loadGhosts(Matcher matcher1) {
        Ghost[] ghosts = new Ghost[4];
        Matcher matcher3 = ghostsPattern.matcher(matcher1.group("ghosts"));
        for (int i = 0; i < 4; i++) {
            if (matcher3.find()) {
                ghosts[i] = new Ghost(Integer.parseInt(matcher3.group("xPosition")),
                        Integer.parseInt(matcher3.group("yPosition")),
                        GhostColor.getEnumByValue(matcher3.group("color")));
                ghosts[i].setCurrentColor(
                        GhostColor.getEnumByValue(matcher3.group("currentColor")));
                ghosts[i].changeGhostColor(
                        GhostColor.getEnumByValue(matcher3.group("currentColor")));
                ghosts[i].setPreviousDirection(
                        Direction.getDirectionByString(matcher3.group("direction")));
                ghosts[i].setTurnsRemainingAsFrightened(
                        Integer.parseInt(matcher3.group("remainingFrightened")));
            }
        }
        return ghosts;
    }

    private int[][] makeMaze(String mazeString) {
        int[][] maze = new int[51][51];
        Matcher matcher = mazeRow.matcher(mazeString);
        for (int i, j = i = 0; matcher.find(); i++) {
            if (i == 51) {
                i = 0;
                j++;
            }
            maze[j][i] = Integer.parseInt(matcher.group(1));
        }
        return maze;
    }

    public void deleteThisUser(User user) throws IOException {
        File file = new File("saves/" + user.getUsername() + ".txt");
        if (file.exists())
            Files.delete(Paths.get(file.toURI()));
    }

    private void addDefaultMaps(String text) {
        Matcher matcher = extractMazePattern.matcher(text);
        ArrayList<int[][]> defaultMaps = User.getDefaultMap();
        while (matcher.find()) {
            defaultMaps.add(makeMaze(matcher.group(0)));
        }
    }

}
