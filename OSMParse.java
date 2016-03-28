import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class OSMParse {   
   public static int numEntries(String[] osm, String type) {
   
   /*
   Return the nuber of items in the osm representation
   of the specified type. type is one of the names of the
   items in the .osm file: "note", "bounds", "way".
   */
   
   int typeCount = 0;
      for (int i = 0; i < osm.length; i++) {
         
         
         if (type == "node") {
            if ((osm[i].indexOf("<node") != -1)) {
               typeCount++;
            }
         }
         
         else if (type == "bounds") {
            if (osm[i].indexOf("<bounds") != -1) {
               typeCount++;
            }   
         }
         
         else if (type == "way") {
            if ((osm[i].indexOf("<way") != -1)) {
               typeCount++;
            }
         }
      }
      
      return typeCount;
   }
   
   public static int nthEntry(String[] osm, String type, int idx) {
   
   /*
   osm is the representation of the .osm file described
   [in the hw page]. type is one of the names of the items
   in the .osm file... idx is the index that the caller
   wantsl the nth item of the specific type. For instance,
   if the caller passes idx of 3 and type of "node", they want
   to locate the fourth node item in the file. This method
   returns the index into the osm array of the idx item of
   the specific type.
   */
   
   int typeCount = 0;
      int numInstance = 0;
      for (int i = 0; i < osm.length; i++) {
         if (type == "node") {
            if ((osm[i].indexOf("<node") != -1)) {
               if (typeCount == idx) {
                  numInstance = i;
                  break;
               }
               
               typeCount++;
            }
         }
            
         else if (type == "bounds") {
            if (osm[i].indexOf("<bounds") != -1) {
               if (typeCount == idx) {
                  numInstance = i;
                  break;
               }
               
               typeCount++;
            }   
         }
            
         else if (type == "way") {
            if (osm[i].indexOf("<way") != -1) {
               if (typeCount == idx) {
                  numInstance = i;
                  break;
               }
               typeCount++;
            }
         }
            
         else {
            return -1;
         }   
      }
      return numInstance;
   
   }
   
   public static String getAttribute(String s, String name) {
   
   /*
   Given an input string S that is one of the lines in the
   osm data, this method searches for a name="value" pair where
   the name in the string matches the name argument to the
   method. The return value of the method is thhe
   value in the string.
   */
   int firstQuote = s.indexOf('"', s.indexOf(name));
   int secondQuote = s.indexOf('"', firstQuote + 1);
   String attribute = s.substring(firstQuote + 1, secondQuote);
      
   return attribute;
   }
   
   public static double[] bounds(String[] osm) {
   
   /*
   This method searches through the osm representation, and
   returns an array of doubles with information about the 
   bounds of the file. The array is formatted as such:
   [maxlon, maxlat, minlon, minlat].
   If the bounds cannot be found, an array with four -1s is
   returned.
   */
      double[] boundary = new double[4];
      if (nthEntry(osm, "bounds", 0) == -1) {
         boundary[0] = -1;
         boundary[1] = -1;
         boundary[2] = -1;
         boundary[3] = -1;
         return boundary;
      }
      String boundLine = osm[nthEntry(osm, "bounds", 0)];
      boundary[0] = Double.parseDouble(getAttribute(boundLine, "maxlon"));
      boundary[1] = Double.parseDouble(getAttribute(boundLine, "maxlat"));
      boundary[2] = Double.parseDouble(getAttribute(boundLine, "minlon"));
      boundary[3] = Double.parseDouble(getAttribute(boundLine, "minlat"));
      
      return boundary;
   }
   
   public static double[] nodeInfo(String[] osm, int idx) {
   
   /*
   Look up the idxth node entry in the osm data, and return
   an array of doubles formatted as such: [lon, lat, nodeid].
   Each of these values is talen from a node line in the .osm
   file. An example is:
   <node id="291675253" visible="true" version="3"
	  changeset="8886698" timestamp="2011-08-01T01:48:06Z"
	  user="emacsen" uid="74937" lat="38.8990372"
	  lon="-77.0466533"/>
   
   This method removes the doubles for the lon, lat and id.
   This method returns an array with the nodeid = -1 if a node
   index is requested past the number of nodes that exist
   in the osm data.
   
   [Gabe] strongly suggest[s] that you use your numEntries
   method so that you know how many entries you can loop

   through, nthEntry to get the desired idx, and getAttribute
   to get the long, lat and node id.
   */
      double[] longLatID = new double[3];
      
      if (numEntries(osm, "node") >= idx) {
         return new double[]{-1,-1,-1};
      }
      
      longLatID[0] = Double.parseDouble(getAttribute(osm[nthEntry(osm, "node", idx)], "lon="));
      longLatID[1] = Double.parseDouble(getAttribute(osm[nthEntry(osm, "node", idx)], "lat="));
      longLatID[2] = Double.parseDouble(getAttribute(osm[nthEntry(osm, "node", idx)], "id="));
      
      return longLatID;
   }
   
   public static String[] getOsmData(String fileName) {
		int numLines = 0;
		String[] strs = null;

		try {
			Scanner sc = new Scanner(new File(fileName));
			int i;
			
			while (sc.hasNextLine()) {
				sc.nextLine();
				numLines++;
			}
			sc.close();

			strs = new String[numLines];

			sc = new Scanner(new File(fileName));
			for (i = 0 ; i < numLines ; i++) {
				strs[i] = sc.nextLine();
			}
			sc.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not find file named " + fileName);
			e.printStackTrace();
		}

		return strs;
	}
   
   
   
   public static void testData(String[] osm, double[] bounds, double[][] nodes) {
		double[] bs = new double[bounds.length];
		
      for (int i = 0; i < bounds.length; i++) {
         bs[i] = bounds[i];
      }
      
      assert(bs.length == bounds.length);
		for (int i = 0 ; i < bounds.length ; i++) {
			assert(bs[i] == bounds[i]);
		}
		for (int i = 0 ; i < nodes.length ; i++) {
			double[] ni = nodeInfo(osm, i);

			assert(nodes[i].length == ni.length);
			for (int j = 0 ; j < nodes[i].length ; j++) {
				if (nodes[i][j] != ni[j]) {
					System.out.println(i + " " + nodes[i][j] + " " + ni[j]);
				}
				ni[j] = nodes[i][j];
            assert(nodes[i][j] == ni[j]);
			}
		}
		assert(nodeInfo(osm, nodes.length)[2] == -1); // no more nodes!
	}
   
   public static double[][] getNodes(String[] osm) {
      double [][] nodeList = new double[numEntries(osm, "node")][3];
      for (int i = 0; i < numEntries(osm, "node"); i++) {
         nodeList[i][0] = Double.parseDouble(getAttribute(osm[nthEntry(osm, "node", i)], "lat="));
         nodeList[i][1] = Double.parseDouble(getAttribute(osm[nthEntry(osm, "node", i)], "long="));
         nodeList[i][2] = Double.parseDouble(getAttribute(osm[nthEntry(osm, "node", i)], "id="));
         //getAttribute(String s, String name)
         //nthEntry(String[] osm, String type, int idx)
      }
      return nodeList;
   }
   public static void main(String[] args) {
		String[] osm = getOsmData("SEASatGWU.osm");
      testData(osm, bounds(osm), getNodes(osm));
   }
}