package it.polito.dp2.NFFG.sol3.service;

import java.io.IOException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class NffgServiceResponseCreatedStatusFilter implements ContainerResponseFilter {
	
	private static final String nffgsSuffix = "/nffgs";
	private static final String policiesSuffix = "/policies";
	
	@Override
	public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
		
		String requestMethod = arg0.getMethod();
		String requestAbsolutePath = arg0.getUriInfo().getAbsolutePath().getPath();
		int responseStatus = arg1.getStatus();
		
		if(responseStatus == 200){
			/* OK RESPONSE */
			if(requestMethod.equals(HttpMethod.POST) && requestAbsolutePath.endsWith(NffgServiceResponseCreatedStatusFilter.nffgsSuffix))
				/* if it is a response to a POST on resource "/nffgs" */
				arg1.setStatus(201);	
			if(requestMethod.equals(HttpMethod.PUT) && requestAbsolutePath.endsWith(NffgServiceResponseCreatedStatusFilter.policiesSuffix))
				/* if it is a response to a PUT on resource "/policies" */
				arg1.setStatus(201);
		}
	}
}
