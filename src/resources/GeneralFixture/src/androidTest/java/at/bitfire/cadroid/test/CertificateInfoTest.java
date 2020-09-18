package at.bitfire.cadroid.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;
import at.bitfire.cadroid.CertificateInfo;
import at.bitfire.cadroid.ConnectionInfo;
import lombok.Cleanup;

public class CertificateInfoTest extends InstrumentationTestCase {
    private AssetManager assetManager;
    private CertificateFactory certificateFactory;

    CertificateInfo
            infoDebianTestCA,
            infoDebianTestNoCA,
            infoGTECyberTrust,
            infoMehlMX;

    protected void setUp() throws Exception {
        assetManager = getInstrumentation().getContext().getAssets();
        certificateFactory = CertificateFactory.getInstance("X.509");

        infoDebianTestCA = loadCertificateInfo("DebianTestCA.pem");
        infoDebianTestNoCA = loadCertificateInfo("DebianTestNoCA.pem");
        infoGTECyberTrust = loadCertificateInfo("GTECyberTrustGlobalRoot.pem");

        // user-submitted test cases
        infoMehlMX = loadCertificateInfo("mehl.mx.pem");
    }

    protected CertificateInfo loadCertificateInfo(String assetFileName) throws IOException, CertificateException {
        @Cleanup InputStream is = assetManager.open(assetFileName);
        X509Certificate rootGTECyberTrust = (X509Certificate)certificateFactory.generateCertificate(is);
        return new CertificateInfo(rootGTECyberTrust);
    }


    public void testIsCA() {
        assertTrue(infoDebianTestCA.isCA());
        assertFalse(infoDebianTestNoCA.isCA());
        assertNull(infoGTECyberTrust.isCA());

        assertFalse(infoMehlMX.isCA());
    }


    public void testPreinstalledCertificate() throws Exception {
        ConnectionInfo result = ConnectionInfo.fetch(new URL("https://sni.velox.ch/"));
        assertEquals("sni.velox.ch", result.getHostName());
        assertTrue(result.isHostNameMatching());
        assertTrue(result.isTrusted());
    }

    public void testSelfSignedUntrustedCertificate() throws Exception {
        ConnectionInfo result = ConnectionInfo.fetch(new URL("https://www.pcwebshop.co.uk/"));
        assertEquals("www.pcwebshop.co.uk", result.getHostName());
        assertFalse(result.isHostNameMatching());
        assertFalse(result.isTrusted());
    }


}
