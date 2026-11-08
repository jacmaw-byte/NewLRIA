package org.game.scheisseRenderer.UI.containers;

public interface OutputConverter<T extends OutputTypes> {
    T from_string(String value);
}
