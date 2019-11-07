package com.liufeng.security;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

@Component("ClientDetailServiceImpl")
@Primary
public class ClientDetailServiceImpl implements ClientDetailsService {

    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String PASSWORD = "password";
    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String SCOPES = "all";





    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
//        Response<App> response = appClient.findByClientID(clientId);
//
//        if (!response.getCode().equals(200))
//            return null;
//
//        App app = response.getData();
        BaseClientDetails baseClientDetails = new BaseClientDetails();
        baseClientDetails.setClientId("client");
        baseClientDetails.setClientSecret("{noop}" + "secret");
        baseClientDetails.setScope(Lists.newArrayList(SCOPES));
        baseClientDetails.setAuthorizedGrantTypes(Lists.newArrayList(AUTHORIZATION_CODE, PASSWORD, CLIENT_CREDENTIALS, REFRESH_TOKEN));
        baseClientDetails.setAccessTokenValiditySeconds(1);
        baseClientDetails.setRefreshTokenValiditySeconds(1);
        return baseClientDetails;
    }

}
