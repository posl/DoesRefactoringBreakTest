package utils.file;



import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyFileReadWriteUtils {
	BufferedReader br;
	File file;

	public MyFileReadWriteUtils(String file_name) throws FileNotFoundException {
		file = new File(file_name);
		FileReader filereader = new FileReader(file);
		br = new BufferedReader(filereader);
	}

	public static String readAll(final String path) throws IOException {
		return Files.lines(Paths.get(path), StandardCharsets.UTF_8)
				.collect(Collectors.joining(System.getProperty("line.separator")));
	}

	public static void writeAll(String dir, String filename, List<String> contents) throws IOException {
		createDirs(dir+filename);
		Files.write(Paths.get(dir, filename), contents,
				StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
	}
	public static void writeAll(String dir, String filename, String contents) throws IOException {
		FileUtils.writeStringToFile( new File(dir+filename), contents, "utf-8" );
	}
	public static void createDirs(String file_path, boolean isFile) {
		Path path = Paths.get(file_path);
		if(isFile){
			file_path=path.getParent().toString();
		}
		File file = new File(file_path);
		file.mkdirs(); //for several levels, without the "s" for one level
	}
	public static void createDirs(String file_path) {
		createDirs(file_path, true);
	}

	public String readLine() throws IOException {
		return br.readLine();
	}

	public BufferedReader getBufferedReader() {
		return br;
	}

	public void close() throws IOException {
		if (br != null) {
			br.close();
		}
	}

//	public static List<CSVRecord> getCSVRecords(String fileName) throws IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "utf-8"));
//		CSVParser parse = CSVFormat.EXCEL.parse(br);
//		List<CSVRecord> recordList = parse.getRecords();
//		return recordList;
//	}

	public static List<String> getFileList(String dir, String extension){
		List<String> result = null;
		try (Stream<Path> walk = Files.walk(Paths.get(dir))) {
			result = walk.map(x -> x.toString()).filter(f -> f.endsWith(extension)).collect(Collectors.toList());
		} catch (IOException e) {
			System.err.println("No test methods were executed (Not found XML file)");
		}
		return result;
	}

}
