package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.management.relation.RelationException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.sol3.service.DBs.NffgsDB;
import it.polito.dp2.NFFG.sol3.service.DBs.PoliciesDB;
import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.EntityPointerType;
import it.polito.dp2.NFFG.sol3.service.jaxb.EntityPointers;
import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;
import it.polito.dp2.NFFG.sol3.service.jaxb.VerificationResultType;


public class NffgService {
	
	/* both following class are instances of a singleton, they can be used to synchronize */ 
	private NffgsDB nffgsDB = NffgsDB.getNffgsDB();
	private PoliciesDB policiesDB = PoliciesDB.getPoliciesDB();
	
	private static final String POSITIVE_POLICY_RESULT_DESCRIPTION = "Policy verification result true";
	private static final String NEGATIVE_POLICY_RESULT_DESCRIPTION = "Policy verification result not true";
	private static final String NFFGS_TYPE = "nffgs";
	private static final String POLICIES_TYPE = "policies";
	
	private URI NffgServiceURL;
	
	public NffgService(){
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		if(baseURL == null)
			this.NffgServiceURL = URI.create("http://localhost:8080/NffgService/rest");
		else
			this.NffgServiceURL = URI.create(baseURL);
	}
	
	/* store nffgs into the DB */
	/* reentrant method */
	public EntityPointers storeNffgs(Nffgs nffgs) throws AlreadyLoadedException, ServiceException {
		EntityPointers nffgPointers = new EntityPointers();
		for (Nffgs.Nffg nffg : nffgs.getNffg()) {
			/* storeNffg is reentrant */
			this.nffgsDB.storeNffg(nffg);
			String nffgName = nffg.getName();
			EntityPointerType nffgPointer = this.createEntityPointer(NffgService.NFFGS_TYPE, nffgName);
			nffgPointers.getEntityPointer().add(nffgPointer);
		}
		return nffgPointers;
	}

	/* get the list of nffg names and pointer of the nffgs stored into the DB */
	/* reentrant method */
	public EntityPointers getNffgPointers() throws NoGraphException, ServiceException {
		/* getNffgsNames is "atomic" */
		Set<String> nffgNames = nffgsDB.getNffgNames();
		EntityPointers nffgPointers = new EntityPointers();
		
		for (String nffgName : nffgNames) {
			EntityPointerType nffgPointer = this.createEntityPointer(NffgService.NFFGS_TYPE, nffgName);
			nffgPointers.getEntityPointer().add(nffgPointer);
		}
		
		return nffgPointers;
	}
	
	/* get a single nffg stored into the DB */
	/* reentrant method */
	public Nffgs getSingleNffgs(String nffgName) throws UnknownNameException, ServiceException{
		Nffgs nffgsToSend = new Nffgs();
		/* getNffg is reentrant*/
		Nffgs.Nffg nffg = nffgsDB.getNffg(nffgName);
		nffgsToSend.getNffg().add(nffg);
		return nffgsToSend;
	}
	
