package it.polito.dp2.NFFG.sol3.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

@Path("/policies")
public class PoliciesResource {

	private NffgsDB nffgsDB = NffgsDB.newNffgsDB();
	private PoliciesDB policiesDB = PoliciesDB.newPoliciesDB();
	
	/* send to the client the list of policies loaded on the DB */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getNffgs(){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Policies policies = policiesDB.getPolicies();
			return Response.ok(policies).build();
		} 
		catch (NoPolicyException e) {
			return Response.noContent().build();
		}
		catch( RuntimeException  e){
			return Response.serverError().entity(e.toString()).build();
		}
		//catch(RuntimeException  e){
		//	return Response.serverError().build();
		//}
	}
	
	//TODO => demo version => no nffg check!
	/* store the posted policies into the DB */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response storePolicies(Policies policies){
		try{
			policiesDB.storePolicies(policies);
			return Response.noContent().build();
		} 
		catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	/* delete all the policies */
	@DELETE
	public Response deletePolicies(){
		try{
			policiesDB.deletePolicies();
			return Response.noContent().build();
		} 
		catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	@Path("/{policyName}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	/* get a single policy */
	public Response getSinglePolicies(@PathParam("policyName") String policyName){
		try{
			/* obtain from PoliciesDB the names of the loaded policies */
			Policies policies = policiesDB.getPolicies(policyName);
			return Response.ok(policies).build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.toString()).build();
		}
		catch(RuntimeException  e){
			return Response.serverError().entity(e.toString()).build();
		}
		//catch(ServiceException | RuntimeException  e){
		//	return Response.serverError().build();
		//}
	}
	
	/* delete a single policy */
	@Path("/{policyName}")
	@DELETE
	public Response deletePolicies(@PathParam("policyName") String policyName){
		try{
			policiesDB.deletePolicies(policyName);
			return Response.noContent().build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.toString()).build();
		}
		catch(RuntimeException e){
			return Response.serverError().entity(e.toString()).build();
		}
	}
	
}
