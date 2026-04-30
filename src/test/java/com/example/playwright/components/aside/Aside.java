package com.example.playwright.components.aside;

import com.example.playwright.components.aside.asideItems.AsideItem;
import com.example.playwright.components.aside.asideItems.listItems.*;
import com.example.playwright.components.parts.*;
import com.example.playwright.helpers.enums.IconType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class Aside  {

    Button collapse;                    // COMPACT, MEDIUM
    AsideMenu menu;                     // COMPACT, MEDIUM, FULL
    AsideHeader header;                 // COMPACT, MEDIUM, FULL
    AsideAction action;                 // COMPACT, MEDIUM, FULL

    List<AsideItem> asideItems;         // COMPACT
    Table table;                        // MEDIUM, FULL

    AsideFooter footer;                 // COMPACT, MEDIUM, FULL

    @JsonIgnore
    public boolean isCompact() {
        return asideItems != null;
    }

    //     Accept any list of items that extend AsideItem
    public void setAsideItems(List<? extends AsideItem> asideItems) {
        this.asideItems = new ArrayList<>(asideItems);
    }

    @JsonIgnore
    public List<OverviewItem> getOverviewItems() { return getItemsByClassType(OverviewItem.class); }

    @JsonIgnore
    public List<DeviceItem> getDeviceItems() {
        return getItemsByClassType(DeviceItem.class);
    }

    @JsonIgnore
    public List<ScheduledReportItem> getScheduleReportItems() {
        return getItemsByClassType(ScheduledReportItem.class);
    }

    @JsonIgnore
    public List<ProjectItem> getProjectItems() {
        return getItemsByClassType(ProjectItem.class);
    }

    @JsonIgnore
    public List<UserItem> getUserItems() {
        return getItemsByClassType(UserItem.class);
    }

    @JsonIgnore
    public List<MeasuringPointItem> getMeasuringPointItems() {
        return getItemsByClassType(MeasuringPointItem.class);
    }

    @JsonIgnore
    public List<BlastItem> getBlastItems() {
        return getItemsByClassType(BlastItem.class);
    }

    @JsonIgnore
    public List<DataReportItem> getDataReportItems() {
        return getItemsByClassType(DataReportItem.class);
    }

    @JsonIgnore
    public List<MessageRuleItem> getMessageRuleItems() {
        return getItemsByClassType(MessageRuleItem.class);
    }

    @JsonIgnore
    private <T extends AsideItem> List<T> getItemsByClassType(Class<T> type) {
        return asideItems.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    // todo: Denna kan nog byggas ut så att jag kan hämta vilken typ av objekt som helst, typ getDeviceItemByIconType
    /**
     * Returns the first OverviewItem from asideItems matching the given IconType.
     * Throws NoSuchElementException if none found.
     */
    @JsonIgnore
    public OverviewItem getOverviewItemByIconType(IconType iconType) {
        return OverviewItem.getOverviewItemByIconType(this.asideItems, iconType);
    }

    @JsonIgnore
    public List<String> getAsideTableHeaders() {
        return this.table.getHeader().getAllValuesAsString();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AsideMenu  {

        Icon providerTypeIcon;
        Dropdown dropdown;
        Button plusButton;

        NoticeItem commitNotice;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AsideHeader  {
        Button backButton;
        String searchValue;
        Button searchButton;
        Button sortButton;
        Button filterButton;
        Button warningButton;
        Button refreshButton;
        Button menuButton;

        // Returns a list of all non-null buttons in the AsideHeader
        @JsonIgnore
        public List<Button> getButtons() {
            List<Button> buttons = new ArrayList<>();

            if (backButton != null) buttons.add(backButton);
            if (searchButton != null) buttons.add(searchButton);
            if (sortButton != null) buttons.add(sortButton);
            if (filterButton != null) buttons.add(filterButton);
            if (warningButton != null) buttons.add(warningButton);
            if (refreshButton != null) buttons.add(refreshButton);
            if (menuButton != null) buttons.add(menuButton);

            return buttons;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AsideAction  {  // aka bulk action
        // Blast
        Icon loadLatestIcon;
        String loadLatestText;

        // Bulk action
//        Icon checkbox;
        Checkbox checkbox;
        String summaryText;

        // Account device
        Button createProjectButton;

        // Project Mp
        Button createDataReportButton;
        Button editTimeFrameButton;

        // Returns a list of all non-null buttons in the AsideAction
        @JsonIgnore
        public List<Button> getButtons() {
            List<Button> buttons = new ArrayList<>();

            if (createProjectButton != null) buttons.add(createProjectButton);
            if (createDataReportButton != null) buttons.add(createDataReportButton);
            if (editTimeFrameButton != null) buttons.add(editTimeFrameButton);

            return buttons;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AsideFooter  {
        String text;
    }
}
