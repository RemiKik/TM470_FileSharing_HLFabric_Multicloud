import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ClientIdentity;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "CredentialCC",
        info = @Info(
                title = "Cloud Credenial Chaincode",
                description = "TM470 Project ",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "remi@example.com",
                        name = "remi",
                        url = "https://hyperledger.example.com")))
@Default
public final class CredentialsCC implements ContractInterface {

    private final Genson genson = new Genson();

    private enum CredentialErrors {
        NOT_FOUND,
        ALREADY_EXISTS,
        ACCESS_DENIED
    }

    @Transaction()
    public Credential queryCredential(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String ccState = stub.getPrivateDataUTF8("collectionCredentials", key);
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));
        if (ccState.isEmpty()) {
            String errorMessage = String.format("Credential %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CredentialErrors.NOT_FOUND.toString());
        }
        Credential cc = genson.deserialize(ccState, Credential.class);

        if (cc.getAccess_level().equals("1")){
            return cc;
        }
        if (cc.getAccess_level().equals("2") && access >= 2){
            return cc;
        }
        if (access >= 3){
            return cc;
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CredentialErrors.ACCESS_DENIED.toString());
        }
    }

    /**
     * Creates some initial Credentials on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] credData = {
                "{ \"provider\": \"AWS\", \"access_key\": \"abc123\", \"secret_key\": \"qweryu\", \"access_level\": \"1\" }",
                "{ \"provider\": \"AWS\", \"access_key\": \"abc123\", \"secret_key\": \"qweryu\", \"access_level\": \"2\" }",
                "{ \"provider\": \"GCS\", \"access_key\": \"abc123\", \"secret_key\": \"qweryu\", \"access_level\": \"1\" }",
                "{ \"provider\": \"GCS\", \"access_key\": \"abc123\", \"secret_key\": \"qweryu\", \"access_level\": \"2\" }"
        };

        for (int i = 0; i < credData.length; i++) {
            String key = String.format("CC%d", i);

            Credential cred = genson.deserialize(credData[i], Credential.class);
            String ccState = genson.serialize(cred);
            stub.putPrivateData("collectionCredentials",key, ccState);
        }
    }

    /**
     * Creates a new credential on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key for the new credential
     * @param provider the provider of the new credential
     * @param access_key the access_key of the new credential
     * @param secret_key the secret_key of the new credential
     * @param access_level the access_level of the new credential
     * @return the created credential
     */
    @Transaction()
    public Credential createCredential(final Context ctx, final String key, final String provider, final String access_key,
                           final String secret_key, final String access_level) {
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity cid = ctx.getClientIdentity();
        String access = cid.getAttributeValue("alevel");
        String credState = stub.getPrivateDataUTF8("collectionCredentials", key);
        if (!credState.isEmpty()) {
            String errorMessage = String.format("Credential %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CredentialErrors.ALREADY_EXISTS.toString());
        }
        Credential cred = null;
        if (access.equals("3")){
            cred = new Credential(provider, access_key, secret_key, access_level);
            credState = genson.serialize(cred);
            stub.putPrivateData("collectionCredentials",key, credState);
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CredentialErrors.ACCESS_DENIED.toString());
        }
        return cred;
    }

    /**
     * Updates of a credential on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @param provider updated cloud provider of the new credential
     * @param access_key updated access key of the new credential
     * @param secret_key updated secret key of the new credential
     * @param access_level updated access level of the new credential
     * @return the updated credential
     */
    @Transaction()
    public Credential updateCredential(final Context ctx, final String key, final String provider, final String access_key,
                           final String secret_key, final String access_level) {
        ChaincodeStub stub = ctx.getStub();
        String credState = stub.getPrivateDataUTF8("collectionCredentials", key);
        ClientIdentity cid = ctx.getClientIdentity();
        String access = cid.getAttributeValue("alevel");
        if (credState.isEmpty()) {
            String errorMessage = String.format("Credential %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CredentialErrors.NOT_FOUND.toString());
        }
        Credential cred = null;

        if (access.equals("3")){
            cred = new Credential(provider, access_key, secret_key, access_level);
            credState = genson.serialize(cred);
            stub.putPrivateData("collectionCredentials",key, credState);
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, CredentialErrors.ACCESS_DENIED.toString());
        }
        return cred;
    }

    @Transaction()
    public CredentialQueryResult[] queryAllCredentials(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));

        final String startKey = "CC00";
        final String endKey = "CC99";

        List<CredentialQueryResult> queryResults = new ArrayList<CredentialQueryResult>();
        QueryResultsIterator<KeyValue> results = stub.getPrivateDataByRange("collectionCredentials",startKey, endKey);

        for (KeyValue result: results) {
            Credential cc = genson.deserialize(result.getStringValue(), Credential.class);
            if (Integer.valueOf(cc.getAccess_level()) <= access){
                queryResults.add(new CredentialQueryResult(result.getKey(),cc));
            }
        }
        CredentialQueryResult[] response = queryResults.toArray(new CredentialQueryResult[queryResults.size()]);
        return response;
    }

}


