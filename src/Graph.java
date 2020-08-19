import java.util.List;
import java.util.ArrayList;


public class Graph {
	public static class Edge {
		public int line;								//stores which line the subway travels from its start to finish stations
		public String station_name1;					//the name of the start station
		public String station_code1;					//the code of the start station
		public String station_name2;					//the name of the finish station
		public String station_code2;					//the code of the finish station
		public double weight;							//the distance between the two stations
		public Vertice start;							//the vertex associated with the start station
		public Vertice finish;							//the vertex associated with the finish station
		
		//Constructor method
		Edge(int a, String b, String c, String d, String e, double f){
			line = a;
			station_name1 = b;
			station_code1 = c;
			station_name2 = d;
			station_code2 = e;
			weight = f;
			start = null;
			finish = null;
		}
		
		//mutator for start
		public void setStart(Vertice s){
			start = s;
		}
		
		//mutator for finish
		public void setFinish(Vertice f){
			finish = f;
		}
	}
	
	public static class Vertice {
		public int line;						//the line the station is on
		public String name;						//the name of the station
		public String code;						//the code of the station
		public double lat;						//the longitude of the station, was not used
		public double lon;						//the latitude of the station, was not used
		public double distance;					//the distance from the source station, used in Dijkstra's Algorithm
		public Vertice previous;				//the station visited previous in a path, used in Dijkstra's Algorithm
		public double transfer_distance;			//the distance from one transfer station to the next
		
		//Constructor method
		Vertice(int a, String b, String c, double d, double e){
			line = a;
			name = b;
			code = c;
			lat = d;
			lon = e;
		}
		
		//Constructor method
		Vertice(String a){
			code = a;
		}
		
		//Mutator for distance
		public void setDistance(double d){
			distance = d;
		}
		
		//Mutator for distance_transfer
		public void setTransferDistance(double d){
			transfer_distance = d;
		}
		
		//Accessor for distance
		public double getDistance(){
			return distance;
		}
		
		//Accessor for distance_transfer
		public double getTransferDistance(){
			return transfer_distance;
		}
		
		//Accessor for line
		public int getLine(){
			return line;
		}
		
		//Accessor for name
		public String getName(){
			return name;
		}
		
		//Accessor for code
		public String getCode(){
			return code;
		}
		
		//Mutator for previous
		public void setPrevious(Vertice p) {
			previous = p;			
		}
					
	}
	
	public List<Vertice> Vertices;		//holds the stations in the subway graph
	public List<Edge> Edges;			//holds the railways between stations in the subway graph		
	
	//Default constructor method
	Graph(){
		Vertices = new ArrayList<Vertice>(300);
		Edges = new ArrayList<Edge>(300);
	}
	
	//Constructor method
	Graph(List<Vertice> v, List<Edge> e){
		Vertices = v;
		Edges = e;
	}
	
	//searches all vertices for a specific subway code the and returns that vertex if found
	public Vertice findVertice(String c){
		for(Vertice v : Vertices){
			if(v.code.equals(c))
				return v;
		}
		return null;
	}
	
	//returns true if there is at least one station in the graph
	public boolean isEmpty(){
		if (Vertices.size() == 0)
			return false;
		else
			return true;
	}
	
	//returns the distance between two adjacent vertices
	public double getEdgeDistance(Vertice a, Vertice b){
		for(Edge e : Edges){
			if((a.name.equals(e.station_name1) && b.name.equals(e.station_name2)) || 
					(b.name.equals(e.station_name1) && a.name.equals(e.station_name2))){
					return e.weight;				
			}
		}
		return 0;	//throw exception
	}
	
	//returns a list of all the incident stations of a station
	//if it is a transfer station this includes all the incident stations on other subway lines
	public List<Vertice> incidentVertice(Vertice v){
		List<Vertice> incident= new ArrayList<Vertice>();
		
		for (Edge e : Edges){
			if (v.code.equals(e.station_code1)){
				incident.add(e.finish);
			}
			if (v.code.equals(e.station_code2)){
				incident.add(e.start);
			}
		}
		return incident;				
	}

