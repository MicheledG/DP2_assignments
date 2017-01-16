package it.polito.dp2.NFFG.sol3.service;

//TODO: check reachability check behavior
import java.lang.invoke.WrongMethodTypeException;

import javax.management.relation.RelationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

//TODO: remove from the response with status code SERVER ERROR the description of the exception

@Path("/policies")
public class PoliciesResource {

	private NffgService nffgService = new NffgService();
	
	/* store or update the posted policies into the service */
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response storePolicies(Policies policies){
		try{
			nffgService.storePolicies(policies);
			return Response.noContent().build();
		} 
		catch (RelationException e) {
			return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
		} 
		catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	/* send to the client the list of policies loaded on the service */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getPolicies(){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			Policies policies = nffgService.getPolicies();
			return Response.ok(policies).build();
		} 
		catch (NoPolicyException e) {
			return Response.noContent().build();
		}
		catch(RuntimeException  e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@Path("/{policyName}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	/* get a single policy */
	public Response getSinglePolicies(@PathParam("policyName") String policyName){
		try{
			/* obtain from PoliciesDB the names of the loaded policies */
			Policies policies = nffgService.getSinglePolicies(policyName);
			return Response.ok(policies).build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
		catch(RuntimeException  e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	/* delete all the policies */
	@DELETE
	public Response deletePolicies(){
		try{
			nffgService.deletePolicies();
			return Response.noContent().build();
		} 
		catch(RuntimeException e){
			return Response.serverError().build();
		}
	}
	
	/* delete a single policy */
	@Path("/{policyName}")
	@DELETE
	public Response deleteSinglePolicies(@PathParam("policyName") String policyName){
		try{
			nffgService.deleteSinglePolicies(policyName);
			return Response.noContent().build();
		}
		catch (UnknownNameException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
		catch(RuntimeException e){
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	/* update the single posted policies into the service */
	@Path("/verifier")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response verifyPolicies(NamedEntities policyNames){
		try{
			Policies policiesVerified = nffgService.verifyReachabilityPolicies(policyNames);
			return Response.ok(policiesVerified).build();
		}
		catch (UnknownNameException /*| WrongMethodTypeException */ e) {
			return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
		} 
		catch(ServiceException | RuntimeException e){
			return Response.serverError().build();
		}
	}
	

}
