import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

/**
 * File Data QueryResult structure used for handling result of query
 *
 */
@DataType()
public final class fileDataQueryResult {
    @Property()
    private final String key;

    @Property()
    private final fileData record;

    public fileDataQueryResult(@JsonProperty("Key") final String key, @JsonProperty("Record") final fileData record) {
        this.key = key;
        this.record = record;
    }

    public String getKey() {
        return key;
    }

    public fileData getRecord() {
        return record;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        fileDataQueryResult other = (fileDataQueryResult) obj;

        Boolean recordsAreEquals = this.getRecord().equals(other.getRecord());
        Boolean keysAreEquals = this.getKey().equals(other.getKey());

        return recordsAreEquals && keysAreEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey(), this.getRecord());
    }

    @Override
    public String toString() {
        return "{\"Key\":\"" + key + "\"" + "\"Record\":{\"" + record + "}\"}";
    }

}