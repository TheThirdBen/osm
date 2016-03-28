public class OSMNode {
   
   private long idNum;
   private double nodeLat;
   private double nodeLon;
   boolean isSignal = false;
   OSMNode(long id) {
      
      /*
      initialize the data for the node with the passed in
      identifier, and both longitude and latitude set to a
      default value of 0.
      */
      idNum = id;
      nodeLat = 0;
      nodeLon = 0;
   }
   
   OSMNode(double longitude, double latitude, long id) {
      /*
      initialize the data for the node with the passed
      in arguments.
      */
      idNum = id;
      nodeLat = latitude;
      nodeLon = longitude;
   }
   
   boolean equals(OSMNode n) {
      if (n.getId() == idNum) {
         return true;
      }
      return false;   
   }
   
   double getLon() { return nodeLon; }
   double getLat() { return nodeLat; }
   long getId()    { return idNum;   }
   
   boolean isSignal() { return isSignal; }
   
   void setAsSignal() {
      isSignal = true;
   }
}

