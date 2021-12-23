package controller;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MazeController {

    private static MazeController mazeController;

    private MazeController() {
    }

    public static MazeController getInstance() {
        if (mazeController == null)
            mazeController = new MazeController();
        return mazeController;
    }

    public GridPane makeMazeGrid(int[][] maze) {
        GridPane pane = new GridPane();
        for (int i = 0; i < 51; i++) {
            for (int j = 0; j < 51; j++) {
                if (maze[i][j] == 2 || maze[i][j] == 4) {
                    visualizePlacesThatYouCanStandOn(maze, pane, i, j);
                } else if (maze[i][j] != 3) {
                    visualizeWalls(maze, pane, i, j);
                } else {
                    visualizeWallConnectors(maze, pane, i, j);
                }
            }
        }
        for (Node child : pane.getChildren()) {
            GridPane.setHalignment(child, HPos.CENTER);
            GridPane.setValignment(child, VPos.CENTER);
        }
        return pane;
    }

    private void visualizeWallConnectors(int[][] maze, GridPane pane, int i, int j) {
        Rectangle rectangle = new Rectangle(1, 1, 4, 4);
        if (showWallConnector(maze, i, j))
            rectangle.setFill(Color.rgb(0, 13, 43));
        else rectangle.setFill(Color.rgb(16, 53, 104));
        pane.add(rectangle, j, i);
    }

    private void visualizeWalls(int[][] maze, GridPane pane, int i, int j) {
        Rectangle rectangle;
        if (putHorizontally(maze, i, j)) {
            rectangle = new Rectangle(1, 1, 20, 4);
        } else {
            rectangle = new Rectangle(1, 1, 4, 20);
        }
        if (maze[i][j] == 1)
            rectangle.setFill(Color.rgb(0, 13, 43));
        else rectangle.setFill(Color.rgb(16, 53, 104));
        pane.add(rectangle, j, i);
    }

    private void visualizePlacesThatYouCanStandOn(int[][] maze, GridPane pane, int i, int j) {
        Rectangle rectangle = new Rectangle(1, 1, 20, 20);
        rectangle.setFill(Color.rgb(16, 53, 104));
        pane.add(rectangle, j, i);
        if (!((i == 25 && j == 25) || ((i == 1 || i == 49) && (j == 1 || j == 49))))
            if (maze[i][j] == 4) {
                Circle circle = new Circle();
                circle.setRadius(5);
                circle.setFill(Color.rgb(163, 191, 15));
                pane.add(circle, j, i);
            } else {
                Rectangle rectangle1 = new Rectangle(1, 1, 4, 4);
                rectangle1.setFill(Color.rgb(193, 186, 37));
                pane.add(rectangle1, j, i);
            }
    }

    private boolean putHorizontally(int[][] maze, int i, int j) {
        if (i == 0 || i == 50)
            return true;
        if (j == 0 || j == 50)
            return false;
        return maze[i - 1][j] == 2 || maze[i + 1][j] == 2 || maze[i - 1][j] == 4 || maze[i + 1][j] == 4;
    }

    public int[][] makeANewMaze() {
        int m, n = m = 51;
        return makeEmptyMaze(m, n);
    }

    private int[][] makeEmptyMaze(int m, int n) {
        int[][] maze = new int[m][n];
        boolean[][] whetherAWallConnectorIsEmptyOrNot = new boolean[(m + 3) / 2][(n + 3) / 2];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                    maze[i][j] = 1;                 // "1" represents wall
                    if ((i & 0b1) == 0 && (j & 0b1) == 0) whetherAWallConnectorIsEmptyOrNot[i / 2][j / 2] = true;
                } else {
                    if ((j & 0b1) == 1 && (i & 0b1) == 1) maze[i][j] = 2;  // "2" represents tiles that you can stand on
                    else {
                        if ((j & 0b1) == 0 && (i & 0b1) == 0) {
                            maze[i][j] = 3;   // "3" represents places that walls could attach to (referred to as wallConnecters in here)
                            whetherAWallConnectorIsEmptyOrNot[i / 2][j / 2] = false;
                        } else maze[i][j] = 0; // "0" represents empty wall locations
                    }
                }
            }
        }
        placeWalls(maze, m, n, whetherAWallConnectorIsEmptyOrNot);
        for (int i = 0; i < 51; i += 2) {
            maze[0][i] = 3;
            maze[50][i] = 3;
            maze[i][0] = 3;
            maze[i][50] = 3;
        }
        return maze;
    }

    private void placeWalls(int[][] maze, int m, int n, boolean[][] whetherAWallConnectorIsEmptyOrNot) {
        Random randomNumber = new Random();
        ArrayList<Integer> emptyWallConnectors = new ArrayList<>();
        for (int i = 1; i < m - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (maze[i][j] == 3 && !whetherAWallConnectorIsEmptyOrNot[i / 2][j / 2]) {
                    emptyWallConnectors.add(i);
                    emptyWallConnectors.add(j);
                }
            }
        }
        ArrayList<Integer> possibleWallLocations = new ArrayList<>();
        while (emptyWallConnectors.size() > 0) {
            attachWallToWallConnector(maze, whetherAWallConnectorIsEmptyOrNot, randomNumber, emptyWallConnectors,
                    possibleWallLocations);
        }
        removeSomeRandomWalls(maze, randomNumber);
        addEdibles(maze, randomNumber);
    }

    private void attachWallToWallConnector(int[][] maze, boolean[][] whetherAWallConnectorIsEmptyOrNot,
                                           Random randomNumber, ArrayList<Integer> emptyWallConnectors,
                                           ArrayList<Integer> possibleWallLocations) {
        int i, x, y;
        i = randomNumber.nextInt(emptyWallConnectors.size());
        if ((i & 0b1) == 1) i--;
        x = emptyWallConnectors.get(i);
        y = emptyWallConnectors.get(i + 1);
        if (whetherAWallConnectorIsEmptyOrNot[x / 2 - 1][y / 2]) {
            possibleWallLocations.add(x - 1);
            possibleWallLocations.add(y);
        }
        if (whetherAWallConnectorIsEmptyOrNot[x / 2 + 1][y / 2]) {
            possibleWallLocations.add(x + 1);
            possibleWallLocations.add(y);
        }
        if (whetherAWallConnectorIsEmptyOrNot[x / 2][y / 2 - 1]) {
            possibleWallLocations.add(x);
            possibleWallLocations.add(y - 1);
        }
        if (whetherAWallConnectorIsEmptyOrNot[x / 2][y / 2 + 1]) {
            possibleWallLocations.add(x);
            possibleWallLocations.add(y + 1);
        }
        if (possibleWallLocations.size() > 0) {
            int j;
            j = randomNumber.nextInt(possibleWallLocations.size());
            if ((j & 0b1) == 1) j--;
            maze[possibleWallLocations.get(j)][possibleWallLocations.get(j + 1)] = 1;
            whetherAWallConnectorIsEmptyOrNot[x / 2][y / 2] = true;
            emptyWallConnectors.remove(i);
            emptyWallConnectors.remove(i);
            possibleWallLocations.clear();
        }
    }

    private void removeSomeRandomWalls(int[][] maze, Random random) {
        int i, j;
        for (int count = 350; count > 0; count--) {
            do {
                i = random.nextInt(51);
                j = random.nextInt(51);
            }
            while (i == 0 || i == 50 || j == 0 || j == 50 || maze[i][j] != 1);
            maze[i][j] = 0;
        }
    }

    private void addEdibles(int[][] maze, Random random) {
        int i, j;
        for (int count = 4; count > 0; count--) {
            do {
                i = random.nextInt(51);
                j = random.nextInt(51);
            }
            while (maze[i][j] != 2 || (i == 25 && j == 25) || ((i == 1 || i == 49) && (j == 1 || j == 49)));
            maze[i][j] = 4;
        }
    }

    private boolean showWallConnector(int[][] maze, int i, int j) {
        try {
            if (maze[i + 1][j] == 1)
                return true;
        } catch (Exception ignored) {
        }
        try {
            if (maze[i - 1][j] == 1)
                return true;
        } catch (Exception ignored) {
        }
        try {
            if (maze[i][j + 1] == 1)
                return true;
        } catch (Exception ignored) {
        }
        try {
            if (maze[i][j - 1] == 1)
                return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public void removeVisitedFromPane(GridPane pane, boolean[][] visited) {
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[i].length; j++) {
                if (visited[i][j]) {
                    Rectangle rectangle = new Rectangle(1, 1, 20, 20);
                    rectangle.setFill(Color.rgb(16, 53, 104));
                    pane.add(rectangle, j, i);
                }
            }
        }
    }
}
