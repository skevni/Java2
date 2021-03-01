package ru.geekbrains.sklyarov.javacore2.lesson3;

import java.util.*;

public class MainApp {
    public static void main(String[] args) {

        /*
        1.
         */
        List<String> words = new ArrayList<>(Arrays.asList("One", "Two", "One", "Four", "Five", "Three", "Six"
                , "Three", "Seven", "Eight", "Three", "Three"));
        Map<String, Integer> uniqueWord = new HashMap<>();
//        Iterator<String> wordsIterator = words.listIterator();
//        while (wordsIterator.hasNext()){
//            String strWord = wordsIterator.next();
//            int wordCount = uniqueWord.get(strWord) == null ? 0 : uniqueWord.get(strWord);
//                uniqueWord.put(strWord,++wordCount);
//        }
        for (String word :
                words) {
            int wordCount = uniqueWord.getOrDefault(word,0);
            uniqueWord.put(word, ++wordCount);
        }
        // Вывод уникальных слов
        System.out.println(uniqueWord.keySet());
        // Количество слов в массиве
        System.out.println(uniqueWord);

        /*
        2. Добавил проверку на пустые номера. Думаю не сильно нагромоздил код.
            Если имени нет, а номер есть, вместо имени укажем номер телефона.
         */

        PhoneBook phoneBook = new PhoneBook("Ivanov", "89005667877");
        phoneBook.add("Ivanov", "88002000001");
        phoneBook.add("Ivanov", "88002000002");
        phoneBook.add("Petrov", new HashSet<>(Arrays.asList("970", "971", "972", "973")));
        phoneBook.add("Petrov", "980");
        phoneBook.add("Sidorov", "900");
//        phoneBook.add("1", new HashSet<String>());
//        phoneBook.add("2", "");
        phoneBook.add("", "88002000099");
        phoneBook.add("", new HashSet<>(Arrays.asList("1000", "1001", "1002")));


        System.out.println("Petrov's phone number: " + phoneBook.get("Petrov"));
        System.out.println(phoneBook.get("Sidorov2"));
        System.out.println(phoneBook);


        PhoneBook phoneBook2 = new PhoneBook("Sikorskiy", "999999");
//
        System.out.println(phoneBook2);
        System.out.println(phoneBook2.get("Sikorskiy"));
    }
}