	/* store or update already present policies into the DB */
	public EntityPointers storePolicies(Policies policies) throws RelationException, ServiceException{
		
		EntityPointers policyPointers = new EntityPointers();
		/* load a policy at a time */
		for (Policies.Policy policy : policies.getPolicy()) {
			String policyName = policy.getName();
			String nffgName = policy.getNffg();
			/* there is no need to synchronize the following block because there is no possibility of remove nffgs,
			 * so if it present it is ok, otherwise stop 
			 */
			try{
				/* check if the referenced nffg is present in NffgsDB */
				if(!this.nffgsDB.containsNffg(nffgName))
					throw new RelationException("Missing nffg "+nffgName+" referenced by policy "+policyName);
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
			/* need to synchronize to avoid a possible update of a policy being verified */
			synchronized (this.policiesDB){
				policiesDB.storePolicy(policy);
			}
			EntityPointerType policyPointer = this.createEntityPointer(NffgService.POLICIES_TYPE, policyName);
			policyPointers.getEntityPointer().add(policyPointer);
		}
		return policyPointers;
	
	}
	
	/* get the list of policies loaded on the DB */
	/* reentrant method, it always read a consistent status */
	public EntityPointers getPolicyPointers() throws NoPolicyException{
		Set<String> policyNames = policiesDB.getPolicyNames();
		EntityPointers policyPointers = new EntityPointers();
		
		for (String policyName : policyNames) {
			EntityPointerType policyPointer = new EntityPointerType();
			policyPointer.setName(policyName);
			policyPointer.setPointer(this.NffgServiceURL+"/policies/"+policyName);
			policyPointers.getEntityPointer().add(policyPointer);
		}
		
		return policyPointers;
	}
	
	/* get a single policy from policiesDB */
	/* reentrant method, it always read a consistent status  */ 
	public Policies getSinglePolicies(String policyName) throws UnknownNameException{
		/* get a single policy stored into the DB */
		Policies policiesToSend = new Policies();
		/* get policy is atomic */
		Policies.Policy policy = policiesDB.getPolicy(policyName);
		policiesToSend.getPolicy().add(policy);
		return policiesToSend;
	}
	
	/* delete all the policies */
	/* need to synchronize to avoid interleaving during verification */
	public void deletePolicies(){
		synchronized (this.policiesDB){
			this.policiesDB.deletePolicies();
		}
	}
	
	/* delete a single policy */
	/* need to synchronize to avoid interleaving during verification */
	public void deletePolicy(String policyName) throws UnknownNameException{
		synchronized (this.policiesDB){
			this.policiesDB.deletePolicy(policyName);
		}
		return;
	}
	
	/* verify a reachability policy already loaded on policiesDB */
	public Policies verifyReachabilityPolicies(NamedEntities policyNamesPosted) throws UnknownNameException, ServiceException {
		
		List<String> policyNames = policyNamesPosted.getName();
		
		/*verify each policy*/
		Policies policiesVerified = new Policies();
		for (String policyName: policyNames) {
			Policies.Policy policyToVerify;
			/* to avoid getting a UnknownNameException on the getPolicy call since a delete is interleaved */
			synchronized(this.policiesDB){
				/* check if the policy is on the PoliciesDB */
				this.policiesDB.containsPolicy(policyName);
				/* extract information to perform the verification */
				policyToVerify = policiesDB.getPolicy(policyName);	
			}
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
	
	/* create an entity pointer */
	private EntityPointerType createEntityPointer(String entityType, String entityName) {
		EntityPointerType entityPointer = new EntityPointerType();
		entityPointer.setName(entityName);
		entityPointer.setPointer(this.NffgServiceURL+"/"+entityType+"/"+entityName);
		return entityPointer;
	}
	
	private XMLGregorianCalendar getNowTimeXMLGregorianCalendar(){
		Date now = new Date(System.currentTimeMillis());
		/* create the xml gregorian calendar */
		GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
		nowGregorianCalendar.setTime(now);
		XMLGregorianCalendar nowXMLGregorianCalendar;
		try {
			nowXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(nowGregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			nowXMLGregorianCalendar = null;
		}
		return nowXMLGregorianCalendar;
	}
	
	///* delete all the data from the DB */
	//public void deleteAll() throws ServiceException {
	//	/* delete both nffgs and policies */
	//	nffgsDB.deleteNffgs();
	//	policiesDB.deletePolicies();
	//	return;
	//}
	
	//public void deleteNffg(String nffgName, boolean deletePolicies) throws UnknownNameException,
	//RelationException, ServiceException{
	//	
	//	if(!nffgsDB.containsNffg(nffgName))
	//		/* missing nffg */
	//		throw new UnknownNameException("missing nffg named "+nffgName);
	//	
	//	if(deletePolicies){
	//		/* delete eventual policies referring to this nffg */
	//		if(policiesDB.areReferringThisNffg(nffgName)){
	//			/* get the names of the policies that refers to this nffg */
	//			List<String> policyNames = policiesDB.getPolicyNamesReferringNffg(nffgName);
	//			/* remove the policies referring this nffg from policiesDB */
	//			for (String policyName : policyNames) {
	//				policiesDB.deletePolicy(policyName);
	//			}
	//		}
	//	}	
	//	else{
	//		/* don't have to remove the policies but you must check if there are policies referring this nffg */
	//		if(policiesDB.areReferringThisNffg(nffgName))
	//			throw new RelationException("there are policies referring nffg "+nffgName);
	//	}
	//	
	//	/* eventually delete the nffg */
	//	nffgsDB.deleteNffg(nffgName);
	//	
	//	return;
	//}
	
}
