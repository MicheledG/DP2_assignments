package it.polito.dp2.NFFG.sol3.service;

import javax.management.relation.RelationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;

//TODO: remove from the response with status code SERVER ERROR the description of the exception

@Path("/nffgs")
public class NffgsResource {
	
	private NffgService nffgService = new NffgService();
	
	/* store the posted nffgs into the service */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response storeNffgs(Nffgs nffgs){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			nffgService.storeNffgs(nffgs);
			return Response.noContent().build();
		}
		catch (AlreadyLoadedException e) {
			return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
		}
		catch(RuntimeException | ServiceException e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	/* send to the client the list of nffg loaded on the service */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getNffgs(){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Nffgs nffgs = nffgService.getNffgs();
			return Response.ok(nffgs).build();
		} 
		catch (NoGraphException e) {
			return Response.noContent().build();
		}
		catch(ServiceException | RuntimeException  e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@Path("/{nffgName}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	/* send to the client a single nffg loaded on the service */
	public Response getSingleNffgs(@PathParam("nffgName") String nffgName){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Nffgs nffgs = nffgService.getSingleNffgs(nffgName);
			return Response.ok(nffgs).build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
		catch(ServiceException | RuntimeException  e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	/* delete all the nffgs */
	@DELETE
	public Response deleteNffgs(){
		try{
			nffgService.deleteNffgs();
			return Response.noContent().build();
		}
		catch(ServiceException| RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	/* delete a single nffg */
	@Path("/{nffgName}")
	@DELETE
	public Response deleteNffgs(
			@PathParam("nffgName") String nffgName,
			@DefaultValue("false") @QueryParam("deletePolicies") boolean deletePolicies){
		try{
			nffgService.deleteSingleNffgs(nffgName, deletePolicies);
			return Response.noContent().build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
		catch (RelationException e) {
			return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
		}
		catch(ServiceException| RuntimeException e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
}
