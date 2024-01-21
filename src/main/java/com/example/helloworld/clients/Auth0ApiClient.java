package com.example.helloworld.clients;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class Auth0ApiClient {

    @Value("${okta.oauth2.issuer}")
    private String oktaIssuer;

    @Value("${okta.oauth2.audience}")
    private String oktaAudience;

    @Value("${okta.other.client-id}")
    private String oktaClientId;

    @Value("${okta.other.client-secret}")
    private String oktaClientSecret;

    @Value("${okta.other.domain}")
    private String oktaDomain;

    @Getter
    private ManagementAPI client;

    @PostConstruct
    public void initialize() throws Auth0Exception {
        String token = getManagementApiToken();
        this.client = ManagementAPI.newBuilder(oktaDomain, token).build();
    }

    String getManagementApiToken() throws Auth0Exception {
        AuthAPI authAPI = AuthAPI.newBuilder(oktaIssuer, oktaClientId, oktaClientSecret).build();
        TokenRequest tokenRequest = authAPI.requestToken("https://" + oktaDomain + "/api/v2/");
        TokenHolder holder = tokenRequest.execute().getBody();
        return holder.getAccessToken();
    }
}
