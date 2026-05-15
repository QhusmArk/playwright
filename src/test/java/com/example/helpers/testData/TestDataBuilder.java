package com.example.helpers.testData;

import com.example.api.endpoints.*;
import com.example.api.glue.ApiChangeGlue;
import com.example.api.models.ScheduledReport;
import com.example.api.models.agenda.Agenda;
import com.example.api.models.agenda.Definition;
import com.example.api.models.blast.Blast;
import com.example.api.models.comment.Comment;
import com.example.api.models.device.Change;
import com.example.api.models.device.Channel;
import com.example.api.models.device.Device;
import com.example.api.models.device.Vib;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.message.ContextSelection;
import com.example.api.models.message.MessageRule;
import com.example.api.models.message.NotificationMpValue;
import com.example.api.models.project.Project;
import com.example.api.models.report.DataReport;
import com.example.api.models.report.Search;
import com.example.api.models.user.User;
import com.example.helpers.JsonUtil;
import com.example.helpers.Randomizer;
import com.example.helpers.TimeConverter;
import com.example.helpers.builders.*;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.config.TestDataReader;
import com.example.playwright.config.TestEnvironment;
import com.example.playwright.hooks.ScenarioContext;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.helpers.builders.BuilderFactory.Providers.MESSAGE_RULE;
import static com.example.helpers.builders.NotificationMpValueBuilder.TriggType.ABSOLUTE;
import static com.example.helpers.builders.NotificationMpValueBuilder.TriggType.TRIGGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDataBuilder {

    private final String directory;
    private List<String> files;
    private final Context context;

    /**
     * This variable is used in almost all methods and is therefore allowed to be defined here.
     * All other variables are only visible within the methods.
     */
    private int projectId;

    public TestDataBuilder(final Context context, final String directory) {
//        RequestService.setUp();
        this.directory = directory + "/";
        this.context = context;
    }

    public TestDataBuilder addTestData(String testDataFilePath) {
        if (files == null) {
            files = new ArrayList<>();
        }

        // Before we add the file, see if it exists.
        try {
            new TestDataReader("src/test/resources/testdata/" + directory + testDataFilePath + ".properties");
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("File '" + testDataFilePath + ".properties' not found.");
        }

        files.add(testDataFilePath);
        return this;
    }

    public void build() {
        // Check if any string in the list does not contain '-'
        if (files.stream()
                .anyMatch(file -> !file.contains("-"))) {
                    throw new IllegalArgumentException("All testdata file names must contain '-'.");
        }

        files.forEach(this::buildAll);

        context.storeMeasuringPointDevices();
        validateContext();
    }

    /**
     * Sometimes an API request for creation returns a "non-workable" status code.
     * Assert basic requirements are met before entering browser stage, to avoid noisy Selenium failures.
     */
    private void validateContext() {

        // Remove file identifier (e.g., '-1' from 'agenda-1'
        List<String> fileProviders = files.stream()
                .map(file -> file.split("-")[0])
                .toList();

        // Just validate one of each provider
        Set<String> uniqueFileProviders = new HashSet<>(fileProviders);
        uniqueFileProviders.forEach(uniqueFileProvider -> {

            if (uniqueFileProvider.equals("change")) {
                // Do nothing for change
            } else {
                // Go through fileProviders and count e.g., how many filenames is called 'user'
                int filesWithThisProvider = (int) fileProviders.stream()
                        .filter(uniqueFileProvider::equals)
                        .count();

                int contextHasThisManyOfThisProvider = switch (uniqueFileProvider) {
                    case "project" -> 1;
                    case "agenda" -> sizeOrZero(context.getAgendas());
                    case "blast" -> sizeOrZero(context.getBlasts());
                    case "comment" -> sizeOrZero(context.getComments());
                    case "search" -> sizeOrZero(context.getSearches());
                    case "measuringpoint" -> sizeOrZero(context.getMeasuringPoints());
                    case "messagerule" -> sizeOrZero(context.getMessageRules());
                    case "user" -> sizeOrZero(context.getUsers());
                    case "scheduledreport" -> sizeOrZero(context.getScheduledReports());
                    default -> {
                        System.out.println("context:");
                        JsonUtil.jsonToPrint(context);
                        throw new IllegalArgumentException("Unknown uniqueFileProvider " + uniqueFileProvider);
                    }
                };

                // Validate that each file created objects and put them in context
                assertEquals(filesWithThisProvider, contextHasThisManyOfThisProvider,
                        () -> "Context mismatch for " + uniqueFileProvider + ": expected=" + filesWithThisProvider + ", actual=" + contextHasThisManyOfThisProvider);
            }
        });
    }

    /** Null-safe collection size. */
    private int sizeOrZero(List<?> list) {
        return (list != null)
                ? list.size()
                : 0;
    }

    private void buildAll(final String testDataFilePath) {
        TestDataReader prop = new TestDataReader("src/test/resources/testdata/" + directory + testDataFilePath + ".properties");

        switch (testDataFilePath.split("-")[0]) { // Splitting based on "-" to identify the prefix
            case "agenda" -> buildAgenda(prop);
            case "blast" -> buildBlast(prop);
            case "comment" -> buildComment(prop);
            case "search" -> buildSearch(prop);
            case "measuringpoint" -> buildMeasuringPoint(prop);
            case "messagerule" -> buildMessageRule(prop);
            case "project" -> buildProject(prop);
            case "user", "userinput" -> buildUser(prop);
            case "scheduledreport" -> buildScheduledReport(prop);
            case "change" -> buildChangeAndPossiblyCommit(prop);
            case "datareport" -> throw new IllegalArgumentException("'Datareport' is deprecated.");
            default -> throw new IllegalArgumentException("Unknown propertyFile: " + testDataFilePath);
        }
    }

    private void buildChangeAndPossiblyCommit(TestDataReader prop) {
        String type = prop.getString("type");

        switch (prop.getString("type")) {
            case "C22" ->  c22BuildChangeAndPossiblyCommit(prop);
            case "D10" ->  d10BuildChangeAndPossiblyCommit(prop);
            default -> throw new IllegalStateException(type + " is not a suitable type for change.");
        }
    }

    private void d10BuildChangeAndPossiblyCommit(TestDataReader prop) {
        String type = "D10";
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial(type));

        Device logger = DeviceApi.getDevice(type, serial);

        List<com.example.api.models.device.Sensor> channelSensors = new ArrayList<>();
        int sensorsInChange = prop.getInteger("sensors");
        for (int s = 1; s <= sensorsInChange; s++) {

            String sensorType = prop.getString("sensor-"+s+"-serial-numbers");
            int sensorSerial = Integer.parseInt(DeviceProperties.getConnectedSerial(sensorType));

            int channelsInSensor = prop.getInteger("sensor-"+s+"-channels");
            List<Channel> sensorChannels = new ArrayList<>();
            for (int i = 1; i <= channelsInSensor; i++) {
                String channelName = prop.getString("sensor-"+s+"-channel-"+i+"-name");
                Double channelValue = prop.getDouble("sensor-"+s+"-channel-"+i+"-value");
                Boolean channelState = prop.getBoolean("sensor-"+s+"-channel-"+i+"-state");

                Channel channel = Channel.builder()
                        .name(channelName)
                        .maxThresholdValue((channelValue))
                        .maxThresholdEnable(channelState)
                        .build();
                sensorChannels.add(channel);
            }

            com.example.api.models.device.Sensor sensor = com.example.api.models.device.Sensor.builder()
                    .serial(sensorSerial)
                    .channels(sensorChannels)
                    .build();
            channelSensors.add(sensor);
        }

        Change change = Change.builder()
                .hash(logger.getHash())
                .sensorsHash(logger.getSensorsHash())
                .sensors(channelSensors)
                .build();

        DeviceApi.updateChange("D10", serial, change.buildJson());

        if (prop.hasKey("commit") && (prop.getBoolean("commit"))) {
            DeviceApi.commitChange(type, serial, "{ \"action\":\"commit\" }");
        }

    }

    private void c22BuildChangeAndPossiblyCommit(TestDataReader prop) {
        // First make sure the device is not occupied with un/committed change
        ApiChangeGlue.clearChangeFromC22();

        String type = "C22";
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial(type + "-1"));

        String configID = DeviceApi.getDevice(type, serial).getConfigId();

        int channelCount = prop.getInteger("channels");

        List<Channel> channels = new ArrayList<>();
        for (int i = 1; i <= channelCount; i++) {
            String channelName = prop.getString("channel-"+i+"-name");
            Double channelValue = prop.getDouble("channel-"+i+"-value");
            Boolean channelState = prop.getBoolean("channel-"+i+"-state");

            Channel channel = Channel.builder()
                    .name(channelName)
                    .triggerValue(channelValue)
                    .triggerEnable(channelState)
                    .build();
            channels.add(channel);
        }

        Vib vib = Vib.builder()
            .channels(channels)
            .standard(prop.getString("standard"))
            .interval(prop.getInteger("interval_time"))
            .build();

        if (prop.hasKey("frequency_weighting")
                && (!prop.getString("frequency_weighting").equals("null"))) {
            vib.setFrequencyWeighting(prop.getString("frequency_weighting"));
        }

        Change change = Change.builder()
                .configId(configID)
                .vib(vib)
                .build();

        DeviceApi.updateChange(type, serial, change.buildJson());

        if (prop.hasKey("commit")
                && (prop.getBoolean("commit"))) {
            DeviceApi.commitChange(type, serial, "{ \"action\":\"commit\" }");
        }

        if (prop.hasKey("connect")
                && (prop.getBoolean("connect"))) {
            // Get the device so connect to INFRA
            ApiChangeGlue.connect("C22");
            // Start polling to see change consumed and implemented
            ApiChangeGlue.validateCreatorChangeIsConsumed(configID);
        }
    }

    private void buildAgenda(final TestDataReader prop) {
        AgendaBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.AGENDA,
                AgendaBuilder.class);

        builder
                .withName(prop.getString("name"));

        long timeslotCount = prop.getKeys().stream()
                .filter(i -> i.startsWith("label-"))
                .count();

        // Add timeslots if there are any to be added
        if (timeslotCount != 0) {

            for (int i = 1; i < (timeslotCount + 1); i++) {
                String label = prop.getString("label-" + i);
                List<Integer> weekday = prop.getIntArray("weekday-" + i, ",");
                int start = prop.getInteger("start-" + i);
                int duration = prop.getInteger("duration-" + i);

                builder
                        .givenLabel(label)
                        .thenWeekday(weekday.stream().mapToInt(j -> j).toArray())
                        .thenStartHour(start)
                        .thenDuration(duration);
            }

        } else {
            builder.givenLabel();
            builder.withDefinitions();
        }

        builder.build();

        Agenda agenda = AgendaApi.createAgenda(projectId, builder.buildJson());
        context.addAgenda(agenda);
    }

    private void buildBlast(final TestDataReader prop)  {
        if (context.getProject().getBlastStandard() == null) {
            throw new IllegalStateException("Cannot add blast to a project without blast standard.");
        }

        BlastBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.BLAST,
            BlastBuilder.class);

        builder
                .withBlastName(prop.getString("name"))
                    .withSupervisor(prop.getInteger("supervisor"))
                    .withBlastType(prop.getString("type"))
                    .withDatetime(TimeConverter.convertToStandardizedDateTime(prop.getString("time")))
                    .withTimeSpan(prop.getInteger("timespan"))
                    .withMaxInstantaneousCharge(prop.getDouble("mic"))
                .givenLocation()
                    .thenDescription("")
                    .thenWsg84Lat(prop.getDouble("latitude"))
                    .thenWsg84Lng(prop.getDouble("longitude"))
                    .thenWsg84Elevation(prop.getDouble("altitude"));

        builder.build();

        Blast blast = BlastApi.createBlast(projectId, builder.buildJson());
        context.addBlast(blast);
    }

    private void buildComment(final TestDataReader prop) {
        CommentBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.COMMENT,
            CommentBuilder.class);

        User user = UserApi.getCurrentUser();
        builder
                .withUserId(user.getId())
                .withComment(prop.getString("comment"));

        builder.build();

        Comment comment = CommentApi.createComment(projectId, builder.buildJson());
        context.addComment(comment);
    }

    private void buildSearch(final TestDataReader prop) {
        SearchBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.SEARCH,
                SearchBuilder.class);

        builder
                .withDateTimeFrom(TimeConverter.convertToStandardizedDateTime(prop.getString("start-time")))
                .withDateTimeTo(TimeConverter.convertToStandardizedDateTime(prop.getString("stop-time")));

        if (prop.hasKey("aggregator")) {
            builder.withAggregator(prop.getInteger("aggregator"));
        }

        if (prop.hasKey("tag")) {
            builder.addTag(prop.getString("tag"));
        }

        addMeasuringPointsToSearchBuilder(prop, builder);

        addDataTypesToSearchBuilder(prop, builder);

        if (prop.hasKey("name")) {
            builder.withName(prop.getString("name"));
        }

        if (prop.hasKey("shared")) {
            builder.thenShared(prop.getBoolean("shared"));
        }

        builder.build();

        // Sometimes we do not want to wait for search to be finished
        Search search = (prop.hasKey("dont_wait_for_search_to_be_finished"))
                ? SearchApi.createSearch(projectId, builder.buildJson(), false)
                : SearchApi.createSearch(projectId, builder.buildJson(), true);

        context.addSearch(search);

        // In the 'old' days the finished Search was used to fetch the full DataReport (using buildDataReport(prop), and add it to context.
        // We don't do that anymore, as the time consuming part was waiting for POST /search to finish.
        // If DataReport is needed we fetch it from the api.

        // This is a test too see if context is as stable as ReportApi, and if the comment above is not valid.
        if (!prop.hasKey("dont_wait_for_search_to_be_finished")) {
            DataReport report = ReportApi.getData(projectId, search.getId());
            context.addReport(report);
        }
    }

    private void addDataTypesToSearchBuilder(final TestDataReader prop, SearchBuilder builder) {
        if (prop.hasKey("interval_type") || prop.hasKey("transient_type") || prop.hasKey("blast_type") || prop.hasKey("monon_type")) {
            builder.givenDataTypes();

            if (prop.hasKey("interval_type")) {
                builder.thenDataTypeInterval(prop.getBoolean("interval_type"));
            }
            if (prop.hasKey("transient_type")) {
                builder.thenDataTypeTransient(prop.getBoolean("transient_type"));
            }
            if (prop.hasKey("blast_type")) {
                builder.thenDataTypeBlast(prop.getBoolean("blast_type"));
            }
            if (prop.hasKey("monon_type")) {
                builder.thenDataTypeMonon(prop.getBoolean("monon_type"));
            }
        }
    }

    private void addMeasuringPointsToSearchBuilder(final TestDataReader prop, SearchBuilder builder) {
        // Fetch all the created measuring points
        List<MeasuringPoint> measuringPoints = context.getMeasuringPoints();
        if (measuringPoints.isEmpty()) {
            throw new IllegalStateException("A Search requires at least one Measuring Point");
        } else {
            builder.givenMeasurePoint();
        }

        if (prop.hasKey("create_for")) {
            List<String> mpNamesForSearch = prop.getStringArray("create_for", ",");

            // Only named Mps are to be added to builder
            measuringPoints.stream()
                    .filter(mp -> mpNamesForSearch.contains(mp.getName()))
                    .map(MeasuringPoint::getId)
                    .forEach(builder::thenMeasuringPointId);
        } else {

            // Add all measuring point IDs directly to the builder
            measuringPoints.stream()
                    .map(MeasuringPoint::getId)
                    .forEach(builder::thenMeasuringPointId);
        }
    }

    private void buildScheduledReport(TestDataReader prop) {
        ScheduledReportBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.SCHEDULED_REPORT,
                ScheduledReportBuilder.class);

        builder
                .withName(prop.getString("name"))
                .addOutputOption(prop.getString("outputOptions"))
                .withReportType(new ArrayList<>(List.of("interval", "transient")))        // todo: to be deprecated?
                .withFileFormat(prop.getString("fileFormat"))
                .withFileType(prop.getString("fileType"))     // values, charts, multi charts
                .withRecurringType(prop.getString("recurringType"))
                .withSendOn(prop.getString("sendOn"))       // Every day, Weekdays only, Monday(etc)
                .withRecurringTime(prop.getString("recurringTime")) // eg 06:00
                .withDisabled(prop.getBoolean("disabled"));

        int projectId = context.getProject().getId();

        // Add the mp's in the properties-file, to the SDR
        List<String> mpNames = prop.getStringArray("measurePointNames", ",");
        mpNames.forEach(mpName -> {

            List<MeasuringPoint> mps = MeasuringPointApi.getMeasuringPoints(projectId);

            int mpId = mps.stream()
                    .filter(mp -> mp.getName().contains(mpName))
                    .findFirst()
                    .map(MeasuringPoint::getId)
                    .orElseThrow(
                            () -> new IllegalStateException("No matching measuring point found"));

            builder.addMeasurePoint(mpId);
        });

        // Add the user-recipients in the properties-file
        List<String> recipientEmails = prop.getStringArray("recipientEmails", ",");
        recipientEmails.forEach(email -> {
            Integer userId = UserApi.getUserByMail(email).getId();
            builder
                    .addUserRecipient(userId);
        });

        builder.build();

        ScheduledReport sdr = ReportApi.createSDR(projectId, builder.buildJson());

        context.addScheduledReport(sdr);
    }

    private void buildMeasuringPoint(final TestDataReader prop) {
        MeasuringPointBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.MP,
            MeasuringPointBuilder.class);

        // Below code is fix for problem de/serializing Integer and int.
        // price can be null, 0 or >0, but prod.get("price") cannot be cast to Integer.
        Integer price = (prop.hasKey("price"))
                ? prop.getInteger("price")
                : null;

        Integer dBCorr =  (prop.hasKey("dBCorr"))
                ? prop.getInteger("dBCorr")
                : null;

        // To be able to create replicate mp's for SDR-tests we add some chars in the mp-name.
        String name = (prop.getBoolean("sdr"))
                ? prop.getString("name") + "-" + Randomizer.randomString(4)
                : prop.getString("name");

        builder
                .withName(name)
                .withIsActive(prop.getBoolean("active"))
                .withTimezone(prop.getString("timezone"))
                .withTimeFrom(TimeConverter.convertToStandardizedDateTime(prop.getString("start-time")))
                .withTimeTo(TimeConverter.convertToStandardizedDateTime(prop.getString("stop-time")))
                .withSensorType(prop.getString("type"))
                .withPrice(price)
                .withProperties(dBCorr)
                .givenLocation(prop.getString("location-name"))
                .thenWsg84Lat(prop.getDouble("latitude"))
                .thenWsg84Lng(prop.getDouble("longitude"))
                .thenWsg84Elevation(prop.getDouble("altitude"));

        if (prop.hasKey("serial-numbers")) {
            addConnectedDevicesToMeasuringPointBuilder(builder, prop);
        }

        builder.build();

        MeasuringPoint measuringPoint = MeasuringPointApi.createMeasuringPoint(projectId, builder.buildJson());
        int measuringPointId = measuringPoint.getId();

        if (prop.hasKey("blast-properties")) {
            measuringPoint = addBlastPropertiesToMeasuringPoint(builder, measuringPointId, prop);
        }

        // Add Lcustom agenda
        if (prop.hasKey("add-agenda")) {
            measuringPoint = addLcustomAgendaToMeasuringPoint(builder, measuringPointId, prop);
        }

        // Add noise report agenda
        if (prop.hasKey("add-noise")) {
            measuringPoint = addNoiseAgendaToMeasuringPoint(builder, measuringPointId, prop);
        }

        // Add vibration report agenda
        if (prop.hasKey("add-vibration")) {
            measuringPoint = addVibrationAgendaToMeasuringPoint(builder, measuringPointId, prop);
        }

        context.addMeasuringPoint(measuringPoint);

