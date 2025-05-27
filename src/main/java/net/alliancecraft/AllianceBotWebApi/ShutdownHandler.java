package net.alliancecraft.AllianceBotWebApi;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHandler implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        IPBanFilter.saveBanIps();
    }
}
