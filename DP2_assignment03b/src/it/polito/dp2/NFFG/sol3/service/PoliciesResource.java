package it.polito.dp2.NFFG.sol3.service;

import javax.management.relation.RelationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.EntityPointers;
import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;


@Path("/policies")
public class PoliciesResource {

	private NffgService nffgService = new NffgService();
	
	/* store or update the posted policies into the service */
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public void storePolicies(Policies policies){
		try{
			nffgService.storePolicies(policies);
			return;
		} 
		catch (RelationException e) {
			Response forbiddenResponse = Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build(); 
			throw new ForbiddenException(forbiddenResponse);
		} 
		catch(ServiceException | RuntimeException e){
			throw new InternalServerErrorException();
		}
	}
	
	/* send to the client the list of policies loaded on the service */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public EntityPointers getPolicyPointers(){
		try{
			/* obtain from NffgDB the names of the loaded NFFGs */
			EntityPointers policyPointers = nffgService.getPolicyPointers();
			return policyPointers;
		} 
		catch (NoPolicyException e) {
			return null;
		}
		catch(RuntimeException  e){
			throw new InternalServerErrorException();
		}
	}
	
	@Path("/{policyName}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	/* get a single policy */
	public Policies getSinglePolicies(@PathParam("policyName") String policyName){
		try{
			/* obtain from PoliciesDB the names of the loaded policies */
			Policies policies = nffgService.getSinglePolicies(policyName);
			return policies;
		}
		catch (UnknownNameException e) {
			Response notFoundResponse = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new NotFoundException(notFoundResponse);
		}
		catch(RuntimeException e){
			throw new InternalServerErrorException();
		}
	}
	
	/* delete all the policies */
	@DELETE
	public void deletePolicies(){
		try{
			nffgService.deletePolicies();
			return;
		} 
		catch(RuntimeException e){
			throw new InternalServerErrorException();
		}
	}
	
	/* delete a single policy */
	@Path("/{policyName}")
	@DELETE
	public void deleteSinglePolicies(@PathParam("policyName") String policyName){
		try{
			nffgService.deletePolicy(policyName);
			return;
		}
		catch (UnknownNameException e) {
			Response notFoundResponse = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new NotFoundException(notFoundResponse);
		}
		catch(RuntimeException e){
			throw new InternalServerErrorException();
		}
	}
	
	/* update the single posted policies into the service */
	@Path("/verifier")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Policies verifyPolicies(NamedEntities policyNames){
		try{
			Policies policiesVerified = nffgService.verifyReachabilityPolicies(policyNames);
			return policiesVerified;
		}
		catch (UnknownNameException e) {
			Response forbiddenResponse = Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build(); 
			throw new ForbiddenException(forbiddenResponse);
		} 
		catch(ServiceException | RuntimeException e){
			throw new InternalServerErrorException();
		}
	}
	

}
