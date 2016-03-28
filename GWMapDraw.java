//import java.util.*;

public class GWMapDraw {
   
	static OSMNode hi, lo;

	static double convLon(OSMNode n) {
		double lon = n.getLon();
		// bounds of 10
		return ((lon - lo.getLon())/(hi.getLon() - lo.getLon()) * 5) + 2.5;
	}

	static double convLat(OSMNode n) {
		double lat = n.getLat();
		// bounds of 10
		return ((lat - lo.getLat())/(hi.getLat() - lo.getLat()) * 5) + 2.5;
	}
	
	static void line(OSMNode a, OSMNode b) {
		Graphics.drawLine(convLon(a), convLat(a), convLon(b), convLat(b));
	}
	
	static OSMNode[] displayDirections(GWDirections d, long aId, long bId) {
		OSMNode a, b;
		GWMap m = d.getMap();

		b = a = m.getNode(0);
		for (int i = 0 ; i < m.numNodes() ; i++) {
			OSMNode n = m.getNode(i);

			if (n.getId() == aId) {
				a = n;
			}
			if (n.getId() == bId) {
				b = n;
			}
		}

		return d.getDirections(a, b);
	}

	public static void main(String[] args) {
		GWMap m = new GWMap("OSMTest.osm");
		GWDirections d = new GWDirections(m);
		OSMNode[] bounds = m.getBounds();
		OSMNode[] nodes;

		hi = bounds[0];
		lo = bounds[1];
		Graphics.setXYRange(0, 10, 0, 10);
		Graphics.display();

		for (int i = 0 ; i < m.numRoads() ; i++) {
			OSMRoad r = m.getRoad(i);

			for (int j = 0 ; j < r.getNumNodes()-1 ; j++) {
				line(r.getNode(j), r.getNode(j+1));
			}
		}
		nodes = displayDirections(d, 49762184L, 1381210497L);
		Graphics.setLineColor("magenta");
		for (int i = 0 ; i < nodes.length ; i++) {
			if (i < nodes.length - 1) {
				line(nodes[i], nodes[i+1]);
			}
			Graphics.drawPoint(convLon(nodes[i]), convLat(nodes[i]));
		}

		//Graphics.drawLine(0, 0, 10, 10);
	}
}
