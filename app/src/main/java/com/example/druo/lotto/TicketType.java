package com.example.druo.lotto;

public enum TicketType {
    FIVE, SIX, SKANDINAV;

    public String getType() {
        String type = null;

        switch (this) {
            case FIVE:
                type = "Ötöslottó";
                break;
            case SIX:
                type = "Hatoslottó";
                break;
            case SKANDINAV:
                type = "Skandináv lottó";
                break;
        }
        return type;
    }
}
