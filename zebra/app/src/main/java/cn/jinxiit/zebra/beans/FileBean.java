package cn.jinxiit.zebra.beans;

public class FileBean
{
    /**
     * file_key : d33c215fe197695b196d11495d0791b00c76c511
     * ext : .jpg
     */

    private String file_key;
    private String ext;

    public String getFile_key() {
        return file_key == null ? "" : file_key;
    }

    public void setFile_key(String file_key) {
        this.file_key = file_key;
    }

    public String getExt() {
        return ext == null ? "" : ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
