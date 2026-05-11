package com.example.playwright.pageObjects;

import com.example.api.models.project.Project;
import com.example.helpers.StatusAssesser.Status;
import com.example.playwright.components.map.MainPane;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Icon;
import com.example.playwright.components.parts.LocationFloatingPanel;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.IconType;
import com.microsoft.playwright.options.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static com.example.helpers.StatusAssesser.Status.*;


public class MapPO extends CommonPO {

    private static String objectStringBuilder(String input) {return "//img[contains(@title, '" + input + "')]";}
    //MP icons
    //map project list item
    private static final String PROJECT_TOGGLE = "//div[text()='Inactive' or text()='Active']";
    private static final String PROJECT_CLOSE = "//*[text() = 'close']";

    private static final String ZOOM_IN = "//main //div[@class='leaflet-control-container'] //a[@title='Zoom in']";
    private static final String ZOOM_OUT = "//main //div[@class='leaflet-control-container'] //a[@title='Zoom out']";

    public void changeZoom(String direction) {
        switch (direction) {
            case "in" -> actions().makeClick(ZOOM_IN);
            case "out" -> actions().makeClick(ZOOM_OUT);
        }
    }
    public void changeProjectActivityToggleFromMapIcon(Project project) {
        //click on map icon
        actions().makeClick(objectStringBuilder(project.getName()));
        //click on toggle
        actions().makeClick(PROJECT_TOGGLE);
        //close the panel
        actions().makeClick(PROJECT_CLOSE);
    }

    /**
     * Needs project map marker not to be out-of-zoom or grouped.
     * If list-filter is set to Active and project is inactive, mtd will not work.
     */
    public Status projectMapIconStatus(String projectName) {
        //fetch the img-tag holding the icon-information
        String iconInfo = actions().findOneElementsAttribute(objectStringBuilder(projectName), "src");

        return iconInfo.contains("project-on") ? ACTIVE : INACTIVE;
    }

    public Status mpMapIconStatus(String mpName) {
        //fetch the img-tag holding the icon-information
        String iconInfo = actions().findOneElementsAttribute(objectStringBuilder(mpName), "src");

        return iconInfo.contains("-on") ? ACTIVE : INACTIVE;
    }

    /**
     * Find a marker on map based on device serial number.
     * @param serial Serial for device map icon we're looking for.
     * @return ACTIVE, INACTIVE, WARNING or NOT_PRESENT (if not found)
     */
    public Status deviceMapIconStatus(String serial) {
        String deviceMapIconLocator = objectStringBuilder(serial);

        if (!actions().elementExistAndVisible(objectStringBuilder(serial), false, 5)) {
            System.out.println("\t -> No device map icon found");
            return NOT_PRESENT;
        }

        String iconInfo = actions().findOneElementsAttribute(deviceMapIconLocator, "src");

        if (iconInfo.contains("device-on")) { return ACTIVE;
        } else if (iconInfo.contains("device-off")) { return INACTIVE;
        } else if (iconInfo.contains("device-alert")) {return WARNING;
        } else {
            // todo: add NOT_PRESENT as return value. OH
            throw new IllegalStateException("Unrecognized device map icon state for serial: " + serial);
        }
    }

    public void makeSearchInFindLocation(String searchText) {
        // Make a search
        actions().clearAndType("//*[@data-qa-id='find-location-field'] //input", searchText);
        PlaywrightActions.sleep(1); // Give listbox a sec to load
    }

    public boolean isSearchPinOnMap() {
        return actions().elementExistAndVisible("//img[@src='svg/mp/map-marker-mp-group.svg']", false, 0);
    }

    public boolean isIconOnMap(String icon) {
        String iconType = switch (icon) {
                case "large project" -> "project/map-marker-project-hover";
                case "large mp" -> "mp/map-marker-mp-group";
                default -> throw new IllegalStateException("Unknown icon: " + icon);
            };

        return actions().elementExistAndVisible("//div[@class='leaflet-pane leaflet-popup-pane'] //img[@src='svg/"+iconType+".svg']");
    }

    public boolean mapMarkerExist(String labelText) {
        return actions().elementExistAndVisible("//img[@title='"+labelText+"']");
    }

    public BoundingBox getLocationOfLargeIcon(String icon) {
        String iconType = switch (icon) {
            case "large project" -> "project/map-marker-project-hover";
            case "large mp" -> "mp/map-marker-mp-group";
            default -> throw new IllegalStateException("Unknown icon: " + icon);
        };

        if (!actions().elementExistAndVisible("//div[@class='leaflet-pane leaflet-popup-pane'] //img[@src='svg/"+iconType+".svg']", false, 1)) {
            return null;
        }

        // First get current position
        return actions().readElementPosition("//div[@class='leaflet-pane leaflet-popup-pane'] //img[@src='svg/"+iconType+".svg']");
    }

