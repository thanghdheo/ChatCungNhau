package Client;

import Client.GUI.Login;
import Client.GUI.Main;
import Client.GUI.Start;

public class Form {

    public static Login login;
    public static Start start;
    public static Main main;

    public static void showLogin() {
        if (login == null) {
            login = new Login();
        }
        login.setVisible(true);
    }

    public static void hideLogin() {
        if (login != null) {
            login.setVisible(false);
        }
    }

    public static void showMain() {
        if (main == null) {
            main = new Main();
        }
        main.setVisible(true);
    }

    public static void newMain() {
        main = new Main();
        main.setVisible(true);
    }

    public static void hideMain() {
        if (main != null) {
            main.setVisible(false);
        }
    }

    public static void showStart() {
        if (start == null) {
            start = new Start();
        }
        start.setVisible(true);
    }

    public static void hideStart() {
        if (start != null) {
            start.setVisible(false);
        }
    }

    public static void newStart() {
        start = new Start();
        start.setVisible(true);
    }

}
