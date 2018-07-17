public interface ProgressListener {
    void onJobSubmitted(String fileName);

    void onProgressUpdate(double progress);

    void onJobDone();

    void onError(Error error);
}
