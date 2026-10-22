package model;

/** Starts the terminal app and shelter map. */
public class UI {
    public static void main(String[] args) {
        Thread terminal = new Thread(() -> {
            HurricaneReliefApp app = new HurricaneReliefApp();
            app.initialize();
            app.run();
        });

        terminal.start();
        MapUI.main(args);
    }
}
