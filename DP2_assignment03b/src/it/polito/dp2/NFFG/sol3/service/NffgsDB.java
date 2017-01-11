package it.polito.dp2.NFFG.sol3.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;
import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;
import javafx.util.Pair;


public class NffgsDB {
	
	private Map<String, String> mapNodeIdNffgName = new HashMap<String, String>();
	private static NffgsDB nffgsDB = new NffgsDB();
	
	//TODO: create the public method to return a DB
	
	/* get from neo4j the list of the loaded NFFG and store it into the map*/
	private NffgsDB(){
		/* initialize mapNodeIdNodeName */
		initMapNodeIdNffgName();
		return;
	}
	
	private void initMapNodeIdNffgName(){
		/* download all the nodes loaded on Neo4J */
		Neo4JXMLClient client = new Neo4JXMLClient();
		Nodes nodes = null;
		try {
			nodes = client.getAllNodes();
		} catch (RuntimeException e) {
			return;
		}
		
		if(!nodes.getNode().isEmpty()){
			/* there are nodes loaded to neo4j */
			for (Nodes.Node node: nodes.getNode()) {
				/* check if a node is a NFFG */
				if(this.nodeIsNffg(node)){
					/* the node is an NFFG => add to the map */
					String nffgName = this.getNodeProperty(node.getProperty(), "name");
					//TODO at the moment don't check null nffgName 
					this.mapNodeIdNffgName.put(node.getId(), nffgName);
				}
			}
		}
	}
	
	/* extract the property value of the property named propertyName from a List<Property> */
	private String getNodeProperty(List<Property> propertyList, String propertyName){
		String propertyValue = null;
		for (Property property : propertyList) {
			if(property.getName().equals(propertyName)){
				propertyValue = property.getValue();
				break;
			}
		}
		return propertyValue;
	}
	
	/* check if a node is a NFFG */
	private boolean nodeIsNffg(Nodes.Node node){
		
		/* check if the node is a NFFG looking at the label */
		Neo4JXMLClient client = new Neo4JXMLClient();
		Labels labels = null;
		try {
			labels = client.getLabelByNodeId(node.getId());
		} catch (RuntimeException e) {
			return false;
		}
		
		if(!labels.getValue().contains("NFFG"))
			/* the node is not a NFFG */
			return false;
		else
			return true;
	}
	
	/* return the name of the nffg stored in the cache map */
	public List<String> getNffgNames(){
		List<String> nffgNames = new ArrayList<String>();
		for (Map.Entry<String, String> mapEntry: this.mapNodeIdNffgName.entrySet()) {
			nffgNames.add(mapEntry.getValue());
		}
		return nffgNames;
	}
	
	/* create a NFFG on neo4j */
	public Nffg createNffg(Nffg nffg){
		//TODO take trace of the loaded entities,
		//if something goes wrong you must clear neo4j
		/* load all the nodes */
		/* load all the relationships */
		/* load the nffg node */
		/* load the nffg relationships */
		/* add the nffg name to the map */
	}
	
	
	/* return a NFFG stored inside neo4j */
	public Nffg getNffg(String nffgName) {
		
	}
	
	
}
