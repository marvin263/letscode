package com.syniverse.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.FileTableBean;
import com.syniverse.info.ProcessingFileInfo;
import com.syniverse.io.IOUtil;

public class MoveFileAndBuildOrgnf {
	public static final String Suffix_copydone = "_copydone";
	public static final String Suffix_unzipdone = "_unzipdone";
	public static final String Suffix_ignore_tmp = ".tmp";
	public static final String Suffix_ignore_temp = ".temp";

	private static final Log LOGGER = LogFactory
			.getLog(MoveFileAndBuildOrgnf.class);

	/**
	 * What this method does
	 * 
	 * 1. Move file's from original user uploaded path to working directory
	 * <p>
	 * 2. unzip the file if necessary
	 * 
	 * 3. Construct ProcessingFileInfo. NOT set
	 * ProcessingFileInfo.originalFileTotalRowCount in this method
	 * 
	 * originalFileTotalRowCount will returned by Splitting file or from
	 * FEED_SPLIT_FILE.SPLIT_TOTAL_ROW_COUNT
	 * 
	 * @param conn
	 * @param fb
	 * @return
	 * @throws RuntimeException
	 */
	public ProcessingFileInfo createOrgnfInfoByFeedLog(Connection conn,
			FileTableBean fb) throws RuntimeException {

		String workingDir = PCSLoader.getWorkingDir();
		String orgnFilename = null;

		String datafilename = null;
		String datafileFullPath = null;
		String datafileParentFolder = null;
		// upload file
		if (fb.isUpload()) {
			orgnFilename = fb.filenameInDB;
			String orgnDir = PCSLoader.getPCSLoadResultDir(fb);
			String orgnFullPath = orgnDir + "/" + orgnFilename;
			// original file should be moved to working dir.
			// after it's moved to working dir
			// it's full path will be <code>workingdirFullPath</code>
			String workingdirFullPath = workingDir + "/" + orgnFilename;

			String moveDoneFullPath = workingDir + "/" + orgnFilename
					+ Suffix_copydone;
			String unzipDoneFullPath = workingDir + "/" + orgnFilename
					+ Suffix_unzipdone;

			copyIfNecessary(orgnFullPath, workingdirFullPath, moveDoneFullPath);

			String[] arrayZipData = deduceZipAndDatafilename(orgnFilename,
					workingdirFullPath);
			datafilename = arrayZipData[1];
			unzipIfNecessary(arrayZipData, workingdirFullPath, datafilename,
					unzipDoneFullPath);
			datafileFullPath = workingDir + "/" + datafilename;
			datafileParentFolder = workingDir;
			// never touch it
			if (fb.getTotalRow() == -1) {
				int totalRow = IOUtil.getLineCount(datafileFullPath, null);
				fb.setTotalRow(totalRow);
				// update totalRow if necessary
				DBManipulate.updateTotalRow(fb, conn, totalRow);
			}
		}
		// range
		else {
			orgnFilename = decideRangeFilename(fb.getRangeLower(),
					fb.getRangeUpper());
			datafilename = orgnFilename;
			datafileFullPath = workingDir + "/" + datafilename;
			datafileParentFolder = workingDir;

			generateFileByRange(fb.getRangeLower(), fb.getRangeUpper(),
					datafileFullPath);
		}

		ProcessingFileInfo orgnfInfo = new ProcessingFileInfo(orgnFilename);
		orgnfInfo.setDatafileFullPath(datafileFullPath);
		orgnfInfo.setDatafilename(datafilename);
		orgnfInfo.setDatafileParentFolder(datafileParentFolder);
		orgnfInfo.setGroupID(fb.getGroupID());
		// set orgnfInfo by PCSFileLogTableBean

		return orgnfInfo;
	}

	/**
	 * 1. Move original file to working directory
	 * <p>
	 * 2. CREATE moveDone file (under workingdirFullPath)
	 * <p>
	 * 3. DELETE pickDone file (under orgnFullPath)
	 * 
	 * 
	 * @param orgnFullPath
	 * @param workingdirFullPath
	 * @param copyDoneFullPath
	 * @return
	 */
	private boolean copyIfNecessary(String orgnFullPath,
			String workingdirFullPath, String copyDoneFullPath) {
		LOGGER.info("copyDoneFullPath=" + copyDoneFullPath);
		// Still not move to working dir
		if (new File(copyDoneFullPath).exists() == false) {
			LOGGER.info("_copydone does not exist. copying....");
			boolean mov = _copyOrgn2Workingdir(orgnFullPath, workingdirFullPath);
			return mov;
		}
		// already copied here
		else {
			LOGGER.info("_copydone has already existed. unzip if necessary");
			return true;
		}
	}

