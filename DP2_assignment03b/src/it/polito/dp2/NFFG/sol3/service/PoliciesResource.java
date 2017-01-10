package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/policies")
public class PoliciesResource {

	@GET
	@Produces("text/plain")
	public String policies(){
		return new String("polices");
	}
	
}
