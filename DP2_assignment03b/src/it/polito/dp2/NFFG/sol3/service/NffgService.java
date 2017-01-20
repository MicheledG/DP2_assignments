package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.management.relation.RelationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.EntityPointerType;
import it.polito.dp2.NFFG.sol3.service.jaxb.EntityPointers;
import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;
import it.polito.dp2.NFFG.sol3.service.jaxb.VerificationResultType;


public class NffgService {
	
	private NffgsDB nffgsDB = NffgsDB.newNffgsDB();
	private PoliciesDB policiesDB = PoliciesDB.newPoliciesDB();
	
	private static final String POSITIVE_POLICY_RESULT_DESCRIPTION = "Policy verification result true";
	private static final String NEGATIVE_POLICY_RESULT_DESCRIPTION = "Policy verification result not true";
	private static URI NffgServiceURL;
	private static final String NFFGS_TYPE = "nffgs";
	private static final String POLICIES_TYPE = "policies";
	
	public NffgService(){
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		if(baseURL == null)
			NffgService.NffgServiceURL = URI.create("http://localhost:8080/NffgService/rest");
		else
			NffgService.NffgServiceURL = URI.create(baseURL);
	}
	
	/* store nffgs into the DB */
	public EntityPointers storeNffgs(Nffgs nffgs) throws AlreadyLoadedException, ServiceException {
		EntityPointers nffgPointers = new EntityPointers();
		for (Nffgs.Nffg nffg : nffgs.getNffg()) {
			this.nffgsDB.storeNffg(nffg);
			String nffgName = nffg.getName();
			EntityPointerType nffgPointer = this.createEntityPointer(NffgService.NFFGS_TYPE, nffgName);
			nffgPointers.getEntityPointer().add(nffgPointer);
		}
		return nffgPointers;
	}

	/* get the list of nffg names and pointer of the nffgs stored into the DB */
	public EntityPointers getNffgPointers() throws NoGraphException, ServiceException {
		Set<String> nffgNames = nffgsDB.getNffgNames();
		EntityPointers nffgPointers = new EntityPointers();
		
		for (String nffgName : nffgNames) {
			EntityPointerType nffgPointer = this.createEntityPointer(NffgService.NFFGS_TYPE, nffgName);
			nffgPointers.getEntityPointer().add(nffgPointer);
		}
		
		return nffgPointers;
	}
	
	/* get a single nffg stored into the DB */
	public Nffgs getSingleNffgs(String nffgName) throws UnknownNameException, ServiceException{
		Nffgs nffgsToSend = new Nffgs();
		Nffgs.Nffg nffg = nffgsDB.getNffg(nffgName);
		nffgsToSend.getNffg().add(nffg);
		return nffgsToSend;
	}
	
	/* delete all the data from the DB */
	public void deleteAll() throws ServiceException {
		/* delete both nffgs and policies */
		nffgsDB.deleteNffgs();
		policiesDB.deletePolicies();
		return;
	}
	
	public void deleteNffg(String nffgName, boolean deletePolicies) throws UnknownNameException,
	RelationException, ServiceException{
		
		if(!nffgsDB.containsNffg(nffgName))
			/* missing nffg */
			throw new UnknownNameException("missing nffg named "+nffgName);
		
		if(deletePolicies){
			/* delete eventual policies referring to this nffg */
			if(policiesDB.areReferringThisNffg(nffgName)){
				/* get the names of the policies that refers to this nffg */
				List<String> policyNames = policiesDB.getPolicyNamesReferringNffg(nffgName);
				/* remove the policies referring this nffg from policiesDB */
				for (String policyName : policyNames) {
					policiesDB.deletePolicy(policyName);
				}
			}
		}	
		else{
			/* don't have to remove the policies but you must check if there are policies referring this nffg */
			if(policiesDB.areReferringThisNffg(nffgName))
				throw new RelationException("there are policies referring nffg "+nffgName);
		}
		
		/* eventually delete the nffg */
		nffgsDB.deleteNffg(nffgName);
		
		return;
	}
	
	/* store or update already present policies into the DB */
	public EntityPointers storePolicies(Policies policies) throws RelationException, ServiceException{
		
		/* check if the policies to store refers to an Nffg not stored inside the DB*/
		this.checkNffgsReferencedByPolicies(policies);
		
		EntityPointers policyPointers = new EntityPointers();
		/* load a policy at a time */
		for (Policies.Policy policy : policies.getPolicy()) {
			String policyName = policy.getName();
			String nffgName = policy.getNffg();
			try{
				/* check if the referenced nffg contains the src node */
				String srcNodeName = policy.getSourceNode();
				if(!nffgsDB.nffgContainsNode(nffgName, srcNodeName))
					throw new RelationException("Missing node "+srcNodeName+" in nffg "+nffgName+" referenced by policy "+policyName);
				/* check if the referenced nffg contains the dst node */
				String dstNodeName = policy.getDestinationNode();
				if(!nffgsDB.nffgContainsNode(nffgName, dstNodeName))
					throw new RelationException("Missing node "+dstNodeName+" in nffg "+nffgName+" referenced by policy "+policyName);	
			} 
			catch (UnknownNameException e) {
				throw new ServiceException("corrupted DB! nffg "+nffgName+" removed during the verification");
			}
			/* store the policies into policiesDB */
			policiesDB.storePolicy(policy);
			EntityPointerType policyPointer = this.createEntityPointer(NffgService.POLICIES_TYPE, policyName);
			policyPointers.getEntityPointer().add(policyPointer);
		}
		return policyPointers;
	
	}
	
