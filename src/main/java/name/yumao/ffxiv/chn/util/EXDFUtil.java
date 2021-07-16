package name.yumao.ffxiv.chn.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import name.yumao.ffxiv.chn.model.EXDFDataset;
import name.yumao.ffxiv.chn.model.EXDFEntry;
import name.yumao.ffxiv.chn.model.EXDFFile;
import name.yumao.ffxiv.chn.model.EXDFPage;
import name.yumao.ffxiv.chn.model.EXHFFile;
import name.yumao.ffxiv.chn.model.SqPackDatFile;
import name.yumao.ffxiv.chn.model.SqPackIndex;
import name.yumao.ffxiv.chn.model.SqPackIndexFile;
import name.yumao.ffxiv.chn.model.SqPackIndexFolder;
import name.yumao.ffxiv.chn.util.res.Config;

public class EXDFUtil {
	
	private String pathToIndexSE;
	private String pathToIndexCN;
	private List<String> fileList;
	//added
	private String fileLang;
	
	public EXDFUtil(String pathToIndexSE) {
		this.pathToIndexSE = pathToIndexSE;
		// added
		this.fileLang = Config.getProperty("FLanguage");
	}
	
	public EXDFUtil(String pathToIndexSE, List<String> fileList) {
		this.pathToIndexSE = pathToIndexSE;
		this.fileList = fileList;
		// added
		this.fileLang = Config.getProperty("FLanguage");
	}
	
	public EXDFUtil(String pathToIndexSE, String pathToIndexCN, List<String> fileList) {
		this.pathToIndexSE = pathToIndexSE;
		this.pathToIndexCN = pathToIndexCN;
		this.fileList = fileList;
		// added
		this.fileLang = Config.getProperty("FLanguage");
	}
	
