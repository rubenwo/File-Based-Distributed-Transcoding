public interface ProgressListener {
    void onProgressUpdate(String fileName, double progress);

    void onJobDone();

    void onError(Error error);
}
