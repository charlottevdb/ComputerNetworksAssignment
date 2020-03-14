package com.company;
import HttpPackage.*;

import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args2) throws IOException {
        Client client = new Client();
        //String[] args = client.readArguments();
        String[] args = new String[] {"GET", "http://www.google.com/", "80", "en"};
        client.setArguments(args);

        String path = new URL(args[1]).getPath();
        client.connectSocket();
        client.sendRequest("GET", path, null);
    }
}
