package com.example.playwright.steps;

import com.example.helpers.testData.Context;
import com.example.helpers.testData.TestContextHolder;
import com.example.playwright.pageObjects.*;


public class BaseGlue {

    protected final AgendaPO agendaPO = new AgendaPO();
    protected final AsidePO asidePO = new AsidePO();
    protected final BillingPO billingPO = new BillingPO();
    protected final BlastPO blastPO = new BlastPO();
    protected final DataReportPO drPO = new DataReportPO();
    protected final DeviceDetailsPO ddPO = new DeviceDetailsPO();
    protected final DevicePO devicePO = new DevicePO();
    protected final DialogPO dialogPO = new DialogPO();
    protected final FilterPO filterPO = new FilterPO();
    protected final LoginPO loginPO = new LoginPO();
    protected final MapPO mapPO = new MapPO();
    protected final MeasuringPointPO mpPO = new MeasuringPointPO();
    protected final MenuPO menuPO = new MenuPO();
    protected final MessageRulesPO mrPO = new MessageRulesPO();
    protected final ProjectPO projectPO = new ProjectPO();
    protected final SupportPO sPO = new SupportPO();
    protected final TableColumnSettingsPO tcsPO = new TableColumnSettingsPO();
    protected final TransientChartsPO tcPO = new TransientChartsPO();
    protected final UserPO userPO = new UserPO();
    protected final UserProfilePO upPO = new UserProfilePO();
    protected final ScheduleReportsPO srPO = new ScheduleReportsPO();
    protected final IconPO iconPO = new IconPO();


    /** Gives glue classes shared access to common page objects. */
    protected BaseGlue() {
    }

    /** Returns the context belonging to the current scenario thread. */
    protected Context context() {
        return TestContextHolder.getContext();
    }
}
