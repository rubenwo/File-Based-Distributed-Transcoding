public class FileMetaData {
    private String fileName;
    private long size;

    static FileMetaData from(final String request) {
        assert !request.isEmpty();

        String[] contents = request.replace(Constants.END_MESSAGE_MARKER, "").split(Constants.MESSAGE_DELIMITTER);
        return new FileMetaData(contents[0], Long.valueOf(contents[1]));
    }

    private FileMetaData(final String fileName, final long size) {
        assert !fileName.isEmpty();

        this.fileName = fileName;
        this.size = size;
    }

    String getFileName() {
        return this.fileName;
    }

    long getSize() {
        return this.size;
    }
}
