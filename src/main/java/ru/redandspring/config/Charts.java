package ru.redandspring.config;

import java.util.List;

public class Charts {

    private final List<String> values;

    public Charts(final List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
}
