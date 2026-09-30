package com.techvengershurricane.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/* DATA: all JSON reading and writing belongs in this class. */
public class JsonDataAccess<T> {
    private final Path file;
    private final Class<T[]> arrayType;
    private final Function<T, String> idReader;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonDataAccess(Path file, Class<T[]> arrayType, Function<T, String> idReader) {
        this.file = file;
        this.arrayType = arrayType;
        this.idReader = idReader;
    }

    public List<T> getAll() {
        /* LOAD: gets the saved list from one JSON file. */
        if (Files.notExists(file)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            T[] items = gson.fromJson(reader, arrayType);
            return items == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(items));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + file, exception);
        }
    }

    public void add(T item) {
        /* ADD: place validation in the facade before calling this. */
        List<T> items = getAll();
        if (items.stream().anyMatch(saved -> idReader.apply(saved).equals(idReader.apply(item)))) {
            throw new IllegalArgumentException("ID already exists: " + idReader.apply(item));
        }
        items.add(item);
        save(items);
    }

    public boolean edit(T replacement) {
        /* EDIT: replaces the item that has the same ID. */
        List<T> items = getAll();
        for (int index = 0; index < items.size(); index++) {
            if (idReader.apply(items.get(index)).equals(idReader.apply(replacement))) {
                items.set(index, replacement);
                save(items);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String id) {
        /* DELETE: removes the item that has this ID. */
        List<T> items = getAll();
        boolean removed = items.removeIf(item -> idReader.apply(item).equals(id));
        if (removed) {
            save(items);
        }
        return removed;
    }

    private void save(List<T> items) {
        /* SAVE: writes safely through a temporary file. */
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                gson.toJson(items, writer);
            }
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save " + file, exception);
        }
    }
}
