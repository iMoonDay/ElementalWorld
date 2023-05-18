package com.imoonday.elemworld.api;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Translation<T> {

    public static final String DEFAULT = "en_us";
    private final Map<String, String> translations = new HashMap<>();
    private final T instance;

    public Translation(T instance) {
        this.instance = instance;
    }

    public Translation(T instance, @NotNull String defaultContent) {
        this(instance);
        this.add(DEFAULT, defaultContent);
    }

    public Translation(T instance, @NotNull String languageCode, @NotNull String content) {
        this(instance);
        this.add(languageCode, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Translation<?> that)) return false;
        return Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance);
    }

    public T getInstance() {
        return instance;
    }

    @Nullable
    public String get(String languageCode) {
        return translations.get(languageCode);
    }

    public String get() {
        return get(DEFAULT);
    }

    public void add(String languageCode, String content) {
        if (languageCode != null) {
            if (content != null) {
                translations.put(languageCode, content);
            } else {
                translations.remove(languageCode);
            }
        }
    }

    public int size() {
        return translations.size();
    }

    public Iterator<Map.Entry<String, String>> iterator() {
        return ImmutableMap.copyOf(translations).entrySet().iterator();
    }

    public Set<String> getLanguageCodes() {
        return ImmutableMap.copyOf(translations).keySet();
    }
}
