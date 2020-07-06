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
        name = "fileContract",
        info = @Info(
                title = "Cloud File Sharing Chaincode",
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
public final class fileContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FileErrors {
        NOT_FOUND,
        ALREADY_EXISTS,
        ACCESS_DENIED
    }

    /**
     * Creates a new fileData on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key for the new fileData
     * @param name of the file
     * @param format of the file
     * @param size of the file
     * @param hash of the file
     * @param creation_date of the file
     * @param encryption_key of the file
     * @param buckets of the file
     * @param access_level the access_level of the file
     * @return the created fileData
     */
    @Transaction()
    public fileData createFileData(final Context ctx, final String key, final String name, final String format,
                                       final String size, final String hash, final String creation_date,
                                   final String encryption_key, final String buckets, final String access_level ) {
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));
        String fdState = stub.getPrivateDataUTF8("collectionFiles", key);
        String author = cid.getMSPID();
        if (!fdState.isEmpty()) {
            String errorMessage = String.format("fileData %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.ALREADY_EXISTS.toString());
        }

        fileData fd = null;
        if (access > 1 && Integer.valueOf(access_level) <= access){
            fd = new fileData(name, format, size, hash, author, creation_date, encryption_key, buckets, access_level);
            fdState = genson.serialize(fd);
            stub.putPrivateData("collectionFiles",key, fdState);
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.ACCESS_DENIED.toString());
        }
        return fd;
    }

    /**
     * Reads a fileData on the ledger.
     * @param ctx the transaction context
     * @param key the key for the new fileData
     * @return  fileData
     */
    @Transaction()
    public fileData readFileData(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String fdState = stub.getPrivateDataUTF8("collectionFiles", key);
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));
        if (fdState.isEmpty()) {
            String errorMessage = String.format("fileData %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.NOT_FOUND.toString());
        }
        fileData fd = genson.deserialize(fdState, fileData.class);
        if(access >= Integer.valueOf(fd.getAccess_level())){ return fd; }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.ACCESS_DENIED.toString());
        }
    }

    /**
     * Updates of a fileData on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @param name of the file
     * @param format of the file
     * @param size of the file
     * @param hash of the file
     * @param creation_date of the file
     * @param encryption_key of the file
     * @param buckets of the file
     * @param access_level the access_level of the file
     * @return the updated fileData
     */
    @Transaction()
    public fileData updateFileData(final Context ctx, final String key, final String name, final String format,
                                   final String size, final String hash, final String creation_date,
                                   final String encryption_key, final String buckets, final String access_level ) {
        ChaincodeStub stub = ctx.getStub();
        String fdState = stub.getPrivateDataUTF8("collectionFiles", key);
        ClientIdentity cid = ctx.getClientIdentity();
        String access = cid.getAttributeValue("alevel");
        String author = cid.getMSPID();
        if (fdState.isEmpty()) {
            String errorMessage = String.format("fileData %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.NOT_FOUND.toString());
        }
        fileData fd = genson.deserialize(fdState, fileData.class);

        if (access.equals("3") || author.equals(fd.getAuthor())){
            fd = new fileData(name, format, size, hash, author, creation_date, encryption_key, buckets, access_level);
            fdState = genson.serialize(fd);
            stub.putPrivateData("collectionFiles",key, fdState);
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.ACCESS_DENIED.toString());
        }
        return fd;
    }

    /**
     * Deletes a fileData from the ledger.
     * @param ctx the transaction context
     * @param key the key for the fileData to delete
     */
    @Transaction()
    public void deleteFileData(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String fdState = stub.getPrivateDataUTF8("collectionFiles", key);
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));
        String author = cid.getMSPID();
        if (fdState.isEmpty()) {
            String errorMessage = String.format("fileData %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.NOT_FOUND.toString());
        }
        fileData fd = genson.deserialize(fdState, fileData.class);

        if (access.equals("3") || author.equals(fd.getAuthor())){
            stub.delPrivateData("collectionFiles",key);
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.ACCESS_DENIED.toString());
        }
    }

    /**
     * Change the owner of a fileData on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @param author of the new file owner
     * @return the updated fileData
     */
    @Transaction()
    public fileData updateFileDataOwner(final Context ctx, final String key, final String author) {
        ChaincodeStub stub = ctx.getStub();
        String fdState = stub.getPrivateDataUTF8("collectionFiles", key);
        ClientIdentity cid = ctx.getClientIdentity();
        String access = cid.getAttributeValue("alevel");
        String owner = cid.getMSPID();
        if (fdState.isEmpty()) {
            String errorMessage = String.format("fileData %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.NOT_FOUND.toString());
        }
        fileData fd = genson.deserialize(fdState, fileData.class);
        fileData fdUpdated = null;
        if (access.equals("3") || owner.equals(fd.getAuthor())){
            fdUpdated = new fileData(fd.getName(), fd.getFormat(), fd.getSize(), fd.getHash(), author, fd.getCreation_date(), fd.getEncryption_key(), fd.getBuckets(), fd.getAccess_level());
            fdState = genson.serialize(fdUpdated);
            stub.putPrivateData("collectionFiles",key, fdState);
        }
        else {
            String errorMessage = String.format("Access denied", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.ACCESS_DENIED.toString());
        }
        return fdUpdated;
    }




    @Transaction()
    public Boolean ProofOfExistence(final Context ctx, final String hash) {
        String queryString = String.format("{\"selector\":{\"hash\":\"%s\"}}", hash);
        fileDataQueryResult[] queryResults = queryResultForQueryString(ctx, queryString);
        if (queryResults.toString().equals("[]")){
            String errorMessage = String.format("fileData for given hash: %s, does not exist", hash);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FileErrors.NOT_FOUND.toString());
        }
        else { return true; }
    }



    @Transaction()
    public fileDataQueryResult[] queryByAuthor(final Context ctx, final String author) {
        String queryString = String.format("{\"selector\":{\"author\":\"%s\"}}", author);
        fileDataQueryResult[] queryResults = queryResultForQueryString(ctx, queryString);
        return queryResults;
    }

    @Transaction()
    public fileDataQueryResult[] queryAllByAccessLevel(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));
        String queryString = String.format("{\"selector\":{\"access_level\":{\"$lte\":\"%i\"}}}", access);
        fileDataQueryResult[] queryResults = queryResultForQueryString(ctx, queryString);
        return queryResults;
    }

    @Transaction()
    public fileDataQueryResult[] queryForMyAccessLevel(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        ClientIdentity cid = ctx.getClientIdentity();
        Integer access = Integer.valueOf(cid.getAttributeValue("alevel"));
        String queryString = String.format("{\"selector\":{\"access_level\":{\"$eq\":\"%i\"}}}", access);
        fileDataQueryResult[] queryResults = queryResultForQueryString(ctx, queryString);
        return queryResults;
    }


    @Transaction()
    public fileDataQueryResult[] queryByName(final Context ctx, final String name) {
        String queryString = String.format("{\"selector\":{\"name\":\"%s\"}}", name);
        fileDataQueryResult[] queryResults = queryResultForQueryString(ctx, queryString);
        return queryResults;
    }

    @Transaction()
    public fileDataQueryResult[] queryResultForQueryString(final Context ctx, final String queryString) {
        ChaincodeStub stub = ctx.getStub();
        List<fileDataQueryResult> queryResults = new ArrayList<fileDataQueryResult>();
        QueryResultsIterator<KeyValue> results = stub.getPrivateDataQueryResult("collectionFiles", queryString);
        for (KeyValue result: results) {
            fileData cc = genson.deserialize(result.getStringValue(), fileData.class);
            queryResults.add(new fileDataQueryResult(result.getKey(),cc));
        }
        fileDataQueryResult[] response = queryResults.toArray(new fileDataQueryResult[queryResults.size()]);
        return response;
    }
}




/*

*/
