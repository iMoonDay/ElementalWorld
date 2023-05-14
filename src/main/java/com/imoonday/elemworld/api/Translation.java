package com.imoonday.elemworld.api;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Translation<T> {

    public static final String DEFAULT = "en_us";
    private final Map<String, String> translations = new HashMap<>();
    private final T instance;

    public Translation(T instance, @Nullable String defaultContent) {
        this.instance = instance;
        if (defaultContent != null) {
            this.add(DEFAULT, defaultContent);
        }
    }

    public T getInstance() {
        return instance;
    }

    @Nullable
    public String getContent(String languageCode) {
        return translations.get(languageCode);
    }

    public String getContent() {
        return getContent(DEFAULT);
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

    public Iterator<Map.Entry<String, String>> iterator() {
        return ImmutableMap.copyOf(translations).entrySet().iterator();
    }

    public Set<String> getLanguageCodes() {
        return ImmutableMap.copyOf(translations).keySet();
    }
}
