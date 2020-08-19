import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ReadSubwayData {
	// Byte order mask (BOM) for UTF-8 encording
	public static final String UTF8_BOM = "\uFEFF";
	
	public static List<String[]> parseVertices(String path) throws IOException{
		// read vertices data from file in UTF-8 encoding
		// each line contains vertices (stations) information
		// UTF-8 make it possible to store extra character sets such Hangul 
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
		List<String[]> vertices = new ArrayList<String[]>();
		
		// BOM should not be included in the parsed data
		String line = in.readLine();
		if ( line.startsWith(UTF8_BOM) )
			line = line.substring(1);
		
		// read each line, and separate the line to several elements
		do {
			vertices.add(line.split(","));
		} while ( (line = in.readLine()) != null);
		
		in.close();
		
		return vertices;
	}
	
	public static List<String[]> parseEdges(String path) throws IOException {
		// basically, each line contains edges (intervals between stations) information like vertices
		return parseVertices(path);
	}
	
	public static void printVerticeAndEdges(List<String[]> vertices, List<String[]> edges) {
		// print each vertex or edge in console
		// types of data are string, but you can parse data for usage
		for(String[] v : vertices) {
			String msg = String.format("Line %d: (Station %s) (Code %s) (Loc, %.3f and %.3f)",
					Integer.parseInt(v[0]), v[1], v[2], Double.parseDouble(v[3]), Double.parseDouble(v[4]));
			System.out.println(msg);
		}
		
		for(String[] e : edges) {
			String msg = String.format("On Line %d: from (Station %s, %s) to (Station %s, %s) (Distance %.5f)", 
					Integer.parseInt(e[0]), e[1], e[2], e[3], e[4], Double.parseDouble(e[5]));
			System.out.println(msg);
		}
	}
	
	//////////////////////////////////////////////////////////////////
	// Example Usage
	//////////////////////////////////////////////////////////////////
	public static void main(String[] args) throws IOException {
		List<String[]> v = parseVertices("subway_vertice.txt");
		List<String[]> e = parseEdges("subway_edges.txt");
		
		printVerticeAndEdges(v, e);
	}
	//////////////////////////////////////////////////////////////////
}
