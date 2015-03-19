package com.opentok.util;

import com.google.appengine.api.urlfetch.*;
import com.opentok.constants.Version;
import com.opentok.exception.OpenTokException;
import com.opentok.exception.RequestException;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

public class HttpClient implements URLFetchService {

    private URLFetchService urlFetchService;
    private final String apiUrl;
    private final int apiKey;
    private final String apiSecret;

    public HttpClient(int apiKey, String apiSecret, String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.apiSecret = apiSecret;
        urlFetchService = URLFetchServiceFactory.getURLFetchService();
    }

    public String createSession(Map<String, Collection<String>> params) throws RequestException {
        String responseString = null;
        HTTPResponse response = null;
        FluentStringsMap paramsString = new FluentStringsMap().addAll(params);

        try {
            response = fetch(new HTTPRequest(new URL(this.apiUrl + "/session/create?" + paramsString), HTTPMethod.POST));
        } catch (IOException e) {
            throw new RequestException("Could not create an OpenTok Session", e);
        }

        switch (response.getResponseCode()) {
            case 200:
                responseString = new String(response.getContent());
                break;
            default:
                throw new RequestException("Could not create an OpenTok Session. The server response was invalid." +
                        " response code: " + response.getResponseCode());
        }
        return responseString;
    }

    public String getArchive(String archiveId) throws RequestException {
        // TODO(ramatevish) stub
        throw new RequestException("Not implemented");
    }

    public String getArchives(int offset, int count) throws RequestException {
        // TODO(ramatevish) stub
        throw new RequestException("Not implemented");
    }

    public String startArchive(String sessionId, String name) throws OpenTokException, RequestException {
        // TODO(ramatevish) stub
        throw new RequestException("Not implemented");
    }

    public String stopArchive(String archiveId) throws RequestException {
        // TODO(ramatevish) stub
        throw new RequestException("Not implemented");
    }

    public String deleteArchive(String archiveId) throws RequestException {
        // TODO(ramatevish) stub
        throw new RequestException("Not implemented");
    }

    @Override
    public HTTPResponse fetch(URL url) throws IOException {
        HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.GET);
        return fetch(httpRequest);
    }

    @Override
    public HTTPResponse fetch(HTTPRequest httpRequest) throws IOException {
        httpRequest.setHeader(new HTTPHeader("User-Agent", "Opentok-Java-SDK/" + Version.VERSION));
        httpRequest.setHeader(new HTTPHeader("X-TB-PARTNER-AUTH", this.apiKey + ":" + this.apiSecret));

        return urlFetchService.fetch(httpRequest);
    }

    @Override
    public Future<HTTPResponse> fetchAsync(URL url) {
        return null;
    }

    @Override
    public Future<HTTPResponse> fetchAsync(HTTPRequest httpRequest) {
        return null;
    }
}
