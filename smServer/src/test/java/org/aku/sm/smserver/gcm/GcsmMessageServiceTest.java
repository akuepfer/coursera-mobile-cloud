package org.aku.sm.smserver.gcm;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GcsmMessageServiceTest {

	@Autowired
	GoogleCloudMessagingService cms;

	@Test
	public void testSendMessage() throws KeyManagementException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException {
// 11-30 15:02:42.004    2358-2375/org.aku.sm D/Retrofit﹕ ---> HTTP GET https://aba.home:8443/login/aflower/

		Message message = new Message();
		
		String armin = "APA91bGMKDXOH8lonZiwOJCtoDc7VK9D8Cg_lnQeR_CSCreVKWdt8UC2fux7eY7HHkxRLnztTDCJZ_2-QDMAXr5INAMA4TW8LUBK6k7R3ySVK3cLFZ5zCVIs3V5rEca_op1oxrWvx8Xo6ENZqud0RvTVEc_fdMzazQ";
		String aflower = "APA91bETRh8expBtEQx185iVRLPpwRV6RYUg0IVlgReBynYzcCaWURqsnq0SDdoy6mq9y643sMWaHeGeOi2rS5w8Ph4db9xKByX05d2agejVbhWl0RuM91FB9j9L_SrqmCS3faC5GmElI0b8CCacCnmKs8ksPJ-kew";
		
		message.addRegistration(aflower);
		message.addData("patientId", "4");
		message.addData("firstName", "Chris");
		message.addData("lastName", "Black");
		message.addData("medicalRecordNumber", "11-22-33");

		GoogleCloudMessagingService cms = new GoogleCloudMessagingService();
		Response response = cms.sendNotification(message);
		Assert.assertEquals(1, response.getSuccess());
	}

}
