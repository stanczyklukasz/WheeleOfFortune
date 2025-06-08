public enum PlayerAction {
    EXIT("[EXIT]"),
    JOIN("[JOIN]"),
    START("[START]"),
    SURRENDER("[SURRENDER]"),
    SCOREBOARD("[SCOREBOARD]"),
    OTHER("other");

    private final String value;

    PlayerAction(String value) {
        this.value = value;
    }

    public static PlayerAction tryFrom(String value) {
        for (PlayerAction action : PlayerAction.values()) {
            if (action.value.equalsIgnoreCase(value)) {
                return action;
            }
        }

        return PlayerAction.OTHER;
    }

    public boolean isExit() {
        return this.value.equalsIgnoreCase(PlayerAction.EXIT.value);
    }

    public boolean isJoin() {
        return this.value.equalsIgnoreCase(PlayerAction.JOIN.value);
    }

    public boolean isOther() {
        return this.value.equalsIgnoreCase(PlayerAction.OTHER.value);
    }

    public boolean isStart() {
        return this.value.equalsIgnoreCase(PlayerAction.START.value);
    }

    public boolean isSurrender() {
        return this.value.equalsIgnoreCase(PlayerAction.SURRENDER.value);
    }
    public boolean isScoreboard() {
        return this.value.equalsIgnoreCase(PlayerAction.SCOREBOARD.value);
    }
}