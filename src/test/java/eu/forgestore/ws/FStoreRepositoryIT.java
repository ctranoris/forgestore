/**
 * Copyright 2014 forgestore.eu, University of Patras 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.forgestore.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import eu.forgestore.ws.model.FStoreUser;
import eu.forgestore.ws.model.UserSession;
import eu.forgestore.ws.util.EncryptionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//RUN a single Integration Test only, but runs all unit tests
//mvn clean -Pjetty.integration -Dit.test=BakerRepositoryIT verify

public class FStoreRepositoryIT {

	private static String endpointUrl;
	private static final transient Log logger = LogFactory.getLog(FStoreRepositoryIT.class.getName());
	private NewCookie cookieJSESSIONID;

	@BeforeClass
	public static void beforeClass() {
		endpointUrl = System.getProperty("service.url");
		logger.info("EbeforeClass endpointUrl = " + endpointUrl);

	}

	
	@Before
	public void APIlogin(){
		Response r = execPOSTonURLForAPILogin(endpointUrl + "/services/api/repo/sessions", "admin", "changeme");
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());

		Map<String, NewCookie> cookies = r.getCookies();
		
		Iterator it = cookies.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        logger.info("=======> RESPONSE COOKIES  =>"+pairs.getKey() + " = " + pairs.getValue());
	    }
		 cookieJSESSIONID = cookies.get("JSESSIONID");
	}

	@Test
	public void testManagementOfRepo() throws Exception {
		List<FStoreUser> busers = getUsers();
		int initialBakerUserList = busers.size();
		FStoreUser bu = new FStoreUser();
		bu.setName("ATESTUSER");
		bu.setOrganization("ANORGANIZATION");
		bu.setPasswordUnencrypted("APASS");
		bu.setUsername("AUSERNAME");
		bu.setEmail("ANEMAIL");

		// add a user...
		FStoreUser retBU = addUser(bu);
		assertNotNull(bu.getId());
		assertEquals(bu.getName(), retBU.getName());
		assertEquals(bu.getOrganization(), retBU.getOrganization());
		//assertEquals( EncryptionUtil.hash( bu.getPassword() ), retBU.getPassword());
		assertEquals(bu.getUsername(), retBU.getUsername());

		// should be one more user in the DB
		assertEquals(initialBakerUserList + 1, getUsers().size());

		// GET a user by Id
		FStoreUser retBUbyGET = getUserById(retBU.getId());
		assertEquals(retBU.getName(), retBUbyGET.getName());
		assertEquals(retBU.getOrganization(), retBUbyGET.getOrganization());
		//assertEquals(EncryptionUtil.hash( bu.getPassword() ), retBUbyGET.getPassword());
		assertEquals(retBU.getUsername(), retBUbyGET.getUsername());

		// update user
		bu = new FStoreUser();
		bu.setName("ATESTUSERNEW");
		bu.setOrganization("ANORGANIZATIONNEW");
		bu.setPasswordUnencrypted("APASSNEW");
		bu.setUsername("AUSERNAMENEW");
		bu.setId(retBU.getId());
		FStoreUser retBUUpdated = updateUser(retBU.getId(), bu);
		assertEquals(retBU.getId(), retBUUpdated.getId());
		assertEquals(bu.getName(), retBUUpdated.getName());
		assertEquals(bu.getOrganization(), retBUUpdated.getOrganization());
//		assertEquals(EncryptionUtil.hash( bu.getPassword() ), retBUUpdated.getPassword());
		assertEquals(bu.getUsername(), retBUUpdated.getUsername());

		// should be again the same user count in the DB
		assertEquals(initialBakerUserList + 1, getUsers().size());

		// GET the updated user by Id
		retBUbyGET = getUserById(retBU.getId());
		assertEquals(bu.getId(), retBUbyGET.getId());
		assertEquals(bu.getName(), retBUbyGET.getName());
		assertEquals(bu.getOrganization(), retBUbyGET.getOrganization());
//		assertEquals(EncryptionUtil.hash( bu.getPassword() ), retBUbyGET.getPassword());
		assertEquals(bu.getUsername(), retBUbyGET.getUsername());
		
		
		//delete our added user
		deleteUserById(retBU.getId());

		assertEquals(initialBakerUserList , getUsers().size());
		

	}

	private void deleteUserById(int id) {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());

		WebClient client = WebClient.create(endpointUrl + "/services/api/repo/users/" + id, providers);
		client.cookie(cookieJSESSIONID);
		Response r = client.accept("application/json").type("application/json").delete();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		
	}

	private FStoreUser updateUser(int id, FStoreUser bu) throws JsonParseException, IOException {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());

		WebClient client = WebClient.create(endpointUrl + "/services/api/repo/users/" + id, providers);
		client.cookie(cookieJSESSIONID);
		Response r = client.accept("application/json").type("application/json").put(bu);
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());

		MappingJsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createJsonParser((InputStream) r.getEntity());
		FStoreUser output = parser.readValueAs(FStoreUser.class);
		return output;
	}

	private FStoreUser getUserById(int id) throws JsonParseException, IOException {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());

		WebClient client = WebClient.create(endpointUrl + "/services/api/repo/users/" + id, providers);
		client.cookie(cookieJSESSIONID);
		Response r = client.accept("application/json").type("application/json").get();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());

		MappingJsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createJsonParser((InputStream) r.getEntity());
		FStoreUser output = parser.readValueAs(FStoreUser.class);
		return output;
	}

	private FStoreUser addUser(FStoreUser bu) throws JsonParseException, IOException {

		List<Object> providers = new ArrayList<Object>();
		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());
		
		//without session cookie first! SHould return 401 (UNAUTHORIZED)
		WebClient client = WebClient.create(endpointUrl + "/services/api/repo/users", providers);
		Response r = client.accept("application/json").type("application/json").post(bu);
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
		
		//again with session cookie

		client = WebClient.create(endpointUrl + "/services/api/repo/users", providers);
		client.cookie(cookieJSESSIONID);
		r = client.accept("application/json").type("application/json").post(bu);
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		
		MappingJsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createJsonParser((InputStream) r.getEntity());
		FStoreUser output = parser.readValueAs(FStoreUser.class);
		return output;
	}

	public List<FStoreUser> getUsers() throws Exception {

		logger.info("Executing TEST = testGetUsers");

		
		Response r = execGETonURL(endpointUrl + "/services/api/repo/users", cookieJSESSIONID);
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());

		String bakerAPIVersionListHeaders = (String) r.getHeaders().getFirst("X-Baker-API-Version");
		assertEquals("1.0.0", bakerAPIVersionListHeaders);

		MappingJsonFactory factory = new MappingJsonFactory();
		JsonParser parser = factory.createJsonParser((InputStream) r.getEntity());

		JsonNode node = parser.readValueAsTree();
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<FStoreUser>> typeRef = new TypeReference<List<FStoreUser>>() {
		};
		List<FStoreUser> bakerUsersList = mapper.readValue(node.traverse(), typeRef);
		for (FStoreUser f : bakerUsersList) {
			logger.info("user = " + f.getName() + ", ID = " + f.getId());
		}

		return bakerUsersList;
	}

	private Response execPOSTonURLForAPILogin(String url, String username, String passw) {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());

		WebClient client = WebClient.create(url, providers, username, passw, null);

		Cookie cookie = new Cookie("X-Baker-Key", "123456") ;
		client.cookie(cookie );
		
		UserSession uses = new UserSession();
		uses.setUsername(username);
		uses.setPassword(passw);
		Response r = client.accept("application/json").type("application/json").post(uses);
		return r;
	}

	private Response execGETonURL(String url, Cookie sessioncookie) {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());

		WebClient client = WebClient.create(url, providers);

		Cookie cookie = new Cookie("X-Baker-Key", "123456") ;
		client.cookie(cookie );
		client.cookie(sessioncookie);
		
		Response r = client.accept("application/json").type("application/json").get();
		return r;
	}

//	private Response execGETonURL(String url) {
//		List<Object> providers = new ArrayList<Object>();
//		providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());
//
//		WebClient client = WebClient.create(url, providers);
//		Cookie cookie = new Cookie("X-Baker-Key", "123456") ;
//		client.cookie(cookie );
//		
//		Response r = client.accept("application/json").type("application/json").get();
//		return r;
//	}

}
