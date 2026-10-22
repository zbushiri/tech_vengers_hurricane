package model;

/**
 * Starts the terminal app. The menus live in HurricaneReliefApp, so this
 * only loads the data and runs them.
 */
public class UI {
    public static void main(String[] args) {
        HurricaneReliefApp app = new HurricaneReliefApp();
        app.initialize();
        app.run();
    }
}
