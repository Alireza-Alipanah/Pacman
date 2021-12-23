package controller;

import javafx.scene.layout.GridPane;
import model.User;

import java.io.IOException;
import java.util.ArrayList;

public class UserController {

    private static final SaveController SAVE_CONTROLLER;
    private static UserController userController;

    static {
        SAVE_CONTROLLER = SaveController.getInstance();
    }

    private UserController() {
    }

    public static UserController getInstance() {
        if (userController == null)
            userController = new UserController();
        return userController;
    }

    public boolean usernameAlreadyExists(String username) {
        ArrayList<User> allUsers = User.getAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username))
                return true;
        }
        return false;
    }

    public void registerUser(String username, String password) {
        new User(username, password);
    }

    public boolean passwordIsRight(String username, String password) {
        ArrayList<User> allUsers = User.getAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username))
                return user.getPassword().equals(password);
        }
        return false;
    }

    public User getUserByUsername(String username) {
        ArrayList<User> allUsers = User.getAllUsers();
        for (User user : allUsers) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    public void removeUser(User user) throws IOException {
        SAVE_CONTROLLER.deleteThisUser(user);
        ArrayList<User> allUsers = User.getAllUsers();
        allUsers.remove(user);
        User.setLoggedIn(null);
    }

    public ArrayList<User> getTenBestUsers() {
        ArrayList<User> allUsers = new ArrayList<>(User.getAllUsers());
        ArrayList<User> allUsersSorted = new ArrayList<>();
        int place = 1;
        User candidate;
        while (allUsers.size() > 0) {
            candidate = allUsers.get(0);
            if (allUsers.size() > 1) {
                for (int i = 1; i < allUsers.size(); i++) {
                    if (allUsers.get(i).getScore() > candidate.getScore())
                        candidate = allUsers.get(i);
                    else if (allUsers.get(i).getScore() == candidate.getScore()) {
                        candidate = allUsers.get(i).getLastScoreTimeWithPrecision()
                                .compareTo(candidate.getLastScoreTimeWithPrecision()) > 0 ?
                                candidate : allUsers.get(i);
                    }
                }
            }
            if (allUsersSorted.size() > 0) {
                if (allUsersSorted.get(allUsersSorted.size() - 1).getScore() == candidate.getScore())
                    place = allUsersSorted.get(allUsersSorted.size() - 1).getRank();
                else place = allUsersSorted.size() + 1;
            }
            allUsersSorted.add(candidate);
            candidate.setRank(place);
            allUsers.remove(candidate);
        }
        ArrayList<User> usersToSend = new ArrayList<>();
        for (User user : allUsersSorted) {
            if (user.getRank() < 11)
                usersToSend.add(user);
        }
        return usersToSend;
    }

    public void logout() {
        User loggedIn = User.getLoggedIn();
        if (loggedIn != null) {
            if (loggedIn.getUsername().isEmpty())
                User.getAllUsers().remove(loggedIn);
            User.setLoggedIn(null);
        }
    }

    public void saveMap(int[][] maze) {
        User.getLoggedIn().addNewMaze(maze);
    }

    public boolean userHaveAlreadySavedThisMap(int[][] maze) {
        ArrayList<int[][]> mazes = User.getLoggedIn().getMazes();
        for (int[][] savedMaze : mazes) {
            if (savedMaze == maze)
                return true;
            for (int i = 0; i < savedMaze.length; i++) {
                for (int j = 0; j < savedMaze.length; j++) {
                    if (maze[i][j] != savedMaze[i][j])
                        return false;
                }
            }
        }
        return !mazes.isEmpty();
    }

    public boolean loggedInUserHasMap() {
        return !User.getLoggedIn().getMazes().isEmpty();
    }

    public int[][] getNextUserMap(int[][] currentMap) {
        ArrayList<int[][]> allMaps = User.getLoggedIn().getMazes();
        if (currentMap == null)
            return allMaps.get(0);
        else {
            for (int i = 0; i < allMaps.size(); i++) {
                if (allMaps.get(i) == currentMap)
                    return i < allMaps.size() - 1 ? allMaps.get(i + 1) : allMaps.get(0);
            }
        }
        return null;
    }

    public boolean userHasSavedGame() {
        return User.getLoggedIn().getCurrentGame() != null;
    }

    public GridPane getSavedUserGame() {
        return User.getLoggedIn().getCurrentGame().getBoard();
    }

    public void removeDefaultMaps(){
        ArrayList<User> allUsers = User.getAllUsers();
        ArrayList<int[][]> defaultMaps = User.getDefaultMap();
        ArrayList<int[][]> maps;
        for (User user : allUsers) {
            maps = user.getMazes();
            maps.removeAll(defaultMaps);
        }
    }
}
