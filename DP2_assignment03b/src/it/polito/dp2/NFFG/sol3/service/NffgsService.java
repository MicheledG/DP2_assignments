package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/nffgs")
public class NffgsService {

	@GET
	@Produces("text/plain")
	public String nffgs(){
		return new String("nffgs");
	}
	
}
