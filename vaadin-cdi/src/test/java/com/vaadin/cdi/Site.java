package com.vaadin.cdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Site {
    private static int TIMEOUT_IN_MS = 2000;

    public static String getContent(String uri) {
        try {
            return getContent(new URL(uri));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot fetch content for uri: "
                    + uri + " reason " + e);
        }

    }

    public static String getContent(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(TIMEOUT_IN_MS);
        urlConnection.setReadTimeout(TIMEOUT_IN_MS);
        InputStream stream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder content = new StringBuilder("");
        while ((line = br.readLine()) != null) {
            content.append(line);
        }
        br.close();
        return content.toString();
    }
}
