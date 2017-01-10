package it.polito.dp2.NFFG.sol3.service;

import java.util.Map;

import it.polito.dp2.NFFG.sol3.service.jaxb.Nffg;

public class NffgsDB {
	
	private static Map<String, String> mapNffgIdNffgName;
	private Map<String, Nffg> mapNffgNameNffg;
	
	/* get from neo4j the list of the loaded NFFG and store it into the map*/
	private NffgsDB(){
		/* download all the nodes from neo4j */
		/* parse the nodes to populate the two maps */
	}
	
}
