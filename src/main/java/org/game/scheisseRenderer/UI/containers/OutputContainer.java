package org.game.scheisseRenderer.UI.containers;

public class OutputContainer<T extends OutputTypes> {
    private T data;
    private final OutputConverter<T> converter;

    public OutputContainer(OutputConverter<T> converter) {
        this.converter = converter;
    }

    public void set_data_from_string(String value) {
        data = converter.from_string(value);
    }

    public void set_data(T data) { this.data = data; }
    public T get_data() { return data; }
}