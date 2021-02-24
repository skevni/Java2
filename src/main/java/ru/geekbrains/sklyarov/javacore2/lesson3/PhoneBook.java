package ru.geekbrains.sklyarov.javacore2.lesson3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PhoneBook {
    private final HashMap<String, ArrayList<String>> phoneBook;

    public PhoneBook(String name, String phone) {
        phoneBook = new HashMap<>();
        phoneBook.put(name, new ArrayList<>(Collections.singletonList(phone)));
    }

    public void add(String name, String phone) {
        if (phone.equals("")) {
            return;
        }
        if (name.equals("")) {
            name = phone;
        }
        addPhone(name, new ArrayList<>(Collections.singletonList(phone)));
    }

    public void add(String name, ArrayList<String> phone) {
        if (!phone.isEmpty()) {
            if (name.equals("")) {
                for (String strPhone : phone) {
                    if (!strPhone.equals("")) {
                        name = strPhone;
                    }
                    this.addPhone(name, new ArrayList<>(Collections.singletonList(strPhone)));
                }
            } else {
                this.addPhone(name, phone);
            }
        }
    }

    private void addPhone(String name, ArrayList<String> phone) {
        if (this.phoneBook.containsKey(name)) {
            ArrayList<String> newPhone = this.phoneBook.get(name);
            newPhone.addAll(phone);
            this.phoneBook.put(name, newPhone);
        } else {
            this.phoneBook.put(name, phone);
        }
    }

    public ArrayList<String> get(String name) {
        if (this.phoneBook.containsKey(name)) {
            return this.phoneBook.get(name);
        } else {
            return (ArrayList<String>) Collections.singletonList("");
        }
    }

    @Override
    public String toString() {
        return phoneBook.toString();
    }
}