    public BoundingBox getLocationOfMapIcon(String labelText) {
        if (!actions().elementExistAndVisible("//img[@title='"+labelText+"']", true, 1)) {
           throw new IllegalArgumentException("Cannot find mp map marker: " + labelText);
        }
        return actions().readElementPosition("//img[@title='"+labelText+"']");
    }

    public void clickOnScreen(int xAxis, int yAxis) {
        actions().clickAtCoordinates(xAxis, yAxis);
    }

    public LocationFloatingPanel getLocationPanel() {
        LocationFloatingPanel locationPanel = new LocationFloatingPanel();

        String panelTopPath = "//div[@class='bg-white rounded-borders shadow-1 z-marginals text-infra-primary absolute-bottom']";

        String latitude = actions().findOneElementsValueAttribute(panelTopPath + " //form //input[contains(@aria-label,'Latitude')]");
        locationPanel.setLatitude(latitude);

        String longitude = actions().findOneElementsValueAttribute(panelTopPath + " //form //input[contains(@aria-label,'Longitude')]");
        locationPanel.setLongitude(longitude);

        return locationPanel;
    }

    public void setLocationCoordinatesByLocationPanel(double lat, double lng) {
        if (getLocationPanel() == null) {
            throw new IllegalStateException("Could not find LocationFloatingPanel");
        }
        String panelTopPath = "//div[@class='bg-white rounded-borders shadow-1 z-marginals text-infra-primary absolute-bottom']";
        // Set latitude value
        actions().clearAndType(panelTopPath + " //form //input[contains(@aria-label,'Latitude')]", String.valueOf(lat));
        // Set longitude value
        actions().clearAndType(panelTopPath + " //form //input[contains(@aria-label,'Longitude')]", String.valueOf(lng));
    }

    public MainPane getMainPane() {
        MainPane mainPane = new MainPane();

        // Get the two zoom buttons
        List<String> zoomButtons = actions().findManyElementsAttribute("//main //div[@class='leaflet-control-container'] //a[contains(@title, 'Zoom')]", "class");
        zoomButtons.forEach(mainPane::setZoomButton);

        // Get the buttons in right bottom corner
        int buttonsInRightBottomCornerCount = actions().countHowManyElements("//main //div[@class='column justify-center items-center q-gutter-y-sm'] //button");

        for (int i = 1; i <= buttonsInRightBottomCornerCount; i++) {
            String className;
            String buttonText;

            // Big green plus button only exist for 3- or 4-button groups
            if ((i == 3 || i == 4) && i == buttonsInRightBottomCornerCount) { // ie the big green plus icon
                className = actions().findOneElementsAttribute("((//main //div[@class='column justify-center items-center q-gutter-y-sm'] //button)["+i+"] //span)[last()]", "class");
                buttonText = "";
            } else {
                className = actions().findOneElementsAttribute("(//main //div[@class='column justify-center items-center q-gutter-y-sm'] //button)["+i+"] //i", "class");
                buttonText = actions().findOneElementsText("(//main //div[@class='column justify-center items-center q-gutter-y-sm'] //button)["+i+"] //i");
            }
            mainPane.setButtonInRightBottomCorner(className, buttonText);
        }

        boolean hasMapMarker = actions().elementExistAndVisible("//main //div[@class='leaflet-pane leaflet-marker-pane leaflet-zoom-hide'] //img", false);
        if (hasMapMarker) {
            List<Button> mapMarkerButtons = new ArrayList<>();

            // The map markers takes a sec or two to load to map
            PlaywrightActions.sleep(1);

            // Get all map markers
            int mapMarkerCount = actions().countHowManyVisibleElements("//main //div[@class='leaflet-pane leaflet-marker-pane leaflet-zoom-hide'] //img");

            for (int i = 1; i <= mapMarkerCount; i++) {
                String mapMarkerPath = "(//main //div[@class='leaflet-pane leaflet-marker-pane leaflet-zoom-hide'] //img)["+i+"]";
                Button mapMarkerButton = new Button();

                Icon mapIcon = new Icon();
                String mapMarkerSrc = actions().findOneElementsAttribute(mapMarkerPath, "src");
                IconType mapMarkerType = IconType.fromClassName(getSvgName(mapMarkerSrc));
                mapIcon.setType(mapMarkerType);
                mapMarkerButton.setIcon(mapIcon);

                // Grouped icons do not have title
                if (!mapMarkerSrc.contains("group")) {
                    String mapMarkerPopupText = actions().findOneElementsAttribute(mapMarkerPath, "title");
                    mapMarkerButton.setText(mapMarkerPopupText);
                }

                mapMarkerButtons.add(mapMarkerButton);
            }

            mainPane.setMapMarkerButtons(mapMarkerButtons);
        }

        return mainPane;
    }

    private static String getSvgName(String mapMarkerSrc) {
        int start = mapMarkerSrc.lastIndexOf('/') + 1;
        int end = mapMarkerSrc.lastIndexOf(".svg");

        if (end == -1 || end <= start) {
            return "";
        }

        return mapMarkerSrc.substring(start, end);
    }


}
