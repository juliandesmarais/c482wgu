package jdc195.support;

import jdc195.model.User;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UserManager {

  private static UserManager instance;
  private static User currentUser;
  private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private UserManager() {
    LOGGER.setLevel(Level.INFO);
    try {
      FileHandler fileHandler = new FileHandler("UserLog.txt", true);
      fileHandler.setFormatter(new SimpleFormatter());
      LOGGER.addHandler(fileHandler);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, String.format("Failed to initialize file handler for logger. Error: %s", e.getLocalizedMessage()));
      e.printStackTrace();
    }
  }

  public static synchronized UserManager getInstance() {
    if (instance == null) {
      synchronized (jdc195.support.UserManager.class) {
        if (instance == null) {
          instance = new UserManager();
        }
      }
    }

    return instance;
  }

  public void resetUser() {
    LOGGER.log(Level.INFO, String.format("User: %s logged out at: %s", currentUser.getUserName(), DateUtility.getCurrentZonedDateTimeInUTC()));
    currentUser = null;
  }

  public void setUser(User user) {
    LOGGER.log(Level.INFO, String.format("User: %s logged in at: %s", user.getUserName(), DateUtility.getCurrentZonedDateTimeInUTC()));
    currentUser = user;
  }

  public User getUser() {
    return currentUser;
  }
}
