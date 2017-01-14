package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;

@Path("/nffgs")
public class NffgsResource {
	
	private NffgsDB nffgsDB = NffgsDB.newNffgsDB();
	
	/* send to the client the list of nffg loaded on the DB */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getNffgs(){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Nffgs nffgs = nffgsDB.getNffgs();
			return Response.ok(nffgs).build();
		} 
		catch (NoGraphException e) {
			return Response.noContent().build();
		}
		catch(ServiceException | RuntimeException  e){
			return Response.serverError().entity(e.toString()).build();
		}
		//catch(ServiceException | RuntimeException  e){
		//	return Response.serverError().build();
		//}
	}
	
	/* store the posted nffgs into the DB */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response createNffgs(Nffgs nffgs){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			nffgsDB.createNffgs(nffgs);
			return Response.noContent().build();
		} 
		catch(ServiceException e){
			return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).build();
		}
		catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	//TODO demo version => no policies check
	/* delete all the nffgs */
	@DELETE
	public Response deleteNffgs(){
		try{
			nffgsDB.deleteNffgs();
			return Response.noContent().build();
		} 
		catch(ServiceException| RuntimeException e){
			return Response.serverError().build();
		}
	}
	
}
