import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class GWMap {
   private String[] osm;
   private OSMNode[] nodeList;
   private OSMRoad[] roadList;
   private String[] roadNames;
   GWMap(String OSMfilename) {
      /*
      A constructor that takes the filename of the
      .osm file in the current folder/directory,
      and parses through the file, add adds all
      the /relevant/ nodes and roads(ways).
      More details about parsing the file follow.
      */
      osm = OSMParse.getOsmData(OSMfilename);
      int temp1 = OSMParse.numEntries(osm, "node");
      nodeList = new OSMNode[(OSMParse.numEntries(osm, "node"))];
      for (int i = 0; i < nodeList.length; i++) {
         nodeList[i] = new OSMNode(Double.parseDouble(OSMParse.getAttribute(osm[(OSMParse.nthEntry(osm, "node", i))], "lon=")),
                                   Double.parseDouble(OSMParse.getAttribute(osm[(OSMParse.nthEntry(osm, "node", i))], "lat=")),
                                   Long.parseLong(OSMParse.getAttribute(osm[(OSMParse.nthEntry(osm, "node", i))], "id=")));   
      }
      for (int i = 0; i < nodeList.length; i++) {
         if (osm[OSMParse.nthEntry(osm, "node", i)].indexOf("/>") == -1) {
            for (int j = OSMParse.nthEntry(osm, "node", i); osm[j].indexOf("</node>") == -1; j++) {
               if ((osm[j].indexOf("v=\"traffic_signals\"") != -1) && (osm[j].indexOf("k=\"highway\"") != -1)) {
                  nodeList[i].setAsSignal();
               }
            }
         }
      }
      int numRoads = 0;
      for (int i = 0; i < osm.length; i++) {
         if (checkIfRoad(i)) {
            for (int j = i + 1; (osm[j].indexOf("</way>") == -1); j++) {
               if (osm[j].indexOf("k=\"highway") != -1) {
                  numRoads = numRoads + 1;
                  break;
               }
            }
         }
      }
      roadList = new OSMRoad[numRoads];
      //Make an array of all the way lines that are valid roads
      
      int[] roadLines = new int[numRoads];
      int roadLineIndex = 0;
      for (int i = 0; i < osm.length; i++) {
         if (checkIfRoad(i) && (roadLineIndex < roadLines.length)) {
            roadLines[roadLineIndex] = i;
            roadLineIndex++;
         }
      }
      
      //Make an array of all the valid ways' names
      roadNames = new String[numRoads];
      for (int i = 0; i < roadLines.length; i++) {
         String name = "Invalid";
         for (int j = roadLines[i]; osm[j].indexOf("</way>") == -1; j++) {
            //OSMParse.getAttribute(String [the line], String "v=")
            if (osm[j].indexOf("tag k=\"name\"") != -1) {
               name = OSMParse.getAttribute(osm[j], "v=");
               break;
            }
         }
         roadNames[i] = name;
      }
      
      OSMNode[] roadNodes;
      for (int i = 0; i < roadList.length; i++) {
         int numNodes = 0;
/**/     for (int j = roadLines[i]; osm[j].indexOf("</way>") == -1; j++) {
            if (osm[j].indexOf("nd ref=") != -1) {
               numNodes++;
            }
         }
         roadNodes = new OSMNode[numNodes];
         
         int k = 0;
         for (int j = roadLines[i]; osm[j].indexOf("</way>") == -1; j++) {
            long nodeIdNum = 0;
            double nodeLonNum = 0;
            double nodeLatNum = 0;
            
            if (osm[j].indexOf("nd ref=") != -1) {
               nodeIdNum = Long.parseLong(OSMParse.getAttribute(osm[j], "nd ref="));
               for (int nodeFind = 0; nodeFind < nodeList.length; nodeFind++) {
                  if (nodeIdNum == nodeList[nodeFind].getId()) {
                     nodeLonNum = nodeList[nodeFind].getLon();
                     nodeLatNum = nodeList[nodeFind].getLat();
                     break;
                  }
               }
               
               roadNodes[k] = new OSMNode(nodeLonNum , nodeLatNum , nodeIdNum);
               k++;
               if (k >= roadNodes.length) {
                  break;
               }
            }                               
         }
         roadList[i] = new OSMRoad(roadNames[i], roadNodes);   
      }
   }
   
   String[] giveRoadNames() {
      return roadNames;
   }
   
   boolean checkIfRoad(int wayLine) {
      boolean firstHalf = false;
      boolean secondHalf = false;
      if (osm[wayLine].indexOf("<way") != -1) {
         for (int i = wayLine; osm[i].indexOf("</way>") == -1; i++) {
            if (osm[i].indexOf("k=\"highway\"") != -1) {
               firstHalf = true;
               break;
            }
         }
         for (int i = wayLine; osm[i].indexOf("</way>") == -1; i++) {
            if (osm[i].indexOf("\"HFCS\"") != -1) {
               secondHalf = true;
               break;
            }
         }
      }   
      if (firstHalf && secondHalf) {
         return true;
      }
      return false;
   }
   
   int numNodes() {
      /* return the number of nodes in our map */
      return nodeList.length;
   }
   
   OSMNode getNode(int nth) {
      /* return the nth node in our map */
      return nodeList[nth];
   }
   
   int numRoads() {
      /*
      return the number of roads in our map
      */
      return roadList.length;
   }
   
   OSMRoad getRoad(int nth) {
      /*
      return the nth road in our map
      */
      return roadList[nth];
   }
   
   OSMNode[] getBounds() {
      /*
      return an array of length 2 that includes
      two nodes: the maximum coordinate of our
      map, and the minumum
      */
      OSMNode[] boundNodes = new OSMNode[2];
      boundNodes[0] = getMaxNode();
      boundNodes[1] = getMinNode();
      return boundNodes;
   }
   
   OSMNode getMaxNode() {
      double maxLat = 0;
      double maxLon = 0;
      OSMNode maxNode = nodeList[0];
      for (int i = 0; i < nodeList.length; i++) {
         if (nodeList[i].getLon() > maxLon && nodeList[i].getLat() > maxLat) {
            maxNode = nodeList[i];
         }
      }   
      return maxNode;
   }
   
   OSMNode getMinNode() {
      double minLat = 0;
      double minLon = 0;
      OSMNode minNode = nodeList[0];
      for (int i = 0; i < nodeList.length; i++) {
         if (nodeList[i].getLon() > minLon && nodeList[i].getLat() > minLat) {
            minNode = nodeList[i];
         }
      }   
      return minNode;
   }
}