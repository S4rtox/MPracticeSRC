package me.s4rtox.mmhunt.handlers.gamehandlers.arena.states;

public enum GameState {
    STARTING("&e&lStarting"),
    WAITING("&a&lWaiting"),
    ACTIVE("&c&lOngoing"),
    FINISHING("&7&lFinishing"),
    RESTARTING("&8&lRestarting"),
    SETUP("&9&lSetting Up");

    private final String displayedName;

    GameState(String displayedName){
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }

}
