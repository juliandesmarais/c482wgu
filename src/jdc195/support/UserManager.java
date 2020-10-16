package jdc195.support;

import jdc195.model.User;

public class UserManager {

  private static UserManager instance;
  private static User currentUser;

  private UserManager() {}

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

  public void setUser(User user) {
    currentUser = user;
  }

  public User getUser() {
    return currentUser;
  }
}
