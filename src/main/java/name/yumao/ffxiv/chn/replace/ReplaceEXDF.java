package name.yumao.ffxiv.chn.replace;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import name.yumao.ffxiv.chn.builder.BinaryBlockBuilder;
import name.yumao.ffxiv.chn.builder.EXDFBuilder;
import name.yumao.ffxiv.chn.model.EXDFDataset;
import name.yumao.ffxiv.chn.model.EXDFEntry;
import name.yumao.ffxiv.chn.model.EXDFFile;
import name.yumao.ffxiv.chn.model.EXDFPage;
import name.yumao.ffxiv.chn.model.EXHFFile;
import name.yumao.ffxiv.chn.model.SqPackDatFile;
import name.yumao.ffxiv.chn.model.SqPackIndex;
import name.yumao.ffxiv.chn.model.SqPackIndexFile;
import name.yumao.ffxiv.chn.model.SqPackIndexFolder;
// import name.yumao.ffxiv.chn.model.TeemoUpdateVo;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import name.yumao.ffxiv.chn.util.ArrayUtil;
import name.yumao.ffxiv.chn.util.EXDFUtil;
import name.yumao.ffxiv.chn.util.FFCRC;
import name.yumao.ffxiv.chn.util.FFXIVString;
import name.yumao.ffxiv.chn.util.HexUtils;
import name.yumao.ffxiv.chn.util.LERandomAccessFile;
import name.yumao.ffxiv.chn.util.LERandomBytes;
import name.yumao.ffxiv.chn.util.nlpcn.JianFan;
import name.yumao.ffxiv.chn.util.res.Config;

public class ReplaceEXDF {
	
	private String pathToIndexSE;
	private String pathToIndexCN;
	private List<String> fileList;
	
	private HashMap<String, byte[]> exQuestMap;
	private HashMap<String, String> transMap;
	private PercentPanel percentPanel;
	
	private String slang;
	private String dlang;
	// private List<TeemoUpdateVo> updates;
	// added
	// flang should be CHS or JA at default.
	private String flang;
	private boolean csv;
	
	public ReplaceEXDF(String pathToIndexSE, String pathToIndexCN,/* List<TeemoUpdateVo> updates,*/ PercentPanel percentPanel) {
		// 這兩個檔案會是0a0000.win32.index
		this.pathToIndexSE = pathToIndexSE;
		this.pathToIndexCN = pathToIndexCN;
		// this.updates = updates;
		this.percentPanel = percentPanel;
		this.fileList = new ArrayList<>();
		this.exQuestMap = (HashMap)new HashMap<>();
		this.transMap = new HashMap<>();
		this.slang = Config.getProperty("SLanguage");
		this.dlang = Config.getProperty("DLanguage");
		// added
		this.flang = Config.getProperty("FLanguage");
		if (this.flang.equals("CSV")) {
			this.csv = true;
		} else {
			this.csv = false;
		}
	}
	
