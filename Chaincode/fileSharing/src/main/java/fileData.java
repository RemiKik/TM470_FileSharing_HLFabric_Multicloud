import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class fileData {

    @Property()
    private final String name;

    @Property()
    private final String format;

    @Property()
    private final String size;

    @Property()
    private final String hash;

    @Property()
    private final String author;

    @Property()
    private final String creation_date;

    @Property()
    private final String encryption_key;

    @Property()
    private final String buckets;

    @Property()
    private final String access_level;

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public String getSize() {
        return size;
    }

    public String getHash() {
        return hash;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getEncryption_key() {
        return encryption_key;
    }

    public String getBuckets() {
        return buckets;
    }

    public String getAccess_level() {
        return access_level;
    }

    public fileData(@JsonProperty("name") final String name, @JsonProperty("format") final String format,
                      @JsonProperty("size") final String size, @JsonProperty("hash") final String hash,
                    @JsonProperty("author") final String author, @JsonProperty("creation_date") final String creation_date,
                    @JsonProperty("encryption_key") final String encryption_key, @JsonProperty("buckets") final String buckets,
                    @JsonProperty("access_level") final String access_level) {

        this.name = name;
        this.format = format;
        this.size = size;
        this.hash = hash;
        this.author = author;
        this.creation_date = creation_date;
        this.encryption_key = encryption_key;
        this.buckets = buckets;
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

        fileData other = (fileData) obj;

        return Objects.deepEquals(new String[] {getName(), getFormat(), getSize(),getHash(),getAuthor(),getCreation_date(),getEncryption_key(),getBuckets(), getAccess_level()},
                new String[] {other.getName(), other.getFormat(), other.getSize(),other.getHash(),other.getAuthor(),other.getCreation_date(),other.getEncryption_key(),other.getBuckets(), other.getAccess_level()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getFormat(), getSize(),getHash(),getAuthor(),getCreation_date(),getEncryption_key(),getBuckets(), getAccess_level());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [name=" + name + ", format=" + format + ", size=" + size + ", hash=" + hash + ", author=" + author + ", creation_date=" + creation_date +", encryption_key="
                + encryption_key + ", buckets=" + buckets +  ", access_level=" + access_level + "]";
    }
}