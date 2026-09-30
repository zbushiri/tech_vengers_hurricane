package com.techvengershurricane.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techvengershurricane.model.Identifiable;

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
import java.util.Optional;

public class JsonCrudRepository<T extends Identifiable> implements CrudRepository<T> {
    private final Path filePath;
    private final Class<T[]> arrayType;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonCrudRepository(Path filePath, Class<T[]> arrayType) {
        this.filePath = filePath;
        this.arrayType = arrayType;
    }

    @Override
    public synchronized List<T> getAll() {
        /* JSON: loads the saved list. */
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            T[] items = gson.fromJson(reader, arrayType);
            return items == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(items));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + filePath, exception);
        }
    }

    @Override
    public synchronized Optional<T> findById(String id) {
        return getAll().stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    @Override
    public synchronized void add(T item) {
        /* JSON: prevents duplicate IDs. */
        List<T> items = getAll();
        if (items.stream().anyMatch(saved -> saved.getId().equals(item.getId()))) {
            throw new IllegalArgumentException("ID already exists: " + item.getId());
        }
        items.add(item);
        saveAll(items);
    }

    @Override
    public synchronized boolean edit(T item) {
        List<T> items = getAll();
        for (int index = 0; index < items.size(); index++) {
            if (items.get(index).getId().equals(item.getId())) {
                items.set(index, item);
                saveAll(items);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean delete(String id) {
        List<T> items = getAll();
        boolean removed = items.removeIf(item -> item.getId().equals(id));
        if (removed) {
            saveAll(items);
        }
        return removed;
    }

    private void saveAll(List<T> items) {
        /* JSON: writes a temp file before replacing the real file. */
        Path parent = filePath.toAbsolutePath().getParent();
        Path temporary = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try {
            Files.createDirectories(parent);
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                gson.toJson(items, writer);
            }
            try {
                Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveNotSupported) {
                Files.move(temporary, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save " + filePath, exception);
        }
    }
}