//        // Add mp-sensors to ease access
        context.storeMeasuringPointDevice(measuringPoint);
    }

    private void addConnectedDevicesToMeasuringPointBuilder(MeasuringPointBuilder builder, TestDataReader prop) {
        List<String> sensorStartTime = prop.getStringArray("sensor-start-time", ",");
        List<String> sensorStopTime = prop.getStringArray("sensor-stop-time", ",");

        // Get the serial number from the property file with sensor type
        List<String> serialNumbers = prop.getStringArray("serial-numbers", ",").stream()
                .map(DeviceProperties::getConnectedSerial)
                .toList();

        for (int i = 0; i < serialNumbers.size(); i++) {
            String start = TimeConverter.convertToStandardizedDateTime(sensorStartTime.get(i));
            String stop = TimeConverter.convertToStandardizedDateTime(sensorStopTime.get(i));

            builder
                    .addSensor()
                    .thenSensorSerial(serialNumbers.get(i))
                    .thenSensorTimeFrom(start)
                    .thenSensorTimeTo(stop);
        }
    }

    private MeasuringPoint addBlastPropertiesToMeasuringPoint(MeasuringPointBuilder builder, int measuringPointId, TestDataReader prop) {
            builder.givenBlastProperties();

            Optional.ofNullable(prop.getInteger("blast-alert"))
                    .ifPresent(builder::thenBlastPropertiesAlert);

            Optional.ofNullable(prop.getInteger("blast-alarm"))
                    .ifPresent(builder::thenBlastPropertiesAlarm);

            Optional.ofNullable(prop.getString("blast-maxvalue"))
                    .ifPresent(builder::thenBlastPropertiesMaxValues);

            Optional.ofNullable(prop.getDouble("blast-guide"))
                    .ifPresent(builder::thenBlastPropertiesGuideValue);

            Optional.ofNullable(prop.getInteger("blast-uncorrected-frequency"))
                    .ifPresent(builder::thenBlastPropertiesUncorrectedFrequency);

            Optional.ofNullable(prop.getBoolean("blast-distant-dependent"))
                    .ifPresent(builder::thenBlastPropertiesDistanceDependent);

            Optional.ofNullable(prop.getInteger("blast-static-distance"))
                    .ifPresent(builder::thenBlastPropertiesStaticDistance);

            builder.build();

            return MeasuringPointApi.updateMeasuringPoint(projectId, measuringPointId, builder.buildJson());

    }

    private MeasuringPoint addLcustomAgendaToMeasuringPoint(MeasuringPointBuilder builder, int measuringPointId, TestDataReader prop) {
            Agenda agenda = context.getLastAgenda();
            List<Definition> definitions = agenda.getDefinitions();

            int positions = definitions.size() + 1;

            builder
                    .givenCustomAgendaSettings()
                    .withCustomAgendaSettingsType("accumulative")
                    .withCustomAgendaSettingsId(String.valueOf(agenda.getId()));

            for (int i = 0; i < positions; i++) {
                builder.addSetting(5, 6, 7);
            }

            builder.build();

            return MeasuringPointApi.updateMeasuringPoint(projectId, measuringPointId, builder.buildJson());
    }

    private MeasuringPoint addNoiseAgendaToMeasuringPoint(MeasuringPointBuilder builder, int measuringPointId, TestDataReader prop) {
            Agenda agenda = context.getLastAgenda();

            int labels = agenda.getLabels().size() - 1;

            builder
                    .givenNoiseReportSettings()
                    .withNoiseReportAgendaId(String.valueOf(agenda.getId()));

            // Put same value to all noise report timeslots
            for (int i = 0; i < labels; i++) {
                builder.addNoiseReportSetting(55);
            }

            builder.build();

            return MeasuringPointApi.updateMeasuringPoint(projectId, measuringPointId, builder.buildJson());
    }


    private MeasuringPoint addVibrationAgendaToMeasuringPoint(MeasuringPointBuilder builder, int measuringPointId, TestDataReader prop) {
            Agenda agenda = context.getLastAgenda();

            int timeslots = agenda.getLabels().size() - 1;

            builder.givenVibrationAgendaSettings()
                    .withVibrationAgendaSettingsId(String.valueOf(agenda.getId()));

            // Put same value to all vibration report timeslots
            int value = prop.getInteger("vibration-report-settings-threshold");
            for (int i = 0; i < timeslots; i++) {
                builder.addVibrationReportSetting(value);
            }

            builder.build();

            return MeasuringPointApi.updateMeasuringPoint(projectId, measuringPointId, builder.buildJson());
    }

    /**
     * MessageRule aka Notification
     */
    private void buildMessageRule(final TestDataReader prop) {
        // todo: byt ut alla mr-absolut med typ 0.005 mm/s till trigger när vi har stöd för syntetiska transienter

        MessageRuleBuilder builder = BuilderFactory.getBuilder(
                MESSAGE_RULE,
                MessageRuleBuilder.class);

        // If recipient is absent use current user (most often an Admin)
        String recipient = prop.hasKey("recipient")
                ? prop.getString("recipient")
                : String.valueOf(UserApi.getCurrentUser().getId());

        // If recipient is "none", then no need to continue with recipient
        if (!"none".equals(recipient)) {
            if (!prop.hasKey("recipient")) {
                builder.addUserId(recipient);

            } else {
                User user = UserApi.getUsers().stream()
                        .filter(u -> u.getEmail().equals(recipient))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Wanted recipient could not be found."));

                builder.addUserId(String.valueOf(user.getId()));
            }
        }

        ContextSelection contextSelection = new ContextSelection()
                .thenContextCompanyName(prop.getBoolean("context-company-name"))
                .thenContextProjectName(prop.getBoolean("context-project-name"))
                .thenContextMessageName(prop.getBoolean("context-message-name"))
                .thenContextMeasuringPointName(prop.getBoolean("context-measuringpoint-name"))
                .thenContextMeasuringPointDescription(prop.getBoolean("context-measuringpoint-description"))
                .thenContextSensorName(prop.getBoolean("context-sensorname"))
                .thenContextSensorSerial(prop.getBoolean("context-sensorserial"))
                .thenContextValue(prop.getBoolean("context-value"))
                .thenContextDate(prop.getBoolean("context-date"))
                .thenContextTime(prop.getBoolean("context-time"))
                .thenContextChannel(prop.getBoolean("context-channel"))
                .thenContextStandard(prop.getBoolean("context-standard"));

        builder.withContextSelection(contextSelection);

        builder
                .withName(prop.getString("name"))
                .withActive(prop.getBoolean("active"))
                .withWeekdays(prop.getString("weekdays"))
                .withHours(prop.getString("hours"))
                .withSmsPerHour(prop.getInteger("sms"))
                .withWaitTime(prop.getInteger("waittime"))
                .withPreTime(prop.getInteger("pretime"))
                .withTrigTypes(prop.getString("trigtypes"))
                .withBlastManager(prop.getBoolean("blastmanager"))
                .withStyleSheetId(prop.getInteger("stylesheet-id"))
                .withDateTimeFrom(prop.getString("start-time"))
                .withDateTimeTo(prop.getString("stop-time"));

        builder.build();

        MessageRule messageRule = MessageRuleApi.createMessageRule(projectId, builder.buildJson());

        context.addMessageRule(messageRule);

        // Support for connecting multiple mp to a notification_mp_value, ie to a message_rule
        if (prop.hasKey("connect-to-mp")) {
            List<MeasuringPoint> measuringPoints = context.getMeasuringPoints();

            List<String> mpsToConnectTo = prop.splitByComma(prop.getString("connect-to-mp"));

            mpsToConnectTo.forEach(mp -> {
                MeasuringPoint measuringPoint = measuringPoints.stream()
                        .filter(i -> i.getName().startsWith(mp))
                        .findAny()
                        .orElseThrow(
                                () -> new IllegalStateException("No mp found that start with: " + prop.getString("connect-to-mp")));

                // First POST is to create MR, below call is to POST connection between MR and Notification
                createNotificationMpValue(prop, measuringPoint.getId(), messageRule.getId(), prop.getString("connect-trigger"));
            });
        }
    }

    private void createNotificationMpValue(final TestDataReader prop, final int measuringPointId,
                                           int messageRuleId, final String type) {
        NotificationMpValueBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.NOTIFICATION_MP_VALUE,
                NotificationMpValueBuilder.class);

        if (type.equals("absolute")) {
            builder.givenTrigType(ABSOLUTE);

            builder.withMeasurePointId(measuringPointId);

            Optional.ofNullable(prop.getDouble("lmaxChannel"))
                    .ifPresent(builder::thenLmax);

            Optional.ofNullable(prop.getDouble("leqChannel"))
                    .ifPresent(builder::thenLeq);

            Optional.ofNullable(prop.getDouble("lasChannel"))
                    .ifPresent(builder::thenLas);

            Optional.ofNullable(prop.getDouble("lafChannel"))
                    .ifPresent(builder::thenLaf);

            Optional.ofNullable(prop.getDouble("laeqChannel"))
                    .ifPresent(builder::thenLaeq);

            Optional.ofNullable(prop.getDouble("laeqRollingChannel"))
                    .ifPresent(builder::thenLaeqRolling);

            Optional.ofNullable(prop.getDouble("laeqAccuChannel"))
                    .ifPresent(builder::thenLaeqAccu);

            Optional.ofNullable(prop.getDouble("l90Channel"))
                    .ifPresent(builder::thenL90);

            Optional.ofNullable(prop.getDouble("l50Channel"))
                    .ifPresent(builder::thenL50);

            Optional.ofNullable(prop.getDouble("l10Channel"))
                    .ifPresent(builder::thenL10);

            Optional.ofNullable(prop.getDouble("lnChannel"))
                    .ifPresent(builder::thenLn);

            Optional.ofNullable(prop.getDouble("vChannel"))
                    .ifPresent(builder::thenV);

            Optional.ofNullable(prop.getDouble("lChannel"))
                    .ifPresent(builder::thenL);

            Optional.ofNullable(prop.getDouble("tChannel"))
                    .ifPresent(builder::thenT);

            Optional.ofNullable(prop.getDouble("rChannel"))
                    .ifPresent(builder::thenR);

            Optional.ofNullable(prop.getDouble("rVChannel"))
                    .ifPresent(builder::thenRv);

            Optional.ofNullable(prop.getDouble("rLChannel"))
                    .ifPresent(builder::thenRl);

            Optional.ofNullable(prop.getDouble("rTChannel"))
                    .ifPresent(builder::thenRt);

            Optional.ofNullable(prop.getDouble("vdvVChannel"))
                    .ifPresent(builder::thenVdvV);

            Optional.ofNullable(prop.getDouble("vdvLChannel"))
                    .ifPresent(builder::thenVdvL);

            Optional.ofNullable(prop.getDouble("vdvTChannel"))
                    .ifPresent(builder::thenVdvT);

            Optional.ofNullable(prop.getDouble("vdvVaccuChannel"))
                    .ifPresent(builder::thenVdvVaccu);

            Optional.ofNullable(prop.getDouble("vdvLaccuChannel"))
                    .ifPresent(builder::thenVdvLaccu);

            Optional.ofNullable(prop.getDouble("vdvTaccuChannel"))
                    .ifPresent(builder::thenVdvTaccu);
        }

        if (type.equals("trigger")) {
            builder
                    .withMeasurePointId(measuringPointId)
                    .givenTrigType(TRIGGER);
        }

        // Add the hardcoded attributes
        builder
                .withAbsolute(0)
                .withPercent(0);

        builder.build();

        NotificationMpValue notificationMpValue = NotificationMpValueApi.createNotificationMpValue(
                projectId, messageRuleId, builder.buildJson());

        context.addNotificationMpValue(notificationMpValue);
    }

    private void buildProject(final TestDataReader prop) {
        ProjectBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.PROJECT,
            ProjectBuilder.class);

        // If @deleteProject did not work there might be residual projects w same name.
        // These project/s need to be deleted so that test don't break for this reason.
