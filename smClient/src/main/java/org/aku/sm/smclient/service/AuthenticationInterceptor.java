package org.aku.sm.smclient.service;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import org.aku.sm.smclient.C;
import org.aku.sm.smclient.R;
import org.aku.sm.smclient.common.Global;
import org.aku.sm.smclient.settings.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import retrofit.RequestInterceptor;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;


/**
 * Handle basic authentication with retrofit
 * Setup a OkHttpClient to handle SSL
 *
 * Required in http-header
 * Authorization Basic base64(<username>:<password>)
 * Required in http-header
 * Cookie:JSESSIONID=9B79B09D43CA99E466081C518249ABA4
 */
public class AuthenticationInterceptor extends OkClient implements RequestInterceptor {

    private final String username;
    private final String password;
    private final String cloudUrl;


    private AuthenticationInterceptor(OkHttpClient okHttpClient,
                                     String username, String password, String cloudUrl) {
        super(okHttpClient);
        this.username = username;
        this.password = password;
        this.cloudUrl = cloudUrl;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    @Override
    public void intercept(RequestFacade request) {
        if (Global.getJSessionId() == null) {
            request.addHeader("Authorization", buildBasicAuthenticationString());
        } else {
            request.addHeader("Cookie:", Global.getJSessionId());
        }
    }


    /**
     * Retrieve JSESSIONID of Header
     * Set-Cookie:JSESSIONID=9B79B09D43CA99E466081C518249ABA4; Path=/; HttpOnly
     */
    @Override
    public Response execute(Request request) throws IOException
    {
        Response response = super.execute(request);
        for (Header header : response.getHeaders()) {
            if (header.getName() != null && header.getName().equals("Set-Cookie")) {
                // JSESSIONID=62F2CD38763C98C93C070A72147A9623; Path=/; HttpOnly
                String value = header.getValue();
                //jSessionId = value.substring(0, value.indexOf(';'));
                Global.setJSessionId(value.substring(0, value.indexOf(';')));
            }
        }

        return response;
    }


    /**
     * Returns the String
     */
    private String buildBasicAuthenticationString() {
        final String userAndPassword = username + ":" + password;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }

    /**
     * Certificate manager trusting all certificates, not required self signed certificates seems to
     * be ok.
     * @return
     */
    private static TrustManager[] trustAllCertTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
                        return;
                    }
                    @Override
                    public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
                        return;
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                }
        };
        return trustAllCerts;
    }

    /**
     * Trust manager using self signed certificate located ad R.raw.smtruststore (./raw/smtruststore)
     * @param context
     * @return
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private static TrustManager[] buildCertTrustManagers(Context context) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore trusted = KeyStore.getInstance("BKS");
        InputStream in = context.getResources().openRawResource(R.raw.smtruststore);
        trusted.load(in, null); // trust store without password
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trusted);
        return trustManagerFactory.getTrustManagers();
    }

    /**
     * Connect to https://aba.home:8443 works with the trust managers from 'buildCertTrustManagers(...)'
     * Connect to https://syma.cfapps.io/ doesn't work with this trust manager because the  trust store
     * with verfied certificates is not available.
     *
     * @param context Android application context
     * @return SSL socket factory using ./raw/smtruststore trust store
     */
    private static SSLSocketFactory createSslSocketFactory(Context context) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, buildCertTrustManagers(context) , new java.security.SecureRandom());
            // sslContext.init(null, trustAllCertTrustManager(), new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (Exception e) {
            Log.e(C.TAG, e.getMessage(), e);
        }
        return null;
    }


    /**
     * Create a AuthenticationInterceptor retrieving username, password and URL from the settings.
     *
     * @param activity Android activity
     * @return AuthenticationInterceptor for retrofit
     * @see #create(android.app.Activity, String, String, String)
     */
    public static AuthenticationInterceptor create(Activity activity) {
        return create(activity,
                Settings.getUsername(activity),
                Settings.getPassword(activity),
                Settings.getCloudUrl(activity));
    }


    /**
     * Create a AuthenticationInterceptor where the host name verification is disabled.
     * TOTO Try to create SAN certificate with openSSL, didn't find a way to create
     * them with Java keytool.

     * @return AuthenticationInterceptor for retrofit
     */
    public static AuthenticationInterceptor create(Activity activity,
            String username, String password, String cloudUrl) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setSslSocketFactory(createSslSocketFactory(activity));
        okHttpClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        return new AuthenticationInterceptor(okHttpClient, username, password, cloudUrl);
    }

}

