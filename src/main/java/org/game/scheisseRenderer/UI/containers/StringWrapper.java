package org.game.scheisseRenderer.UI.containers;

public final class StringWrapper implements OutputTypes {
    private String value;

    public StringWrapper(String value){
        this.value = value;
    }

    public String get_value() {
        return value;
    }

    public static StringWrapper from_string(String value) {
        return new StringWrapper(value);
    }
}
