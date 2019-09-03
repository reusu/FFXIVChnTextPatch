package name.yumao.ffxiv.chn.replace;

import name.yumao.ffxiv.chn.builder.BinaryBlockBuilder;
import name.yumao.ffxiv.chn.builder.EXDFBuilder;
import name.yumao.ffxiv.chn.model.*;
import name.yumao.ffxiv.chn.swing.PercentPanel;
import name.yumao.ffxiv.chn.util.nlpcn.JianFan;
import name.yumao.ffxiv.chn.util.res.Config;
import name.yumao.ffxiv.chn.util.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;


public class ReplaceEXDF {

	private String pathToIndexSE;
	private String pathToIndexCN;
	private List<String> fileList;

	private HashMap<String, byte[]> exQuestMap;
	private HashMap<String, String> transMap;
	private PercentPanel percentPanel;

	private String lang;

	public ReplaceEXDF(String pathToIndexSE, String pathToIndexCN, PercentPanel percentPanel) {
		this.pathToIndexSE = pathToIndexSE;
		this.pathToIndexCN = pathToIndexCN;
		this.fileList = new ArrayList<String>();
		this.exQuestMap = new HashMap();
		this.transMap = new HashMap<>();
		this.lang = Config.getProperty("Language");
	}

	public void replace() throws Exception {
		System.out.println("Loading Transtable");
		File transtable = new File("conf" + File.separator + "transtable.properties");
		if (transtable.exists()){
			transtable.delete();
		}
		transtable.createNewFile();
		Config.setConfigResource("transtable", "conf" + File.separator + "transtable.properties");
		System.out.println("Loading Root File...");
		this.initFileList();
		String patternStr = "^[a-zA-Z0-9_'./\\s]*$";
		Pattern pattern = Pattern.compile(patternStr);
		System.out.println("Loading Index File...");
		HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
		HashMap<Integer, SqPackIndexFolder> indexCN = new SqPackIndex(pathToIndexCN).resloveIndex();
		System.out.println("Loading Index Complete");
		LERandomAccessFile leIndexFile  = new LERandomAccessFile(pathToIndexSE, "rw");
		LERandomAccessFile leDatFile    = new LERandomAccessFile(pathToIndexSE.replace("index", "dat0"), "rw");
		long datLength = leDatFile.length();
		leDatFile.seek(datLength);
		try {
			System.out.println("Loading exQuestMap...");
			exQuestMap = new EXDFUtil(pathToIndexSE, pathToIndexCN, fileList).exCompleteJournalSE(exQuestMap);
			exQuestMap = new EXDFUtil(pathToIndexSE, pathToIndexCN, fileList).exQuestSE(exQuestMap);
			System.out.println("Loading exQuestMap Complete");
		}catch (Exception mapLoadingException){}
		try {
			System.out.println("Loading transMap...");
			transMap = new EXDFUtil(pathToIndexSE, pathToIndexCN, fileList).transMap(transMap);
			System.out.println("Loading transMap Complete");
		}catch (Exception mapLoadingException){
		}
		List skipFiles = Arrays.asList(Config.getProperty("SkipFiles").toLowerCase().split("[|]"));
		// 根据传入的文件进行遍历
        int fileCount = 0;
		for (String replaceFile : fileList) {
			percentPanel.percentShow((double)(++fileCount) / (double)fileList.size());
			if (replaceFile.toUpperCase().endsWith(".EXH")) {
				System.out.println("Now File : " + replaceFile);
				// 准备好文件目录名和文件名
				String filePatch = replaceFile.substring(0, replaceFile.lastIndexOf("/"));
				String fileName = replaceFile.substring(replaceFile.lastIndexOf("/") + 1);
				// 计算文件目录CRC
				Integer filePatchCRC = FFCRC.ComputeCRC(filePatch.toLowerCase().getBytes());
				// 计算头文件CRC
				Integer exhFileCRC = FFCRC.ComputeCRC(fileName.toLowerCase().getBytes());
				// 解压并且解析头文件
				if(indexSE.get(filePatchCRC) == null) continue;
				SqPackIndexFile exhIndexFileSE = indexSE.get(filePatchCRC).getFiles().get(exhFileCRC);
				if (exhIndexFileSE == null) continue;
				byte[] exhFileSE = extractFile(pathToIndexSE, exhIndexFileSE.getOffset());
				EXHFFile exhSE = new EXHFFile(exhFileSE);
				EXHFFile exhCN = null;
				HashMap<EXDFDataset, EXDFDataset> datasetMap = new HashMap();
				boolean cnEXHFileAvailable = true;
				try {
					SqPackIndexFile exhIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exhFileCRC);
					byte[] exhFileCN = extractFile(pathToIndexCN, exhIndexFileCN.getOffset());
					exhCN = new EXHFFile(exhFileCN);
					// 添加对照StringDataset
					int cnDatasetPossition = 0;
					if (datasetStringCount(exhSE.getDatasets()) > 0 && datasetStringCount(exhSE.getDatasets()) == datasetStringCount(exhCN.getDatasets())) {
						for (EXDFDataset datasetSE : exhSE.getDatasets()) {
							if (datasetSE.type == 0) {
								while (exhCN.getDatasets()[cnDatasetPossition].type != 0) {
									cnDatasetPossition++;
								}
								datasetMap.put(datasetSE, exhCN.getDatasets()[cnDatasetPossition++]);
							}
						}
					}
				} catch (Exception cnEXHFileException) {
					cnEXHFileAvailable = false;
				}
				if (exhSE.getLangs().length > 0) {
					// 根据头文件 轮询资源文件
					for (EXDFPage exdfPage : exhSE.getPages()) {
						// 获取资源文件的CRC
						Integer exdFileCRCJA = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_JA.EXD")).toLowerCase().getBytes());
						// 进行CRC存在校验
						System.out.println("Replace File : " + fileName.substring(0, fileName.indexOf(".")));
						// 提取对应的文本文件
						SqPackIndexFile exdIndexFileJA = indexSE.get(filePatchCRC).getFiles().get(exdFileCRCJA);
						byte[] exdFileJA = null;
						try {
							exdFileJA = extractFile(pathToIndexSE, exdIndexFileJA.getOffset());
						}catch (Exception jaEXDFileException){
							continue;
						}
						// 解压本文文件 提取内容
						EXDFFile ja_exd = new EXDFFile(exdFileJA);
						HashMap<Integer, byte[]> jaExdList = ja_exd.getEntrys();
						HashMap<Integer, byte[]> chsExdList = null;
						boolean cnEXDFileAvailable = true;
						if (cnEXHFileAvailable){
							try{
								Integer exdFileCRCCN = FFCRC.ComputeCRC((fileName.replace(".EXH", "_" + String.valueOf(exdfPage.pageNum) + "_CHS.EXD")).toLowerCase().getBytes());
								SqPackIndexFile exdIndexFileCN = indexCN.get(filePatchCRC).getFiles().get(exdFileCRCCN);
								byte[] exdFileCN = extractFile(pathToIndexCN, exdIndexFileCN.getOffset());
								EXDFFile chs_exd = new EXDFFile(exdFileCN);
								chsExdList = chs_exd.getEntrys();
							}catch (Exception cnEXDFileException){
								cnEXDFileAvailable = false;
							}
						}
						// 填充中文内容 如果有需要可以自行修改规则
						for (Entry<Integer, byte[]> listEntry : jaExdList.entrySet()) {
							Integer listEntryIndex = listEntry.getKey();
							EXDFEntry exdfEntryJA = new EXDFEntry(listEntry.getValue(), exhSE.getDatasetChunkSize());
							EXDFEntry exdfEntryCN = null;
							boolean cnEntryAvailable = true;
							if (cnEXDFileAvailable){
								try{
									byte[] data = chsExdList.get(listEntryIndex);
									exdfEntryCN = new EXDFEntry(data, exhCN.getDatasetChunkSize());
								}catch (Exception cnEntryNullException){
									cnEntryAvailable = false;
								}
							}
							LERandomBytes chunk = new LERandomBytes(new byte[exdfEntryJA.getChunk().length], true, false);
							chunk.write(exdfEntryJA.getChunk());
							byte[] newFFXIVString = new byte[0];
							int stringCount = 1;
							for ( EXDFDataset exdfDatasetSE : exhSE.getDatasets()) {
								// 只限文本内容
								if (exdfDatasetSE.type == 0) {
									byte[] jaBytes = exdfEntryJA.getString(exdfDatasetSE.offset);
									String jaStr = new String(jaBytes, "UTF-8");
									String jaFFStr = FFXIVString.parseFFXIVString(jaBytes);
									// 打印内容
									// 更新Chunk指针
									chunk.seek(exdfDatasetSE.offset);
									chunk.writeInt(newFFXIVString.length);
									// 更新文本内容
									String transKey = replaceFile.substring(0, replaceFile.lastIndexOf(".")).toLowerCase() + "_" + String.valueOf(listEntryIndex) + "_" + String.valueOf(stringCount);
									if (Config.getConfigResource("transtable") != null && Config.getProperty("transtable", transKey) != null && Config.getProperty("transtable", transKey).length() >0 ){
										// 权重0 transTable
										newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(HexUtils.hexStringToBytes(Config.getProperty("transtable", transKey))));
									}else if (pattern.matcher(jaFFStr).find()){
										// 权重1 正则
										newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
									}else if (exQuestMap.get(transKey) != null && exQuestMap.get(transKey).length > 0){
										// 权重2 questMap
                                        newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(exQuestMap.get(transKey)));
                                    }else if (skipFiles.contains(replaceFile.substring(0, replaceFile.lastIndexOf(".")).toLowerCase())){
										// 权重3 skipFile
										newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
									}else {
										// 权重E cnResouce
										if (cnEXHFileAvailable && cnEXDFileAvailable && cnEntryAvailable
												&& jaBytes.length > 0
												&& datasetMap.get(exdfDatasetSE)!=null
												&& exdfEntryCN.getString(datasetMap.get(exdfDatasetSE).offset).length > 0){
											byte[] chBytes = exdfEntryCN.getString(datasetMap.get(exdfDatasetSE).offset);
											String chFFStr = FFXIVString.parseFFXIVString(chBytes);
											if(pattern.matcher(chFFStr).find()){
												newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
											}else {
												newFFXIVString = ArrayUtil.append(newFFXIVString, convertString(chBytes));
											}
										}else if (transMap.get(jaStr)!=null && transMap.get(jaStr).length()>0){
                                            newFFXIVString = ArrayUtil.append(newFFXIVString, transMap.get(jaStr).getBytes("UTF-8"));
                                        }else {
											newFFXIVString = ArrayUtil.append(newFFXIVString, jaBytes);
										}
									}
									newFFXIVString = ArrayUtil.append(newFFXIVString, new byte[]{0x00});
									stringCount ++;
								}
							}
							// 打包整个Entry %4 Padding
							byte[] newEntryBody = ArrayUtil.append(chunk.getWork(), newFFXIVString);
							int paddingSize = 4 - (newEntryBody.length % 4);
							paddingSize = paddingSize == 0 ? 4 : paddingSize;
							LERandomBytes entryBody = new LERandomBytes(new byte[newEntryBody.length + paddingSize]);
							entryBody.write(newEntryBody);
							// 转成byte[] 存入Map
							listEntry.setValue(entryBody.getWork());
						}
						// 准备好修改好的内容
						byte[] exdfFile = new EXDFBuilder(jaExdList).buildExdf();
						byte[] exdfBlock = new BinaryBlockBuilder(exdfFile).buildBlock();
						// 填充修改后的内容到新文件
						leIndexFile.seek(exdIndexFileJA.getPt() + 8);
						leIndexFile.writeInt((int) (datLength / 8));
						datLength += exdfBlock.length;
						leDatFile.write(exdfBlock);
					}
				}
			}
		}
		leDatFile.close();
		leIndexFile.close();
		System.out.println("Replace Complete");
	}

	private void initFileList() throws Exception{
		HashMap<Integer, SqPackIndexFolder> indexSE = new SqPackIndex(pathToIndexSE).resloveIndex();
		Integer filePathCRC = FFCRC.ComputeCRC("exd".toLowerCase().getBytes());
		Integer rootFileCRC = FFCRC.ComputeCRC("root.exl".toLowerCase().getBytes());
		SqPackIndexFile rootIndexFileSE = indexSE.get(filePathCRC).getFiles().get(rootFileCRC);
		byte[] rootFile = extractFile(pathToIndexSE, rootIndexFileSE.getOffset());
		BufferedReader rootBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(rootFile)));
		String fileName;
		while((fileName = rootBufferedReader.readLine()) != null){
			fileList.add("EXD/" + (fileName.contains(",") ? fileName.split(",")[0] : fileName) + ".EXH");
		}
	}

	private byte[] convertString(byte[] chBytes){
		if (lang.equals("CHT")) {
			try {
				LERandomBytes inBytes = new LERandomBytes(chBytes, true, false);
				LERandomBytes outBytes = new LERandomBytes();
				Vector<byte[]> outBytesVector = new Vector<byte[]>();
				byte[] newFFXIVStringBytes = new byte[0];
				while (inBytes.hasRemaining()){
					byte b = inBytes.readByte();
					if (b == 0x02) {
						outBytesVector.add(outBytes.getWork());
						outBytes.clear();
						parsePayload(inBytes, outBytes);
						outBytesVector.add(outBytes.getWork());
						outBytes.clear();
					} else {
						outBytes.writeByte(b);
					}
				}
				outBytesVector.add(outBytes.getWork());
				outBytes.clear();
				for(byte[] bytes: outBytesVector){
					if (bytes.length > 0) {
						if (bytes[0] == 0x02) {
							newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, bytes);
						} else {
							newFFXIVStringBytes = ArrayUtil.append(newFFXIVStringBytes, JianFan.getInstance().j2f(new String(bytes, "UTF-8")).getBytes("UTF-8"));
						}
					}
				}
				return newFFXIVStringBytes;
			} catch (Exception e) {
			}
		}
		return chBytes;
	}

	private static void parsePayload(LERandomBytes inBytes, LERandomBytes outBytes) {
		int possition = inBytes.position();
		int type = inBytes.readByte() & 0xFF;
		int size = getBodySize((inBytes.readByte() & 0xFF), inBytes);
		byte[] body = new byte[size - 1];
		inBytes.readFully(body);
		inBytes.skip();
		long fullLength = inBytes.position() - possition + 1;
		byte[] full = new byte[(int)fullLength];
		inBytes.seek(possition - 1);
		inBytes.readFully(full);
		outBytes.write(full);
	}

	private static int getBodySize(int payloadSize, LERandomBytes inBytes) {
		if (payloadSize < 0xF0)
			return payloadSize;
		switch (payloadSize){
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
		int datNum = (int) ((dataOffset & 0xF) / 2L);
		dataOffset -= (dataOffset & 0xF);
		pathToOpen = pathToOpen.replace("index2", "dat" + datNum);
		pathToOpen = pathToOpen.replace("index", "dat" + datNum);
		SqPackDatFile datFile = new SqPackDatFile(pathToOpen);
		byte[] data = datFile.extractFile(dataOffset * 8L, false);
		datFile.close();
		return data;
	}
	private int datasetStringCount(EXDFDataset[] datasets){
		int count = 0;
		for(EXDFDataset dataset : datasets){
			if(dataset.type == 0){
				count ++;
			}
		}
		return count;
	}
}
