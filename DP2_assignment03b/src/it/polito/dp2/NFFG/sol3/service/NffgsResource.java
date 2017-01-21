package it.polito.dp2.NFFG.sol3.service;

//import javax.management.relation.RelationException;
import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
//import javax.ws.rs.DefaultValue;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.EntityPointers;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;

@Path("/nffgs")
public class NffgsResource {
	
	private NffgService nffgService = new NffgService();
	
	/* store the posted nffgs into the service */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public EntityPointers storeNffgs(Nffgs nffgs){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			EntityPointers nffgPointers = nffgService.storeNffgs(nffgs);
			return nffgPointers;
		}
		catch (AlreadyLoadedException e) {
			Response forbiddenResponse = Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build(); 
			throw new ForbiddenException(forbiddenResponse);
		}
		catch(ServiceException | RuntimeException e){
			throw new InternalServerErrorException();
		}
	}
	
	/* send to the client the list of nffg loaded on the service */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public EntityPointers getNffgPointers(){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			EntityPointers nffgPointers = nffgService.getNffgPointers();
			return nffgPointers;
		} 
		catch (NoGraphException e) {
			return null;
		}
		catch(ServiceException | RuntimeException  e){
			throw new InternalServerErrorException();
		}
	}
	
	@Path("/{nffgName}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	/* send to the client a single nffg loaded on the service */
	public Nffgs getSingleNffgs(@PathParam("nffgName") String nffgName){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Nffgs nffgs = nffgService.getSingleNffgs(nffgName);
			return nffgs;
		}
		catch (UnknownNameException e) {
			Response notFoundResponse = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new NotFoundException(notFoundResponse);
		}
		catch(ServiceException | RuntimeException  e){
			throw new InternalServerErrorException();
		}
	}
	
	/* DELETE SECTION -> NO MORE IMPLEMENTED AND SO NO CONCURRENT*/
	///* delete all the nffgs */
	//@DELETE
	//public void deleteNffgs(){
	//	try{
	//		nffgService.deleteAll();
	//		return;
	//	}
	//	catch(ServiceException| RuntimeException e){
	//		throw new InternalServerErrorException();
	//	}
	//}
	
	///* delete a single nffg */
	//@Path("/{nffgName}")
	//@DELETE
	//public void deleteSingleNffgs(
	//		@PathParam("nffgName") String nffgName,
	//		@DefaultValue("false") @QueryParam("deletePolicies") boolean deletePolicies){
	//	try{
	//		nffgService.deleteNffg(nffgName, deletePolicies);
	//		return;
	//	}
	//	catch (UnknownNameException e) {
	//		Response notFoundResponse = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
	//		throw new NotFoundException(notFoundResponse);
	//	}
	//	catch (RelationException e) {
	//		Response forbiddenResponse = Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build(); 
	//		throw new ForbiddenException(forbiddenResponse);
	//	}
	//	catch(ServiceException| RuntimeException e){
	//		throw new InternalServerErrorException();
	//	}
	//}
	
}
