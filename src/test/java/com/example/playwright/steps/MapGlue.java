package com.example.playwright.steps;

import com.example.api.endpoints.MeasuringPointApi;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.playwright.components.parts.Listbox;
import com.example.playwright.helpers.PlaywrightActions;
import com.microsoft.playwright.options.BoundingBox;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;


public class MapGlue extends BaseGlue {

    @When("user has selected a search result in Find location")
    public void userHasSelectedASearchResultInFindLocation() {
        mapPO.makeSearchInFindLocation("Spetsvägen");
        PlaywrightActions.sleep(3);
        // Get all selectable options
        Listbox listbox = mpPO.getListbox();
        String selectThisOption = listbox.getOptions().getFirst().getText();

        mapPO.selectListboxOption(selectThisOption);
    }

    @Then("a large pin should appear on the map")
    public void aLargePinShouldAppearOnTheMap() {
        assertTrue(mapPO.isSearchPinOnMap());
    }

    @Then("mp will have new location")
    public void mpWillHaveNewLocation() {
        // Get the mp we use when setting up the project
        MeasuringPoint mp = context().getMeasuringPoints().getFirst();
        Double oldMpLat = mp.getLocation().getWgs84().getLat();
        Double oldMpLng = mp.getLocation().getWgs84().getLng();

        // Get the same mp, but from api
        MeasuringPoint alteredMp = MeasuringPointApi.getMeasuringPoint(context().getProject().getId(), mp.getId());
        Double alteredMpLat = alteredMp.getLocation().getWgs84().getLat();
        Double alteredMpLng = alteredMp.getLocation().getWgs84().getLng();

        // Assert that the moving of large map marker generates a new location
        assertNotEquals(oldMpLat, alteredMpLat, 0.0,
                () -> "oldMpLat/alteredMpLat: " + oldMpLat + "/" + alteredMpLat);
        assertNotEquals(oldMpLng, alteredMpLng, 0.0,
                () -> "oldMpLng/alteredMpLng: " + oldMpLng + "/" + alteredMpLng);
    }

    @Then("I can use Pin-on-map to change location")
    public void iCanUsePinOnMapToChangeLocation() {

        mpPO.clickOnButton("Pin on map");
        PlaywrightActions.sleep(2);

        // Make sure large mp icon is on map
        if (!mapPO.isIconOnMap("large mp")) {
            throw new IllegalStateException("Mp large map marker not visible");
        }

        // Get the current position of Mp.Location marker
        BoundingBox box = mapPO.getLocationOfLargeIcon("large mp");
        int newPositionX = (int) (box.x + 50);;
        int newPositionY = (int) (box.y + 50);;

                // Use the current location to set a new one
        mapPO.clickOnScreen(newPositionX, newPositionY);
        // Transfer the new location to mp settings location
        mpPO.clickOnButton("Apply");

        mpPO.clickButton("Save");

        // Make sure the large icon is gone
        assertNull(mapPO.getLocationOfLargeIcon("large mp"),
                () -> "Large map marker is still on map after save.");
    }

    @Then("map is centered on the {string}")
    public void mapIsCenteredOnTheBlast(String provider) {
        switch (provider) {
            case "blast" -> {
                String blastName = context().getBlasts().getFirst().getBlastId();
                assertTrue(mapPO.mapMarkerExist(blastName));
            }
            case "measuring point" -> {
                String mpName = context().getMeasuringPoints().getFirst().getName();
                assertTrue(mapPO.mapMarkerExist(mpName));
            }
        }
    }

    @When("I set location by input")
    public void iSetLocationByInput() {
        double newLat = 60.0;
        double newLng = 70.0;

        MeasuringPoint mp = context().getMeasuringPoints().getFirst();
        mpPO.clickOnButton("Pin on map");
        PlaywrightActions.sleep(2);

        mapPO.setLocationCoordinatesByLocationPanel(newLat, newLng);
        mpPO.clickOnButton("Apply");
        mpPO.clickOnButton("Save");
    }
}