	public void replace() throws Exception {
		File transtableFile;
		Pattern pattern = null;
		List<String> skipFiles = null;
		Logger log = Logger.getLogger("GPLogger");
		if (!this.csv) {
			log.info("[ReplaceEXDF] Loading Transtable");
			transtableFile = new File("conf" + File.separator + "transtable.properties");
			transtableFile.deleteOnExit();
			transtableFile.createNewFile();
			Config.setConfigResource("transtable", "conf" + File.separator + "transtable.properties");
			log.info("[ReplaceEXDF] Loading Root File...");
			String patternStr = this.slang.equals("EN") ? "teemopattern" : "^[a-zA-Z0-9_'./\\s]*$";
			pattern = Pattern.compile(patternStr);
		}
		System.out.println("[ReplaceEXDF] Initializing File List...");
		log.info("[ReplaceEXDF] Initializing File List...");
		initFileList();
		System.out.println("[ReplaceEXDF] Loading Index File...");
		log.info("[ReplaceEXDF] Loading Index File...");
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexSE)).resloveIndex();
		HashMap<Integer, SqPackIndexFolder> indexCN = null;
		if (!this.csv) {
			indexCN = (new SqPackIndex(this.pathToIndexCN)).resloveIndex();
			log.info("[ReplaceEXDF] Resolved IndexCN");
		} else {
			log.info("[ReplaceEXDF] Skipped IndexCN");
		}
		System.out.println("[ReplaceEXDF] Loading Index Complete");
		log.info("[ReplaceEXDF] Loading Index Complete");
		LERandomAccessFile leIndexFile = new LERandomAccessFile(this.pathToIndexSE, "rw");
		LERandomAccessFile leDatFile = new LERandomAccessFile(this.pathToIndexSE.replace("index", "dat0"), "rw");
		long datLength = leDatFile.length();
		leDatFile.seek(datLength);
		if (!this.csv) {
			try {
				log.info("[ReplaceEXDF] Loading exQuestMap...");
				// exQuestMap這時候是個空的hashMap，透過以下兩行將東西填進去
				this.exQuestMap = (new EXDFUtil(this.pathToIndexSE, this.pathToIndexCN, this.fileList)).exCompleteJournalSE(this.exQuestMap);
				this.exQuestMap = (new EXDFUtil(this.pathToIndexSE, this.pathToIndexCN, this.fileList)).exQuestSE(this.exQuestMap);
				log.info("[ReplaceEXDF] Loading exQuestMap Complete");
			} catch (Exception exception) {}
			try {
				log.info("[ReplaceEXDF] Loading transMap...");
				this.transMap = (new EXDFUtil(this.pathToIndexSE, this.pathToIndexCN, this.fileList)).transMap(this.transMap);
				log.info("[ReplaceEXDF] Loading transMap Complete");
			} catch (Exception exception) {}
			skipFiles = Arrays.asList(Config.getProperty("SkipFiles").toLowerCase().split("[|]"));
		}
		// 根據傳入的檔案進行遍歷
		int fileCount = 0;
		for (String replaceFile : this.fileList) {
			// edited
			// this.percentPanel.percentShow(++fileCount / this.fileList.size());
			percentPanel.percentShow((double)(++fileCount) / (double)fileList.size());
			if (replaceFile.toUpperCase().endsWith(".EXH")) {
				System.out.println("[ReplaceEXDF] Now File : " + replaceFile);
				percentPanel.progressShow("正在替換文本：", replaceFile);
				log.info("[ReplaceEXDF] Now File : " + replaceFile);
				// 準備好檔案目錄名和檔案名
				String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
				String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1);
				// 計算檔案目錄CRC
				Integer filePatchCRC = Integer.valueOf(FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes()));
				// 計算 EXH CRC
				Integer exhFileCRC = Integer.valueOf(FFCRC.ComputeCRC(fileName.toLowerCase().getBytes()));
				// 解壓縮並解析EXH
				if (indexSE.get(filePatchCRC) == null)
					continue; 
				SqPackIndexFile exhIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exhFileCRC);
				if (exhIndexFileSE == null)
					continue; 
				byte[] exhFileSE = extractFile(this.pathToIndexSE, exhIndexFileSE.getOffset());
				EXHFFile exhSE = new EXHFFile(exhFileSE);
				EXHFFile exhCN = null;
				HashMap<EXDFDataset, EXDFDataset> datasetMap = new HashMap<>();
				boolean cnEXHFileAvailable = true;
				if (!this.csv) {
					try {
						SqPackIndexFile exhIndexFileCN = (SqPackIndexFile)((SqPackIndexFolder)indexCN.get(filePatchCRC)).getFiles().get(exhFileCRC);
						byte[] exhFileCN = extractFile(this.pathToIndexCN, exhIndexFileCN.getOffset());
						exhCN = new EXHFFile(exhFileCN);
						// 添加對照的StringDataset
						int cnDatasetPossition = 0;
						if (datasetStringCount(exhSE.getDatasets()) > 0 && datasetStringCount(exhSE.getDatasets()) == datasetStringCount(exhCN.getDatasets()))
							for (EXDFDataset datasetSE : exhSE.getDatasets()) {
								if (datasetSE.type == 0) {
									while ((exhCN.getDatasets()[cnDatasetPossition]).type != 0)
										cnDatasetPossition++; 
									datasetMap.put(datasetSE, exhCN.getDatasets()[cnDatasetPossition++]);
								} 
							}  
					} catch (Exception cnEXHFileException) {
						cnEXHFileAvailable = false;
					} 
				} else {
					cnEXHFileAvailable = false;
				}
				if ((exhSE.getLangs()).length > 0) {
					// added for CSV
					HashMap<Integer, Integer> offsetMap = new HashMap<>();
					// ArrayList<List<String>> dataList = new ArrayList<List<String>>();
					HashMap<Integer, String[]> csvDataMap = new HashMap<>();
					if (this.csv) {
						try {
							CsvParserSettings csvSettings = new CsvParserSettings();
							csvSettings.setMaxCharsPerColumn(-1);
							csvSettings.setMaxColumns(4096);
							CsvParser csvParser = new CsvParser(csvSettings);
							String csvPath = "resource" + File.separator + "rawexd" + File.separator +  replaceFile.substring(4, replaceFile.indexOf(".")) + ".csv";
							if (new File(csvPath).exists()) {
								List<String[]> allRows = csvParser.parseAll(new FileReader(csvPath, StandardCharsets.UTF_8));
								for (int i = 1; i < allRows.get(1).length; i++) {
									offsetMap.put(Integer.valueOf((allRows.get(1))[i]), i - 1);
								}						
								int rowNumber = allRows.size();
								for (int i = 3; i < rowNumber; i++) {
									csvDataMap.put(Integer.valueOf((allRows.get(i))[0]), Arrays.copyOfRange(allRows.get(i), 1, allRows.get(i).length));
								}
							} else {
								System.out.println("\t\tCSV file not exists! " + csvPath);
								log.warning("CSV file not exists! " + csvPath);
								continue;
							}
						} catch (Exception csvFileIndexValueException) {
							System.out.println("\t\tCSV Exception. " + csvFileIndexValueException.getMessage());
							log.warning("CSV Exception. " + csvFileIndexValueException.getMessage());
							continue;
						}
					}

					// 根據標頭檔案輪詢資源檔案
					for (EXDFPage exdfPage : exhSE.getPages()) {
						// 獲取資源檔案的CRC
						Integer exdFileCRCJA = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + this.slang + ".EXD").toLowerCase().getBytes()));
						// 進行CRC存在校驗
						log.info("Replace File : " + fileName.substring(0, fileName.indexOf(".")));
						// 提取對應的文本檔案
						SqPackIndexFile exdIndexFileJA = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePatchCRC)).getFiles().get(exdFileCRCJA);
						byte[] exdFileJA = null;
						try {
							exdFileJA = extractFile(this.pathToIndexSE, exdIndexFileJA.getOffset());
						} catch (Exception jaEXDFileException) {
							continue;
						}
						
						// added 檢查日文檔案是否損毀
						if (exdFileJA == null) {
							System.out.println("\t\t[ERROR] exdFileJA null detected!");
							log.severe("[ERROR] exdFileJA null detected!");
							System.out.println("\t\t[ERROR] exdIndexFileJA.getOffset(): " + String.valueOf(exdIndexFileJA.getOffset()));
							log.severe("[ERROR] exdIndexFileJA.getOffset(): " + String.valueOf(exdIndexFileJA.getOffset()));
							continue;
						}
						
						// 解壓文本檔案並提取內容
						EXDFFile ja_exd = new EXDFFile(exdFileJA);
						HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
						HashMap<Integer, byte[]> chsExdList = null;
						boolean cnEXDFileAvailable = true;
						if ((!this.csv) && cnEXHFileAvailable) {
							try {
								// edited
								// Integer exdFileCRCCN = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD").toLowerCase().getBytes()));
								Integer exdFileCRCCN = Integer.valueOf(FFCRC.ComputeCRC(fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_" + this.flang + ".EXD").toLowerCase().getBytes()));
								SqPackIndexFile exdIndexFileCN = (SqPackIndexFile)((SqPackIndexFolder)indexCN.get(filePatchCRC)).getFiles().get(exdFileCRCCN);
								byte[] exdFileCN = extractFile(this.pathToIndexCN, exdIndexFileCN.getOffset());
								EXDFFile chs_exd = new EXDFFile(exdFileCN);
								chsExdList = chs_exd.getEntrys();
							} catch (Exception cnEXDFileException) {
								cnEXDFileAvailable = false;
							}
						}
						// 填充中文內容，規則可以自行更改
						for (Map.Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
							// a listEntry is a key-value pair of <row entry, total offset>.
							// texhSE.getDatasetChunkSize() is the total size of a row.
							// It should be the same value for all rows in an EXH.
							Integer listEntryIndex = listEntry.getKey();
							EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
							EXDFEntry exdfEntryCN = null;
							boolean cnEntryAvailable = true;
							if (cnEXDFileAvailable) {
								try {
									byte[] data = chsExdList.get(listEntryIndex);
									exdfEntryCN = new EXDFEntry(data, exhCN.getDatasetChunkSize());
								} catch (Exception cnEntryNullException) {
									cnEntryAvailable = false;
								}
							}
							LERandomBytes chunk = new LERandomBytes(new byte[(exdfEntryJA.getChunk()).length], true, false);
							chunk.write(exdfEntryJA.getChunk());
							byte[] newFFXIVString = new byte[0];
							int stringCount = 1;
							for (EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
								// 只限文本內容
								if (exdfDatasetSE.type == 0) {
									
									// added check
									// System.out.println("\t\t\texdfDatasetSE.offset: " + exdfDatasetSE.offset);
									byte[] jaBytes = exdfEntryJA.getString(exdfDatasetSE.offset);
									// jaStr是原文直接轉成String，jafFFStr會將指令碼轉成<hex:02100103>這樣
									String jaStr = new String(jaBytes, "UTF-8");
									String jaFFStr = FFXIVString.parseFFXIVString(jaBytes);
									// 印出內容
									// 更新Chunk指針
									chunk.seek(exdfDatasetSE.offset);
									chunk.writeInt(newFFXIVString.length);
									// 更新文本內容
									// EXD/warp/WarpInnUldah.EXH -> exd/warp/warpinnuldah_xxxxx_xxxxx
									String transKey = replaceFile.substring(0, replaceFile.lastIndexOf(".")).toLowerCase() + "_" + String.valueOf(listEntryIndex) + "_" + String.valueOf(stringCount);
									if (this.csv) {

										// added CSV mode
										// a name like quest or quest/000/ClsHrv001_00003 (replaceFile)
										// an offset (exdfDatasetSE.offset)
										// System.out.println("\t\toffset: " + String.valueOf(exdfDatasetSE.offset));
										Integer offsetInteger = offsetMap.get(Integer.valueOf(exdfDatasetSE.offset));
										// System.out.println("\t\t\toffset integer: " + String.valueOf(offsetInteger));
										// System.out.println("\t\t\t\tlist entry index: " + String.valueOf(listEntryIndex));
										String[] rowStrings = csvDataMap.get(listEntryIndex);
										if (rowStrings != null) {
											String readString = rowStrings[offsetInteger];
											// System.out.println("\t\t\t\t\t" + readString);
											String newString = new String();
											boolean isHexString = false;
											if (readString != null) {
												for (int i = 0; i < readString.length(); i++) {
													char currentChar = readString.charAt(i);
													switch (currentChar) {
														case '<': {
															if ((readString.charAt(i+1) == 'h') && (readString.charAt(i+2) == 'e') && (readString.charAt(i+3) == 'x')) {
																if (isHexString) {
																	log.warning("TagInTagException!" + readString);
																	throw new Exception("TagInTagException!" + readString);
																} else {
																	isHexString = true;
																}
															}
															if (newString.length() > 0) {
																newFFXIVString = ArrayUtil.append(newFFXIVString, newString.getBytes("UTF-8"));
																newString = "";
															}
															newString += currentChar;
															break;
														}
														case '>': {
															newString += currentChar;
															if (isHexString) {
																newFFXIVString = ArrayUtil.append(newFFXIVString, HexUtils.hexStringToBytes(newString.substring(5, newString.length() - 1)));
																newString = "";
																isHexString = false;
															}
															break;
														}
														default: {
															newString += currentChar;
														}
													}
												}
												if (newString.length() > 0) {
													newFFXIVString = ArrayUtil.append(newFFXIVString, newString.getBytes("UTF-8"));
													newString = "";
												}
											} else {
												// System.out.println("\t\tCannot find listEntryIndex " + String.valueOf(listEntryIndex));
												newFFXIVString = ArrayUtil.append(newFFXIVString,  jaBytes);
											}
										} else {
											newFFXIVString = ArrayUtil.append(newFFXIVString,  jaBytes);
										}
										
									} else if (Config.getConfigResource("transtable") != null && Config.getProperty("transtable", transKey) != null && Config.getProperty("transtable", transKey).length() > 0) {
										// 0. transTable
										newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(HexUtils.hexStringToBytes(Config.getProperty("transtable", transKey))));
									} else if (pattern.matcher(jaFFStr).find()) {
										// 1. 正則
										// 如果只有一個純由英數字和特定符號組成的單字，不用處理，直接加進來
										newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
									} else if (this.exQuestMap.get(transKey) != null && ((byte[])this.exQuestMap.get(transKey)).length > 0) {
										// 2. questMap
										// 如果transkey有在任務map裡面找到且其長度>0，則引出其值
										newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(this.exQuestMap.get(transKey)));
									} else if (skipFiles.contains(replaceFile.substring(0, replaceFile.lastIndexOf(".")).toLowerCase())) {
										// 3. SkipFiles
										newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
									} else if (cnEXHFileAvailable && cnEXDFileAvailable && cnEntryAvailable && jaBytes.length > 0 && datasetMap
										
										.get(exdfDatasetSE) != null && (exdfEntryCN
										.getString(((EXDFDataset)datasetMap.get(exdfDatasetSE)).offset)).length > 0) {
										// cnResource
										byte[] chBytes = exdfEntryCN.getString(((EXDFDataset)datasetMap.get(exdfDatasetSE)).offset);
										String chFFStr = FFXIVString.parseFFXIVString(chBytes);
										if (pattern.matcher(chFFStr).find()) {
											// 如果中文字串只有一個純英數字的詞，就使用日文原文。
											// System.out.println("\tchFFStr: " + chFFStr);
											// System.out.println("\tjpHEX:   " + HexUtils.bytesToHexStringWithOutSpace(jaBytes));
											newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
										} else {
											// 如果不是，在進行簡繁轉換（如需要）之後送出。
											newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(chBytes));
										} 
									} else if (this.transMap.get(jaStr) != null && ((String)this.transMap.get(jaStr)).length() > 0) {
										// 在一個<jaStr, chStr>的HashMap裡面找對應的詞條。這個HashMap裡面只包含"EXD/Action", "EXD/ENpcResident", "EXD/BNpcName", "EXD/PlaceName"
										newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(((String)this.transMap.get(jaStr)).getBytes("UTF-8")));
									} else {
										// 原文
										newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
									} 
									// newFFXIVString是每一個dataset重置一次
									newFFXIVString = ArrayUtil.append(newFFXIVString, new byte[] { 0 });
									// System.out.println("\tnewFFXIVString: " + HexUtils.bytesToHexStringWithOutSpace(newFFXIVString));
									stringCount++;
								} 
							}
							// 打包整個Entry %4 Padding
							byte[] newEntryBody = ArrayUtil.append(chunk.getWork(), newFFXIVString);
							// System.out.println("\tnewEntryBody: " + HexUtils.bytesToHexStringWithOutSpace(newEntryBody));
							int paddingSize = 4 - newEntryBody.length % 4;
							paddingSize = (paddingSize == 0) ? 4 : paddingSize;
							LERandomBytes entryBody = new LERandomBytes(new byte[newEntryBody.length + paddingSize]);
							entryBody.write(newEntryBody);
							// 轉成byte[] 存入Map
							// System.out.println("\tnetryBody.getWork(): " + HexUtils.bytesToHexStringWithOutSpace(entryBody.getWork()));
							listEntry.setValue(entryBody.getWork());
						} 
						// 準備修改好的內容
						byte[] exdfFile = (new EXDFBuilder(jaExdList)).buildExdf();
						byte[] exdfBlock = (new BinaryBlockBuilder(exdfFile)).buildBlock();
						// 填充修改好的內容到新檔案
						leIndexFile.seek(exdIndexFileJA.getPt() + 8L);
						leIndexFile.writeInt((int)(datLength / 8L));
						datLength += exdfBlock.length;
						leDatFile.write(exdfBlock);
					}
				}
			} 
		} 
		leDatFile.close();
		leIndexFile.close();
		System.out.println("Replace Completed");
		log.info("Replace Completed");
	}
	
	private void initFileList() throws Exception {
		HashMap<Integer, SqPackIndexFolder> indexSE = (new SqPackIndex(this.pathToIndexSE)).resloveIndex();
		Integer filePathCRC = Integer.valueOf(FFCRC.ComputeCRC("exd".toLowerCase().getBytes()));
		Integer rootFileCRC = Integer.valueOf(FFCRC.ComputeCRC("root.exl".toLowerCase().getBytes()));
		SqPackIndexFile rootIndexFileSE = (SqPackIndexFile)((SqPackIndexFolder)indexSE.get(filePathCRC)).getFiles().get(rootFileCRC);
		byte[] rootFile = extractFile(this.pathToIndexSE, rootIndexFileSE.getOffset());
		BufferedReader rootBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(rootFile)));
		String fileName;
		while ((fileName = rootBufferedReader.readLine()) != null)
			this.fileList.add("EXD/" + (fileName.contains(",") ? fileName.split(",")[0] : fileName) + ".EXH"); 
	}
	
	private byte[] convertString(byte[] chBytes) {
		if (this.dlang.equals("CHT")) {
			try {
				LERandomBytes inBytes = new LERandomBytes(chBytes, true, false);
				LERandomBytes outBytes = new LERandomBytes();
				Vector<byte[]> outBytesVector = (Vector)new Vector<>();
				byte[] newFFXIVStringBytes = new byte[0];
				while (inBytes.hasRemaining()) {
					byte b = inBytes.readByte();
					if (b == 0x02) {
						outBytesVector.add(outBytes.getWork());
						outBytes.clear();
						parsePayload(inBytes, outBytes);
						outBytesVector.add(outBytes.getWork());
						outBytes.clear();
						continue;
					} 
					outBytes.writeByte(b);
				} 
				outBytesVector.add(outBytes.getWork());
				outBytes.clear();
				for (byte[] bytes : outBytesVector) {
					if (bytes.length > 0) {
						if (bytes[0] == 0x02) {
							newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, bytes);
							continue;
						} 
						newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, JianFan.getInstance().j2f(new String(bytes, "UTF-8")).getBytes("UTF-8"));
					} 
				} 
				return newFFXIVStringBytes;
			} catch (Exception exception) {} 
		}
		return chBytes;
	}
	
	private static void parsePayload(LERandomBytes inBytes, LERandomBytes outBytes) {
		int possition = inBytes.position();
		int type = inBytes.readByte() & 0xFF;
		int size = getBodySize(inBytes.readByte() & 0xFF, inBytes);
		byte[] body = new byte[size - 1];
		inBytes.readFully(body);
		inBytes.skip();
		long fullLength = (inBytes.position() - possition + 1);
		byte[] full = new byte[(int)fullLength];
		inBytes.seek(possition - 1);
		inBytes.readFully(full);
		outBytes.write(full);
	}
	
	private static int getBodySize(int payloadSize, LERandomBytes inBytes) {
		if (payloadSize < 0xF0)
			return payloadSize; 
		switch (payloadSize) {
			case 0xF0:
				return inBytes.readInt8();
			case 0xF1:
			case 0xF2:
				return inBytes.readInt16();
			case 0xFA:
				return inBytes.readInt24();
			case 0xFE:
				return inBytes.readInt32();
		} 
		return payloadSize;
	}
	
	private byte[] extractFile(String pathToIndex, long dataOffset) throws IOException, FileNotFoundException {
		String pathToOpen = pathToIndex;
		// System.out.println("\tpathToOpen: " + pathToOpen);
		// L代表long數字
		int datNum = (int)((dataOffset & 0xF) / 2L);
		dataOffset -= dataOffset & 0xF;
		pathToOpen = pathToOpen.replace("index2", "dat" + datNum);
		pathToOpen = pathToOpen.replace("index", "dat" + datNum);
		SqPackDatFile datFile = new SqPackDatFile(pathToOpen);
		// change the boolean to toggle the Debug mode.
		byte[] data = datFile.extractFile(dataOffset * 8L, false);
		datFile.close();
		return data;
	}
	
	private int datasetStringCount(EXDFDataset[] datasets) {
		int count = 0;
		for (EXDFDataset dataset : datasets) {
			if (dataset.type == 0)
				count++; 
		} 
		return count;
	}
}
