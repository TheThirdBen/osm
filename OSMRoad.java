public class OSMRoad {
   private String roadName;
   private OSMNode[] nodeList;
   OSMRoad(String name, OSMNode[] nodes) {
      roadName = name;
      nodeList = new OSMNode[nodes.length];
      for (int i = 0; i < nodes.length; i++) {
         nodeList[i] = nodes[i];
      }
   }
   
   //boolean conainsNode(OSMNode n) {  }
   
   OSMNode getNode(int nth) { return nodeList[nth]; }
   
   int getNumNodes() { return nodeList.length; }
   
   String getName() { return roadName; }
   
   OSMNode intersects(OSMRoad r) {
//      if (getName().equals(r.getName())) {
//         return getNode(0);
//      }
      
      for (int i = 0; i < nodeList.length; i++) {
         for (int j = 0; j < r.getNumNodes(); j++) {
            if (nodeList[i].getId() == r.getNode(j).getId()) {
               return nodeList[i];
            }
         }
      }
      return null;
   }
}