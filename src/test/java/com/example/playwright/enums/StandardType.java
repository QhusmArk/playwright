package com.example.playwright.enums;

import java.util.*;
import java.util.stream.Collectors;

// todo: only works for C2x
public enum StandardType {

    STD_1A("1A", "(1A) SS 4604866 Spräng 250mm/s 5-300Hz", false),
    STD_1B("1B", "(1B) SS 4604866 Spräng 25mm/s 5-300Hz", false),
    STD_3("3", "(3) SS 25211 Schakt 25mm/s 5-150Hz", false),
    STD_5("5", "(5) SS 25211 Schakt 25mm/s 2-150Hz", false),
    STD_7("7", "(7) SS 4604861 Komfort 20mm/s 1-80Hz", false),
    STD_8("8", "(8) SS 4604861 Komfort 700mm/s² 1-80Hz", false),
    STD_9("9", "(9) DIN 4150-2 KB 20mm/s 1-80Hz", false),
    STD_15("15", "(15) ÖNORM S9012 700mm/s² 1-80Hz", false),
    STD_16A("16A", "(16A) Acceleration 125m/s² 5-300Hz", false),
    STD_16B("16B", "(16B) Acceleration 12.5m/s² 5-300Hz", false),
    STD_17("17", "(17) ISO 10816-2 200mm/s 5-500Hz", false),
    STD_18A("18A", "(18A) DIN4150-3 Anlage 250mm/s 1-315Hz", true),
    STD_18B("18B", "(18B) DIN4150-3 Anlage 25mm/s 1-315Hz", true),
    STD_("20A", "(20A) NS 8141:2001 Byggverk 250mm/s 5-300Hz", false),
    STD_20B("20B", "(20B) NS 8141:2001 Byggverk 25mm/s 5-300Hz", false),
    STD_22("22", "(22) NS 8176 Komfort 20mm/s 1-80Hz", false),
    STD_23A("23A", "(23A) NS 8141:2022 Byggverk 250mm/s 2-400Hz", false),
    STD_23B("23B", "(23B) NS 8141:2022 Byggverk 25mm/s 2-400Hz", false),
    STD_25A("25A", "(25A) NS 8141:2013 Byggverk 250mm/s 3-400Hz", false),
    STD_25B("25B", "(25B) NS 8141:2013 Byggverk 25mm/s 3-400Hz", false),
    STD_27("27", "(27) ISO 2631-2 20mm/s 1-80Hz", false),
    STD_28A("28A", "(28A) SN 640312a 250mm/s 5-150Hz", true),
    STD_28B("28B", "(28B) SN 640312a 25mm/s 5-150Hz", true),
    STD_30A("30A", "(30A) BS 7385 250mm/s 1-300Hz", true),
    STD_30B("30B", "(30B) BS 7385 25mm/s 1-300Hz", true),
    STD_33("33", "(33) ANSI S2.71 0.8 in/s 1-80Hz", false),
    STD_35("35", "(35) AS 2187.2-2006 250mm/s 2-250Hz", false),
    STD_38A("38A", "(38A) ÖNORM S9020 250mm/s 1-315Hz", false),
    STD_38B("38B", "(38B) ÖNORM S9020 25mm/s 1-315Hz", false),
    STD_40("40", "(40) Arrêté du 1994 250mm/s 1-150Hz", true),
    STD_41("41", "(41) ICPE-Circ86 25mm/s 1-150Hz", true),
    STD_42A("42A", "(42A) IN 1226 250mm/s 1-150Hz", true),
    STD_42B("42B", "(42B) IN 1226 25mm/s 1-150Hz", true),
    STD_44("44", "(44) OfM 9/1997 50-117dB 1-80Hz", false),
    STD_45("45", "(45) Turkey Mining and Quarry 250mm/s 2-250Hz", false),
    STD_46A("46A", "(46A) SBR-A:2010 250mm/s 1-100Hz", true),
    STD_46B("46B", "(46B) SBR-A:2010 25mm/s 1-100Hz", true),
    STD_47("47", "(47) SBR-B 20mm/s 1-80Hz", false),
    STD_48("48", "(48) Toronto bylaw 514 250mm/s 2-250Hz", true),
    STD_49("49", "(49) Toronto bylaw 514 250mm/s 1-100Hz", true),
    STD_51A("51A", "(51A) ISEE Seismograph 10 in/s 2-250Hz", true),
    STD_51B("51B", "(51B) ISEE Seismograph 1 in/s 2-250Hz", true),
    STD_53A("53A", "(53A) Geophone 250mm/s 5-500Hz", false),
    STD_53B("53B", "(53B) Geophone 25mm/s 5-500Hz", false),
    STD_55A("55A", "(55A) ISEE/USBM 250mm/s 2-250Hz", true),
    STD_55B("55B", "(55B) ISEE/USBM 25mm/s 2-250Hz", true),
    STD_57A("57A", "(57A) DIN4150-3 Anlage 10 in/s 1-315Hz", false),
    STD_57B("57B", "(57B) DIN4150-3 Anlage 1 in/s 1-315Hz", false),
    STD_58A("58A", "(58A) PN-B-02170 250mm/s 1-100Hz", false),
    STD_58B("58B", "(58B) PN-B-02170 25mm/s 1-100Hz", false),
    STD_59("59", "(59) FTA VdB 50-118 dB 1-80Hz", false),
    STD_60A("60A", "(60A) NCh 3577 250mm/s 1-315Hz", true),
    STD_60B("60B", "(60B) NCh 3577 25mm/s 1-315Hz", true),
    STD_70A("70A", "(70A) BS 6841 125m/s² (VDV)", false),
    STD_70B("70B", "(70B) BS 6841 12.5m/s² (VDV)", false),
    STD_71A("71A", "(71A) BS 7385&6841 250mm/s 1-300Hz", false),
    STD_71B("71B", "(71B) BS 7385&6841 25mm/s 1-300Hz", false),
    STD_72("72", "(72) Metro Vancouver 250mm/s 3-100Hz", true),
    STD_73("73", "(73) NP 2074:2015 250mm/s 2-80Hz", true),
    STD_74A("74A", "(74A) SBR-A:2017 struc. C1 250mm/s 1-100Hz", true),
    STD_74B("74B", "(74B) SBR-A:2017 struc. C2 25mm/s 1-100Hz", true),
    STD_75A("75A", "(75A) SBR-A:2017 adpt. C1 250mm/s 1-100Hz", true),
    STD_75B("75B", "(75B) SBR-A:2017 adpt. C2 25mm/s 1-100Hz", true);

