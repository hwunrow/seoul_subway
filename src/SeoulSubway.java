import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SeoulSubway extends ReadSubwayData{
	
	public static void main(String[] args) throws IOException{
		if (args.length == 4){
			
			//Check to see if source and destination stations are the same
			if(args[0].equals(args[1])){
				System.out.println("Source and desintation are the same, you don't need to go ANYWHERE!");
				return;
			}
			
			//read vertices and edges from file
			List<String[]> vertices_string = parseVertices("subway_vertice.txt");
			List<String[]> edges_string = parseEdges("subway_edges.txt");
			
			//initialize Vertice and Edge lists to store data from file
			List<Graph.Vertice> vertice = new ArrayList<Graph.Vertice>(vertices_string.size());
			List<Graph.Edge> edge = new ArrayList<Graph.Edge>(edges_string.size());
			
			//for each row in the subway_vertice.txt file a new vertex is created and added to the Vertice List
			for(String[] v : vertices_string) {
				Graph.Vertice a = new Graph.Vertice(Integer.parseInt(v[0]),v[1],v[2],Double.parseDouble(v[3]),Double.parseDouble(v[4]));
				vertice.add(a);				
			}
			
			//for each row in the subway_edges.txt file a new edge is created and added to the Edge List
			for(String[] e : edges_string){
				Graph.Edge b = new Graph.Edge(Integer.parseInt(e[0]), e[1], e[2], e[3], e[4], Double.parseDouble(e[5]));
				edge.add(b);
			}
			
			
			//creates an edge between transfer stations
			for(Graph.Vertice i : vertice){
				for(Graph.Vertice j : vertice)
					if(i.name.equals(j.name) && !i.code.equals(j.code)){
						Graph.Edge b = new Graph.Edge(10, i.getName(), i.getCode(), j.getName(), j.getCode(), 0.0);
						edge.add(b);
					}
			}
			
			//initialize a graph using the Vertice and Edge lists just created
			//this graph represents the subway network
			Graph subway = new Graph(vertice, edge);
			
			//for each edge in the graph we assign a start and finish vertex to make it directed
			for(Graph.Edge e : subway.Edges){
				e.setStart(subway.findVertice(e.station_code1));
				e.setFinish(subway.findVertice(e.station_code2));
			}
			
			//we find the source and destination vertices given as the first two arguments
			Graph.Vertice source = subway.findVertice(args[0]);
			Graph.Vertice destination = subway.findVertice(args[1]);
			
			
			
			//use a variation of Dijkstra's algorithm to return a list of transfer stations and distances between them visited along the 
			//path from the destination station to the source station
			//a transfer station is defined to be a station where you switch from one line to another
			List<Graph.Vertice> transfer_stations = subway.Dijkstra(subway, source, destination);
			
			//calculate the total distance from the source vertex to destination vertex by summing up the distances between transfer nodes
			double total_distance = 0;
			for(Graph.Vertice v : transfer_stations){
				total_distance += v.getTransferDistance();
			}
			
			//output the result of the path in the format:
				//STATION_CODE1, STATION_CODE2, ALL_DISTANCE, TRANSFER_CODE1, TRANSFER_CODE2, … , DISTANCE1, DISTANCE2, … 		
			String msg = String.format("%s, %s, %f", source.getCode(), destination.getCode(), total_distance);
	      	System.out.print(msg);
	      	for(Graph.Vertice v : reversed(transfer_stations)){
	      		if(v != destination)
	      			System.out.print(", " + v.getCode());
	      	}
	      	for(Graph.Vertice v : reversed(transfer_stations)){
	      		String msg2 = String.format(", %f", v.getTransferDistance());
	      		System.out.print(msg2);
	      	}      	
      	
		}
		
		else{
            // Print error message if wrong given arguments
            System.out.println("Wrong argument is given");
		}		
	}
	
	//reverses the order of a list of vertices by utilizing the ListIterator Class
	//this was needed since my dijkstra's algorithm returns a path from destination to source;
	public static List<Graph.Vertice> reversed(List<Graph.Vertice> original){
		List<Graph.Vertice> reverse = new ArrayList<Graph.Vertice>(); 
		ListIterator<Graph.Vertice> iter = original.listIterator(original.size());
		while (iter.hasPrevious()){
			Graph.Vertice vertex = iter.previous();
			reverse.add(vertex);
		}
		return reverse;
	}
}
