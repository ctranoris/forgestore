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

package eu.forgestore.ws.repo;

import eu.forgestore.ws.model.FStoreProperty;
import eu.forgestore.ws.model.FStoreUser;
import eu.forgestore.ws.model.Category;
import eu.forgestore.ws.model.Course;
import eu.forgestore.ws.model.FIREAdapter;
import eu.forgestore.ws.model.IFStoreRepositoryAPI;
import eu.forgestore.ws.model.Product;
import eu.forgestore.ws.model.UserSession;
import eu.forgestore.ws.model.Widget;
import eu.forgestore.ws.util.EmailUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.jms.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.utils.multipart.AttachmentUtils;
import org.apache.cxf.rs.security.cors.CorsHeaderConstants;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.cxf.rs.security.cors.LocalPreflight;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Access.Service.Endpoint;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Access.Service;
import com.woorea.openstack.nova.model.Server;
import com.woorea.openstack.nova.model.Servers;


//CORS support
//@CrossOriginResourceSharing(
//        allowOrigins = {
//           "http://83.212.106.218"
//        },
//        allowCredentials = true
//        
//)
@Path("/repo")
public class FStoreRepositoryAPIImpl implements IFStoreRepositoryAPI {

	@Context
	UriInfo uri;
	
	@Context
	HttpHeaders headers;

	@Context
	MessageContext ws;

	@Context
	protected SecurityContext securityContext;

	private static final transient Log logger = LogFactory.getLog(FStoreRepositoryAPIImpl.class.getName());

	private static final String METADATADIR = System.getProperty("user.home") + File.separator + ".fstore/metadata/";

	private FStoreRepository fstoreRepositoryRef;


