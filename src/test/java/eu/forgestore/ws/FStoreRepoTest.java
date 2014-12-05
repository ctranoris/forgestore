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

import static org.junit.Assert.*;

import java.util.UUID;

import eu.forgestore.ws.impl.FStoreJpaController;
import eu.forgestore.ws.model.Course;
import eu.forgestore.ws.model.FStoreUser;
import eu.forgestore.ws.model.Category;
import eu.forgestore.ws.model.ProductExtensionItem;
import eu.forgestore.ws.model.Widget;
import eu.forgestore.ws.util.EncryptionUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:contextTest.xml" })
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
//@Transactional
public class FStoreRepoTest {

	@Autowired
	private  FStoreJpaController bakerJpaControllerTest;

	// private static final transient Log logger = LogFactory.getLog(BakerRepoTest.class.getName());

	@Before
	public void deletePreviousobjectsDB() {

		bakerJpaControllerTest.deleteAllProducts();
		bakerJpaControllerTest.deleteAllUsers();
		bakerJpaControllerTest.deleteAllCategories();
		

	}

	@Test
	public void testWriteReadDB() {

		bakerJpaControllerTest.getAllProductsPrinted();
		
		FStoreUser bu = new FStoreUser();
		bu.setOrganization("UoP");
		bu.setName("aname");
		bu.setUsername("ausername");
		bu.setPassword("apassword");
		bu.setEmail("e@e.com");

		bakerJpaControllerTest.saveUser(bu);
		
		Widget bmeta = new Widget();
		bmeta.setName("abun");
		String uuid = UUID.randomUUID().toString();
		bmeta.setUuid(uuid);
		bmeta.setLongDescription("longDescription");
		bmeta.setShortDescription("shortDescription");
		bmeta.setPackageLocation("packageLocation");
		bu.addProduct(bmeta);

		bakerJpaControllerTest.updateFStoreUser(bu);
		
		// change name and reSave
		bmeta = (Widget) bakerJpaControllerTest.readProductByUUID(uuid);
		bmeta.setName("NewBunName");
		bakerJpaControllerTest.updateProduct(bmeta);		

		bakerJpaControllerTest.getAllProductsPrinted();
		
		bmeta = new Widget();
		String uuid2 = UUID.randomUUID().toString();
		bmeta.setUuid(uuid2);
		bmeta.setName("abun2");
		bmeta.setLongDescription("longDescription2");
		bmeta.setShortDescription("shortDescription2");
		bmeta.setPackageLocation("packageLocation2");
		bu = bakerJpaControllerTest.readFStoreUserByUsername("ausername");
		bu.addProduct(bmeta);

		bakerJpaControllerTest.updateFStoreUser(bu);

		FStoreUser testbu = bakerJpaControllerTest.readFStoreUserByUsername("ausername");
		assertEquals("aname", testbu.getName());
		assertEquals(EncryptionUtil.hash("apassword"), testbu.getPassword());
		assertEquals("UoP", testbu.getOrganization());
		assertEquals("e@e.com", testbu.getEmail());


		bakerJpaControllerTest.getAllProductsPrinted();
		
		assertEquals(2, testbu.getProducts().size());

		Widget testbm = (Widget) bakerJpaControllerTest.readProductByUUID(uuid);
		assertEquals("NewBunName", testbm.getName());
		assertEquals(uuid, testbm.getUuid());
		assertNotNull(testbm.getOwner());
		assertEquals("ausername", testbm.getOwner().getUsername());

		bu = new FStoreUser();
		bu.setOrganization("UoP2");
		bu.setName("aname2");
		bu.setUsername("ausername2");
		bu.setPassword("apassword2");

		bakerJpaControllerTest.saveUser(bu);
		bakerJpaControllerTest.getAllUsersPrinted();
		assertEquals(2, bakerJpaControllerTest.countUsers());

	}

	
	@Test
	public void testWriteReadApplications() {
		
		Category c = new Category();
		c.setName("acat1");
		assertEquals("acat1", c.getName());
		Category c2 = new Category();
		c2.setName("acat2");
		
		FStoreUser bu = new FStoreUser();
		bu.setUsername("ausername");

		Course appmeta = new Course();
		appmeta.setName("app");
		String uuid = UUID.randomUUID().toString();
		appmeta.setUuid(uuid);
		appmeta.setLongDescription("longDescription");
		appmeta.setShortDescription("shortDescription");
		appmeta.addCategory(c);
		appmeta.addCategory(c2);
		ProductExtensionItem item = new ProductExtensionItem();
		item.setName("param1");
		item.setValue("value1");
		appmeta.addExtensionItem(item );
		ProductExtensionItem item2 = new ProductExtensionItem();
		item.setName("param2");
		item.setValue("value2");
		appmeta.addExtensionItem(item2 );
		bu.addProduct(appmeta);

		bakerJpaControllerTest.saveUser(bu);

		// change name and reSave
		appmeta.setName("NewAppName");
		bakerJpaControllerTest.updateProduct(appmeta);
		assertEquals(2, appmeta.getCategories().size() );
		assertEquals(2, appmeta.getExtensions().size() );

		Course appmeta2 = new Course();
		appmeta2.setName("app2");
		appmeta2.setLongDescription("longDescription2");
		appmeta2.setShortDescription("shortDescription2");
		appmeta2.setOwner(bu);
		appmeta2.addCategory(c);
		bu.addProduct(appmeta2);

		bakerJpaControllerTest.updateFStoreUser(bu);
		bakerJpaControllerTest.getAllUsersPrinted();

		FStoreUser testbu = bakerJpaControllerTest.readFStoreUserByUsername("ausername");
		assertEquals(2, testbu.getProducts().size());

		Course testApp = (Course) bakerJpaControllerTest.readProductByUUID(uuid);
		assertEquals("NewAppName", testApp.getName());
		assertEquals(uuid, testApp.getUuid());
		assertNotNull(testApp.getOwner());
		assertEquals("ausername", testApp.getOwner().getUsername());
		bakerJpaControllerTest.getAllCategoriesPrinted();
		assertEquals("acat1", testApp.getCategories().get(0).getName());


	}

}