	/* get the list of policies loaded on the DB */
	public EntityPointers getPolicyPointers() throws NoPolicyException{
		Set<String> policyNames = policiesDB.getPolicyNames();
		EntityPointers policyPointers = new EntityPointers();
		
		for (String policyName : policyNames) {
			EntityPointerType policyPointer = new EntityPointerType();
			policyPointer.setName(policyName);
			policyPointer.setPointer(NffgService.NffgServiceURL+"/policies/"+policyName);
			policyPointers.getEntityPointer().add(policyPointer);
		}
		
		return policyPointers;
	}
	
	/* get a single policy from policiesDB */
	public Policies getSinglePolicies(String policyName) throws UnknownNameException{
		/* get a single policy stored into the DB */
		Policies policiesToSend = new Policies();
		Policies.Policy policy = policiesDB.getPolicy(policyName);
		policiesToSend.getPolicy().add(policy);
		return policiesToSend;
	}
	
	/* delete all the policies */
	public void deletePolicies(){
		policiesDB.deletePolicies();
	}
	
	/* delete a single policy */
	public void deletePolicy(String policyName) throws UnknownNameException{
		policiesDB.deletePolicy(policyName);
		return;
	}
	
	/* verify a reachability policy already loaded on policiesDB */
	public Policies verifyReachabilityPolicies(NamedEntities policyNamesPosted) throws UnknownNameException, /*WrongMethodTypeException,*/ ServiceException {
		
		List<String> policyNames = policyNamesPosted.getName();
		/* check if all the policies are stored on policiesDB and if they are of type "Reachability"*/
		for (String policyName: policyNames) {
			if(!policiesDB.containsPolicy(policyName))
				throw new UnknownNameException("missing policy named "+policyName);
		}
		
		/*verify each policy*/
		Policies policiesVerified = new Policies();
		for (String policyName: policyNames) {
			/* extract information to perform the verification */
			Policies.Policy policyToVerify = policiesDB.getPolicy(policyName);
			String nffgName = policyToVerify.getNffg();
			String srcNodeName = policyToVerify.getSourceNode();
			String dstNodeName = policyToVerify.getDestinationNode();
			boolean pathIsPresent = nffgsDB.isTherePath(nffgName, srcNodeName, dstNodeName);
			XMLGregorianCalendar nowTime = this.getNowTimeXMLGregorianCalendar();
			
			/* compute the result of the verification */
			boolean satisfied;
			if(policyToVerify.isPositive())	
				satisfied = pathIsPresent;
			else 
				satisfied = !pathIsPresent;
			String verificationDescription;
			if(satisfied)
				verificationDescription = NffgService.POSITIVE_POLICY_RESULT_DESCRIPTION;
			else
				verificationDescription = NffgService.NEGATIVE_POLICY_RESULT_DESCRIPTION;
			
			/* store the verification result */
			VerificationResultType verificationResult = new VerificationResultType();
			verificationResult.setSatisfied(satisfied);
			verificationResult.setLastVerification(nowTime);
			verificationResult.setDescription(verificationDescription);

			/* create the verified policy */
			Policies.Policy policyVerified = policyToVerify;
			policyVerified.setVerificationResult(verificationResult);
			
			/* update the policy on policiesDB */
			policiesDB.storePolicy(policyVerified);
			
			/* add the verified policy to the set of the policies to send to the client */
			policiesVerified.getPolicy().add(policyVerified);
		}
		
		return policiesVerified;
	}
	
	private void checkNffgsReferencedByPolicies(Policies policies) throws RelationException{
		/* check if a policy refers an Nffg not loaded on NffgDB */
		for (Policies.Policy policy : policies.getPolicy()) {
			if(!nffgsDB.containsNffg(policy.getNffg()))
				throw new RelationException("missing nffg "+policy.getNffg()+" for policy " + policy.getName());
		}
	}
	
	/* create an entity pointer */
	private EntityPointerType createEntityPointer(String entityType, String entityName) {
		EntityPointerType entityPointer = new EntityPointerType();
		entityPointer.setName(entityName);
		entityPointer.setPointer(NffgService.NffgServiceURL+"/"+entityType+"/"+entityName);
		return entityPointer;
	}
	
	private XMLGregorianCalendar getNowTimeXMLGregorianCalendar(){
		Date now = new Date(System.currentTimeMillis());
		/* create the xml gregorian calendar */
		GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
		nowGregorianCalendar.setTime(now);
		XMLGregorianCalendarImpl nowXMLGregorianCalendar = new XMLGregorianCalendarImpl(nowGregorianCalendar);
		return nowXMLGregorianCalendar;
	}
	
}
