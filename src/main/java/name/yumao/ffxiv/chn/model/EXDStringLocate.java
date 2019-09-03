package name.yumao.ffxiv.chn.model;

import name.yumao.ffxiv.chn.util.FFXIVString;

public class EXDStringLocate {

    private String fileName;
    private Integer index;
    private Integer strCount;
    private byte[] strBody;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getStrCount() {
        return strCount;
    }

    public void setStrCount(Integer strCount) {
        this.strCount = strCount;
    }

    public byte[] getStrBody() {
        return strBody;
    }

    public void setStrBody(byte[] strBody) {
        this.strBody = strBody;
    }

    public String getFFXIVString() {
        return FFXIVString.parseFFXIVString(strBody);
    }

}
