package zcla71.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.Setter;

public class RestCall {
    private String url;
    private Map<String, String> params;
    @Setter
    private Object data;

    public RestCall(String url) {
        this.url = url;
        this.params = new HashMap<>();
    }

    private void addDataToMapper(ObjectMapper objectMapper, HttpURLConnection con) throws IOException {
        if (this.data != null) {
            String jsonInputString = objectMapper.writeValueAsString(this.data);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
    }

    public void addParam(String param, String value) {
        params.put(param, value);
    }

    private URL buildCompleteUrl() throws MalformedURLException, URISyntaxException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        String separator = "?";
        for (String keyString : params.keySet()) {
            sb.append(separator);
            sb.append(encodeParam(keyString));
            sb.append("=");
            sb.append(encodeParam(params.get(keyString)));
            separator = "&";
        }
        return (new URI(sb.toString())).toURL();
    }

    private String encodeParam(String str) {
        return UriUtils.encode(str, "UTF-8");
    }

    private HttpURLConnection getConnection(String method) throws MalformedURLException, IOException, URISyntaxException {
        HttpURLConnection result = (HttpURLConnection) buildCompleteUrl().openConnection();
        result.setRequestMethod(method);
        result.setDoOutput(true);
        return result;
    }

    private <T> T readBuffer(Class<T> classe, ObjectMapper objectMapper, HttpURLConnection con) throws IOException {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            T result = classe.cast(objectMapper.readValue(bufferedReader, classe));
            return result;
        }
    }

    // JSON

    private <T> T doRequestJson(String method, Class<T> classe) throws URISyntaxException, IOException {
        HttpURLConnection con = getConnection(method);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        addDataToMapper(objectMapper, con);
        return readBuffer(classe, objectMapper, con);
    }

    public <T> T getJson(Class<T> classe) throws MalformedURLException, IOException, URISyntaxException {
        return doRequestJson("GET", classe);
    }

    public <T> T postJson(Class<T> classe) throws URISyntaxException, IOException {
        return doRequestJson("POST", classe);
    }

    // XML

    private <T> T doRequestXml(String method, Class<T> classe) throws URISyntaxException, IOException {
        HttpURLConnection con = getConnection(method);
        con.setRequestProperty("Content-Type", "application/xml");
        con.setRequestProperty("Accept", "application/xml");
        XmlMapper xmlMapper = new XmlMapper();
        addDataToMapper(xmlMapper, con);
        return readBuffer(classe, xmlMapper, con);
    }

    public <T> T getXml(Class<T> classe) throws URISyntaxException, IOException {
        return doRequestXml("GET", classe);
    }
}
