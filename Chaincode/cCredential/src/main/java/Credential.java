import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Credential {

    @Property()
    private final String provider;

    @Property()
    private final String bucket;

    @Property()
    private final String access_key;

    @Property()
    private final String secret_key;

    @Property()
    private final String access_level;

    public String getProvider() {
        return provider;
    }

    public String getBucket() {
        return bucket;
    }

    public String getAccess_key() {
        return access_key;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public String getAccess_level() {
        return access_level;
    }

    public Credential(@JsonProperty("provider") final String provider, @JsonProperty("bucket") final String bucket, @JsonProperty("access_key") final String access_key,
               @JsonProperty("secret_key") final String secret_key, @JsonProperty("access_level") final String access_level) {
        this.provider = provider;
        this.bucket = bucket;
        this.access_key = access_key;
        this.secret_key = secret_key;
        this.access_level = access_level;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Credential other = (Credential) obj;

        return Objects.deepEquals(new String[] {getProvider(), getBucket(), getAccess_key(), getSecret_key(), getAccess_level()},
                new String[] {other.getProvider(), other.getBucket(), other.getAccess_key(), other.getSecret_key(), other.getAccess_level()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProvider(), getBucket(), getAccess_key(), getSecret_key(), getAccess_level());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [provider=" + provider + ", bucket="
                + bucket + ", access_key=" + access_key + ", secret_key=" + secret_key + ", access_level=" + access_level + "]";
    }
}