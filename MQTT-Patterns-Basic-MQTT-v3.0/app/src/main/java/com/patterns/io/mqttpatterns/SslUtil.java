package com.patterns.io.mqttpatterns;

import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/*
This class is extracted from a question in Stack overflow, modified to fit the needs of this implementation
http://stackoverflow.com/questions/12997559/ssl-connection-from-java-client-eclipse-paho-to-mosquitto-broker-unknown-ca
https://gist.github.com/sharonbn/4104301
https://gist.github.com/rohanag12/07ab7eb22556244e9698

The last link is only useful, but this implementation is not taken from there.

Also notice that BouncyCastle Libraries are working properly in standard Java, but incomplete for Android,
instead we use SpongyCastle, which is meant to be the full version for this platform.
*/
public class SslUtil
{

    static SSLSocketFactory getSocketFactory ( final String password, final String brokerString, final String brokerPort, final InputStream caFileStream,
                                                final InputStream clientCertStream,final InputStream clientPrivateStream) throws Exception
    {


        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);

        Security.addProvider(new BouncyCastleProvider());

        int caSize              = caFileStream.available();

        byte[] caCertBytes          = new byte[caSize];

        try {
            BufferedInputStream caBuf               = new BufferedInputStream(caFileStream);


            caBuf            .read(caCertBytes,        0, caCertBytes       .length);

            caBuf            .close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Load CA certificate
        PEMParser reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(caCertBytes  )));
        X509Certificate caCert = new JcaX509CertificateConverter().setProvider( "BC" )
                .getCertificate((X509CertificateHolder) reader.readObject());
        reader.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null,null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        //TODO: Current implementation does not use KeyManagerFactories, and only implements a
        //TODO: CA File, still to implement the rest of the files, they seem to work, but apparently,
        //TODO: there is an problem with Android versions not enabling TLSv1.2 properly
        //TODO: http://stackoverflow.com/questions/24357863/making-sslengine-use-tlsv1-2-on-android-4-4-2
        // one of many occurrences on the web.
        //context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        context.init(null, tmf.getTrustManagers(), null);

        int port = Integer.valueOf(brokerPort);
        SSLSocket sslSocket = (SSLSocket)context.getSocketFactory().createSocket(brokerString, port);
        sslSocket.setEnabledProtocols(new String[] {"TLSv1.2"} );

        return context.getSocketFactory();
    }
}
