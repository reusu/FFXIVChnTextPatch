package name.yumao.ffxiv.chn.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EXHFFile {

    private EXDFDataset[] datasets;
    private EXDFPage[] pages;
    private int[] langs;
    private int datasetChunkSize = 0;
    private int numEntries = 0;
    private int trueNumEntries = 0;

    public EXHFFile(byte[] data) throws IOException {
        loadEXHF(data);
    }

    public EXHFFile(String path) throws IOException, FileNotFoundException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        loadEXHF(data);
    }

    private void loadEXHF(byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN);
        try {
            int magicNum = buffer.getInt();

            if (magicNum != 1163413574) {
                throw new IOException("Not a EXHF");
            }
            int version = buffer.getShort();

            if (version != 3) {
                throw new IOException("Not a EXHF");
            }
            this.datasetChunkSize = buffer.getShort();
            int numDataSetTable = buffer.getShort();
            int numPageTable = buffer.getShort();
            int numLangTable = buffer.getShort();
            buffer.getShort();
            buffer.getShort();
            buffer.getShort();
            this.numEntries = buffer.getInt();
            buffer.getInt();
            buffer.position(32);

            this.datasets = new EXDFDataset[numDataSetTable];
            this.pages = new EXDFPage[numPageTable];
            this.langs = new int[numLangTable];

            for (int i = 0; i < numDataSetTable; i++) {
                this.datasets[i] = new EXDFDataset(buffer.getShort(), buffer.getShort());
            }

            for (int i = 0; i < numPageTable; i++) {
                this.pages[i] = new EXDFPage(buffer.getInt(), buffer.getInt());
                this.trueNumEntries += this.pages[i].numEntries;
            }

            for (int i = 0; i < numLangTable; i++) {
                this.langs[i] = buffer.get();
                buffer.get();
            }
        } catch (BufferUnderflowException localBufferUnderflowException) {
        } catch (BufferOverflowException localBufferOverflowException) {
        }
    }

    public int getDatasetChunkSize() {
        return datasetChunkSize;
    }

    public EXDFDataset[] getDatasets() {
        return datasets;
    }

    public EXDFPage[] getPages() {
        return pages;
    }

    public int[] getLangs() {
        return langs;
    }

    public static void main(String[] args) throws Exception{
        new EXHFFile("F:\\exd\\exd\\achievement.exh");
    }
}