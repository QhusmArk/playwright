package com.example.helpers.hardware;

import com.example.api.endpoints.DeviceApi;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.config.FirmwareProperties;
import com.example.playwright.config.JenkinsProperties;
import com.example.playwright.enums.DeviceType;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class JenkinsDeviceSSHConnector {

    private String USB_PORT;
    private Session session;
    private Channel channel;
    private PrintStream printStream;
    private InputStream inputStream;
    private final DeviceType currentDeviceType;
    private Integer recordingTime = 5000;

    public JenkinsDeviceSSHConnector(DeviceType deviceType) {

        switch (deviceType) {
            case C22 -> USB_PORT = DeviceProperties.getConnectedSerial("C22");
            case C50 -> USB_PORT = DeviceProperties.getConnectedSerial("C50");
            case D10 -> USB_PORT = DeviceProperties.getConnectedSerial("D10");
            case POINT -> USB_PORT = DeviceProperties.getConnectedSerial("POINT");
        }

        this.currentDeviceType = deviceType;

        setupSshAndShell();
        setupPicocom();

        switch (deviceType) {
            case C22 -> this.recordingTime = DeviceApi.getDevice("C22", USB_PORT).getPostTrigTime() * 1000;
            case D10, C50, POINT -> this.recordingTime = 5000;
        }
    }

    private void setupSshAndShell() {
        try {
            JSch jsch = new JSch();

            System.out.println("sshUser: " + JenkinsProperties.getValue("SSH_USER"));
            System.out.println("sshHost: " + JenkinsProperties.getValue("SSH_HOST"));

            this.session = jsch.getSession(JenkinsProperties.getValue("SSH_USER"), JenkinsProperties.getValue("SSH_HOST"), 22);
            session.setPassword(JenkinsProperties.getValue("SSH_PWD"));
            session.setConfig("StrictHostKeyChecking", "no");

            System.out.println("***** SSH *****");
            System.out.println("-> Connecting to server...");
            session.connect();
            System.out.println("-> Connection established");

            this.channel = session.openChannel("shell");
            channel.connect();

            try {
                this.inputStream = channel.getInputStream();
                this.printStream = new PrintStream(channel.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            readChannelOutput(5000, "Welcome to Ubuntu");

        } catch (JSchException e) {
            System.out.println("Access to test_computer denied. Try other connection method, e.g., wifi.");
            throw new RuntimeException(e);
        }
    }

    private void setupPicocom() {
        System.out.println("***** Picocom *****");
        System.out.println("-> Start picocom");
        System.out.println("Device to use = " + USB_PORT);

        sendCommand("sudo picocom -b 115200 --omap delbs --imap lfcrlf /dev/serial/by-id/usb-STMicroelectronics_STM32_Virtual_ComPort_" + USB_PORT + "-if00");
        String outputAfterUSBCheck = readChannelOutput(10000, "[sudo] password");

        if (outputAfterUSBCheck.contains("Resource temporarily unavailable")) {
            throw new IllegalStateException("USB connected device "+USB_PORT+" blocked by other user, or faulty usb cable.");
        }

        System.out.println("-> Enter pwd");
//        System.out.println("DeviceProperties.getUSBLogin(1): " + DeviceProperties.getUSBLogin());
        sendCommand(FirmwareProperties.getValue("USB_PWD"));
        String output1 = readChannelOutput(5000, "Terminal ready");
        sendCommand("\n");
//        System.out.println("output1: " + output1);

        System.out.println("-> Log into device");
        sendCommand("\n");

//        System.out.println("DeviceProperties.getUSBLogin(2): " + DeviceProperties.getUSBLogin());
        sendCommand(FirmwareProperties.getValue("USB_PWD"));
        readChannelOutput(5000, "OK");

        System.out.println("USB_PORT = " + USB_PORT);
        if (USB_PORT.equals("103748")) {
            System.out.println("-> Checking for 'Unknown command' as device is D10.");
            String output2 = readChannelOutput(5000, "Unknown command");
//            System.out.println("output2: " + output2);
        }


    }

    private void sendCommand(String command) {
        printStream.println(command);
        try {
            Thread.sleep(3000); // Wait for command execution
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void createSyntheticTransient(final int channel, final int amplitude, final int frequency) {
        System.out.println("***** Execute synthetic transient *****");
//        Todo: synth.rec borde vara kortare än rec_time
//        todo: ska jag slumpa fram ett värde?
//        todo: ska jag ställa in alla tre kanaler, eller räcker det med en?
        // Trigger behöver vara igång på lämplig nivå.
        // Enbart heltal

        System.out.println("mon set sinushanning 0 "+amplitude+" "+frequency+" 1");
        sendCommand("mon set sinushanning " + channel + " " + amplitude + " " + frequency + " 1");

        // Do not call 'mon rec'
        //todo: skapa 15 likadana transienter borde väl ge samma resultat i regressionsanalysen
        // Typ period på 60 min
        // Då hinner jag köra Test_env o sen Prod_env.
        // Då kan en sökning på den där timmen ge lika många transienter o intervall för bägge miljöer.


    }


    public void createManualTrigger() {
        System.out.println("***** Execute manual transient *****");
        sendCommand("mon rec");

        String output = readChannelOutput(recordingTime + 2000, "Recording started...");  // Recording can be up to 120 seconds
        if (output.contains("Recording started...")) {
            System.out.println("-> Recording started...");
        }
    }

    public void syncConnect() {
        System.out.println("***** Execute manual connect *****");

        String command = switch (currentDeviceType) {
            case C22, C50, POINT -> "sync connect\n";
            case D10 -> "can_master upload\n";
            default -> throw new IllegalStateException("Unexpected value: " + currentDeviceType);
        };

        sendCommand(command);
        String output = readChannelOutput(5000, "OK");

        if (output == null) {
            throw new IllegalStateException("-> No output received");
        }

        if (output.contains("OK")) {
            System.out.println("-> Connection initiated");
        } else {
            throw new IllegalStateException("Expected output 'OK' was not read.");
        }
    }

    public String getDeviceType() {
        System.out.println("***** Checking device type *****");
        sendCommand("*IDN?");
        String[] output = readChannelOutput(5000);
        return output[2];
    }

    public boolean rebootDevice() {
        System.out.println("***** Rebooting device *****");
        sendCommand("system reboot\n");
        String output = readChannelOutput(5000, "OK");
        return output.equals("OK");
    }

    public Integer getSerialNumber() {
        System.out.println("***** Checking serial number *****");
        sendCommand("cp get serial_number");
        String[] output = readChannelOutput(5000);
        System.out.println("output: " + Arrays.toString(output));
        return getSerialFromOutput(output[2]);
    }

    public int getBatteryLevel() {
        System.out.println("***** Checking battery level *****");
        sendCommand("env battery_level");
        String[] output = readChannelOutput(5000);
        System.out.println(Arrays.toString(output));
        return getBatteryLevelFromOutput(output[2]);
    }

    /**
     * @return a device serial. Ie. 100024 from "serial_number: 100024"
     */
    private Integer getSerialFromOutput(String output) {
        String[] parts = output.split(": ");
        return parts.length > 1 ? Integer.parseInt(parts[1].trim()) : null;
    }

    /**
     *
     * @param output Like "Battery level: 60%"
     * @return an int between 0 - 100, depending on battery level.
     */
    private int getBatteryLevelFromOutput(String output) {
        return Integer.parseInt(output.substring(output.indexOf(": ") + 2, output.indexOf("%")));
    }

    public void closeConnections() {
        System.out.println("-> Closing connections\n");

        // As printStream wraps outputStream we only need to close printStream.
        printStream.close();
        channel.disconnect();
        session.disconnect();
    }

    private String readChannelOutput(long timeoutMillis, String endPattern) {
        String segment = null;
        try {
            byte[] buffer = new byte[1024];
            StringBuilder output = new StringBuilder();
            long endTime = System.currentTimeMillis() + timeoutMillis;

            while (System.currentTimeMillis() < endTime) {
                int available = inputStream.available();
                if (available > 0) {
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead == -1) break; // End of stream
                    segment = new String(buffer, 0, bytesRead);
                    output.append(segment);

                    if (segment.contains(endPattern)) {
                        break;
                    }
                }
                Thread.sleep(1000); // Small delay to prevent tight loop
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return segment;
    }

    public String[] readChannelOutput(long timeoutMillis) {

        StringBuilder output = new StringBuilder();
        byte[] buffer = new byte[1024];
        long endTime = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < endTime) {
            while (true) {
                try {
                    if (!(inputStream.available() > 0)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int bytesRead = 0;
                try {
                    bytesRead = inputStream.read(buffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (bytesRead == -1) break; // End of stream
                output.append(new String(buffer, 0, bytesRead));
            }
            try {
                Thread.sleep(100); // Small delay to prevent tight loop
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Split the output into lines
        return output.toString().split("\\r?\\n");
    }

    /**
     * Sends a command 20 times, starting at the next even minute,
     * and repeating every 120 seconds.
     * Require connection up and running.
     * Used to create transients that matches in Test_ and Prod_env.
     *
     */
    public void sendCommandRepeat(int repeat, int seconds) {
//        waitUntilNextEvenMinute();

        for (int i = 0; i <= repeat; i++) {

            List<Integer> amplitudes =List.of(
                    1200, 1300, 1400, 1500, 1600,
                    1200, 1300, 1400, 1500, 1600,
                    1200, 1300, 1400, 1500, 1600,
                    1200, 1300, 1400, 1500, 1600,
                    1200, 1300, 1400, 1500, 1600
                    );

            List<Integer> frequencies =List.of(
                    1, 2, 3, 4, 5,
                    1, 2, 3, 4, 5,
                    1, 2, 3, 4, 5,
                    1, 2, 3, 4, 5,
                    1, 2, 3, 4, 5
                    );

            int channel = 0;
            int amplitude = amplitudes.get(i);
            int frequency = frequencies.get(i);

            System.out.println(i + ": Sending command at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            createSyntheticTransient(channel, amplitude, frequency);

            if (i < 20) {
                try {
                    Thread.sleep(Duration.ofSeconds(seconds).toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Sleep interrupted. Stopping execution.");
                    break;
                }
            }
        }
    }

    /**
     * Waits until the start of the next even minute (e.g., 12:00, 12:02, etc.).
     */
    private void waitUntilNextEvenMinute() {
        ZonedDateTime now = ZonedDateTime.now();
        int currentMinute = now.getMinute();
        int minutesToAdd = (currentMinute % 2 == 0) ? 2 : 1;

        ZonedDateTime nextEvenMinute = now
                .withSecond(0)
                .withNano(0)
                .plusMinutes(minutesToAdd);

        Duration waitDuration = Duration.between(ZonedDateTime.now(), nextEvenMinute);

        try {
            Thread.sleep(waitDuration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Initial wait interrupted. Command will start immediately.");
        }
    }
}
