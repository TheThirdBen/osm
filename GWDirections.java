import java.util.ArrayList;

public class GWDirections {
   private GWMap map;
   public static double distance(OSMNode a, OSMNode b) {
      /*
      Return the distance in miles between two
      OSMNodes. This is calculated as follows:
      
      1. Calculate the latitudinal distance.
      (lat A - lat B) * 69.172 = distance(miles)
      2. Calculate the longitudinal distance.
      (lon A - lon B) * 69.172 * cos(avgLat)
      */
      double latA = a.getLat();
      double latB = b.getLat();
      double lonA = a.getLon();
      double lonB = b.getLon();
      
      double latDiff = difference(latA, latB);
      double latDistance = latDiff * 69.172;
      
      double avgLat = (latA + latB) / 2.0;
      
      double lonDiff = difference(lonA, lonB);
      double lonDistance = lonDiff * 69.172 * Math.cos(Math.toRadians(avgLat));
      
      /*Now, solving this is all about the pthagoreum theorem*/
      /*But... what if the diff in lat or lon is 0?*/
      double nodeDistance;
      nodeDistance = Math.sqrt( (latDistance * latDistance) + (lonDistance * lonDistance) );
      
      return nodeDistance;
   }
/*Private*/   
/**/   private static double difference(double a, double b) {
/**/      double diff = 0;
/**/      if (a > b) {
/**/         diff = a - b;
/**/      }
/**/      else {
/**/         diff = b - a;
/**/      }
/**/      return diff;
/**/   }
   
   
   public static OSMNode closerNode(OSMNode a, OSMNode b, OSMNode dest) {
      /*
      Takes as arguments two nodes, a & b, and
      returns the one that is closer to the /dest/
      node.
      
      dest = destination...
      Took me forever to figure that out, thanks Gabe :P
      */
      
      /*
      TO DO:
      1. Find distance from a - dest.
      2. Find distance from b - dest.
      3. Compare and return lesser one.
      */
      
      double distanceA = distance(a, dest);
      double distanceB = distance(b, dest);
      
      if (distanceA < distanceB) {
         return a;
      }
      return b;
   }
   
   public double totalDistance(OSMNode[] ns) {
      /*
      This method takes an array of nodes as an
      argument, and returns the total distance
      of sequence of nodes. That is, the distance
      of the 0th node from the 1st plust the 
      distance from the 1st to the 2nd, etc.
      */
      
      /*
      TO DO:
      1. Initialize total distance. Set to 0
      2. Measure distance between each node in the array
         a. for loop. end 2 before array length. if array length < 2, return 0
         b. find distance between i-th node and (i+1)-th node
         c. add distance to total
      3. return total distance   
      */
      
      if (ns.length < 2) {
         return 0;
      }
      
      double totalDistance = 0;
      
      for (int i = 1; i < (ns.length); i++) {
         totalDistance = totalDistance + distance(ns[i-1], ns[i]);
      }
      
      return totalDistance;
   }
   
   public OSMRoad[] getNodesRoads(OSMNode n) {
      /*
      This method returns an array of only the
      OSMRoads that have the OSMNode passed in as an
      argument in them. A node that is only in a
      single road will return that road only.
      A node that is an intersection between roads
      will return all intersecting roads.
      The order of these roads is in the same order
      they appear in the .osm file.
      */
      
      /*
      TO DO:
      1. Check each road in the map
         a. Check each node in the map. if n is a node in the road, add road to the array
      2. Return array
      */
      
      // ArrayList? Maybe...
      
      ArrayList<OSMRoad> roads = new ArrayList<OSMRoad>();
      
      int numRoads = map.numRoads();
      
      for (int i = 0; i < numRoads; i++) {
         OSMRoad r = map.getRoad(i);
         boolean nIsInr = false;
         for (int j = 0; j < r.getNumNodes(); j++) {
            if (n.equals(r.getNode(j))) {
               nIsInr = true;
               break;
            }
         }
         if (nIsInr) {
            roads.add(r);
         }
      }
      
      OSMRoad[] roadsWithN = new OSMRoad[roads.size()];
      roads.toArray(roadsWithN);
      
      return roadsWithN;
   }
   
