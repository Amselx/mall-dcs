package com.luban.config;

import com.luban.component.MallTokenEnhancer;
import com.luban.properties.JwtCAProperties;
import com.luban.mall.service.MallUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(value = JwtCAProperties.class)
public class MallAuthServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MallUserDetailService JwtUserDetailService;

    @Autowired
    private JwtCAProperties jwtCAProperties;



    @Bean
    public TokenStore tokenStore(){

        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //jwt?????????
        converter.setKeyPair(keyPair());

        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(jwtCAProperties.getKeyPairName()), jwtCAProperties.getKeyPairStoreSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtCAProperties.getKeyPairAlias(), jwtCAProperties.getKeyPairSecret().toCharArray());
    }


    @Bean
    public MallTokenEnhancer JwtTokenEnhancer() {
        return new MallTokenEnhancer();
    }


    /**
     * ??????????????????
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }

    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        //?????????????????????????????????
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(JwtTokenEnhancer(),jwtAccessTokenConverter()));

        endpoints.tokenStore(tokenStore()) //????????????????????????token ???????????????
                .tokenEnhancer(tokenEnhancerChain)
                .userDetailsService(JwtUserDetailService) //???????????????token??????????????? ??????????????????
                .authenticationManager(authenticationManager);
    }


    /**
     * ??????????????????:???????????????????????????
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //????????????????????????token???????????? clientId ???clientSecret?????????
        security .checkTokenAccess("isAuthenticated()")
                 .tokenKeyAccess("isAuthenticated()");//??????????????????tokenKey????????????clientId,clientSecret

        security.allowFormAuthenticationForClients();
    }

}
