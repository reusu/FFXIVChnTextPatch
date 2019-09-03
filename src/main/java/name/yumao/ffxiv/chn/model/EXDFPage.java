package name.yumao.ffxiv.chn.model;

public class EXDFPage {
    public final int pageNum;
    public final int numEntries;

    public EXDFPage(int pageNum, int numEntries) {
        this.pageNum = pageNum;
        this.numEntries = numEntries;
    }
}