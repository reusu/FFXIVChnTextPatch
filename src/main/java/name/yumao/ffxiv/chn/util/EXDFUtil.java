package name.yumao.ffxiv.chn.util;

import name.yumao.ffxiv.chn.model.*;

import java.io.*;
import java.util.*;

public class EXDFUtil {

    private String pathToIndexSE;
    private String pathToIndexCN;
    private List<String> fileList;

    public EXDFUtil(String pathToIndexSE) {
        this.pathToIndexSE = pathToIndexSE;
    }

    public EXDFUtil(String pathToIndexSE, List<String> fileList) {
        this.pathToIndexSE = pathToIndexSE;
        this.fileList = fileList;
    }

    public EXDFUtil(String pathToIndexSE, String pathToIndexCN, List<String> fileList) {
        this.pathToIndexSE = pathToIndexSE;
        this.pathToIndexCN = pathToIndexCN;
        this.fileList = fileList;
    }

    private HashMap<String, byte[]> exCompleteJournalCN() throws Exception {
        HashMap<String, byte[]> competeJournalMap = new HashMap<>();
        if (pathToIndexCN == null) return competeJournalMap;
        HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexCN).resloveIndex();
        // 根据传入的文件进行遍历
        int fileCount = 0;
        String replaceFile = "EXD/CompleteJournal";
        // 准备好文件目录名和文件名
        String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
        String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
        // 计算文件目录CRC
        Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
        // 计算头文件CRC
        Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
        // 解压并且解析头文件
        if (indexSE.get(filePatchCRC) == null) return competeJournalMap;
        SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
        if (exhIndexFileSE == null) return competeJournalMap;
        byte[] exhFileSE = extractFile(pathToIndexCN, exhIndexFileSE.getOffset());
        EXHFFile exhSE = new EXHFFile(exhFileSE);
        if (exhSE.getLangs().length > 0) {
            // 根据头文件 轮询资源文件
            for (EXDFPage exdfPage : exhSE.getPages()) {
                // 获取资源文件的CRC
                Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD")).toLowerCase().getBytes());
                // 提取对应的文本文件
                SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
                byte[] exdFileJA = null;
                try {
                    exdFileJA = extractFile(pathToIndexCN, exdIndexFileJA.getOffset());
                } catch (Exception jaEXDFileException) {
                    continue;
                }
                // 解压本文文件 提取内容
                EXDFFile ja_exd = new EXDFFile(exdFileJA);
                HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
                for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
                    EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
                    Integer key04 = null;
                    Integer key0c = null;
                    Short key70 = null;
                    Byte key72 = null;
                    byte[] value = null;
                    for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
                        // 只限文本内容
                        if (exdfDatasetSE.type == 0x7 && exdfDatasetSE.offset == 0x04) {
                            key04 = exdfEntryJA.getInt(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x6 && exdfDatasetSE.offset == 0x0c) {
                            key0c = exdfEntryJA.getInt(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x5 && exdfDatasetSE.offset == 0x70) {
                            key70 = exdfEntryJA.getShort(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x3 && exdfDatasetSE.offset == 0x72) {
                            key72 = exdfEntryJA.getByte(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x0) {
                            value = exdfEntryJA.getString(exdfDatasetSE.offset);
                        }
                        if (key04 != null && key0c != null && key70 != null && key72!=null  && value != null && value.length > 0) {
                            String key = key04.toString() + key0c.toString() + key70.toString() + key72.toString();
                            competeJournalMap.put(key, value);
                            break;
                        }
                    }
                }
            }
        }
        return competeJournalMap;
    }

    public HashMap<String, byte[]> exCompleteJournalSE(HashMap<String, byte[]> exMap) throws Exception {
        HashMap<String, byte[]> sourceMap = exCompleteJournalCN();
        if (pathToIndexSE == null) return exMap;
        HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
        // 根据传入的文件进行遍历
        int fileCount = 0;
        String replaceFile = "EXD/CompleteJournal";
        // 准备好文件目录名和文件名
        String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
        String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
        // 计算文件目录CRC
        Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
        // 计算头文件CRC
        Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
        // 解压并且解析头文件
        if (indexSE.get(filePatchCRC) == null) return exMap;
        SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
        if (exhIndexFileSE == null) return exMap;
        byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
        EXHFFile exhSE = new EXHFFile(exhFileSE);
        if (exhSE.getLangs().length > 0) {
            // 根据头文件 轮询资源文件
            for (EXDFPage exdfPage : exhSE.getPages()) {
                // 获取资源文件的CRC
                Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD")).toLowerCase().getBytes());
                // 提取对应的文本文件
                SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
                byte[] exdFileJA = null;
                try {
                    exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
                } catch (Exception jaEXDFileException) {
                    continue;
                }
                // 解压本文文件 提取内容
                EXDFFile ja_exd = new EXDFFile(exdFileJA);
                HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
                for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
                    Integer listEntryIndex = listEntry.getKey();
                    EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
                    Integer key04 = null;
                    Integer key0c = null;
                    Short key70 = null;
                    Byte key72 = null;
                    byte[] value = null;
                    for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
                        // 只限文本内容
                        if (exdfDatasetSE.type == 0x7 && exdfDatasetSE.offset == 0x04) {
                            key04 = exdfEntryJA.getInt(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x6 && exdfDatasetSE.offset == 0x0c) {
                            key0c = exdfEntryJA.getInt(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x5 && exdfDatasetSE.offset == 0x70) {
                            key70 = exdfEntryJA.getShort(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x3 && exdfDatasetSE.offset == 0x72) {
                            key72 = exdfEntryJA.getByte(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x0) {
                            value = exdfEntryJA.getString(exdfDatasetSE.offset);
                        }
                        if (key04 != null && key0c != null && key70 != null && key72!=null  && value != null && value.length > 0) {
                            String key = key04.toString() + key0c.toString() + key70.toString() + key72.toString();
                            if(sourceMap.get(key) != null){
                                exMap.put(("EXD/CompleteJournal_".toLowerCase() + String.valueOf(listEntryIndex) + "_1").toLowerCase(), sourceMap.get(key));
                            }
                            break;
                        }
                    }
                }
            }
        }
        return exMap;
    }

    private HashMap<String, byte[]> exQuestCN() throws Exception {
        HashMap<String, byte[]> questMap = new HashMap<>();
        if (pathToIndexCN == null) return questMap;
        HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexCN).resloveIndex();
        // 根据传入的文件进行遍历
        int fileCount = 0;
        String replaceFile = "EXD/Quest";
        // 准备好文件目录名和文件名
        String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
        String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
        // 计算文件目录CRC
        Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
        // 计算头文件CRC
        Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
        // 解压并且解析头文件
        if (indexSE.get(filePatchCRC) == null) return questMap;
        SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
        if (exhIndexFileSE == null) return questMap;
        byte[] exhFileSE = extractFile(pathToIndexCN, exhIndexFileSE.getOffset());
        EXHFFile exhSE = new EXHFFile(exhFileSE);
        if (exhSE.getLangs().length > 0) {
            // 根据头文件 轮询资源文件
            for (EXDFPage exdfPage : exhSE.getPages()) {
                // 获取资源文件的CRC
                Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD")).toLowerCase().getBytes());
                // 提取对应的文本文件
                SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
                byte[] exdFileJA = null;
                try {
                    exdFileJA = extractFile(pathToIndexCN, exdIndexFileJA.getOffset());
                } catch (Exception jaEXDFileException) {
                    continue;
                }
                // 解压本文文件 提取内容
                EXDFFile ja_exd = new EXDFFile(exdFileJA);
                HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
                for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
                    EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
                    String key = null;
                    byte[] value = null;
                    for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
                        // 只限文本内容
                        if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x0) {
                            value = exdfEntryJA.getString(exdfDatasetSE.offset);
                        }
                        if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x968) {
                            key = new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8");
                        }
                        if (key != null && key.length() > 0 && value != null && value.length > 0) {
                            questMap.put(key, value);
                            break;
                        }
                    }
                }
            }
        }
        return questMap;
    }

    public HashMap<String, byte[]> exQuestSE(HashMap<String, byte[]> exMap) throws Exception {
        HashMap<String, byte[]> sourceMap = exQuestCN();
        if (pathToIndexSE == null || pathToIndexCN == null) return exMap;
        HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
        HashMap<Integer, SqPackIndexFolder> indexCN = new SqPackIndex(pathToIndexCN).resloveIndex();
        // 根据传入的文件进行遍历
        int fileCount = 0;
        String replaceFile = "EXD/Quest";
        // 准备好文件目录名和文件名
        String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
        String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
        // 计算文件目录CRC
        Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
        // 计算头文件CRC
        Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
        // 解压并且解析头文件
        if (indexSE.get(filePatchCRC) == null) return exMap;
        SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
        SqPackIndexFile exhIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exhFileCRC);
        if (exhIndexFileSE == null || exhIndexFileCN == null) return exMap;
        byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
        EXHFFile exhSE = new EXHFFile(exhFileSE);
        if (exhSE.getLangs().length > 0) {
            // 根据头文件 轮询资源文件
            for (EXDFPage exdfPage : exhSE.getPages()) {
                // 获取资源文件的CRC
                Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD")).toLowerCase().getBytes());
                // 提取对应的文本文件
                SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
                byte[] exdFileJA = null;
                try {
                    exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
                } catch (Exception jaEXDFileException) {
                    continue;
                }
                // 解压本文文件 提取内容
                EXDFFile ja_exd = new EXDFFile(exdFileJA);
                HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
                for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
                    Integer listEntryIndex = listEntry.getKey();
                    EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
                    for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
                        // 只限文本内容
                        if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x968) {
                            String key = new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8");
                            if (sourceMap.get(key) != null) {
                                exMap.put(("EXD/Quest_".toLowerCase() + String.valueOf(listEntryIndex) + "_1").toLowerCase(), sourceMap.get(key));
                            }
                            break;
                        }
                    }
                }
            }
        }
        return exMap;
    }

    public boolean isTransDat() throws Exception {
        if (pathToIndexSE == null) return false;
        HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
        String replaceFile = "EXD/Addon";
        // 准备好文件目录名和文件名
        String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
        String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
        // 计算文件目录CRC
        Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
        // 计算头文件CRC
        Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
        // 解压并且解析头文件
        if (indexSE.get(filePatchCRC) == null) return false;
        SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
        if (exhIndexFileSE == null) return false;
        byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
        EXHFFile exhSE = new EXHFFile(exhFileSE);
        if (exhSE.getLangs().length > 0) {
            // 根据头文件 轮询资源文件
            for (EXDFPage exdfPage : exhSE.getPages()) {
                // 获取资源文件的CRC
                Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD")).toLowerCase().getBytes());
                // 提取对应的文本文件
                SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
                byte[] exdFileJA = null;
                try {
                    exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
                } catch (Exception jaEXDFileException) {
                    continue;
                }
                // 解压本文文件 提取内容
                EXDFFile ja_exd = new EXDFFile(exdFileJA);
                HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
                EXDFEntry exdfEntryJA = new EXDFEntry(jaExdList.get(5506), exhSE.getDatasetChunkSize());
                for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
                    // 只限文本内容
                    if (exdfDatasetSE.type == 0) {
                        byte[] jaBytes = exdfEntryJA.getString(exdfDatasetSE.offset);
                        String jaStr = new String(jaBytes, "UTF-8");
                        if(jaStr.contains("teemo.link") || jaStr.contains("sdo.com")){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public HashMap<String,String> transMap(HashMap<String, String> transMap) throws Exception {
        return this.transMap(transMap, "JA");
    }

    public HashMap<String,String> transMap(HashMap<String, String> transMap, String lang) throws Exception {
        if (pathToIndexSE == null) return transMap;
        HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
        HashMap<Integer, SqPackIndexFolder> indexCN = new SqPackIndex(pathToIndexCN).resloveIndex();
        // 根据传入的文件进行遍历
        int fileCount = 0;
        String[] replaceFiles = {"EXD/Action" , "EXD/ENpcResident", "EXD/BNpcName", "EXD/PlaceName"};
        for(String replaceFile : replaceFiles) {
            // 准备好文件目录名和文件名
            String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
            String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
            // 计算文件目录CRC
            Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
            // 计算头文件CRC
            Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
            // 解压并且解析头文件
            if (indexSE.get(filePatchCRC) == null) return transMap;
            SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
            SqPackIndexFile exhIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exhFileCRC);
            if (exhIndexFileSE == null || exhIndexFileCN == null) return transMap;
            byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
            byte[] exhFileCN = extractFile(pathToIndexCN, exhIndexFileCN.getOffset());
            EXHFFile exhSE = new EXHFFile(exhFileSE);
            EXHFFile exhCN = new EXHFFile(exhFileCN);
            if (exhSE.getLangs().length > 0) {
                // 根据头文件 轮询资源文件
                for (EXDFPage exdfPage : exhSE.getPages()) {
                    // 获取资源文件的CRC
                    Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + lang + ".EXD")).toLowerCase().getBytes());
                    Integer exdFileCRCCN = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD")).toLowerCase().getBytes());
                    // 提取对应的文本文件
                    SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
                    SqPackIndexFile exdIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exdFileCRCCN);
                    byte[] exdFileJA = null;
                    byte[] exdFileCN = null;
                    try {
                        exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
                        exdFileCN = extractFile(pathToIndexCN, exdIndexFileCN.getOffset());
                    } catch (Exception jaEXDFileException) {
                        continue;
                    }
                    // 解压本文文件 提取内容
                    EXDFFile ja_exd = new EXDFFile(exdFileJA);
                    EXDFFile cn_exd = new EXDFFile(exdFileCN);
                    HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
                    HashMap<Integer, byte[]> cnExdList = cn_exd.getEntrys();
                    for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
                        Integer listEntryIndex = listEntry.getKey();
                        EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
                        byte[] data = cnExdList.get(listEntryIndex);
                        if (data != null && data.length > 0) {
                            EXDFEntry exdfEntryCN = new EXDFEntry(data, exhCN.getDatasetChunkSize());
                            for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
                                // 只限文本内容
                                if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x0) {
                                    String jaStr = new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8");
                                    String cnStr = new String(exdfEntryCN.getString(exdfDatasetSE.offset), "UTF-8");
                                    if (jaStr != null && jaStr.length() > 0 && cnStr != null && cnStr.length() > 0) {
                                        transMap.put(jaStr, cnStr);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return transMap;
    }

    private byte[] extractFile(String pathToIndexSE, long dataOffset) throws IOException, FileNotFoundException {
        String pathToOpen = pathToIndexSE;
        int datNum = (int) ((dataOffset & 0xF) / 2L);
        dataOffset -= (dataOffset & 0xF);
        pathToOpen = pathToOpen.replace("index2", "dat" + datNum);
        pathToOpen = pathToOpen.replace("index", "dat" + datNum);
        SqPackDatFile datFile = new SqPackDatFile(pathToOpen);
        byte[] data = datFile.extractFile(dataOffset * 8L, false);
        datFile.close();
        return data;
    }

}