   public OSMNode[] getAdjacentNodes(OSMRoad r, OSMNode n) {
      /*
      OSMNode n must be in road r, and this
      method returns an array of length 2 that
      includes the two nodes on the road that are
      adjacent to n, ordered so that a node earlier
      in this array is earlier in the list of node
      references in the road description in the
      .osm file. If n is either the first or last node
      in the road, then the first, or second node,
      respectively, is set to null.
      */
      
      /*
      TO DO:
      1. Initialize an array of OSMNodes with length 2
      2. "If n is either the first or last node...
         then the first, or second node, respectively, is set to null."
      3. Do what the method title says
      */
      
      OSMNode[] adjacentNodes = new OSMNode[2];
            
      //r.getNumNodes()
      //r.getNode()
      
      OSMNode[] roadNodeList = getRoadNodes(r);
      
      for (int i = 0; i < roadNodeList.length; i++) {
         if (n.equals(r.getNode(i))) {
            
            if (i == 0) {
            adjacentNodes[0] = null;
            adjacentNodes[1] = r.getNode(i+1);
            }
            
            else if (i == (roadNodeList.length - 1)) {
            adjacentNodes[0] = r.getNode(i-1);
            adjacentNodes[1] = null;
            }
            
            else {
            adjacentNodes[0] = r.getNode(i-1);
            adjacentNodes[1] = r.getNode(i+1);
            }
            
         }
      }
      
      return adjacentNodes;
   }

/*Private*/   
/**/   private static OSMNode[] getRoadNodes(OSMRoad r) {
/**/      OSMNode[] newNodeList = new OSMNode[r.getNumNodes()];
/**/      for (int i = 0; i < r.getNumNodes(); i++) {
/**/         newNodeList[i] = r.getNode(i);
/**/      }
/**/      return newNodeList;
/**/   }
   
   public OSMNode[] getDirections (OSMNode a, OSMNode b) {
      /*
      This is the big method.
      It uses many of the previous ones to generate
      the directions using the greedy algorithm
      spelled out above, starting at node a, and
      always moving toward b until we reach it.
      The nodes that are directly connected via
      roads connect A and B. The first node in the
      list is A, and the last is B. However, if
      at any point, no progress can be made from a
      current node toward a destination, we will
      return null. Please note that this
      method can be relatively simple if you make use
      of the previous two methods to, at the current
      node, find all other connected nodes, and
      closerNode to find the one to move to next.
      */
      
      /*
      TO DO:
      1. Initialize an array list for the nodes that lead to the destination
      2. Start at the node A
      3. Get the road(s)
      4. Get the adjacent nodes
         a.Don't forget to get nodes if there's another
           road the node is connected to
      5. Find which of these nodes are closer to the destination: node B
      6. Add that to the array list
      7. Repeat 3-6 until you get to the destination
      8. Add destination node to the array list
      9. Convert array list to array
      10. Return the array
      */
      
      ArrayList<OSMNode> nodesToDest = new ArrayList<OSMNode>();
      
      nodesToDest.add(a);
      
      OSMNode currentNode = a;
      OSMNode[] nodePath = null;
      
      while (!currentNode.equals(b)) {
         /*Get the road(s)*/
         OSMRoad[] roads = getNodesRoads(currentNode);
         
         /*Get the adjacent nodes*/
         OSMNode[] allAdjacentNodes = new OSMNode[roads.length * 2];
         for (int i = 0; i < roads.length; i++) {
            OSMNode[] adjNodes = getAdjacentNodes(roads[i], currentNode);
            allAdjacentNodes[i*2] = adjNodes[0];
            allAdjacentNodes[(i*2) + 1] = adjNodes[1];
         }
         
         /*Find which of these nodes are closer to the destination: node B*/
         OSMNode theCloserNode = allAdjacentNodes[0];
         for (int i = 1; i < allAdjacentNodes.length; i++) {
            if (allAdjacentNodes[i] == null) {
               continue;
            }
            if (theCloserNode == null) {
               theCloserNode = allAdjacentNodes[i];
               continue;
            }            
            System.out.println(theCloserNode.getId());
            theCloserNode = closerNode(theCloserNode, allAdjacentNodes[i], b);
         }
         
         /*Add that to the array list*/
         nodesToDest.add(theCloserNode);
         currentNode = theCloserNode;         
      }
      
/*      while(currentNode != null && !(currentNode.equals(b))) {
         
         OSMRoad[] roads = getNodesRoads(currentNode);
         OSMNode[] allAdjacentNodes = new OSMNode[roads.length * 2];
         
         for (int i = 0; i < roads.length; i++) {
            OSMNode[] adjNodes = getAdjacentNodes(roads[i], currentNode);
            allAdjacentNodes[i*2] = adjNodes[0];
            allAdjacentNodes[(i*2) + 1] = adjNodes[1];
         }
         
         OSMNode theCloserNode = null;
         //printNodes(allAdjacentNodes);
         for (int i = 1; i < allAdjacentNodes.length && allAdjacentNodes[i] != null; i++) {
            if (i == 1) {
               theCloserNode = allAdjacentNodes[1];
               break;
            }
            
            theCloserNode = closerNode(allAdjacentNodes[i - 1], allAdjacentNodes[i], b);
         }
         
         nodesToDest.add(theCloserNode);
         
         currentNode = theCloserNode;
      }*/
      nodePath = new OSMNode[nodesToDest.size()];
      nodesToDest.toArray(nodePath);      
      return nodePath;
   }
   
   private static void printNodes(OSMNode[] nList) {
      for (int i = 0; i < nList.length; i++) {
         System.out.println(nList[i].getId());
      }
   }
   
   public GWDirections(GWMap m) {
      /*
      The class's constructor. It takes a map as
      an argument for use of the other methods.
      */
      map = m;
   }
   
   public GWMap getMap() {
      /*
      Return the map that the directions are based on.
      */
      return map;
   }
}