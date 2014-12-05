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

package eu.forgestore.ws.util;

import eu.forgestore.ws.impl.FStoreJpaController;
import eu.forgestore.ws.repo.FStoreRepository;
import eu.forgestore.ws.repo.FStoreRepositoryAPIImpl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailUtil {

	private static final transient Log logger = LogFactory.getLog(EmailUtil.class.getName());

	public static void SendRegistrationActivationEmail(String email, String messageBody) {

		Properties props = new Properties();

		// Session session = Session.getDefaultInstance(props, null);

		props.setProperty("mail.transport.protocol", "smtp");
		if ((FStoreRepository.getPropertyByName("mailhost").getValue()!=null)&&(!FStoreRepository.getPropertyByName("mailhost").getValue().isEmpty()))
			props.setProperty("mail.host", FStoreRepository.getPropertyByName("mailhost").getValue());
		if ((FStoreRepository.getPropertyByName("mailuser").getValue()!=null)&&(!FStoreRepository.getPropertyByName("mailuser").getValue().isEmpty()))
			props.setProperty("mail.user", FStoreRepository.getPropertyByName("mailuser").getValue());
		if ((FStoreRepository.getPropertyByName("mailpassword").getValue()!=null)&&(!FStoreRepository.getPropertyByName("mailpassword").getValue().isEmpty()))
			props.setProperty("mail.password", FStoreRepository.getPropertyByName("mailpassword").getValue());

		String adminemail = FStoreRepository.getPropertyByName("adminEmail").getValue();
		String subj = FStoreRepository.getPropertyByName("activationEmailSubject").getValue();
		logger.info("adminemail = " + adminemail);
		logger.info("subj = " + subj);

		Session mailSession = Session.getDefaultInstance(props, null);
		Transport transport;
		try {
			transport = mailSession.getTransport();

			MimeMessage msg = new MimeMessage(mailSession);
			msg.setSentDate(new Date());
			msg.setFrom(new InternetAddress( adminemail , adminemail));
			msg.setSubject(subj);
			msg.setContent(messageBody, "text/html; charset=ISO-8859-1");
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));

			transport.connect();
			transport.sendMessage(msg, msg.getRecipients(Message.RecipientType.TO));

			transport.close();

		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