	@GET
	@Path("/users/")
	@Produces("application/json")
	// @RolesAllowed("admin") //see this for this annotation
	// http://pic.dhe.ibm.com/infocenter/radhelp/v9/index.jsp?topic=%2Fcom.ibm.javaee.doc%2Ftopics%2Ftsecuringejee.html
	public Response getUsers() {

		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null)
				logger.info(" securityContext.getUserPrincipal().toString() >" + securityContext.getUserPrincipal().getName() + "<");

		}

		return Response.ok().entity(fstoreRepositoryRef.getUserValues()).build();
	}

	/**
	 * @return an example user to see how to do POSTS
	 */
	@GET
	@Path("/users/example")
	@Produces("application/json")
	public Response getUserExample() {

		if (securityContext != null) {
			if (securityContext.getUserPrincipal() != null)
				logger.info(" securityContext.getUserPrincipal().toString() >" + securityContext.getUserPrincipal().toString() + "<");

		}

		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser != null) {
			logger.info(" currentUser = " + currentUser.toString());
			logger.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");
			logger.info(" currentUser  employee  = " + currentUser.hasRole("employee"));
			logger.info(" currentUser  boss  = " + currentUser.hasRole("boss"));
		}

		if (ws != null) {
			logger.info("ws = " + ws.toString());
			if (ws.getHttpServletRequest() != null) {
				// sessionid
				logger.info("ws.getHttpServletRequest() = " + ws.getHttpServletRequest().getSession().getId());

				if (ws.getHttpServletRequest().getUserPrincipal() != null)
					logger.info(" ws.getUserPrincipal().toString(): " + ws.getHttpServletRequest().getUserPrincipal().toString());

			}
		}

		FStoreUser b = new FStoreUser();
		b.setName("Christos");
		b.setUsername("ctran");
		b.setPassword("passwd");
		b.setOrganization("UoP");
		ResponseBuilder response = Response.ok(b);

		CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		response.cacheControl(cacheControl);

		return response.build();
	}

	
	
	
	/*************** Users API *************************/
	
	
	
	
	@GET
	@Path("/users/{userid}")
	@Produces("application/json")
	public Response getUserById(@PathParam("userid") int userid) {

		FStoreUser u = fstoreRepositoryRef.getUserByID(userid);

		if (u != null) {
			return Response.ok().entity(u).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("User with id=" + userid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}

	
	
	
	

	@POST
	@Path("/users/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response addUser(FStoreUser user) {

		logger.info("Received POST for usergetUsername: " + user.getUsername());
		// logger.info("Received POST for usergetPassword: " + user.getPassword());
		// logger.info("Received POST for usergetOrganization: " + user.getOrganization());

		if ( (user.getUsername()==null)||(user.getUsername().equals("")||(user.getEmail()==null) ||(user.getEmail().equals("")) ) ){
			ResponseBuilder builder = Response.status(Status.BAD_REQUEST );
			builder.entity("New user with username=" + user.getUsername() + " cannot be registered");
			logger.info("New user with username=" + user.getUsername() + " cannot be registered BAD_REQUEST.");
			throw new WebApplicationException(builder.build());
		}
		

		FStoreUser u= fstoreRepositoryRef.getUserByUsername(user.getUsername());
		if (u!=null){
			return Response.status(Status.BAD_REQUEST ).entity("Username exists").build();
		}
		
		u= fstoreRepositoryRef.getUserByEmail(user.getEmail());
		if (u!=null){
			return Response.status(Status.BAD_REQUEST ).entity("Email exists").build();
		}
				
		u = fstoreRepositoryRef.addFStoreUserToUsers(user);

		if (u != null) {
			return Response.ok().entity(u).build();
		} else {
			ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
			builder.entity("Requested user with username=" + user.getUsername() + " cannot be installed");
			return builder.build();
		}
	}
	
	@POST
	@Path("/register/")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response addNewRegisterUser(List<Attachment> ats) {

		FStoreUser user = new FStoreUser();
		user.setName( getAttachmentStringValue("name", ats) );
		user.setUsername( getAttachmentStringValue("username", ats) );
		user.setPassword( getAttachmentStringValue("userpassword", ats) );
		user.setOrganization( getAttachmentStringValue("userorganization", ats) +
				"^^"+
				getAttachmentStringValue("randomregid", ats));
		user.setEmail( getAttachmentStringValue("useremail", ats) );
		user.setActive(false);//in any case the user should be not active
		user.setRole("ROLE_DEVELOPER"); //otherwise in post he can choose ROLE_ADMIN, and the immediately register :-)
		
		String msg= getAttachmentStringValue("emailmessage", ats);
		logger.info("Received register for usergetUsername: " + user.getUsername());
		
		Response r = addUser(user);
		
		if (r.getStatusInfo().getStatusCode() ==  Status.OK.getStatusCode() ){
			logger.info("Email message: " + msg);
			EmailUtil.SendRegistrationActivationEmail(user.getEmail() , msg);
		}
			
		return r;
	}

	
	@PUT
	@Path("/users/{userid}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateUserInfo(@PathParam("userid") int userid, FStoreUser user) {
		logger.info("Received PUT for user: " + user.getUsername());

		FStoreUser previousUser = fstoreRepositoryRef.getUserByID(userid);
		

		List<Product> previousProducts = previousUser.getProducts();

		if (user.getProducts().size() == 0) {
			user.getProducts().addAll(previousProducts);
		}

		FStoreUser u = fstoreRepositoryRef.updateUserInfo(userid, user);

		if (u != null) {
			return Response.ok().entity(u).build();
		} else {
			ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
			builder.entity("Requested user with username=" + user.getUsername() + " cannot be updated");
			throw new WebApplicationException(builder.build());
		}
	}

	@DELETE
	@Path("/users/{userid}")
	@Produces("application/json")
	public Response deleteUser(@PathParam("userid") int userid) {
		logger.info("Received DELETE for userid: " + userid);

		fstoreRepositoryRef.deleteUser(userid);

		return Response.ok().build();
	}




	
	private Product addNewProductData(Product prod, int userid, String prodName, String shortDescription, String longDescription, String version,
			String categories, Attachment image, Attachment bunFile, List<Attachment> screenshots) {
		String uuid = UUID.randomUUID().toString();
		
		
		
		logger.info("bunname = " + prodName);
		logger.info("version = " + version);
		logger.info("shortDescription = " + shortDescription);
		logger.info("longDescription = " + longDescription);
		
		prod.setUuid(uuid);
		prod.setName(prodName);
		prod.setShortDescription(shortDescription);
		prod.setLongDescription(longDescription);
		prod.setVersion(version);
		prod.setDateCreated(new Date());
		prod.setDateUpdated(new Date());
		
		String[] catIDs = categories.split(",");
		for (String catid : catIDs) {
			Category category = fstoreRepositoryRef.getCategoryByID( Integer.valueOf(catid) );		
			prod.addCategory(category);
		}

		URI endpointUrl = uri.getBaseUri();

		String tempDir = METADATADIR + uuid + File.separator;
		try {
			Files.createDirectories(Paths.get(tempDir));


			if (image!=null){
				String imageFileNamePosted = getFileName(image.getHeaders());
				logger.info("image = " + imageFileNamePosted);
				if (!imageFileNamePosted.equals("")) {
					String imgfile = saveFile(image, tempDir + imageFileNamePosted);
					logger.info("imgfile saved to = " + imgfile);
					prod.setIconsrc(endpointUrl + "repo/images/" + uuid + File.separator + imageFileNamePosted);
				}
			}
			

			if (bunFile!=null){
				String bunFileNamePosted = getFileName(bunFile.getHeaders());
				logger.info("bunFile = " + bunFileNamePosted);
				if (!bunFileNamePosted.equals("")) {
					String bunfilepath = saveFile(bunFile, tempDir + bunFileNamePosted);
					logger.info("bunfilepath saved to = " + bunfilepath);
					prod.setPackageLocation(endpointUrl + "repo/packages/" + uuid + File.separator + bunFileNamePosted);
				}
			}
			
			
			List<Attachment> ss = screenshots;
			String screenshotsFilenames="";
			int i=1;
			for (Attachment shot : ss) {
				String shotFileNamePosted = getFileName(shot.getHeaders());
				logger.info("Found screenshot image shotFileNamePosted = " + shotFileNamePosted);
				logger.info("shotFileNamePosted = " + shotFileNamePosted);
				if (!shotFileNamePosted.equals("")) {
					shotFileNamePosted = "shot"+i+"_"+shotFileNamePosted;
					String shotfilepath = saveFile(shot, tempDir + shotFileNamePosted);
					logger.info("shotfilepath saved to = " + shotfilepath);
					shotfilepath = endpointUrl + "repo/images/" + uuid + File.separator + shotFileNamePosted;
					screenshotsFilenames += shotfilepath+","; 
					i++;
				}
			}
			if (screenshotsFilenames.length()>0)
				screenshotsFilenames = screenshotsFilenames.substring(0, screenshotsFilenames.length()-1);
			
			prod.setScreenshots(screenshotsFilenames);
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Save now bun for User
		FStoreUser bunOwner = fstoreRepositoryRef.getUserByID(userid);
		bunOwner.addProduct(prod);
		fstoreRepositoryRef.updateUserInfo(userid, bunOwner);
		return prod;
	}

	private Product updateProductMetadata(Product prod, String userid, String prodname, String uuid, String shortDescription, String longDescription,
			String version, String categories, Attachment image, Attachment prodFile, List<Attachment> screenshots) {
		
		
		logger.info("userid = " + userid);
		logger.info("bunname = " + prodname);
		logger.info("bunid = " + prod.getId());
		
		logger.info("bunuuid = " + uuid);
		logger.info("version = " + version);
		logger.info("shortDescription = " + shortDescription);
		logger.info("longDescription = " + longDescription);

		// Save now bun for User
		FStoreUser bunOwner = fstoreRepositoryRef.getUserByID( Integer.parseInt(userid) );
		prod.setShortDescription(shortDescription);
		prod.setLongDescription(longDescription);
		prod.setVersion(version);
		prod.setName(prodname);
		prod.setOwner(bunOwner);
		prod.setDateUpdated(new Date());

		
		//first remove the bun from the previous category
		List<Category> cats = prod.getCategories();
		List<Category> catsToUpdate = new ArrayList<Category>();
		for (Category category : cats) {
			catsToUpdate.add(category);
		}		
		
		for (Category c : catsToUpdate) {
			c.removeProduct(prod);
			prod.removeCategory(c);
			fstoreRepositoryRef.updateCategoryInfo( c );
		}
		
		String[] catIDs = categories.split(",");
		for (String catid : catIDs) {
			//and now add the new one
			Category category = fstoreRepositoryRef.getCategoryByID(Integer.valueOf(catid));
			prod.addCategory(category);
		}


		URI endpointUrl = uri.getBaseUri();

		String tempDir = METADATADIR + uuid + File.separator;
		try {
			Files.createDirectories(Paths.get(tempDir));
			
			if (image!=null){
				String imageFileNamePosted = getFileName(image.getHeaders());
				logger.info("image = " + imageFileNamePosted);
				if (!imageFileNamePosted.equals("unknown")) {
					String imgfile = saveFile(image, tempDir + imageFileNamePosted);
					logger.info("imgfile saved to = " + imgfile);
					prod.setIconsrc(endpointUrl + "repo/images/" + uuid + File.separator + imageFileNamePosted);
				}
			}

			if (prodFile!=null){
				String bunFileNamePosted = getFileName(prodFile.getHeaders());
				logger.info("bunFile = " + bunFileNamePosted);
				if (!bunFileNamePosted.equals("unknown")) {
					String bunfilepath = saveFile(prodFile, tempDir + bunFileNamePosted);
					logger.info("bunfilepath saved to = " + bunfilepath);
					prod.setPackageLocation(endpointUrl + "repo/packages/" + uuid + File.separator + bunFileNamePosted);
				}
			}
			
			
			List<Attachment> ss = screenshots;
			String screenshotsFilenames="";
			int i=1;
			for (Attachment shot : ss) {
				String shotFileNamePosted = getFileName(shot.getHeaders());
				logger.info("Found screenshot image shotFileNamePosted = " + shotFileNamePosted);
				logger.info("shotFileNamePosted = " + shotFileNamePosted);
				if (!shotFileNamePosted.equals("")) {
					shotFileNamePosted = "shot"+i+"_"+shotFileNamePosted;
					String shotfilepath = saveFile(shot, tempDir + shotFileNamePosted);
					logger.info("shotfilepath saved to = " + shotfilepath);
					shotfilepath = endpointUrl + "repo/images/" + uuid + File.separator + shotFileNamePosted;
					screenshotsFilenames += shotfilepath+","; 
					i++;
				}
			}
			if (screenshotsFilenames.length()>0)
				screenshotsFilenames = screenshotsFilenames.substring(0, screenshotsFilenames.length()-1);
			
			prod.setScreenshots(screenshotsFilenames);
			

		} catch (IOException e) {
			
			e.printStackTrace();
		}

		fstoreRepositoryRef.updateProductInfo(prod);

		if (bunOwner.getProductById(prod.getId()) == null)
			bunOwner.addProduct(prod);
		fstoreRepositoryRef.updateUserInfo(Integer.parseInt(userid), bunOwner);
		return prod;
	}


	@GET
	@Path("/images/{uuid}/{imgfile}")
	@Produces("image/*")
	public Response getEntityImage(@PathParam("uuid") String uuid, @PathParam("imgfile") String imgfile) {
		logger.info("getEntityImage of uuid: " + uuid);
		String imgAbsfile = METADATADIR + uuid + File.separator + imgfile;
		logger.info("Image RESOURCE FILE: " + imgAbsfile);
		File file = new File(imgAbsfile);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=" + file.getName());
		return response.build();

	}

	@GET
	@Path("/packages/{uuid}/{bunfile}")
	@Produces("application/gzip")
	public Response downloadBunPackage(@PathParam("uuid") String uuid, @PathParam("bunfile") String bunfile) {

		logger.info("bunfile: " + bunfile);
		logger.info("uuid: " + uuid);

		String bunAbsfile = METADATADIR + uuid + File.separator + bunfile;
		logger.info("Bun RESOURCE FILE: " + bunAbsfile);
		File file = new File(bunAbsfile);

		if ((uuid.equals("77777777-668b-4c75-99a9-39b24ed3d8be")) || (uuid.equals("22cab8b8-668b-4c75-99a9-39b24ed3d8be"))) {
			URL res = getClass().getResource("/files/" + bunfile);
			logger.info("TEST LOCAL RESOURCE FILE: " + res);
			file = new File(res.getFile());
		}

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=" + file.getName());
		return response.build();
	}

	


	
	public FStoreRepository getfstoreRepositoryRef() {
		return fstoreRepositoryRef;
	}

	public void setfstoreRepositoryRef(FStoreRepository fstoreRepositoryRef) {
		this.fstoreRepositoryRef = fstoreRepositoryRef;
	}
	

	//Sessions related API
	
//	@OPTIONS
//	@Path("/sessions/")
//	@Produces("application/json")
//	@Consumes("application/json")
//	@LocalPreflight
//	public Response addUserSessionOption(){
//		
//
//		logger.info("Received OPTIONS  addUserSessionOption ");
//		String origin = headers.getRequestHeader("Origin").get(0);
//        if (origin != null) {
//            return Response.ok()
//                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET POST DELETE PUT HEAD OPTIONS")
//                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "true")
//                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept")
//                           .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, origin)
//                           .build();
//        } else {
//            return Response.ok().build();
//        }
//	}

	
	@POST
	@Path("/sessions/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response addUserSession(UserSession userSession) {

		logger.info("Received POST addUserSession usergetUsername: " + userSession.getUsername());
		//logger.info("DANGER, REMOVE Received POST addUserSession password: " + userSession.getPassword());
		
		if (securityContext!=null){
			if (securityContext.getUserPrincipal()!=null)
				logger.info(" securityContext.getUserPrincipal().toString() >" + securityContext.getUserPrincipal().toString()+"<");
		
		}


		Subject currentUser = SecurityUtils.getSubject();
		if (currentUser !=null){
			AuthenticationToken token =	new UsernamePasswordToken(  userSession.getUsername(), userSession.getPassword());
			try {
				currentUser.login(token);
				
				
				FStoreUser u = fstoreRepositoryRef.getUserByUsername( userSession.getUsername() );
				userSession.setUser( u );				
				userSession.setPassword("");;//so not tosend in response
				userSession.setSessionId( currentUser.getSession().getId().toString() );
				
				logger.info(" currentUser = " + currentUser.toString() );
				logger.info( "User [" + currentUser.getPrincipal() + "] logged in successfully." );
				logger.info(" currentUser  employee  = " + currentUser.hasRole("employee")  );
				logger.info(" currentUser  boss  = " + currentUser.hasRole("boss")  );
				
				
				userSession = fstoreRepositoryRef.addUserSession(userSession);
				
				return Response.ok().entity(userSession).build();
				}
				catch (AuthenticationException ae) {
					
					return Response.status(Status.UNAUTHORIZED).build();
				} 			
		}
		
		
		return Response.status(Status.UNAUTHORIZED).build();
	}

	@GET
	@Path("/sessions/logout")
	@Produces("application/json")
	public Response logoutUser() {

		logger.info("Received logoutUser " );
		
		if (securityContext!=null){
			if (securityContext.getUserPrincipal()!=null)
				logger.info(" securityContext.getUserPrincipal().toString() >" + securityContext.getUserPrincipal().toString()+"<");
		
			SecurityUtils.getSubject().logout();
		}
		
		return Response.ok().build();
	}
	
	
	
	
	//THIS IS NOT USED
	@GET
	@Path("/sessions/")
	@Produces("application/json")
	public Response getUserSessions() {

		logger.info("Received GET addUserSession usergetUsername: " );
		logger.info("Received GET addUserSession password: " );
		
		if (securityContext!=null){
			if (securityContext.getUserPrincipal()!=null)
				logger.info(" securityContext.getUserPrincipal().toString() >" + securityContext.getUserPrincipal().toString()+"<");
		
		}


		Subject currentUser = SecurityUtils.getSubject();
		if ((currentUser !=null) && (currentUser.getPrincipal() !=null)){

//				logger.info(" currentUser = " + currentUser.toString() );
//				logger.info( "User [" + currentUser.getPrincipal() + "] logged in successfully." );
//				logger.info(" currentUser  employee  = " + currentUser.hasRole("employee")  );
//				logger.info(" currentUser  boss  = " + currentUser.hasRole("boss")  );
				
				return Response.ok().build();
		}
		
		
		return Response.status(Status.UNAUTHORIZED).build();
	}	
	
	//Subscribed MAchines related API
	
	@GET
	@Path("/categories/")
	@Produces("application/json")
	public Response getCategories() {
		return Response.ok().entity(fstoreRepositoryRef.getCategories()).build();
	}

	
	
	@GET
	@Path("/categories/{catid}")
	@Produces("application/json")
	public Response getCategoryById(@PathParam("catid") int catid) {
		return getAdminCategoryById(catid);
	}

	@GET
	@Path("/admin/categories/")
	@Produces("application/json")
	public Response getAdminCategories() {
		return Response.ok().entity(fstoreRepositoryRef.getCategories()).build();
	}
	
	
	@POST
	@Path("/admin/categories/")
	@Produces("application/json")
	@Consumes("application/json")
	public Response addCategory(Category c) {
		Category u = fstoreRepositoryRef.addCategory(c);

		if (u != null) {
			return Response.ok().entity(u).build();
		} else {
			ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
			builder.entity("Requested Category with name=" + c.getName() + " cannot be installed");
			throw new WebApplicationException(builder.build());
		}
	}

	@PUT
	@Path("/admin/categories/{catid}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateCategory(@PathParam("catid")int catid, Category c) {
		Category previousCategory = fstoreRepositoryRef.getCategoryByID(catid);		

		Category u = fstoreRepositoryRef.updateCategoryInfo(c);

		if (u != null) {
			return Response.ok().entity(u).build();
		} else {
			ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
			builder.entity("Requested Category with name=" + c.getName()+" cannot be updated");
			throw new WebApplicationException(builder.build());
		}

		
	}

	@DELETE
	@Path("/admin/categories/{catid}")
	public Response deleteCategory(@PathParam("catid") int catid) {
		Category category = fstoreRepositoryRef.getCategoryByID(catid);
		if ((category.getProducts().size()>0) ){
			ResponseBuilder builder = Response.status(Status.METHOD_NOT_ALLOWED );
			builder.entity("The category has assigned elements. You cannot delete it!");
			throw new WebApplicationException(builder.build());
		}else{		
			fstoreRepositoryRef.deleteCategory(catid);
			return Response.ok().build();
		}
	}


	@GET
	@Path("/admin/categories/{catid}")
	@Produces("application/json")
	public Response getAdminCategoryById(@PathParam("catid") int catid) {
		Category sm = fstoreRepositoryRef.getCategoryByID(catid);

		if (sm != null) {
			return Response.ok().entity(sm).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("Category " + catid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}

	//Widgets related API

	@GET
	@Path("/widgets")
	@Produces("application/json")
	public Response getWidgets(@QueryParam("categoryid") Long categoryid) {
		logger.info("getWidgets categoryid="+categoryid);
		List<Widget> w = fstoreRepositoryRef.getWidgets(categoryid);
		return Response.ok().entity(w).build();
	}


	@GET
	@Path("/users/{userid}/widgets")
	@Produces("application/json")
	public Response getAllWidgetsofUser(@PathParam("userid") int userid) {
		logger.info("getAllWidgetsofUser for userid: " + userid);
		FStoreUser u = fstoreRepositoryRef.getUserByID(userid);

		if (u != null) {
			List<Product> prods = u.getProducts();
			List<Widget> widgets = new ArrayList<Widget>();
			for (Product p : prods) {
				if (p instanceof Widget)
					widgets.add(  (Widget) p );
			}

			return Response.ok().entity(widgets).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("User with id=" + userid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}


	@GET
	@Path("/widgets/{widgetid}")
	@Produces("application/json")

	public Response getWidgetByID(@PathParam("widgetid") int widgetid) {
		logger.info("getWidgetByID  widgetid=" + widgetid);
		Widget w = (Widget) fstoreRepositoryRef.getProductByID(widgetid);

		if (w != null) {
			return Response.ok().entity(w).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("widget with id=" + widgetid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}

	@GET
	@Path("/widgets/uuid/{uuid}")
	@Produces("application/json")
	public Response getWidgetUUID(@PathParam("uuid") String uuid) {
		logger.info("Received GET for Widget uuid: " + uuid);
		Widget w = null;

		URI endpointUrl = uri.getBaseUri();
		w = (Widget) fstoreRepositoryRef.getProductByUUID(uuid);

		if (w != null) {
			return Response.ok().entity(w).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("Widget with uuid=" + uuid + " not found in local registry");
			throw new WebApplicationException(builder.build());
		}

	}
	
	
	
	@GET
	@Path("/admin/widgets")
	@Produces("application/json")
	public Response getWidgetsAdmin() {		
		
		//must show only widgets owned by user Session. must find user from sessionid
		
		FStoreUser u = fstoreRepositoryRef.getUserBySessionId( SecurityUtils.getSubject().getSession().getId().toString() );
		
		if (u==null){
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("User session not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
		
		List<Widget> w ;
		
		if ( u.getRole().equals("ROLE_ADMIN") ) //an admin will see everything
			w = fstoreRepositoryRef.getWidgets(null);
		else
			w = fstoreRepositoryRef.getUserWidgets(u);
		
		return Response.ok().entity(w).build();
	}
	
	@GET
	@Path("/admin/widgets/{widgetid}")
	@Produces("application/json")

	public Response getWidgetAdminByID(@PathParam("widgetid") int widgetid) {
		logger.info("getWidgetByID  widgetid=" + widgetid);
		Widget w = (Widget) fstoreRepositoryRef.getProductByID(widgetid);

		if (w != null) {
			return Response.ok().entity(w).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("widget with id=" + widgetid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}


	@PUT
	@Path("/admin/widgets/{wid}")
	@Consumes("multipart/form-data")
	public Response updateWidget(@PathParam("wid") int wid, List<Attachment> ats){
		

		//must show allow widges owned by user Session. must find user from sessionid
		
		FStoreUser u = fstoreRepositoryRef.getUserBySessionId( SecurityUtils.getSubject().getSession().getId().toString() );
		
		
		Widget appmeta = (Widget) fstoreRepositoryRef.getProductByID(wid);
		appmeta.setURL(getAttachmentStringValue("url", ats));
		
		appmeta = (Widget) updateProductMetadata(
				appmeta, 
				getAttachmentStringValue("userid", ats),  
				getAttachmentStringValue("prodname", ats),
				getAttachmentStringValue("uuid", ats), 
				getAttachmentStringValue("shortDescription", ats), 
				getAttachmentStringValue("longDescription", ats), 
				getAttachmentStringValue("version", ats), 
				getAttachmentStringValue("categories", ats), //categories are comma separated Ids
				getAttachmentByName("prodIcon", ats), 
				getAttachmentByName("prodFile", ats), 
				getListOfAttachmentsByName("screenshots", ats));
		
		
		return Response.ok().entity(appmeta).build();
	}

	@POST
	//@Path("/users/{userid}/widgets/")
	@Path("/admin/widgets/")
	@Consumes("multipart/form-data")
	public Response addWidget(  List<Attachment> ats){
		
//		MUST STORE Sessions and the from Session ID to get userid
//		must show only widges owned by user Session. must find user from sessionid


		FStoreUser u = fstoreRepositoryRef.getUserBySessionId( SecurityUtils.getSubject().getSession().getId().toString() );
		int userid = u.getId();
		
		Widget sm = new Widget();
		sm.setURL(  getAttachmentStringValue("url", ats) );
		sm = (Widget) addNewProductData(sm, userid, 
				getAttachmentStringValue("prodname", ats), 
				getAttachmentStringValue("shortDescription", ats), 
				getAttachmentStringValue("longDescription", ats), 
				getAttachmentStringValue("version", ats), 
				getAttachmentStringValue("categories", ats), //categories are comma separated Ids
				getAttachmentByName("prodIcon", ats), 
				getAttachmentByName("prodFile", ats), 
				getListOfAttachmentsByName("screenshots", ats));

		return Response.ok().entity(sm).build();
	}
	

	@DELETE
	@Path("/admin/widgets/{widgetid}")
	public void deleteWidget(@PathParam("widgetid") int widgetid) {
		

		//must allow only widget to delete owned by user Session. must find user from sessionid
		
		fstoreRepositoryRef.deleteProduct(widgetid);
		
	}


	@DELETE
	@Path("/courses/{courseid}")
	public void deleteCourse(@PathParam("courseid") int courseid) {
		fstoreRepositoryRef.deleteProduct(courseid);
	}

	@GET
	@Path("/courses")
	@Produces("application/json")
	public Response getCourses(@QueryParam("categoryid") Long categoryid) {
		logger.info("getCourses categoryid="+categoryid);
		List<Course> courses = fstoreRepositoryRef.getCourses(categoryid);
		return Response.ok().entity(courses).build();
	}


	@GET
	@Path("/courses/{courseid}")
	@Produces("application/json")
	public Response getCoursetByID(@PathParam("courseid") int courseid) {
		logger.info("getCoursetByID  courseid=" + courseid);
		Course c = (Course) fstoreRepositoryRef.getProductByID(courseid);

		if (c != null) {
			return Response.ok().entity(c).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("Course with id=" + courseid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}


	@GET
	@Path("/courses/uuid/{uuid}")
	@Produces("application/json")
	public Response getCourseUUID(@PathParam("uuid") String uuid) {

		Course c = (Course) fstoreRepositoryRef.getProductByUUID(uuid);
		if (c != null) {
			return Response.ok().entity(c).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("Course with uuid=" + uuid + " not found in local registry");
			throw new WebApplicationException(builder.build());
		}
	}




	@PUT
	@Path("/courses/{cid}")
	@Consumes("multipart/form-data")
	public Response updateCourse(
			@PathParam("cid") int cid, 
			List<Attachment> ats){

		Course c = (Course) fstoreRepositoryRef.getProductByID(cid);
		
		c = (Course) updateProductMetadata(
				c, 
				getAttachmentStringValue("userid", ats),  
				getAttachmentStringValue("prodname", ats),
				getAttachmentStringValue("uuid", ats), 
				getAttachmentStringValue("shortDescription", ats), 
				getAttachmentStringValue("longDescription", ats), 
				getAttachmentStringValue("version", ats), 
				getAttachmentStringValue("categories", ats), //categories are comma separated Ids
				getAttachmentByName("prodIcon", ats), 
				getAttachmentByName("prodFile", ats), 
				getListOfAttachmentsByName("screenshots", ats));
		
		return Response.ok().entity(c).build();
	}

	

	@POST
	@Path("/users/{userid}/courses/")
	@Consumes("multipart/form-data")
	public Response addCourse(@PathParam("userid") int userid, List<Attachment> ats){
		Course c = new Course();
		c = (Course) addNewProductData(c, userid, 
				getAttachmentStringValue("prodname", ats), 
				getAttachmentStringValue("shortDescription", ats), 
				getAttachmentStringValue("longDescription", ats), 
				getAttachmentStringValue("version", ats), 
				getAttachmentStringValue("categories", ats), //categories are comma separated Ids
				getAttachmentByName("prodIcon", ats), 
				getAttachmentByName("prodFile", ats), 
				getListOfAttachmentsByName("screenshots", ats));
		
		return Response.ok().entity(c).build();
	}



	@GET
	@Path("/users/{userid}/courses/{courseid}")
	@Produces("application/json")
	public Response getCourseofUser(
			@PathParam("userid") int userid, 
			@PathParam("courseid") int courseid) {
		logger.info("getCourseofUser for userid: " + userid + ", courseid=" + courseid);
		FStoreUser u = fstoreRepositoryRef.getUserByID(userid);

		if (u != null) {
			Course course = (Course) u.getProductById(courseid);
			return Response.ok().entity(course).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("User with id=" + userid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}
	
	@GET
	@Path("/users/{userid}/courses")
	@Produces("application/json")
	public Response getAllCoursesofUser(@PathParam("userid") int userid) {
		logger.info("getAllCoursesofUser for userid: " + userid);
		FStoreUser u = fstoreRepositoryRef.getUserByID(userid);

		if (u != null) {
			List<Product> prods = u.getProducts();
			List<Course> courses = new ArrayList<Course>();
			for (Product p : prods) {
				if (p instanceof Course)
					courses.add(  (Course) p );
			}

			return Response.ok().entity(courses).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("User with id=" + userid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}
	

	// Attachment utils ///////////////////////
	private String saveFile(Attachment att, String filePath) {
		DataHandler handler = att.getDataHandler();
		try {
			InputStream stream = handler.getInputStream();
			MultivaluedMap map = att.getHeaders();
			File f = new File(filePath);
			OutputStream out = new FileOutputStream(f);

			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = stream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			stream.close();
			out.flush();
			out.close();
			return f.getAbsolutePath();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getFileName(MultivaluedMap<String, String> header) {
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {
				String[] name = filename.split("=");
				String exactFileName = name[1].trim().replaceAll("\"", "");
				return exactFileName;
			}
		}
		return "unknown";
	}
	
	public String getAttachmentStringValue(String name, List<Attachment> attachments){
		
		Attachment att = getAttachmentByName(name, attachments);
		if (att!=null){
			return att.getObject(String.class);
		}
		return null;
	}
	
	public Attachment getAttachmentByName(String name, List<Attachment> attachments){
		
		for (Attachment attachment : attachments) {	
			String s = getAttachmentName(attachment.getHeaders());
			if ((s!=null) && (s.equals(name)) )
					return attachment;			
		}
		
		return null;
	}
	
	private List<Attachment> getListOfAttachmentsByName(String name, List<Attachment> attachments) {
		
		List<Attachment> la = new ArrayList<Attachment>();
		for (Attachment attachment : attachments) {			
			if (getAttachmentName(attachment.getHeaders()).equals(name) )
					la.add(attachment);			
		}
		return la;
	}


	private String getAttachmentName(MultivaluedMap<String, String> header) {
		
		if (header.getFirst("Content-Disposition")!=null){
			String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
			for (String filename : contentDisposition) {
				if ((filename.trim().startsWith("name"))) {
					String[] name = filename.split("=");
					String exactFileName = name[1].trim().replaceAll("\"", "");
					return exactFileName;
				}
			}
		}
		return null;
	}

	@GET
	@Path("/fireadapters")
	@Produces("application/json")
	public Response getFIREAdapters(@QueryParam("categoryid") Long categoryid) {
		logger.info("getFIREAdapters categoryid="+categoryid);

		List<FIREAdapter> adapters = fstoreRepositoryRef.getFIREAdapters(categoryid);
		return Response.ok().entity(adapters).build();
	}


	@GET
	@Path("/fireadapters/{faid}")
	@Produces("application/json")
	public Response getFIREAdapterByID( @PathParam("faid") int faid) {
		logger.info("getFIREAdapterByID  faid=" + faid);
		FIREAdapter bun = (FIREAdapter) fstoreRepositoryRef.getProductByID(faid);

		if (bun != null) {
			return Response.ok().entity(bun).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("FIREAdapter with id=" + faid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}


	@GET
	@Path("/fireadapters/uuid/{uuid}")
	@Produces("application/json")
	public Response getFIREAdapterByUUID(@PathParam("uuid") String uuid) {
		logger.info("Received GET for FIREAdapter uuid: " + uuid);
		FIREAdapter adapter = null;
		adapter = (FIREAdapter) fstoreRepositoryRef.getProductByUUID(uuid);
		
		if (adapter != null) {
			return Response.ok().entity(adapter).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("Installed FIREAdapter with uuid=" + uuid + " not found in local registry");
			throw new WebApplicationException(builder.build());
		}
	}



	@GET
	@Path("/users/{userid}/fireadapters/{faid}")
	@Produces("application/json")
	public Response getFIREAdapterofUser(@PathParam("userid") int userid, @PathParam("faid") int faid) {
		logger.info("getFIREAdapterofUser for userid: " + userid + ", faid=" + faid);
		FStoreUser u = fstoreRepositoryRef.getUserByID(userid);

		if (u != null) {
			FIREAdapter adapter = (FIREAdapter) u.getProductById(faid);
			return Response.ok().entity(adapter).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("User with id=" + userid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}
	


	@PUT
	@Path("/fireadapters/{faid}")
	@Consumes("multipart/form-data")
	public Response updateFIREAdapter(@PathParam("faid") int faid,  List<Attachment> ats){

		

		FIREAdapter sm = (FIREAdapter) fstoreRepositoryRef.getProductByID(faid);
		sm = (FIREAdapter) updateProductMetadata(
				sm, 
				getAttachmentStringValue("userid", ats),  
				getAttachmentStringValue("prodname", ats),
				getAttachmentStringValue("uuid", ats), 
				getAttachmentStringValue("shortDescription", ats), 
				getAttachmentStringValue("longDescription", ats), 
				getAttachmentStringValue("version", ats), 
				getAttachmentStringValue("categories", ats), //categories are comma separated Ids
				getAttachmentByName("prodIcon", ats), 
				getAttachmentByName("prodFile", ats), 
				getListOfAttachmentsByName("screenshots", ats));
		
		return Response.ok().entity(sm).build();

	}

	@POST
	@Path("/users/{userid}/fireadapters/")
	@Consumes("multipart/form-data")	
	public Response addFIREAdapter( @PathParam("userid") int userid, List<Attachment> ats){
		
		FIREAdapter sm = new FIREAdapter();
		sm = (FIREAdapter) addNewProductData(sm, userid, 
				getAttachmentStringValue("prodname", ats), 
				getAttachmentStringValue("shortDescription", ats), 
				getAttachmentStringValue("longDescription", ats), 
				getAttachmentStringValue("version", ats), 
				getAttachmentStringValue("categories", ats), //categories are comma separated Ids
				getAttachmentByName("prodIcon", ats), 
				getAttachmentByName("prodFile", ats), 
				getListOfAttachmentsByName("screenshots", ats));
		
		return Response.ok().entity(sm).build();

	}

	@DELETE
	@Path("/fireadapters/{faid}")
	public void deleteFIREAdapter( @PathParam("faid") int faid) {
		fstoreRepositoryRef.deleteProduct(faid);
	}


	
	@GET
	@Path("/properties/")
	@Produces("application/json")
	public Response getProperties() {
		return Response.ok().entity(fstoreRepositoryRef.getProperties()).build();
	}
	
	@PUT
	@Path("/properties/{propid}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateProperty(@PathParam("catid")int propid, FStoreProperty p) {
		FStoreProperty previousProperty = fstoreRepositoryRef.getPropertyByID(propid);		

		FStoreProperty u = fstoreRepositoryRef.updateProperty(p);

		if (u != null) {
			return Response.ok().entity(u).build();
		} else {
			ResponseBuilder builder = Response.status(Status.INTERNAL_SERVER_ERROR);
			builder.entity("Requested fstoreProperty with name=" + p.getName()+" cannot be updated");
			throw new WebApplicationException(builder.build());
		}

		
	}
	
	@GET
	@Path("/properties/{propid}")
	@Produces("application/json")
	public Response getPropertyById(@PathParam("propid") int propid) {
		FStoreProperty sm = fstoreRepositoryRef.getPropertyByID(propid);

		if (sm != null) {
			return Response.ok().entity(sm).build();
		} else {
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity("fstoreProperty " + propid + " not found in fstore registry");
			throw new WebApplicationException(builder.build());
		}
	}


}