	/**
	 * 1. Move original file to working directory
	 * <p>
	 * 2. Create moveDone file (under workingdirFullPath)
	 * <p>
	 * 3. Delete pickDone file (under orgnFullPath)
	 * 
	 * 
	 * @param orgnFullPath
	 * @param workingdirFullPath
	 * @return
	 */
	private boolean _copyOrgn2Workingdir(String orgnFullPath,
			String workingdirFullPath) {
		// 1. Move original file to working directory
		boolean rnm = IOUtil.copy2(orgnFullPath, workingdirFullPath);
		// 2. Create copyDone file (under workingdirFullPath)
		boolean createtmp = IOUtil.createNewFile(workingdirFullPath
				+ Suffix_copydone);
		return rnm && createtmp;
	}

	/**
	 * 1. unzip the file
	 * <p>
	 * 2. create zipDone file
	 * 
	 * If necessary, unzip file
	 * 
	 * 1. unzip the file
	 * <p>
	 * 2. CREATE zipDone file
	 * 
	 * 
	 * @param unzipDoneFullPath
	 * @param zipfullpath
	 * 
	 * @return
	 */
	private boolean unzipIfNecessary(String[] arrayZipData,
			String workingdirFullPath, String extractWhat,
			String unzipDoneFullPath) {
		LOGGER.info("unzipDoneFullPath=" + unzipDoneFullPath);
		String zipfilename = arrayZipData[0];
		// NOT zipped file
		if (CommUtil.isEmpty(zipfilename) == true) {
			// NOT zipped file, we assume it's successful
			return true;
		}
		// zipped file
		else {
			// has NOT been unzipped
			if (new File(unzipDoneFullPath).exists() == false) {
				LOGGER.info("zip file and _unzipdone does not exist. unzip....");
				// 1. unzip the file
				boolean unzip = IOUtil.unzip(workingdirFullPath, extractWhat);
				// 2. create zipDone file
				boolean createtmp = IOUtil.createNewFile(unzipDoneFullPath);
				return unzip && createtmp;
			}
			// Has been unzipped
			else {
				return true;
			}
		}
	}

	private String[] deduceZipAndDatafilename(String orgnFilename,
			String workingdirFullPath) {
		String[] arrayZipData = new String[2];
		// zipfilename
		arrayZipData[0] = zipfilenameORnull(orgnFilename);

		// NOT zipped file
		if (CommUtil.isEmpty(arrayZipData[0]) == true) {
			// NOT zipped file, so, orgnFilename is datafilename
			arrayZipData[1] = orgnFilename;
		}
		// Yes, it's zipped file
		else {
			// let's find out what's the datafilename
			arrayZipData[1] = IOUtil.extractWhat(workingdirFullPath);
		}
		return arrayZipData;
	}

	private String decideRangeFilename(String rangeLower, String rangeUpper) {
		return rangeLower + "_" + rangeUpper + ".dat";
	}

	private long calcLastRowProcessed(FileTableBean fb) {
		// int totalRow = 0;
		long successRow = fb.getSuccessRow();
		long failRow = fb.getFailRow();
		long dupsRow = fb.getDupsRow();
		long alreadyProcessed = successRow + failRow + dupsRow;
		return alreadyProcessed + 1;
	}

	/**
	 * if <code>orgnFilename</code> is a zip file(ends with .zip, .gz, .gzip),
	 * return the name directly, otherwise, return null(if it's 9999.csv, we
	 * still return null);
	 * 
	 * @param orgnFilename
	 * @return
	 */
	private String zipfilenameORnull(String orgnFilename) {
		boolean isZip = IOUtil.isZip_by_LastestSuffix(orgnFilename);
		if (isZip == true) {
			return orgnFilename;
		} else {
			// Has suffix or not, but it is NOT zip file. NOT zip file
			return null;
		}
	}

	public static void main(String[] args) {
		BigInteger bi = new BigInteger(
				"1230099999999999999999999999999999999999999999999999999999999999999999");
		StringBuilder sb = new StringBuilder("aaa");
		sb.setLength(4);
		generateFileByRange("0000000123", "289", "c:/a.txt");
		System.out.println(TimeZone.getDefault());
	}

	public static void generateFileByRange(String rangeLower,
			String rangeUpper, String workingdirFullPath) {
		new File(workingdirFullPath).delete();
		int writeThreshold = 2000;
		int finalLength = rangeLower.length();
		BufferedWriter bw = IOUtil.createBufferedWriter(workingdirFullPath,
				null, true);
		try {
			BigInteger from = new BigInteger(rangeLower);
			BigInteger to = new BigInteger(rangeUpper);
			int total = to.subtract(from).intValue();

			StringBuilder sb = new StringBuilder(CommUtil.int2str(from,
					finalLength) + IOUtil.ENTER);
			BigInteger last = from;
			for (int i = 1; i <= total; i++) {
				BigInteger current = last.add(BigInteger.ONE);
				sb.append(CommUtil.int2str(current, finalLength) + IOUtil.ENTER);
				if (i % writeThreshold == 0) {
					IOUtil.writeToFile(bw, sb.toString());
					sb.setLength(0);
				}
				last = current;
			}

			if (sb.length() != 0) {
				IOUtil.writeToFile(bw, sb.toString());
			}

		} finally {
			IOUtil.closeWriter(bw);
		}
	}
}
