package com.dziewit.marek.you2mp3.video_info;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;


public class QueryStringParser {
    private static class NameValuePair {
        final String key;
        final String value;

        NameValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static final String DEFAUL_CHARSET = "UTF-8";

    private Charset charset;
    private List<NameValuePair> query = new ArrayList<NameValuePair>();


    public QueryStringParser(String queryString) throws NullPointerException {
        this(queryString, DEFAUL_CHARSET);
    }


    public QueryStringParser(String queryString, String charsetName) throws NullPointerException, IllegalCharsetNameException, IllegalArgumentException, UnsupportedCharsetException {
        if (queryString == null) {
            throw new NullPointerException("Query String is Null.");
        }

        this.charset = Charset.forName(charsetName);
        parse(queryString);
    }

    // This method is used to parse the query string
    private void parse(String queryString) {
        for (String pair : queryString.split("&")) {
            int idxOfEqual = pair.indexOf("=");

            if (idxOfEqual < 0) {
                addElement(pair, "");
            } else {
                String key = pair.substring(0, idxOfEqual);
                String value = pair.substring(idxOfEqual + 1);
                addElement(key, value);
            }
        }
    }

    // This method adds the given key and value into the List query.
    // Before adding it decodes the key and value with the given charset.
    public void addElement(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key or Value is Null");
        }

        try {
            String charsetName = charset.name();
            query.add(new NameValuePair(URLDecoder.decode(key, charsetName), URLDecoder.decode(value, charsetName)));
        } catch (Exception ignore) {
        }
    }


    public String getParameterValue(String key) {
        for (NameValuePair pair : query) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }

        return null;
    }


    public List<String> getParameterValues(String key) {
        List<String> list = new ArrayList<String>();

        for (NameValuePair pair : query) {
            if (pair.key.equals(key)) {
                list.add(pair.value);
            }
        }

        return list;
    }
}