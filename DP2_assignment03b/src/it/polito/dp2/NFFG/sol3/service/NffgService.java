package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class NffgService {
	
	@Path("hellone")
	@GET
	@Produces("text/plain")
	public String hellone(){
		return new String("hellone");
	}
	
	@Path("ciaone")
	@GET
	@Produces("text/plain")
	public String ciaone(){
		return new String("ciaone");
	}
	
}
