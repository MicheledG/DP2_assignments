package it.polito.dp2.NFFG.sol3.service;

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

@Path("/nffgs")
public class NffgsResource {
	
	private NffgsDB nffgsDB = NffgsDB.newNffgsDB();
	private PoliciesDB policiesDB = PoliciesDB.newPoliciesDB();
	
	/* store the posted nffgs into the DB */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response storeNffgs(Nffgs nffgs){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			nffgsDB.storeNffgs(nffgs);
			return Response.noContent().build();
		}
		catch (AlreadyLoadedException e) {
			return Response.status(Response.Status.FORBIDDEN).entity(e.toString()).build();
		}
		catch(ServiceException e){
			return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).build();
		}
		catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
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
	
	@Path("/{nffgName}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	/* get a single nffg */
	public Response getSingleNffgs(@PathParam("nffgName") String nffgName){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Nffgs nffgs = nffgsDB.getNffgs(nffgName);
			return Response.ok(nffgs).build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.toString()).build();
		}
		catch(ServiceException | RuntimeException  e){
			return Response.serverError().entity(e.toString()).build();
		}
		//catch(ServiceException | RuntimeException  e){
		//	return Response.serverError().build();
		//}
	}
	
	/* delete all the nffgs */
	@DELETE
	public Response deleteNffgs(
			@DefaultValue("false") @QueryParam("deletePolicies") boolean deletePolicies ){
		try{
			if(deletePolicies){
				/* delete all data */
				nffgsDB.deleteNffgs();
				policiesDB.deletePolicies();
			}
			else{
				/* check if there are policies */
				if(policiesDB.isEmpty())
					/* there is no problem deleting all the nffgs */
					nffgsDB.deleteNffgs();
				else
					throw new PolicyRelatedException();
			}
			return Response.noContent().build();
		}
		catch(PolicyRelatedException e){
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		catch(ServiceException| RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	//TODO demo version => no policies check
	/* delete a single nffg */
	@Path("/{nffgName}")
	@DELETE
	public Response deleteNffgs(@PathParam("nffgName") String nffgName){
		try{
			nffgsDB.deleteNffgs(nffgName);
			return Response.noContent().build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.toString()).build();
		}
		catch(ServiceException| RuntimeException e){
			return Response.serverError().entity(e.toString()).build();
		}
	}
	
}
