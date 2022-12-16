import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class RequestUtilities {

    String baseUrl;

    public RequestUtilities(String url) {
        baseUrl = url;
    }

    public HttpResponse getRequest(String uri) throws IOException {
        String fullUri = baseUrl + uri;
        System.out.println(fullUri);
        HttpUriRequest request = new HttpGet(baseUrl + uri);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public HttpResponse postRequest(String uri, String body) throws IOException {
        String fullUri = baseUrl + uri;
        System.out.println(fullUri);
        System.out.println(body);
        HttpPost request = new HttpPost(baseUrl + uri);
        HttpEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public HttpResponse deleteRequest(String uri) throws IOException {
        String fullUri = baseUrl + uri;
        System.out.println(fullUri);
        HttpUriRequest request = new HttpDelete(baseUrl + uri);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public HttpResponse putRequest(String uri, String body) throws IOException {
        String fullUri = baseUrl + uri;
        System.out.println(fullUri);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut request = new HttpPut(baseUrl + uri);
        HttpEntity stringEntity = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);

        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        return response;
    }

    public HttpResponse getInvestigations() throws IOException {
        HttpResponse response = getRequest("investigations/");
        return response;
    }

    public HttpResponse getInvestigation(String id) throws IOException {
        HttpResponse response = getRequest("investigations/" + id);
        return response;
    }

    public HttpResponse createInvestigation(String name, String amount) throws IOException {
        Gson gson = new Gson();
        Investigation inv = new Investigation();
        inv.name = name;
        inv.amount = amount;
        HttpResponse response = postRequest("investigations/", gson.toJson(inv));
        return response;
    }

    public HttpResponse deleteInvestigation(String id) throws IOException {
        HttpResponse response = deleteRequest("investigations/" + id);
        return response;
    }

    public HttpResponse updateInvestigation(String id, String name, String amount) throws IOException {
        Gson gson = new Gson();
        Investigation inv = new Investigation();
        inv.name = name;
        inv.amount = amount;
        HttpResponse response = putRequest("investigations/" + id, gson.toJson(inv));
        return response;
    }
}
