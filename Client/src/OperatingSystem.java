public enum OperatingSystem {
    WINDOWS, MAC, LINUX;

    public static String getEncoderPath(OperatingSystem operatingSystem) {
        if (operatingSystem.equals(WINDOWS))
            return "cmd /c start cmd.exe /K .\\Resources\\Windows\\ffmpeg_windows\\bin\\ffmpeg.exe";
        else if (operatingSystem.equals(MAC))
            return ".\\Resources\\Mac OS X\\";
        else if (operatingSystem.equals(LINUX))
            return ".\\Resources\\Linux\\";
        else return "This operating system is not supported.";
    }
}
