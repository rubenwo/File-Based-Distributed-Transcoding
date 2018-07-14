public interface ProgressListener {
    void onProgressUpdate(double progress);

    void onError(Error error);
}
