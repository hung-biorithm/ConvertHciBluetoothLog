package com.hung.hci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class ConvertBluetoothHciLog {
    private static final String FIELD_SOURCE = "_source";
    private static final String FIELD_LAYERS = "layers";
    private static final String FIELD_BTSPP = "btspp";
    private static final String FIELD_ACL = "bthci_acl";
    private static final String FIELD_ACL_SOURCE = "bthci_acl.src.name";
    private static final String FIELD_BTSPP_DATA = "btspp.data";
    private static final String BIO_RADIO_PREFIX = "BioRadio";

    public static void main(String[] args) throws IOException, JSONException {
        File inputFile = new File("F:\\Biorithm\\Repository\\dumpstate-xiaomi-2021-11-24-10-24-40\\hci_snoop_pretty.json");
        File outputFile = new File(inputFile.getParent(), "HciRawBytes.txt");
        String jsonText = readFile(inputFile);
        JSONArray allPackets = new JSONArray(jsonText);
        List<JSONObject> sppPackets = filterBySppProtocol(allPackets);
        writeSppDataToFile(sppPackets, outputFile);
    }

    private static void writeSppDataToFile(List<JSONObject> sppPackets, File outputFile) throws IOException, JSONException {
        OutputStream outputStream = new FileOutputStream(outputFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        for (JSONObject object : sppPackets) {
            JSONObject source = object.getJSONObject(FIELD_SOURCE);
            JSONObject layers = source.getJSONObject(FIELD_LAYERS);
            JSONObject spp = layers.getJSONObject(FIELD_BTSPP);
            String sppData = spp.getString(FIELD_BTSPP_DATA);
            String writeData = sppData.toUpperCase().replaceAll(":", " ") + "\n";
            bufferedOutputStream.write(writeData.getBytes());
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    public static List<JSONObject> filterBySppProtocol(JSONArray packets) throws JSONException {
        List<JSONObject> sppPackets = new LinkedList<>();
        int length = packets.length();
        for (int i = 0; i < length; i++) {
            JSONObject object = packets.getJSONObject(i);
            if (isProtocolSpp(object) && isReceiveData(object)) {
                sppPackets.add(object);
            }
        }
        return sppPackets;
    }

    public static boolean isProtocolSpp(JSONObject packet) throws JSONException {
        JSONObject source = packet.getJSONObject(FIELD_SOURCE);
        JSONObject layers = source.getJSONObject(FIELD_LAYERS);
        if (!layers.has(FIELD_BTSPP)) return false;
        JSONObject spp = layers.getJSONObject(FIELD_BTSPP);
        if (spp == null) return false;

        return spp.has(FIELD_BTSPP_DATA);
    }

    public static boolean isReceiveData(JSONObject packet) throws JSONException {
        JSONObject source = packet.getJSONObject(FIELD_SOURCE);
        JSONObject layers = source.getJSONObject(FIELD_LAYERS);
        JSONObject acl = layers.getJSONObject(FIELD_ACL);
        String aclSource = acl.getString(FIELD_ACL_SOURCE);
        return aclSource.startsWith(BIO_RADIO_PREFIX);
    }

    public static String readFile(File file) {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            String lineSeparator = System.lineSeparator();

            while (line != null) {
                sb.append(line);
                sb.append(lineSeparator);
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
