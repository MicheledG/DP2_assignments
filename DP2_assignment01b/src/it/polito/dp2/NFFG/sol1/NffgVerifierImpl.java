package it.polito.dp2.NFFG.sol1;

import java.io.File;
import java.util.Calendar;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.PolicyReader;

import it.polito.dp2.NFFG.sol1.jaxb.*;

public class NffgVerifierImpl implements NffgVerifier {
	
	NffgInfoWrapper nffgInfoWrapper;
	
	public NffgVerifierImpl(){
		/*unmarshalls an XML document and feeds the root element with the unmarshalled root element*/
		JAXBContext jaxbContext = null;
		javax.xml.bind.Unmarshaller unmarshaller = null;
		
		try {
			/* create JAXBContext */
			jaxbContext = JAXBContext.newInstance("it.polito.dp2.NFFG.sol1.jaxb");
			unmarshaller = jaxbContext.createUnmarshaller();
		}
		catch(JAXBException e){
			System.err.println("ERROR: unable to create unmarshaller. Exception follows.");
            e.printStackTrace();
            //TODO: server reliability => exit or return?
            System.exit(-1); 
		}
		
		try {
			/* set validating unmarshaller */
			SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//TODO: find a better way to specify the schema location
			Schema schema = schemaFactory.newSchema(new File("xsd/nffgInfo.xsd"));
			unmarshaller.setSchema(schema);
			
			/* unmarshall */
			//TODO: unmarshalls from a file?
			Object rootElement = unmarshaller.unmarshal(new File(XML_FILE_LOCATION));
			if(rootElement instanceof NffgInfoWrapper)
				this.nffgInfoWrapper = (NffgInfoWrapper) rootElement;
			else
				throw new JAXBException("root element type unexpected: "+rootElement.toString());
		}
		catch (JAXBException e) {
			System.err.println("ERROR: unable to unmarshall. Exception follows.");
            e.printStackTrace();
            //TODO: server reliability => exit or return?
            System.exit(-1);
		} catch (SAXException e) {
			System.err.println("ERROR: unable to load the schema. Exception follows.");
            e.printStackTrace();
            //TODO: server reliability => exit or return?
            System.exit(-1);
		}
	}
	
	@Override
	public NffgReader getNffg(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NffgReader> getNffgs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PolicyReader> getPolicies(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
