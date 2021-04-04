package com.squid.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * Hello world!
 */
public class App {

    private static String PROXY_HOST = "54.254.200.94";
    private static int PROXY_PORT = 3129;
    private static String TARGET_HOST = "54.254.200.94";
    private static int TARGET_PORT = 3002;
    private static String KEYSTORE = "./newkeystore.jks";

    public static void main(String[] args) throws IOException {
        PoolingHttpClientConnectionManager pool = null;

        try {
            pool = getPoolingHttpClientConnectionManager("https");
        } catch (Exception e) {
            e.printStackTrace();
        }
        pool.setMaxTotal(Integer.parseInt("100"));
        pool.setDefaultMaxPerRoute(Integer.parseInt("100"));

        RequestConfig params = RequestConfig.custom().build();
        HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(pool)
                .setDefaultRequestConfig(params);

        HttpHost proxyhost = new HttpHost(PROXY_HOST, PROXY_PORT, "https");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyhost);
        clientBuilder = clientBuilder.setRoutePlanner(routePlanner);

        CloseableHttpClient httpclient = clientBuilder.build();
        HttpHost targethost = new HttpHost(TARGET_HOST, TARGET_PORT, "https");

        //Creating an HttpGet object
        HttpGet httpget = new HttpGet("/user/test");

        //Executing the Get request
        HttpResponse httpresponse = httpclient.execute(targethost, httpget);

        //Printing the status line
        System.out.println(httpresponse.getStatusLine());

        //Printing all the headers of the response
        Header[] headers = httpresponse.getAllHeaders();

        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }

        //Printing the body of the response
        HttpEntity entity = httpresponse.getEntity();

        if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
        }
    }

    private static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(String protocol) {

        PoolingHttpClientConnectionManager poolManager;
        if ("https".equals(protocol)) {
            SSLConnectionSocketFactory socketFactory = createSocketFactory();
            org.apache.http.config.Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", socketFactory).build();
            poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            poolManager = new PoolingHttpClientConnectionManager();
        }
        return poolManager;
    }

    private static SSLConnectionSocketFactory createSocketFactory() {
        SSLContext sslContext;
        String path = KEYSTORE;
        String keyStorePath = path;
        String keyStorePassword = "password";

        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            sslContext = SSLContexts.custom().loadTrustMaterial(trustStore).build();

            X509HostnameVerifier hostnameVerifier;
            String hostnameVerifierOption = "AllowAll";

            if ("AllowAll".equalsIgnoreCase(hostnameVerifierOption)) {
                hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            } else if ("Strict".equalsIgnoreCase(hostnameVerifierOption)) {
                hostnameVerifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
            } else {
                hostnameVerifier = SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
            }

            return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }
}
