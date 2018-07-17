public interface CommandListener {
    void onCommandsAvailable(String[] commands);

    void onNoInputSelected();
}