    private final String number;
    private final String fullName;
    private final boolean frequencyWeighting;
    // Map for type lookup
    private static final Map<String, StandardType> numberLookup;
    private static final Map<String, StandardType> fullNameLookup;


    StandardType(String number, String fullName, boolean fw) {
        this.number = number;
        this.fullName = fullName;
        this.frequencyWeighting = fw;
    }


    // Static block for initializing the number lookup map
    static {
        numberLookup = new HashMap<>();
        for (StandardType standardType : StandardType.values()) {
            numberLookup.put(standardType.getNumber(), standardType);
        }
    }

    // Static block for initializing the fullName lookup map
    static {
        fullNameLookup = new HashMap<>();
        for (StandardType standardType : StandardType.values()) {
            fullNameLookup.put(standardType.getFullName(), standardType);
        }
    }

    // Static method to retrieve StandardType by number
    public static StandardType fromNumber(String number) {
        return Optional.ofNullable(numberLookup.get(number))
                .orElseThrow(() -> new IllegalStateException("Unknown number: " + number));
    }

    // Static method to retrieve StandardType by fullName
    public static StandardType fromFullName(String fullName) {
        return Optional.ofNullable(fullNameLookup.get(fullName))
                .orElseThrow(() -> new IllegalStateException("Unknown fullName: " + fullName));
    }

    public static List<StandardType> getFrequencyWeightings() {
        return Arrays.stream(StandardType.values())
                .filter(StandardType::getFrequencyWeighting)
                .collect(Collectors.toList());
    }

    /**
     * @param standard eg '(18A) DIN4150-3 Anlage 250mm/s 1-315Hz'
     * @return true if standard is in the list
     */
    public static boolean hasFrequencyWeighing(String standard) {
        System.out.println(standard);
        standard = standard.substring(standard.indexOf("(") + 1, standard.indexOf(")"));
        List<StandardType> stringList = getFrequencyWeightings();
        return stringList.contains(StandardType.fromNumber(standard));
    }

    public String getNumber() {
        return number;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean getFrequencyWeighting() {
        return frequencyWeighting;
    }


}