	//My implementation of Dijkstra's algorithm
	//returns a list of transfer stations and the distances between them along a path from source to destination stations
	public List<Graph.Vertice> Dijkstra(Graph G, Graph.Vertice s, Graph.Vertice f){
  		double inf = Double.POSITIVE_INFINITY;
  		List<Graph.Vertice> path = new ArrayList<Graph.Vertice>();		//this is the shortest path from source to all other vertices
		List<Graph.Vertice> finishPath = new ArrayList<Vertice>();		//this is the shortest path from source to destination
		List<Graph.Vertice> transfer = new ArrayList<Vertice>();		//this is the list of transfer stations along that shortest path
		
		//set the distance from the source vertex to itself to 0
		//set the distance from the source vertex to all other vertices to infinity
		for(Graph.Vertice v : Vertices){
			if (v.code.equals(s.code)){
				v.setDistance(0);
			}
			
			else{
				v.setDistance(inf);
			}
		}	
		
		//copy all the vertices to keep track of which vertices have been visited
		List<Graph.Vertice> Q = Vertices;
		
		//visit each vertex and relax its adjacent vertices
		while(!Q.isEmpty()){
			//remove the vertex with the smallest distance from the source node that has not been visited yet
			Graph.Vertice u = removeMin(Q);
			
			//break out of the loop if the minimum distance node is infinity
			//this will only happen if the graph is disconnected
			if (u.distance == inf)
				break;
			
			//relax all of the removed vertex's adjacent vertices
			//update the distances and add the minimum spanning graph
			for(Graph.Vertice v: G.incidentVertice(u)){

				if(v.getDistance() > u.getDistance() + G.getEdgeDistance(u,v)){
					v.setDistance(u.distance + G.getEdgeDistance(u,v));
					replaceKey(v,u.getDistance() + G.getEdgeDistance(u,v), Q);					
					v.setPrevious(u);
					if(path.contains(v))
						path.remove(v);
					path.add(v);
				}
			}
		}
		
		//find the destination station in the minimum spanning graph created by dijkstra's algorithm
		Graph.Vertice target = findVertice(f.code, path);
		Graph.Vertice counter = target;
		
		//create the minimum distance path from the source to destination by "backtracking" along the minimum spanning graph
		while(counter.previous != null){
			finishPath.add(counter);
			counter = counter.previous;
		}
		finishPath.add(s);
		//record the transfer stations along the path
		transfer = transferfy(finishPath, s);
		return transfer;
				
	}
	
	//removes the minimum distance vertex from the graph and returns it
	public static Graph.Vertice removeMin(List<Graph.Vertice> V){
		Graph.Vertice min = V.get(0);
		for(Graph.Vertice v : V){
			if(v.distance < min.distance)
				min = v;
		}
		V.remove(min);
		return min;
	}
	
	//sets the distance for a specific node in a list of nodes
	public void replaceKey(Vertice v, double d, List<Graph.Vertice> V){
		findVertice(v.code, V).setDistance(d);
	}
	
	//searches for a specific node by its station code in a list of nodes
	public Vertice findVertice(String c, List<Graph.Vertice> V){
		for(Vertice v : V){
			if(v.code.equals(c))
				return v;
		}
		return null;
	}
	
	//returns the list of transfer stations along a path
	//it also calculates the distance between each transfer station and assigns it to the distance variable of the starting station
	public List<Graph.Vertice> transferfy(List<Graph.Vertice> V, Vertice start){
		List<Graph.Vertice> transfer = new ArrayList<Graph.Vertice>(); 
		Graph.Vertice t = V.get(0);
		transfer.add(t);
		while(t.previous != null){
			while(t.previous != null && t.previous.getLine() == t.getLine()){
				t=t.previous;
			}
			if(t.previous == null)
				break;
			t = t.previous;
			transfer.add(t);
			if(t.previous == null)
				break;
			t = t.previous;
		}
				
		for(int i = 0; i < transfer.size() - 1; i++){
			transfer.get(i).setTransferDistance(transfer.get(i).getDistance() - transfer.get(i+1).getDistance());
		}		
		transfer.get(transfer.size()- 1).setTransferDistance(transfer.get(transfer.size() - 1).getDistance() - start.getDistance());

		return transfer;
	}
	
	//displays the line, code and distance information for each vertex in a list
	//this was used A LOT for debugging purposes
	public void displayVertices(List<Graph.Vertice> V){
		for(Graph.Vertice v : V){
			System.out.println(v.line + " " + v.code + " " + v.transfer_distance);
		}
	}
	
}
