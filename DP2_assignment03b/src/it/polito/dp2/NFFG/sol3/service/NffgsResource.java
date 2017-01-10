package it.polito.dp2.NFFG.sol3.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;

@Path("/nffgs")
public class NffgsResource {
	
	/* send to the client the list of nffg's names loaded on Neo4J */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getLoadedNffgsNames(){
		try{
			/* put the loaded nffg's names into a NamedEntity object */
			NamedEntities loadedNffgs = new NamedEntities();
			return Response.ok(loadedNffgs).build();
		} catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
}
