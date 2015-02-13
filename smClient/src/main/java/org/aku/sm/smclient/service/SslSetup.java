package org.aku.sm.smclient.service;


import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.aku.sm.smclient.R;

import java.io.InputStream;
import java.io.Reader;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


/**
 * http://www.akadia.com/services/ssh_test_certificate.html
 http://stackoverflow.com/questions/10175812/how-to-build-a-self-signed-certificate-with-openssl
 https://devcenter.heroku.com/articles/ssl-certificate-self
 http://www.tomcatexpert.com/knowledge-base/using-openssl-configure-ssl-certificates-tomcat

 http://apetec.com/support/GenerateSAN-CSR.htm
 https://rtcamp.com/wordpress-nginx/tutorials/ssl/multidomain-ssl-subject-alternative-names/
 *
 *
 * Created by armin on 18.11.14.
 */
public class SslSetup {

//    private static final String ENDPOINT = "https://api.yourdomain.com/";
    public static String TRUST_STORE_PASSWORD = "your_secret";
    Context context;

    public SslSetup(Context c) {
        this.context = c;
    }

    private static SSLSocketFactory getPinnedCertSslSocketFactory(Context context) {
        try {
            KeyStore trusted = KeyStore.getInstance("jks");
            InputStream in = context.getResources().openRawResource(R.raw.smtruststore);
            trusted.load(in, null);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trusted);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            Log.e("sm", e.getMessage(), e);
        }
        return null;
    }


    public static OkHttpClient createClient(Context context) {

        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(getPinnedCertSslSocketFactory(context));
        return client;

    }
}