	private HashMap<String, byte[]> exCompleteJournalCN() throws Exception {
		// 首先我們生成一個空的hashMap，如果沒有CN檔案位置就直接回傳
		HashMap<String, byte[]> competeJournalMap = (HashMap)new HashMap<>();
		if (this.pathToIndexCN == null)
			return competeJournalMap; 
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexCN)).resloveIndex();
		
		/*
		for (Map.Entry<Integer, SqPackIndexFolder> entry : indexSE.entrySet()) {
			System.out.println("\t" + entry.getKey().toString() + "\t" + entry.getValue().getName());
		}
		*/
		
		// 根據傳入的檔案進行遍歷
		int fileCount = 0;
		String replaceFile = "EXD/CompleteJournal";
		// 準備好檔案目錄名和檔案名
		String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
		String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
		// 計算檔案目錄和exh的CRC
		Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
		Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
		// 解壓縮並解析exh
		if (indexSE.get(filePatchCRC) == null)
			return competeJournalMap; 
		SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
		if (exhIndexFileSE == null)
			return competeJournalMap; 
		byte[] exhFileSE = extractFile(this.pathToIndexCN, exhIndexFileSE.getOffset());
		EXHFFile exhSE = new EXHFFile(exhFileSE);
		if ((exhSE.getLangs()).length > 0) {
			// 根據標頭檔案輪詢資源檔案
			// 可以理解為Python的 for exdfPage in exhSE.getPages()
			for (EXDFPage exdfPage : exhSE.getPages()) {
				// edited
				// 獲取資源檔案的CRC
				// Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD").toLowerCase().getBytes()));
				Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + this.fileLang + ".EXD").toLowerCase().getBytes()));
				// 提取對應的文本檔案
				SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
				byte[] exdFileJA = null;
				try {
					exdFileJA = extractFile(this.pathToIndexCN, exdIndexFileJA.getOffset());
				} catch (Exception jaEXDFileException) {
					continue;
				}
				
				// 解壓文本檔案並提取內容
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
						// 只限文本內容
						if (exdfDatasetSE.type == 7 && exdfDatasetSE.offset == 4)
							key04 = Integer.valueOf(exdfEntryJA.getInt(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 6 && exdfDatasetSE.offset == 12)
							key0c = Integer.valueOf(exdfEntryJA.getInt(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 5 && exdfDatasetSE.offset == 112)
							key70 = Short.valueOf(exdfEntryJA.getShort(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 3 && exdfDatasetSE.offset == 114)
							key72 = Byte.valueOf(exdfEntryJA.getByte(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 0 && exdfDatasetSE.offset == 0)
							value = exdfEntryJA.getString(exdfDatasetSE.offset); 
						if (key04 != null && key0c != null && key70 != null && key72 != null && value != null && value.length > 0) {
							String key = key04.toString() + key0c.toString() + key70.toString() + key72.toString();
							competeJournalMap.put(key, value);
							// System.out.println("\t" + key + "\t" + HexUtils.bytesToHexStringWithOutSpace(value));
							break;
						} 
					} 
				} 
			}
		}
		return competeJournalMap;
	}
	
	public HashMap<String, byte[]> exCompleteJournalSE(HashMap<String, byte[]> exMap) throws Exception {
		System.out.println("exCompleteJournalCN loading.");
		HashMap<String, byte[]> sourceMap = exCompleteJournalCN();
		System.out.println("exCompleteJournalCN load finished!");
		if (this.pathToIndexSE == null)
			return exMap; 
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexSE)).resloveIndex();
		// 根據傳入的檔案進行遍歷
		int fileCount = 0;
		String replaceFile = "EXD/CompleteJournal";
		// 準備好檔案目錄名和檔案名，例如這裡就是"EXD/"和"CompleteJournal.EXH"
		String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
		String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
		// 計算檔案目錄和EXH的CRC
		Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
		Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
		// 解壓縮並解析EXH
		if (indexSE.get(filePatchCRC) == null)
			return exMap; 
		SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
		if (exhIndexFileSE == null)
			return exMap; 
		byte[] exhFileSE = extractFile(this.pathToIndexSE, exhIndexFileSE.getOffset());
		EXHFFile exhSE = new EXHFFile(exhFileSE);
		if ((exhSE.getLangs()).length > 0) {
			// 根據標頭檔案輪詢資源檔案
			for (EXDFPage exdfPage : exhSE.getPages()) {
				// 獲取資源檔案的CRC
				Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD").toLowerCase().getBytes()));
				// 提取對應的文本檔案
				SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
				byte[] exdFileJA = null;
				try {
					exdFileJA = extractFile(this.pathToIndexSE, exdIndexFileJA.getOffset());
				} catch (Exception jaEXDFileException) {
					continue;
				}
				// 解壓文本檔案並提取內容
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
						// 只限文本內容
						// System.out.println("\tThe type and offset of the exdfDatasetSE: " + String.valueOf(exdfDatasetSE.type) + "\t" + String.valueOf(exdfDatasetSE.offset));
						if (exdfDatasetSE.type == 0x7 && exdfDatasetSE.offset == 0x04)
							key04 = Integer.valueOf(exdfEntryJA.getInt(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 0x6 && exdfDatasetSE.offset == 0x0c)
							key0c = Integer.valueOf(exdfEntryJA.getInt(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 0x5 && exdfDatasetSE.offset == 0x70)
							key70 = Short.valueOf(exdfEntryJA.getShort(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 0x3 && exdfDatasetSE.offset == 0x72)
							key72 = Byte.valueOf(exdfEntryJA.getByte(exdfDatasetSE.offset)); 
						if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x0)
							value = exdfEntryJA.getString(exdfDatasetSE.offset);
						if (key04 != null && key0c != null && key70 != null && key72 != null && value != null && value.length > 0) {
							String key = key04.toString() + key0c.toString() + key70.toString() + key72.toString();
							// System.out.println("\tKey: " + key + "\tValue: " + value);
							if (sourceMap.get(key) != null) {
								exMap.put(("EXD/CompleteJournal_".toLowerCase() + String.valueOf(listEntryIndex) + "_1").toLowerCase(), sourceMap.get(key)); 
								// System.out.println("\t\tKey found, EXD/CompleteJournal_".toLowerCase() + String.valueOf(listEntryIndex) + "_1");
								// System.out.println("\t\tsourceMap.get(key): " + HexUtils.bytesToHexStringWithOutSpace(sourceMap.get(key)));
							} else {
								System.out.println("\t\tKey not found.");
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
		HashMap<String, byte[]> questMap = (HashMap)new HashMap<>();
		if (this.pathToIndexCN == null)
			return questMap; 
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexCN)).resloveIndex();
		// 根據傳入的檔案進行遍歷
		int fileCount = 0;
		String replaceFile = "EXD/Quest";
		// 準備好檔案目錄名和檔案名
		String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
		String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
		// 計算檔案目錄CRC
		Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
		// 計算標頭檔案（exh）CRC
		Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
		// 解壓縮並解析exh
		if (indexSE.get(filePatchCRC) == null) {
			System.out.println("\tindexSE.get(filePatchCRC) == null");
			return questMap; 
		}
		SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
		if (exhIndexFileSE == null) {
			System.out.println("\texhIndexFileSE == null");
			return questMap;
		}
		byte[] exhFileSE = extractFile(this.pathToIndexCN, exhIndexFileSE.getOffset());
		EXHFFile exhSE = new EXHFFile(exhFileSE);
		if ((exhSE.getLangs()).length > 0) {
			// 根據標頭檔案輪詢資源檔案
			for (EXDFPage exdfPage : exhSE.getPages()) {
				// edited
				// 獲取資源檔案的CRC
				// Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD").toLowerCase().getBytes()));
				Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + this.fileLang + ".EXD").toLowerCase().getBytes()));
				// 提取對應的文本檔案
				SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
				byte[] exdFileJA = null;
				try {
					exdFileJA = extractFile(this.pathToIndexCN, exdIndexFileJA.getOffset());
				} catch (Exception jaEXDFileException) {
					System.out.println("jaEXDFileException detected in exQuestCN!");
					continue;
				}
				// 解壓文本檔案並提取內容
				EXDFFile ja_exd = new EXDFFile(exdFileJA);
				HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
				for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
					// System.out.println("\tlistEntry key: " + String.valueOf(listEntry.getKey()));
					EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
					String key = null;
					byte[] value = null;
					for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
						// 只限文本內容
						// System.out.println("\t\tPrint Dataset type and offset " + String.valueOf(exdfDatasetSE.type) + " " + String.valueOf(exdfDatasetSE.offset));
						if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x0) {
							value = exdfEntryJA.getString(exdfDatasetSE.offset); 
							// System.out.println("\t\t\tGet Value! " + HexUtils.bytesToHexStringWithOutSpace(value));
						}
						if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x96C /* it was 0x968 */) {
							key = new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8");
							// System.out.println("\t\t\tGet Key! " + String.valueOf(key));
						}
						if (key != null && key.length() > 0 && value != null && value.length > 0) {
							// System.out.println("\t\t\tPut key and value into questMap!");
							questMap.put(key, value);
							break;
						} 
					} 
				} 
			}
		}
		
		/*
		// check the content
		System.out.println("\n\n====Start checking quest map CN (before)====");
		for (Map.Entry<String, byte[]> checkItem : questMap.entrySet()) {
			System.out.println(checkItem.getKey() + "  " + HexUtils.bytesToHexStringWithOutSpace(checkItem.getValue()));
		}
		System.out.println("====Quest map (before) checked====\n\n");
		*/

		return questMap;
	}
	
	public HashMap<String, byte[]> exQuestSE(HashMap<String, byte[]> exMap) throws Exception {
		// System.out.println("")
		HashMap<String, byte[]> sourceMap = exQuestCN();
		if (this.pathToIndexSE == null || this.pathToIndexCN == null)
			return exMap; 
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexSE)).resloveIndex();
		HashMap<Integer, SqPackIndexFolder> indexCN = (new SqPackIndex(this.pathToIndexCN)).resloveIndex();
		// 根據傳入的檔案進行遍歷
		int fileCount = 0;
		String replaceFile = "EXD/Quest";
		// 準備好檔案目錄名和檔案名
		String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
		String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
		// 計算檔案目錄CRC
		Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
		// 計算exh CRC
		Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
		// 解壓縮並解析exh
		if (indexSE.get(filePatchCRC) == null)
			return exMap; 
		SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
		SqPackIndexFile exhIndexFileCN = (SqPackIndexFile)((SqPackIndexFolder)indexCN.get(filePatchCRC)).getFiles().get(exhFileCRC);
		if (exhIndexFileSE == null || exhIndexFileCN == null)
			return exMap; 
		byte[] exhFileSE = extractFile(this.pathToIndexSE, exhIndexFileSE.getOffset());
		EXHFFile exhSE = new EXHFFile(exhFileSE);
		if ((exhSE.getLangs()).length > 0) {
			// 根據標頭檔案輪詢資源檔案
			for (EXDFPage exdfPage : exhSE.getPages()) {
				// 獲取資源檔案的CRC
				Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD").toLowerCase().getBytes()));
				// 提取對應的文本檔案
				SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
				byte[] exdFileJA = null;
				try {
					exdFileJA = extractFile(this.pathToIndexSE, exdIndexFileJA.getOffset());
				} catch (Exception jaEXDFileException) {
					System.out.println("jaEXDFileException detected in exQuestSE!");
					continue;
				}
				// 解壓文本檔案並提取內容
				EXDFFile ja_exd = new EXDFFile(exdFileJA);
				HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
				for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
					Integer listEntryIndex = listEntry.getKey();
					EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
					for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
						if (exdfDatasetSE.type == 0x0 && exdfDatasetSE.offset == 0x96C /* it was 0x968 */) {
							String key = new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8");
							if (sourceMap.get(key) != null)
								exMap.put(("EXD/Quest_".toLowerCase() + String.valueOf(listEntryIndex) + "_1").toLowerCase(), sourceMap.get(key)); 
							break;
						} 
					} 
				} 
			}
		}
		
		/*
		// check the content
		System.out.println("\n\n====Start checking quest map (after)====");
		for (Map.Entry<String, byte[]> checkItem : exMap.entrySet()) {
			System.out.println(checkItem.getKey() + "  " + HexUtils.bytesToHexStringWithOutSpace(checkItem.getValue()));
		}
		System.out.println("====Quest map (after) checked====\n\n");
		*/
		
		return exMap;
	}
	
	public boolean isTransDat() throws Exception {
		if (this.pathToIndexSE == null)
			return false; 
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexSE)).resloveIndex();
		String replaceFile = "EXD/Addon";
		// 準備好檔案目錄名和檔案名
		String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
		String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
		// 計算檔案目錄CRC
		Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
		// 計算標頭檔案（exh）CRC
		Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
		// 解壓縮並解析exh
		if (indexSE.get(filePatchCRC) == null)
			return false; 
		SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
		if (exhIndexFileSE == null)
			return false; 
		byte[] exhFileSE = extractFile(this.pathToIndexSE, exhIndexFileSE.getOffset());
		EXHFFile exhSE = new EXHFFile(exhFileSE);
		if ((exhSE.getLangs()).length > 0) {
			// 根據標頭檔案輪詢資源檔案
			for (EXDFPage exdfPage : exhSE.getPages()) {
				// 獲取資源檔案的CRC
				Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD").toLowerCase().getBytes()));
				// 提取對應的文本檔案
				SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
				byte[] exdFileJA = null;
				try {
					exdFileJA = extractFile(this.pathToIndexSE, exdIndexFileJA.getOffset());
				} catch (Exception jaEXDFileException) {
					continue;
				}
				// 解壓文本檔案並提取內容
				EXDFFile ja_exd = new EXDFFile(exdFileJA);
				HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
				EXDFEntry exdfEntryJA = new EXDFEntry(jaExdList.get(Integer.valueOf(5506)), exhSE.getDatasetChunkSize());
				for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
					// 只限文本內容
					if (exdfDatasetSE.type == 0) {
						byte[] jaBytes = exdfEntryJA.getString(exdfDatasetSE.offset);
						String jaStr = new String(jaBytes, "UTF-8");
						if (jaStr.contains("teemo.link") || jaStr.contains("sdo.com"))
							return true; 
					} 
				} 
			}
		}
		return false;
	}
	
	public HashMap<String, String> transMap(HashMap<String, String> transMap) throws Exception {
		return transMap(transMap, "JA");
	}
	
	public HashMap<String, String> transMap(HashMap<String, String> transMap, String lang) throws Exception {
		if (this.pathToIndexSE == null)
			return transMap; 
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexSE)).resloveIndex();
		HashMap<Integer, SqPackIndexFolder> indexCN = (new SqPackIndex(this.pathToIndexCN)).resloveIndex();
		// 根據傳入的檔案進行遍歷
		int fileCount = 0;
		String[] replaceFiles = { "EXD/Action", "EXD/ENpcResident", "EXD/BNpcName", "EXD/PlaceName" };
		for (String replaceFile : replaceFiles) {
			// 目錄名和檔案名
			String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
			String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1) + ".EXH";
			// 目錄和EXH的CRC
			Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
			Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
			// 解壓縮並解析EXH
			if (indexSE.get(filePatchCRC) == null)
				return transMap; 
			SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
			SqPackIndexFile exhIndexFileCN = (SqPackIndexFile)((SqPackIndexFolder)indexCN.get(filePatchCRC)).getFiles().get(exhFileCRC);
			if (exhIndexFileSE == null || exhIndexFileCN == null)
				return transMap; 
			byte[] exhFileSE = extractFile(this.pathToIndexSE, exhIndexFileSE.getOffset());
			byte[] exhFileCN = extractFile(this.pathToIndexCN, exhIndexFileCN.getOffset());
			EXHFFile exhSE = new EXHFFile(exhFileSE);
			EXHFFile exhCN = new EXHFFile(exhFileCN);
			if ((exhSE.getLangs()).length > 0) {
				// 根據EXH輪詢資源檔案
				for (EXDFPage exdfPage : exhSE.getPages()) {
					// 獲取資源檔案的CRC
					Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + lang + ".EXD").toLowerCase().getBytes()));
					// edited
					// Integer exdFileCRCCN = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD").toLowerCase().getBytes()));
					Integer exdFileCRCCN = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + this.fileLang + ".EXD").toLowerCase().getBytes()));
					// 提取對應的文本檔案
					SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
					SqPackIndexFile exdIndexFileCN = (SqPackIndexFile)((SqPackIndexFolder)indexCN.get(filePatchCRC)).getFiles().get(exdFileCRCCN);
					byte[] exdFileJA = null;
					byte[] exdFileCN = null;
					try {
						exdFileJA = extractFile(this.pathToIndexSE, exdIndexFileJA.getOffset());
						exdFileCN = extractFile(this.pathToIndexCN, exdIndexFileCN.getOffset());
					} catch (Exception jaEXDFileException) {
						continue;
					}
					// 解壓縮文本檔案並提取內容
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
								// 只限文本內容
								if (exdfDatasetSE.type == 0 && exdfDatasetSE.offset == 0) {
									String jaStr = new String(exdfEntryJA.getString(exdfDatasetSE.offset), "UTF-8");
									String cnStr = new String(exdfEntryCN.getString(exdfDatasetSE.offset), "UTF-8");
									if (jaStr != null && jaStr.length() > 0 && cnStr != null && cnStr.length() > 0)
										transMap.put(jaStr, cnStr); 
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
		int datNum = (int)((dataOffset & 0xFL) / 2L);
		dataOffset -= dataOffset & 0xFL;
		pathToOpen = pathToOpen.replace("index2", "dat" + datNum);
		pathToOpen = pathToOpen.replace("index", "dat" + datNum);
		// System.out.println("\tDataOffset: " + Long.toString(dataOffset) + "\tpathToOpen: " + pathToOpen);
		SqPackDatFile datFile = new SqPackDatFile(pathToOpen);
		byte[] data = datFile.extractFile(dataOffset * 8L, Boolean.valueOf(false));
		datFile.close();
		return data;
	}
}
