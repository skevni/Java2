package ru.geekbrains.sklyarov.network.chat.client;

import java.io.*;

public class Logger {
    private final File historyFile;

    public Logger(String username) throws IOException {
        String filePath = String.format("logs/history_%s.txt", username);
        historyFile = new File(filePath);
        if (!historyFile.exists()) {
            if (!historyFile.createNewFile()) {
                throw new FileNotFoundException("Unable to create file: " + filePath);
            }
        }
    }

    public void writeToFile(String message) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile, true))) {
            writer.write(message);
        }
    }

    public String readFromFile() throws IOException {
        StringBuilder result = new StringBuilder(5000);

        // Этот способ на 10_000_000 строках работает 7985 мс или 7678 мс
/*
        long time = System.currentTimeMillis();
        List<String> result2 = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(historyFile))) {
            result2 = br.lines().collect(Collectors.toList());
        }
        for (int i = result2.size() - 100; i < result2.size(); i++) {
            result.append(result2.get(i)).append("\n");
        }
        System.out.println(System.currentTimeMillis() - time);

*/

        // Этот способ на 10_000_000 строках работает 2220 мс

        long linesCount;
        try (BufferedReader br = new BufferedReader(new FileReader(historyFile))) {
            linesCount = br.lines().count();
        }
/*  По скорости этот блок и блок ниже отрабатывают почти одинаково
        try (BufferedReader br = new BufferedReader(new FileReader(historyFile))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (linesCount <= 100) {
                    result.append(strLine).append("\n");
                }
                linesCount--;
            }
        }
 */
        try (LineNumberReader lnr = new LineNumberReader(new FileReader(historyFile))) {
//            lnr.setLineNumber(3);
            String strLine;
            while ((strLine = lnr.readLine()) != null) {
                if (linesCount - 100 < lnr.getLineNumber()) {
                    result.append(strLine).append("\n");
                }
            }
        }

        result.trimToSize();
        return result.toString();
    }

//    private void fillHistory() throws IOException{
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(historyFile))) {
//            for (int i = 0; i < 10_000_000; i++) {
//                bw.write("User: message #" + i + "\n");
//            }
//        }
//    }

}
