/*
 * Copyright (c) 2014 Xiao Bao Clark
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package com.actisec.clipcaster.parser;

import android.util.Log;

import com.actisec.clipcaster.AbstractJavaScriptTestCase;
import com.actisec.clipcaster.ScrapedCredentials;
import com.actisec.clipcaster.ScrapedData;
import com.actisec.clipcaster.ScrapedDataHandler;
import com.actisec.clipcaster.Source;
import com.actisec.clipcaster.util.JavaScript;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LastPassParserTest extends AbstractJavaScriptTestCase {

    private static final String FULL_SAMPLE_v1 = "script:(function(){var l_fs, l_bf=null, l_err=false;var l_bni=0, l_bnp=0;var l_bte=null, l_bpe=null, l_cpe=null;var l_w; var l_d; try { l_w=window.top; l_d=l_w.document;} catch (l_e){ l_w=window; l_d=document;}var l_iv=function(el, sf){ while (el&&(!sf||el.tagName != 'FORM')){ if (el.hasOwnProperty('style')&&(el.style['display']=='none'||el.style['visibility']=='hidden')) return false; else el=el.parentNode;} return true;};var l_cpp=/(?:existing|old|curr).*pass/i;for(var l_k=-1; l_k < l_w.frames.length; l_k++){ if(l_k==-1){ l_fs=l_d.getElementsByTagName('form');}else{ try{ l_w[l_k].document.domain } catch(e){console.log(e); l_err=true; continue;} l_fs=l_w[l_k].document.getElementsByTagName('form');} for (var l_i=0; l_i < l_fs.length; l_i++){ if (!l_iv(l_fs[l_i])) continue; var l_fe=l_fs[l_i].elements; var l_ni=0, l_np=0; var l_te=null, l_pe=null; for (var l_j=0; l_j < l_fe.length; l_j++){ var l_e=l_fe[l_j]; if ((l_e.type=='text'||l_e.type=='email'||l_e.type=='tel')&&l_iv(l_e, true)){ if (l_ni==0){ l_te=l_e;} l_ni++;} if (l_e.type=='password'&&l_iv(l_e, true)){ if (l_np==0){ l_pe=l_e;} l_np++; if (l_cpp.test(l_e.name)||l_cpp.test(l_e.id)){ l_cpe=l_e;} } } if (l_np==1){ if (!l_bf||(l_ni==1&&l_bni != 1)){ l_bni=l_ni; l_bnp=l_np; l_bf=l_fs[l_i]; l_bte=l_te; l_bpe=l_pe;} } else if (l_np > 1&&l_cpe){ l_bf=l_fs[l_i]; l_bpe=l_cpe;} }}var l_sfv=function(el, v){ try { var c=true; if (el.type=='select-one'&&el.value==v){ c=false;} el.value=v; if (c){ var evt=el.ownerDocument.createEvent('Events'); evt.initEvent('change', true, true); el.dispatchEvent(evt); evt=el.ownerDocument.createEvent('Events'); evt.initEvent('input', true, true); el.dispatchEvent(evt);} } catch(e){}};if (l_bf){ var do_fill=true; if (do_fill){ console.log('fill login form=' + (l_bf.id||l_bf.name)); if (l_bte){ l_sfv(l_bte, decodeURIComponent(escape(atob('dXNlckBleGFtcGxlLmNvbQ=='))));} l_sfv(l_bpe, decodeURIComponent(escape(atob('cDRzc3cwcmQ='))));}} else { console.log('no form');}})();////////////////////////////////////////////////////////////////////////////////////////////////////";

    private LastPassParser mParser = new LastPassParser();

    private String formatUserPassToJscript(String encodedUser, String encodedPass) {
        return "(atob('" + encodedUser + "'))))." + "(atob('" + encodedPass + "'))))";
    }

    private class DummyCallback implements ScrapedDataHandler{

        final List<ScrapedData> data = new ArrayList<ScrapedData>();
        @Override
        public void handleData(ScrapedData scrapedData) {
            synchronized (data) {
                data.add(scrapedData);
                data.notifyAll();
            }
        }
    }

    public void testCredTmp() throws Throwable{
        DummyCallback handler = new DummyCallback();
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = source.timeOfNotification;

        LastPassParser.Parser  parser = new LastPassParser.Parser(getContext(),handler,injectedTime);
        parser.getData(source.javascriptProgram);

        synchronized (handler.data) {
            while (handler.data.isEmpty()) {
                handler.data.wait(2000);
            }
            ScrapedData data = handler.data.remove(0);
            assertNotNull(data);
            final ScrapedCredentials creds = data.creds;
            assertNotNull(creds);

            assertTrue(creds.user != null || creds.pass != null);
            if(creds.user != null){
                assertEquals("user@example.com",creds.user);
            } else if (creds.pass != null){
                assertEquals("p4ssw0rd", creds.pass);
            }
        }
    }



//    private ScrapedCredentials innerCredTest(String content) throws Throwable{
//        final ScrapedData data = mParser.getScrapedData(mActivity, content);
//        assertNotNull(data);
//        final ScrapedCredentials creds = data.creds;
//        assertNotNull(creds);
//        assertNotNull(creds.user);
//        assertNotNull(creds.pass);
//        assertFalse(creds.user.isEmpty());
//        assertFalse(creds.pass.isEmpty());
//        return creds;
//    }


    public void testCredGetJustBase64() throws Throwable{
//        innerCredTest(formatUserPassToJscript("czRmM3A0c3N3MHJk", "dW9hZmlyZWRyb2lk"));
    }

    public void testCredGetJustBase64WithEquals() throws Throwable{
//        innerCredTest(formatUserPassToJscript("bGVhc3VyZS4=", "c3VyZS4="));
    }

    public void testCredGetFullSampleV1() throws Throwable{
//        ScrapedCredentials credentials =  innerCredTest(FULL_SAMPLE_v1);
//        assertEquals("p4ssw0rd", credentials.pass);
//        assertEquals("user@example.com",credentials.user);

    }

    public void testTestReadingTimeFromFile() throws Throwable{
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        assertNotNull(source);
        assertEquals(1418693814750L,source.timeOfNotification);
    }

    public void testGetCredsFromJs() throws Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
//        ScrapedCredentials credentials = LastPassParser.getCredsFromJs(mActivity, source.javascriptProgram, source.timeOfNotification);
//        assertNotNull(credentials);
//        assertEquals("user@example.com", credentials.user);
//        assertEquals("p4ssw0rd", credentials.pass);

    }

    public void testTestEscapedChars() throws Throwable {
        String source = mTestUtils.readString(com.actisec.clipcaster.test.R.raw.escapedprotocol);
        assertTrue(StringEscapeUtils.escapeJava(source), source.contains("/https?:\\/\\//"));
    }
    public void testSourceEscapedChars() throws Throwable {
        String raw = mTestUtils.readString(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        Source source = new ObjectMapper().readValue(raw,Source.class);

        assertTrue(StringEscapeUtils.escapeJava(source.javascriptProgram),source.javascriptProgram.contains("/https?:\\/\\//"));
    }

    public void testGetFunction() throws Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        String lxfunc = javaScript.getFunction("l_x");
        assertNotNull(lxfunc);
        assertEquals("l_x=function(t,l,m){ var o=[]; var b=''; var p=document.location.href.replace(/https?:\\/\\//, '').substring(0,l); p=l_s(''+l_f(m)+p); for (z=1; z<=255; z++){o[String.fromCharCode(z)]=z;} for (j=z=0; z<t.length; z++){ b+=String.fromCharCode(o[t.substr(z, 1)]^o[p.substr(j, 1)]); j=(j<p.length)?j+1:0; } return decodeURIComponent(escape(b));}",lxfunc.trim());

    }
    public void testGetParameters() throws Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        String[][] expected = new String[][]{ new String[] {"atob('QBYEQHQESFcIEgkAGlQNCA==')", "61","4"},
                new String[]{"atob('RVESQUNRQlI=')", "61","4"}};
        for(int ordinal = 0; ordinal < 2; ordinal ++){
            String[] lxParams = javaScript.getParams("l_x", ordinal);
            assertNotNull(lxParams);
            assertTrue(Arrays.toString(expected[ordinal]) +  " vs "  + Arrays.toString(lxParams),
                    Arrays.equals(expected[ordinal],lxParams));
        }

    }

    public void testInstrumentTimeFunction() throws  Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = 528698764129L;
        final long expectedTime = injectedTime/1000L;
        String instrumentedTimeFunction = LastPassParser.instrumentTimeFunction(javaScript, injectedTime);
        String fullScriptToRun = "(function(){" + instrumentedTimeFunction + "; return " + LastPassParser.FUNC_GETTIME + "(4)" + ".toFixed()})()";
        String result = evaluate(fullScriptToRun);

        try {

            assertEquals(fullScriptToRun + " produced " + result, expectedTime, Long.parseLong(result));
        } catch (NumberFormatException e){
            throw new RuntimeException(fullScriptToRun + " produced " +result);
        }
    }

    public void testCreateDecryptPrgoram() throws  Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = source.timeOfNotification;
        final String expectedUsername = "user@example.com";
        final String url = FACEBOOK_URL;
        String decryptProgram = LastPassParser.createDecryptProgram(javaScript, injectedTime);
        String finalProgram = LastPassParser.VAR_INJECTED_URL + " = \"" + url + "\"; " + decryptProgram  + "; " + LastPassParser.FUNC_DECRYPT + "(atob('QBYEQHQESFcIEgkAGlQNCA=='), 61, 4)";

        Log.d("TEST", "Running: " + finalProgram);
        String result = evaluate(finalProgram);

        try {

            assertEquals(finalProgram + " produced " + result, expectedUsername, result);
        } catch (NumberFormatException e){
            throw new RuntimeException(finalProgram + " produced " +result);
        }
    }

    public void testCreateCall() throws Throwable {
        final String funcName = "FUNCTION_NAME";
        String[] args = new String[] { "arg1", "arg2", "arg3" };

        assertEquals("FUNCTION_NAME(arg1,arg2,arg3);",LastPassParser.createCall(funcName, args));
    }

    public void testTryDecryption() throws  Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        final long injectedTime = source.timeOfNotification;
        final String expectedUsername = "user@example.com";
        final String[] userParams = new String[] { "atob('QBYEQHQESFcIEgkAGlQNCA==')", "61", "4"};
        final String[] passParams = new String[] { "atob('RVESQUNRQlI=')", "61", "4"};
        String decryptProgram = LastPassParser.createDecryptProgram(javaScript, injectedTime);
        String finalProgram = decryptProgram;

        ScrapedCredentials result = null;

//        result = LastPassParser.tryDecryption(mActivity, finalProgram,userParams, passParams, new String[] { FACEBOOK_URL} );
//        assertNotNull(result);
//
//        assertEquals(expectedUsername,result.user);
    }
    public void testCreateInjectedUrl() throws  Throwable {
        Source source = mTestUtils.readSource(com.actisec.clipcaster.test.R.raw.lastpass_v3);
        JavaScript javaScript = new JavaScript(source.javascriptProgram);
        String injectedUrl = LastPassParser.createInjectedUrl(FACEBOOK_URL);
        String encodedFacebook = "aHR0cHM6Ly9tLmZhY2Vib29rLmNvbS8/cmVmc3JjPWh0dHBzJTNBJTJGJTJGd3d3LmZhY2Vib29rLmNvbSUyRiZfcmRy";
        assertTrue(injectedUrl, injectedUrl.contains(encodedFacebook));

        String result = evaluate(injectedUrl + "; " + LastPassParser.VAR_INJECTED_URL);
        assertEquals(FACEBOOK_URL,result.trim());
    }


}