//        String newName = prop.getString("name");
//
//        String projectID = prop.hasKey("project_ID")
//                ? prop.getString("project_ID")
//                : Randomizer.randomString(6);
//
//        String description = prop.hasKey("description")
//                ? prop.getString("description") +
//                    ((ScenarioContext.getScenarioName() != null)
//                        ? ", " + ScenarioContext.getScenarioName()
//                        : "")
//                : "Scn: " + ScenarioContext.getScenarioName();

        String randomString = prop.hasKey("project_ID")
                ? prop.getString("project_ID")
                : Randomizer.randomString(6);

        String newName = prop.getString("name") + "-" + randomString;

//        String description = prop.hasKey("description")
//                ? prop.getString("description") +
//                ((ScenarioContext.getScenarioName() != null)
//                        ? ", " + ScenarioContext.getScenarioName()
//                        : "")
//                : "Scn: " + ScenarioContext.getScenarioName();

        long unixMillis = TimeConverter.convertToUnixTimeMillis(LocalDateTime.now());
        String createdAtUnixMillis = Long.toString(unixMillis);

        builder
                .givenName(newName)
                .thenProjectId(createdAtUnixMillis)
                .withDescription("Scn: " + ScenarioContext.getScenarioName())
                .withTimezone(prop.getString("timezone"))
                .withTimeFrom(TimeConverter.convertToStandardizedDateTime(prop.getString("start-time")))
                .withTimeTo(TimeConverter.convertToStandardizedDateTime(prop.getString("stop-time")))
                .withIsActive(prop.getBoolean("active"));

        if (prop.hasKey("latitude") && prop.hasKey("longitude")) {
            builder
                    .givenLocation()
                    .thenWsg84Lat(prop.getDouble("latitude"))
                    .thenWsg84Lng(prop.getDouble("longitude"));
        }

        if (prop.hasKey("blast-standard")) {
            builder.withBlastStandard(prop.getString("blast-standard"));
        }

        if (prop.hasKey("default_price")) {
            builder.withDefaultPrice(prop.getInteger("default_price"));
        }

        builder.build();

        Project project = ProjectApi.createProject(builder.buildJson());
        System.out.println("*** Created Project: " + project.getId() + " [" + TestEnvironment.getWebUrl() + "] ***");

        context.setProject(project);
        projectId = project.getId();
    }

    // the user already exist
    private void buildUser(final TestDataReader prop) {
        if (prop.hasKey("preexistinguser") && prop.getBoolean("preexistinguser")) {
            System.out.println("Pre existing user. Only add to Project.");

            User user = UserApi.getUserByMail(prop.getString("mail"));
            context.addUser(user);

            addUserToProject(user);

        } else {    // todo: This might be non working, as a temporary user must be deleted when project is deleted.
            System.out.println("Non existing user. Only create to Project.");

            UserBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.USER,
                UserBuilder.class);

            builder.withFirstName(prop.getString("fname"))
                    .withLastName(prop.getString("lname"))
                    .withEmail(prop.getString("email"))
                    .withMobilePhone(prop.getString("phone"))
                    .withLanguage(prop.getString("language"))
                    .withRoleId(prop.getInteger("role"))
                    .withUserRole(prop.getString("role"))
                    .withIsActive(prop.getBoolean("isActive"));

            builder.build();

            User user = UserApi.createUser(builder.buildJson());
            context.addUser(user);
        }
    }

    private void addUserToProject(User user) {
        UserBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.USER,
            UserBuilder.class);
        builder.setProvider(user);
        builder.addProjectToUser(projectId);

        builder.build();

        UserApi.addProjectToUser(user.getId(), builder.buildJson());
    }
}
