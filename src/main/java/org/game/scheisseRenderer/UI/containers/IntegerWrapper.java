package org.game.scheisseRenderer.UI.containers;

public final class IntegerWrapper implements OutputTypes{
    private int value;

    public IntegerWrapper(int value){
        this.value = value;
    }

    public int get_value() {
        return value;
    }

    public static IntegerWrapper from_string(String value) {
        return new IntegerWrapper(Integer.parseInt(value));
    }
}
