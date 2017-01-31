package it.polito.dp2.NFFG.sol3.service.validators;

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

import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;


@Provider
@Consumes("application/xml")
public class NffgServiceRequestValidatorNamedEntities implements MessageBodyReader<NamedEntities> {
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == NamedEntities.class;
	}

	@Override
	public NamedEntities readFrom(Class<NamedEntities> type, Type genericType, Annotation[] annotations, MediaType mediaType,
					MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
		
		Logger logger = Logger.getLogger(NffgServiceRequestValidatorNamedEntities.class.getName());
		
		try {
			Unmarshaller unmarshaller = this.instantiateUnmarhaller();
			return (NamedEntities) unmarshaller.unmarshal(entityStream);
		} catch (JAXBException e) {
			String validationErrorMesage = "Request body validation error";
			logger.log(Level.WARNING, validationErrorMesage, e);
			Throwable linked = e.getLinkedException();
			if (linked != null && linked instanceof SAXParseException)
				validationErrorMesage += ": " + linked.getMessage();
			Response badRequestResponse = Response.status(Response.Status.BAD_REQUEST).entity(validationErrorMesage).build();
			throw new BadRequestException(badRequestResponse);
			
		}
	}
	
	/* for thread safety */
	private Unmarshaller instantiateUnmarhaller(){
		Logger logger = Logger.getLogger(NffgServiceRequestValidatorNamedEntities.class.getName());
		
		try {				
			Unmarshaller unmarshaller;
			
			InputStream schemaStream = NffgServiceRequestValidatorNamedEntities.class.getResourceAsStream("/xsd/nffgVerifier.xsd");
			if (schemaStream == null) {
				throw new IOException("xml schema file Not found");
			}
			JAXBContext jc = JAXBContext.newInstance(NamedEntities.class);
	        unmarshaller = jc.createUnmarshaller();
	        SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = sf.newSchema(new StreamSource(schemaStream));
	        unmarshaller.setSchema(schema);
	        return unmarshaller;
		} catch (SAXException | JAXBException | IOException e) {
			logger.log(Level.SEVERE, "Error parsing xml directory file. Service will not work properly.", e);
			return null;
		}
	}
}
