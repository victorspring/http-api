package ru.yandex.practicum.http.storage;


import java.util.List;
import java.util.Optional;

public interface Storage<T> {

    T create(T user);

    void remove(long id);

    Optional<T> findById(Long id);

    List<T> findAll();

    T update(long id, T user);

    void removeAll();
}

