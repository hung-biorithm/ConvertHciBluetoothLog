package com.hung.hci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class CompareData {
    public static void main(String[] args) throws IOException {
        File applicationDataFile = new File("F:\\Biorithm\\Repository\\dumpstate-xiaomi-2021-11-24-10-24-40\\AndroidRawBytes.txt");
        File systemDataFile = new File("F:\\Biorithm\\Repository\\dumpstate-xiaomi-2021-11-24-10-24-40\\HciRawBytes.txt");
        formatFile(applicationDataFile, new File(applicationDataFile.getParent(), "FormatAndroidRawBytes.txt"));
        formatFile(systemDataFile, new File(systemDataFile.getParent(), "FormatHciBytes.txt"));
    }

    private static void formatFile(File srcFile, File dstFile) throws IOException {
        String text = ConvertBluetoothHciLog.readFile(srcFile);
        StringTokenizer tokenizer = new StringTokenizer(text);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dstFile));
        String lineSeparator = System.lineSeparator();
        StringBuilder stringBuilder = new StringBuilder();
        while (tokenizer.hasMoreElements()) {
            stringBuilder.append(tokenizer.nextToken());
            stringBuilder.append(lineSeparator);
            bufferedWriter.write(stringBuilder.toString());
            stringBuilder.setLength(0);
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
