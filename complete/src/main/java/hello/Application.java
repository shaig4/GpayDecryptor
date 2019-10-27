package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.StringWriter;
import java.io.PrintWriter;


import com.google.crypto.tink.apps.paymentmethodtoken.GooglePaymentsPublicKeysManager;
import com.google.crypto.tink.apps.paymentmethodtoken.PaymentMethodTokenRecipient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.RequestBody;

import org.json.*;

@SpringBootApplication
@RestController
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	@RequestMapping(value="/", method=RequestMethod.GET)
   public String Decrypt(String p) {
        try {
            byte[] decodedBytes = Base64.decodeBase64(p.getBytes());

            JSONObject obj = new JSONObject(new String(decodedBytes));
            String merchant = obj.getString("merchant");
            Boolean test = obj.getBoolean("test");
            String priv1= obj.getString("priv1");
            String priv2= obj.getString("priv2");
            String msg= obj.getJSONObject("msg").toString();

         //   System.out.printf(msg);
            return (Decrypt(merchant, test, priv1,priv2, msg));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            return "Bloody hell " + (sStackTrace);
        }
    }


    public String Decrypt(String merchant, Boolean test, String priv1, String priv2, String msg) throws Exception {
                     PaymentMethodTokenRecipient.Builder builder=  new PaymentMethodTokenRecipient.Builder()
                            .fetchSenderVerifyingKeysWith(
                                    test ?    GooglePaymentsPublicKeysManager.INSTANCE_TEST : GooglePaymentsPublicKeysManager.INSTANCE_PRODUCTION)
                            .recipientId(merchant)
                            .addRecipientPrivateKey(priv1);
            if(priv2 != null && priv2.trim().length() != 0) {
                builder  .addRecipientPrivateKey(priv2);
            }
                String decryptedMessage =builder
                            // Multiple private keys can be added to support graceful
                            // key rotations.
                            .build()
                            .unseal(msg);
            return (decryptedMessage);
    }


}
