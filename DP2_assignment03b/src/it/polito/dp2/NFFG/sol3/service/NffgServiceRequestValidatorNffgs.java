package it.polito.dp2.NFFG.sol3.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;


@Provider
@Consumes("application/xml")
public class NffgServiceRequestValidatorNffgs implements MessageBodyReader<Nffgs> {
	
	private final String jaxbPackage = "it.polito.dp2.NFFG.sol3.service.jaxb";
	private Unmarshaller unmarshaller;
	private Logger logger;
	
	public NffgServiceRequestValidatorNffgs(){
		logger = Logger.getLogger(NffgServiceRequestValidatorNffgs.class.getName());
		
		try {				
			InputStream schemaStream = NffgServiceRequestValidatorNffgs.class.getResourceAsStream("/xsd/nffgVerifier.xsd");
			if (schemaStream == null) {
				throw new IOException("xml schema file Not found");
			}
	        
			JAXBContext jc = JAXBContext.newInstance( jaxbPackage );
	        unmarshaller = jc.createUnmarshaller();
	        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = sf.newSchema(new StreamSource(schemaStream));
	        unmarshaller.setSchema(schema);
	        
		} catch (SAXException | JAXBException | IOException e) {
			logger.log(Level.SEVERE, "Error parsing xml directory file. Service will not work properly.", e);
		}
	}
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == Nffgs.class;
	}

	@Override
	public Nffgs readFrom(Class<Nffgs> type, Type genericType, Annotation[] annotations, MediaType mediaType,
					MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
			
		try {
			return (Nffgs) unmarshaller.unmarshal(entityStream);
		} catch (JAXBException e) {
			String validationErrorMessage = "Request body validation error";
			logger.log(Level.WARNING, validationErrorMessage, e);
			Throwable linked = e.getLinkedException();
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMessage += ": " + linked.getMessage();
			Response badRequestResponse = Response.status(Response.Status.BAD_REQUEST).entity(validationErrorMessage).build();
			throw new BadRequestException(badRequestResponse);
			
		}
	}
}
