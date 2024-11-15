package common;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private String fileName;
    private String peerAddress;

    public FileInfo(String fileName, String peerAddress) {
        this.fileName = fileName;
        this.peerAddress = peerAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPeerAddress() {
        return peerAddress;
    }
}