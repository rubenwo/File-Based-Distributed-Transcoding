public enum OperatingSystem {
    WINDOWS, MAC, UNIX;

    public static String getEncoderPath(OperatingSystem operatingSystem) {
        if (operatingSystem.equals(WINDOWS))
            return "./Resources/Windows/ffmpeg";
        else if (operatingSystem.equals(MAC))
            return "./Resources/Mac/ffmpeg";
        else if (operatingSystem.equals(UNIX))
            return "./Resources/Unix/ffmpeg";
        else return "This operating system is not supported.";
    }

    public static OperatingSystem detectOperatingSystem() {
        String osName = System.getProperty("os.name");
        OperatingSystem operatingSystem;
        System.out.println(osName);

        if (osName.contains("Windows")) {
            operatingSystem = OperatingSystem.WINDOWS;
        } else if (osName.contains("Mac")) {
            operatingSystem = OperatingSystem.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            operatingSystem = OperatingSystem.UNIX;
        } else {
            operatingSystem = null;
            System.out.println("This operating system is not supported");
        }
        return operatingSystem;
    }
}
