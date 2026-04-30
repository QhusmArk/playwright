package com.example.playwright.components.parts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Each TableRow should be of equal size.
 * Which is a problem as some tables have more headers than the body, or vice versa.
 */
@Getter
@Setter
@NoArgsConstructor
public class Table {

    private TableRow header;
    private List<TableRow> metaData;
    private List<TableRow> content;
    private String noRowsText;

    private List<String> tableSubtitle; // as in agenda timeslot table  //todo: bygg vidare så jag kan hantera färg+text

    // todo: ersätt med TableCell och custom upp-packare
    // Custom made list for measuring points in Message Rules
    private List<MpTableRow> messageRuleMpRows;

    private Icon footerIcon;
    private Dropdown footerDropdown;
    private String footerText;

    /**
     * @param metaDataKey As the first element in a MetaData.TableRow from TransientTable, etc.
     */
    @JsonIgnore
    public TableRow getMetaDataTableRowByKey(String metaDataKey) {
        return this.getMetaData().stream()
                .filter(tableRow -> tableRow.getAllValuesAsString().stream()
                        .anyMatch(s -> s.equals(metaDataKey)))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No MetaData.TableRow with key: " + metaDataKey)
                );
    }

    /**
     * Looks at the first column for a String matching metaDataKey.
     * @return Which row the key is at
     */
    @JsonIgnore
    public Integer getMetaDataIndexByKey(String metaDataKey) {
        return IntStream.range(0, metaData.size())
                .filter(i -> {
                    // Loop through the rows in metadata, and return the row that have value in first column equal to 'metaDataKey'
                    return metaData.get(i).getStringAtPosition(0).equals(metaDataKey);
                })
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No MetaData.TableRow with key: " + metaDataKey)
                );
    }

    /**
     * Looks at the first column for a String matching timestamp.
     */
    @JsonIgnore
    public Integer getContentRowIndexByKey(String timestampKey) {
        return IntStream.range(0, content.size())
                .filter(i -> {
                    // Loop through the rows in content, and return the row that have value in first column equal to 'timestampKey'
                    return content.get(i).getStringAtPosition(0).equals(timestampKey);
                })
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No Content.TableRow with key: " + timestampKey)
                );
    }

    /**
     * Can only be used by tables that have TableCell.
     */
    @JsonIgnore
    public int getColumnIndexByMpName(String mpName) {
        // Cast List<Object> to TableCell
        List<TableCell> columnHeaders = header.cellValues.stream()
                .filter(obj -> obj instanceof TableCell) // optional: avoids ClassCastException
                .map(tableCell -> (TableCell) tableCell)
                .toList();

        // Now search the position of the map that has the mpName. Start with 1 as the first column is 'Timestamp' key
        int columnIndex =  IntStream.range(0, columnHeaders.size())
                .filter(i -> {
                    String headerText = columnHeaders.get(i).getCellText(0);
                    return headerText != null
                            && headerText.contains(mpName);
                })
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No column with mpName: " + mpName)
                );

        return columnIndex;
    }

    @JsonIgnore
    private List<String> getReportTableTopRowText(int partOfHeaderText) {
        return header.getObjects().stream()
                .skip(1)
                .filter(obj -> obj instanceof TableCell)
                .map(obj -> ((TableCell) obj).getCellText(partOfHeaderText))
                .toList();
    }

    /**
     * Gets each columns TableMpHeader text.
     */
    @JsonIgnore
    public List<String> getTransientTableHeaderMpNamesAndDescription() {
        // Avoid the first column (Timestamp) and then return the text that is the mpName + mpDescription
        return getReportTableTopRowText(0);
    }

    @JsonIgnore
    public List<String> getIntervalsTableHeaderMpNames() {
        // Avoid the first column (Timestamp) and then return the text that is the mpName
        return getReportTableTopRowText(0);

    }

    @JsonIgnore
    public List<String> getIntervalsTableHeaderSensors() {
        // Avoid the first column (Timestamp) and then return the text that is the mpSensor
        return getReportTableTopRowText(1);
    }

    @JsonIgnore
    public List<String> getIntervalsTableHeaderBanners() {
        // Avoid the first column (Timestamp) and then return the text that is the mpName
        List<TableCell> headerTexts = header.getObjects().stream()
                .skip(1)
                .filter(obj -> obj instanceof TableCell)
                .map(obj -> (TableCell) obj)
                .toList();

        return header.getObjects().stream()
                .skip(1)
                .filter(obj -> obj instanceof TableCell)
                .map(obj -> (TableCell) obj)
                .map(TableCell::getCellTexts) // assuming this returns List<String>
                .filter(texts -> texts.size() > 2)
                .flatMap(texts -> texts.subList(2, texts.size()).stream())
                .toList();
    }


    /**
     * For MetaData rows in Intervals and Transients Table
     * @param mpName  The x-axis string we use to get which column the header has.
     * @param metaDataKey   The y-axis string we use to find which row the key has.
     */
    @JsonIgnore
    public String getMetaDataValueByMpNameAndMetaDataKey(String mpName, String metaDataKey) {
        // Get the row where metadata matches (and thereby asserting the existence of the metaData key
        int indexOfRow = getMetaDataIndexByKey(metaDataKey);
        TableRow metaDataRow = metaData.get(indexOfRow);

        // Now use mpName to find out which column we shall get data from
        int indexOfColumn = getColumnIndexByMpName(mpName);

        return (String) metaDataRow.getCellValues().get(indexOfColumn);
    }

    /**
     * For Content rows in Intervals and Transients Table
     * @param mpName  The x-axis string we use to get which column the header has.
     * @param timestampKey   The y-axis string we use to find which row the key has.
     */
    @JsonIgnore
    public String getContentValueByMpNameAndTimestampKey(String mpName, String timestampKey) {
        // Get the row where timestamp matches (and thereby asserting the existence of the timestamp key
        int indexOfTimeStampKey = getContentRowIndexByKey(timestampKey);
        TableRow transientRow = content.get(indexOfTimeStampKey);

        // Now use mpName to find out which column we shall get data from
        int indexOfColumn = getColumnIndexByMpName(mpName);

        return (String) transientRow.getCellValues().get(indexOfColumn);
    }

    /**
     * @param columnHeader  The x-axis string we use to get the
     * @param rowKey   The y-axis row where we need to find the value
     */
    @JsonIgnore
    public String getIntervalTableIntervalCellValue(String columnHeader, String rowKey, String channel) {
        // Get the row where metadata matches (and thereby asserting the existence of the metaData key
        int indexOfTimeStampKey = getContentRowIndexByKey(rowKey);

        // Now get which column we shall get data from
        int indexOfColumn = getColumnIndexByMpName(columnHeader);

        TableRow intervalRow = content.get(indexOfTimeStampKey);
        TableCell cellValue = (TableCell) intervalRow.getCellValues().get(indexOfColumn);       // Else 'Display and compare dB correction for C50 interval' fails

        return switch (channel) {
            case "Lmax" -> cellValue.cellTexts.getFirst();
            case "LAeq" -> cellValue.cellTexts.get(1);
            default -> throw new IllegalArgumentException("No support for channel: " + channel);
        };
    }

    /**
     * Class used for storing table information such as icon, text (e.g., interval channel data) or both.
     *
     * For Intervals Table header cellTexts is: [0]=mpName, [2]=mpSensor, [3-]=banners
     * For Transients Table header cellText is [0]=mpName + ",\n" + mpDescription,
     *
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TableCell  {

        List<String> cellTexts;
        List<Icon> cellIcons;

        @JsonIgnore
        public void addCellText(String text) {
            if (cellTexts == null) {
                cellTexts = new ArrayList<>();
            }
            cellTexts.add(text);
        }

        @JsonIgnore
        public String getCellText() {
            return getCellText(0);
        }

        @JsonIgnore
        public String getCellText(int index) {
            if (cellTexts == null || cellTexts.isEmpty()) {
                throw new IllegalStateException("Values array is empty or null");
            } else {
                return cellTexts.get(index);
            }
        }

        @JsonIgnore
        public void addCellIcon(Icon icon) {
            if (cellIcons == null) {
                cellIcons = new ArrayList<>();
            }
            cellIcons.add(icon);
        }

        @JsonIgnore
        public Icon getCellIcon() {
            if (cellIcons == null || cellIcons.isEmpty()) {
                throw new IllegalStateException("Icon array is empty or null");
            } else {
                return cellIcons.getFirst();
            }
        }


    }

    // todo: byt mot vanlig TableRow. Det är ändå vid uppackandet av Object som vi castar till något förväntat.
    /**
     * Due to the complexity of each listitem in Sending Thresholds, the normal TableRow cannot be used.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MpTableRow {
        // Column 1
//        Icon checkbox;
        Checkbox checkbox;
        // Column 2
        Icon mpTypeIcon;

        String mpName;
        String mpDescription;
        String mpSensor;
        String mpSensorSetting;

        // Column 3
        Dropdown triggerSelector;
        String lastUpdated;
        // Column 4
        List<String> channels;
        String banner;
    }

    /**
     * The first element in a headerRow is often 'Timestamp' + a sorting arrow.
     * The first element in a MetaDataRow is a key.
     * The first element in a ContentRow is a timeStamp.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TableRow  {
        // todo: replace this so that we only use TableCell as the content of a TableRow
        // What I need is to decide on one way to store cell data, and implement that for all tables
        List<Object> cellValues;

        public void addContent(Object object) {
            if (cellValues == null) {
                this.cellValues = new ArrayList<>();
            }
            this.cellValues.add(object);
        }

        /**
         * Method mostly used to get which column a header has.
         * + 1 is added to the return index to match to DOM column structure.
         * @return the position in the array (+ 1) where a match is found.
         */
        @JsonIgnore
        public int getColumnIndexByHeader(String header) {
            return getAllValuesAsString().indexOf(header) + 1;
        }

        @JsonIgnore
        public List<String> getAllValuesAsString() {
            return getObjects(String.class);
        }

        @JsonIgnore
        public List<MpTableRow> getAllValuesAsMpTableRow() {
            return getObjects(MpTableRow.class);
        }

        @JsonIgnore
        public List<Object> getObjects() {
            return this.cellValues;
        }

        @JsonIgnore
        public <T> List<T> getObjects(Class<T> clazz) {
            return this.cellValues.stream()
                    .filter(clazz::isInstance)
                    .map(clazz::cast)
                    .toList();
        }

        /**
         * Uses the headerRow and one of the headers to locate which column we shall look in.
         * @return This rows String value for the column.
         */
        @JsonIgnore
        public String getStringByTableHeader(TableRow headerRow, String header) {
            // First use headerRow and header to find out which column we shall use
            int columnIndex = headerRow.getColumnIndexByHeader(header);
            return getCellValueInColumn(columnIndex, String.class);
        }

        /**
         * Some table columns have both icon and text (e.g., Project.ProjectId.
         */
        @JsonIgnore
        public String getStringSharedByIconByTableHeader(TableRow headerRow, String header) {
            TableCell cellWithBothIconAndText = getTableCellByTableHeader(headerRow, header);
            return cellWithBothIconAndText.getCellText();
        }

        /**
         * Some table columns have both icon and text (e.g., Project.ProjectId.
         */
        @JsonIgnore
        public Icon getIconSharedByIconByTableHeader(TableRow headerRow, String header) {
            TableCell cellWithBothIconAndText = getTableCellByTableHeader(headerRow, header);
            return cellWithBothIconAndText.getCellIcon();
        }

        /**
         * LeftIcon is always in the first column, sometimes shared with a string.
         */
        @JsonIgnore
        public Icon getRowLeftIcon() {
            return getCellValueInColumn(1, Icon.class);
        }

        /**
         * RightIcon is always in the last column, and the column have never header.
         */
        @JsonIgnore
        public Button getRowRightButton() {
            int columnCount = this.getObjects().size();
            return getCellValueInColumn(columnCount, Button.class);
        }

        @JsonIgnore
        @SuppressWarnings("unchecked")
        public List<String> getStringListInTableCellByTableHeader(TableRow headerRow, String header) {
            // First use headerRow and header to find out which column we shall use
            int columnIndex = headerRow.getColumnIndexByHeader(header);
            TableCell tableCell = getCellValueInColumn(columnIndex, TableCell.class);
            return tableCell.getCellTexts();
        }

        @JsonIgnore
        @SuppressWarnings("unchecked")
        public List<String> getStringListByTableHeader(TableRow headerRow, String header) {
            // First use headerRow and header to find out which column we shall use
            int columnIndex = headerRow.getColumnIndexByHeader(header);
            return getCellValueInColumn(columnIndex, List.class);
        }

        /**
         * To match DOM column structure with array element index '1' is removed to match array.
         */
        @JsonIgnore
        private <T> T getCellValueInColumn(int columnNumber, Class<T> tClass) {
            Object cellObject = this.cellValues.get(columnNumber - 1);
            if (tClass.isInstance(cellObject)) {
                return tClass.cast(cellObject);
            }
            throw new IllegalArgumentException("Data in cell must be: " + tClass);
        }

        /**
         * Uses the headerRow and one of the headers to locate which column we shall look in.
         * @return This rows TableCell value for the column.
         */
        @JsonIgnore
        public TableCell getTableCellByTableHeader(TableRow headerRow, String header) {
            int columnIndex = headerRow.getColumnIndexByHeader(header);
            return getCellValueInColumn(columnIndex, TableCell.class);
        }

        /**
         * Returns the string at the given column position.
         * Throws IllegalStateException if any cell is not a String.
         * @param columnNumber Position of the column header.
         */
        @JsonIgnore
        public String getStringAtPosition(int columnNumber) {
            return (String) this.cellValues.get(columnNumber);
        }
    }
}
