package ru.yandex.practicum.http.storage;

import ru.yandex.practicum.http.model.User;

import java.util.*;

public class UserStorage implements Storage<User> {

    private long currentId = 0;

    private final List<User> userList = new ArrayList<>();

    public User create(User user) {
        user.setId(++currentId);
        userList.add(user);

        return user;
    }

    public void remove(long id) {
        userList.removeIf(current -> Objects.equals(current.getId(), id));
    }

    public Optional<User> findById(Long id) {
        List<User> result = userList.stream()
                .filter(u -> Objects.equals(u.getId(), id))
                .toList();

        if (!result.isEmpty()) {
            return Optional.of(result.get(0));
        } else {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        return Collections.unmodifiableList(userList);
    }

    public User update(long id, User user) {
        User existingUser = findById(id).orElseThrow();
        existingUser.setName(user.getName());

        return existingUser;
    }

    @Override
    public void removeAll() {
        userList.clear();
        currentId = 0;
    }

}
