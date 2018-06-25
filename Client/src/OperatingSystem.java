public enum OperatingSystem {
    WINDOWS, MAC, LINUX;

    public static String getEncoderPath(OperatingSystem operatingSystem) {
        if (operatingSystem.equals(WINDOWS))
            return "cmd /c start cmd.exe /K .\\Resources\\Windows\\ffmpeg_windows\\bin\\ffmpeg.exe";
        else if (operatingSystem.equals(MAC))
            return "sh -c ./Resources/Mac/ffmpeg_mac/ffmpeg";
        else if (operatingSystem.equals(LINUX))
            return ".\\Resources\\Linux\\";
        else return "This operating system is not supported.";
    }

    public static OperatingSystem detectOperatingSystem(String osName) {
        OperatingSystem operatingSystem;
        System.out.println(osName);

        if (osName.contains("Windows")) {
            operatingSystem = OperatingSystem.WINDOWS;
        } else if (osName.contains("Mac")) {
            operatingSystem = OperatingSystem.MAC;
        } else if (osName.contains("Linux")) {
            operatingSystem = OperatingSystem.LINUX;
        } else {
            operatingSystem = null;
            System.out.println("This operating system is not supported");
        }
        return operatingSystem;
    }
}
