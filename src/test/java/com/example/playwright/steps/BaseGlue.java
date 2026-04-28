package com.example.playwright.steps;

import com.example.playwright.pageObjects.*;

public class BaseGlue {

    protected final LoginPO loginPO = new LoginPO();
    protected final DevicePO devicePO = new DevicePO();
    protected final CommonElementsPO cePO = new CommonElementsPO();
    protected final MenuPO menuPO = new MenuPO();
    protected final UserProfilePO upPO = new UserProfilePO();


    /**
     * Gives glue classes shared access to common page objects.
     */
    protected BaseGlue() {
    }

}
