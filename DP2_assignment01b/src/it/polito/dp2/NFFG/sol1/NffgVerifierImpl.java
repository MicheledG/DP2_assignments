package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.PolicyReader;

import it.polito.dp2.NFFG.sol1.jaxb.*;

public class NffgVerifierImpl implements NffgVerifier {
	
	NffgInfoWrapper nffgInfoWrapper;
	
	public NffgVerifierImpl() throws NffgVerifierException {
		/*unmarshalls an XML document and feeds the root element with the unmarshalled root element*/
		JAXBContext jaxbContext = null;
		javax.xml.bind.Unmarshaller unmarshaller = null;
		
		try {
			/* create JAXBContext */
			System.out.println("Defining JAXBContext...");
			jaxbContext = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			System.out.println("Creating Unmarshaller...");
			unmarshaller = jaxbContext.createUnmarshaller();
		}
		catch(JAXBException e){
			System.err.println("ERROR: unable to create unmarshaller. Exception follows.");
            e.printStackTrace();
            throw new NffgVerifierException(e);
		}
		
		try {
			/* set validating unmarshaller */
			SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//TODO: find a better way to specify the schema location
			System.out.println("Loading XML Schema...");
			Schema schema = schemaFactory.newSchema(new File("xsd/nffgInfo.xsd"));
			unmarshaller.setSchema(schema);
			
			/* unmarshall */
			String fileName = System.getProperty("it.polito.dp2.NFFG.sol1.NffgInfo.file");
			System.out.println("Unmarshalling file:"+fileName+"...");
			Object rootElement = unmarshaller.unmarshal(new File(fileName));
			if(rootElement instanceof NffgInfoWrapper)
				this.nffgInfoWrapper = (NffgInfoWrapper) rootElement;
			else
				throw new JAXBException("root element type unexpected: "+rootElement.toString());
		}
		catch (JAXBException e) {
			System.err.println("ERROR: unable to unmarshall. Exception follows.");
            e.printStackTrace();
            throw new NffgVerifierException(e);
		} catch (SAXException e) {
			System.err.println("ERROR: unable to load the schema. Exception follows.");
            e.printStackTrace();
            throw new NffgVerifierException(e);
		}
	}
	
	@Override
	public NffgReader getNffg(String arg0) {
		
		NffgReader nffgReader = null;
		for (NffgInfoType nffgInfoType : this.nffgInfoWrapper.getNffgInfos().getNffgInfo()) {
			if(nffgInfoType.getNffg().getName().equals(arg0)){
				nffgReader = NffgReaderImpl.translateNffgTypeToNffgReader(nffgInfoType.getNffg());
				break;
			}
		}
		
		return nffgReader;
	}

	@Override
	public Set<NffgReader> getNffgs() {
		
		Set<NffgReader> nffgReaders = new HashSet<NffgReader>();
		for (NffgInfoType nffgInfoType : this.nffgInfoWrapper.getNffgInfos().getNffgInfo()) {
			nffgReaders.add(NffgReaderImpl.translateNffgTypeToNffgReader(nffgInfoType.getNffg()));
		}
		
		return nffgReaders;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
		for (NffgInfoType nffgInfoType : this.nffgInfoWrapper.getNffgInfos().getNffgInfo()) {
			for (PolicyType policyType : nffgInfoType.getPolicies().getPolicy()) {
				policyReaders.add(PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffgInfoType.getNffg(), policyType));
			}
		}
		
		return policyReaders;
	
	}

	@Override
	public Set<PolicyReader> getPolicies(String arg0) {
		
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
		for (NffgInfoType nffgInfo : this.nffgInfoWrapper.getNffgInfos().getNffgInfo()) {
			for (PolicyType policy : nffgInfo.getPolicies().getPolicy()) {
				if(policy.getNffg().equals(arg0)){
					PolicyReader policyReader = PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffgInfo.getNffg(), policy);
					policyReaders.add(policyReader);
				}
			}
		}
		return policyReaders;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar arg0) {
		
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
		for (NffgInfoType nffgInfo : this.nffgInfoWrapper.getNffgInfos().getNffgInfo()) {
			for (PolicyType policy : nffgInfo.getPolicies().getPolicy()) {
				Calendar lastVerification = NffgReaderImpl.translateXMLGregorianCalendarToCalendar(policy.getLastVerification());
				if(lastVerification.after(arg0)){
					PolicyReader policyReader = PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffgInfo.getNffg(), policy);
					policyReaders.add(policyReader);
				}
			}
		}
		
		return policyReaders;	
	}

}