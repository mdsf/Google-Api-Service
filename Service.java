
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientCredentialsTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * @author Moeid Shariff
 *
 */
@Component(metatype=true, immediate=true, label="Service", description="Client Service")
@Service(value=Service.class)
@Properties({ @Property(
		label = "Token Location",
		name = "clientname.api.tokenlocation",
		description = "Client Name API Token Location",
		value = "/api/oauth2/token") ,
		@Property(
	    label = "Client Id",
		name = "clientname.api.clientid",
		description = "API Client Id",
		value = "server ID"),
		@Property(
	    label = "Client Secret",
		name = "clientname.api.clientsecret",
		description = "Client Name API Client Secret",
		value = "Password"),
		@Property(
		label = "Scope",
		name = "clientname.api.scope",
		description = "Client Name API Scope",
		value = "scope"),
		@Property(
	    label = "Service endpoint",
		name = "clientname.api.variable.endpoint",
		description = "Client Name Service endpoint",
		value = "/api/address"),
		@Property(
		label = "Client Name time endpoint",
		name = "clientname.api.variable.endpoint",
		description = "Client Name Service endpoint",
		value = ""),
		@Property(
		label = "API Root Path",
		name = "client.environment.rootpath",
		description = "Service Request Root Path",
		value = "Api Url address https://")
})

public class Service {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
	
	private static String tokenLocation = "";
	private static String clientId = "";
	private static String clientSecret = "";
	private static String scope = "";
	private static String clientendpoint = "";
	private static String newclientendpoint = "";
	private static String rootpath = "";
	
	
	/**
	 * @param context
	 * @throws Exception
	 */
	protected void activate(final ComponentContext context) throws Exception {
		@SuppressWarnings("unchecked")
		Dictionary<String,String> props = context.getProperties();
		if (props!=null) {
			tokenLocation = props.get("clientname.api.tokenlocation");
			clientId = props.get("clientname.api.clientid");
			clientSecret = props.get("clientname.api.clientsecret");
			scope = props.get("clientname.api.scope");
			clientendpoint = props.get("clientname.api.variable.endpoint");
			newclientendpoint = props.get("clientname.api.variable.endpoint");
			rootpath = props.get("clientname.variable.rootpath");
		}
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	public String getService() {
		
		 String accessToken = null;
		  HttpResponse restresp = null;
			try {
		 NetHttpTransport netHttpTransport = new NetHttpTransport();
		 accessToken = getAccessToken();
		 GenericUrl Url = new GenericUrl(rootpath + ucendpoint);
         netHttpTransport.createRequestFactory(new HttpRequestInitializer() {
			
			@Override
			public void initialize(HttpRequest request) throws IOException {
				HttpHeaders headers = new HttpHeaders();
			    String accept = "Authorization" + "," + "Bearer "+ getAccessToken();
               headers.setCacheControl("no-cache");                         
               request.setHeaders(headers.setAccept(accept));
               request.setRequestMethod(HttpMethods.GET);

			}
		});
         			Credential credential =
        	        new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        	    HttpRequestFactory requestFactory = netHttpTransport.createRequestFactory(credential);
					restresp = requestFactory.buildGetRequest(Url).execute();
					  return  restresp.parseAsString();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.error("IOException in TestServlet.java");
					e.printStackTrace();
					return"{\"message\":\"" + e.getMessage() + "\"}";
				}
        	
      
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */

	private String getAccessToken() throws IOException {
		
		TokenResponse response = null;            
              ArrayList<String> scopes = new ArrayList<String>();
              scopes.add(scope);
     
              
              NetHttpTransport netHttpTransport = new NetHttpTransport();
              netHttpTransport.createRequestFactory(new HttpRequestInitializer() {
				
				@Override
				public void initialize(HttpRequest request) {
					HttpHeaders headers = new HttpHeaders();
                    headers.setContentType("application/x-www-form-urlencoded");
                    headers.setCacheControl("no-cache");                         
                    request.setHeaders(headers);                                
                    request.setRequestMethod(HttpMethods.POST);
					
				}
			});
              
              ClientCredentialsTokenRequest ccRequest =
                      new ClientCredentialsTokenRequest(netHttpTransport, new com.google.api.client.json.jackson2.JacksonFactory(),
                          new GenericUrl(rootpath + tokenLocation))
                                 .setGrantType("client_credentials")
                                 .setScopes(scopes)
                                 .setClientAuthentication(new BasicAuthentication(clientId, clientSecret));
                
               response = ccRequest.execute();
		return response.getAccessToken();
	}
	

}
