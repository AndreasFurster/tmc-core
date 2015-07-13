package hy.tmc.core.commands;

import com.google.common.util.concurrent.ListenableFuture;
import hy.tmc.core.TmcCore;
import hy.tmc.core.commands.VerifyCredentials;
import hy.tmc.core.communication.HttpResult;
import hy.tmc.core.communication.UrlCommunicator;
import hy.tmc.core.communication.authorization.Authorization;
import hy.tmc.core.ClientTmcSettings;
import hy.tmc.core.configuration.TmcSettings;
import hy.tmc.core.domain.Credentials;
import hy.tmc.core.exceptions.TmcCoreException;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import static org.powermock.api.mockito.PowerMockito.mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UrlCommunicator.class)
public class AuthenticateTest {

    private final String testUsername = "test";
    private final String testPassword = "1234";
    private VerifyCredentials auth;
    TmcSettings settings;
    UrlCommunicator comm;

    /**
     * Set up server mock and Authenticate command.
     */
    @Before
    public void setUp() {
        settings = new ClientTmcSettings();
        comm = Mockito.mock(UrlCommunicator.class);
        this.auth = new VerifyCredentials(settings, comm);
    }

    @Test
    public void canAuthenticateWithTestCredentials() throws TmcCoreException, IOException {
        new Authorization(); // for code coverage
        String result = executeWithParams("username", testUsername, "password", testPassword);
        assertTrue(result.contains("Auth successful."));
    }

    @Test
    public void cannotAuthenticateWithUnexistantCredentials() throws TmcCoreException, IOException {
        String result = executeWithParams("username", "samu", "password", "salis");
        assertTrue(result.contains("Auth unsuccessful."));
    }

    @Test(expected = TmcCoreException.class)
    public void failsWithWrongKeys() throws TmcCoreException, IOException {
        executeWithParams("usernamee", testUsername, "passwordi", testPassword);
    }

    private String executeWithParams(String key1, String param1,
            String key2, String param2) throws TmcCoreException, IOException {

        auth.setParameter(key1, param1);
        auth.setParameter(key2, param2);
        Mockito.mock(UrlCommunicator.class);
        powerMockWithCredentials("test:1234", 200);
        powerMockWithCredentials("samu:salis", 400);
        auth.checkData();
        return auth.parseData(auth.call()).get();

    }

    private void powerMockWithCredentials(String credentials, int status) throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult("", status, true);
        Mockito
                .when(comm.makeGetRequest(
                                Mockito.anyString(),
                                Mockito.eq(credentials)))
                .thenReturn(fakeResult);
    }
}
