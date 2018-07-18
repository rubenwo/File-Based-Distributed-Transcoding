public interface FileReceiverListener {
    void onSocketBound();

    void onFileReceived(String input, String output, String tempDir);
}
