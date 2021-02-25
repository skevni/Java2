package ru.geekbrains.sklyarov.javacore2.lesson3;

import java.util.*;

public class PhoneBook {
    private final HashMap<String, Set<String>> phoneBook;

    public PhoneBook(String name, String phone) {
        phoneBook = new HashMap<>();
        phoneBook.put((name.equals("") ? phone : name), new HashSet<>(Collections.singletonList(phone)));
    }

    public void add(String name, String phone) {
        addPhone(name, new HashSet<>(Collections.singleton(phone)));
    }

    public void add(String name, HashSet<String> phone) {
        addPhone(name, phone);
    }

    private void addPhone(String name, HashSet<String> phone) {
        if (phone.isEmpty() || phone.contains("")) {
            throw new IllegalArgumentException("phone must not be empty or must not have string of length 0");
        }
        if (name.equals("")) {
            for (String ph :
                    phone) {
                if (!phoneBook.containsKey(ph)) {
                    phoneBook.put(ph, new HashSet<>());
                }
                phoneBook.get(ph).add(ph);
            }
        } else {
            if (!phoneBook.containsKey(name)) {
                phoneBook.put(name, new HashSet<>());
            }
            phoneBook.get(name).addAll(phone);
        }
    }

    public Set<String> get(String name) {
        return this.phoneBook.getOrDefault(name, Collections.emptySet());
    }

    @Override
    public String toString() {
        return phoneBook.toString();
    }
}
