package com.icodici.universa.contract;

import com.icodici.crypto.EncryptionError;
import com.icodici.crypto.PrivateKey;
import com.icodici.universa.ErrorRecord;
import com.icodici.universa.contract.services.ImmutableEnvironment;
import com.icodici.universa.contract.services.MutableEnvironment;
import com.icodici.universa.contract.services.SlotContract;
import com.icodici.universa.node2.Config;
import com.icodici.universa.node2.Quantiser;
import net.sergeych.biserializer.BiType;
import net.sergeych.biserializer.DefaultBiMapper;
import net.sergeych.tools.Binder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static com.icodici.universa.Errors.BAD_VALUE;
import static com.icodici.universa.Errors.FAILED_CHECK;

@BiType(name = "UniversaSmartContract")
public class SmartContract extends Contract implements NodeContract {


    /**
     * Extract contract from v2 or v3 sealed form, getting revokein and new items from the transaction pack supplied. If
     * the transaction pack fails to resove a link, no error will be reported - not sure it's a good idea. If need, the
     * exception could be generated with the transaction pack.
     * <p>
     * It is recommended to call {@link #check()} after construction to see the errors.
     *
     * @param sealed binary sealed contract.
     * @param pack   the transaction pack to resolve dependeincise agains.
     *
     * @throws IOException on the various format errors
     */
    public SmartContract(byte[] sealed, @NonNull TransactionPack pack) throws IOException {
        super(sealed, pack);
    }

    public SmartContract() {
        super();
    }

    /**
     * Create a default empty new contract using a provided key as issuer and owner and sealer. Default expiration is
     * set to 5 years.
     * <p>
     * This constructor adds key as sealing signature so it is ready to {@link #seal()} just after construction, thought
     * it is necessary to put real data to it first. It is allowed to change owner, expiration and data fields after
     * creation (but before sealing).
     *
     * @param key is {@link PrivateKey} for creating roles "issuer", "owner", "creator" and sign contract
     */
    public SmartContract(PrivateKey key) {
        super(key);
        getDefinition().setExtendedType(SmartContract.SmartContractType.DEFAULT_SMART_CONTRACT.name());
    }

    @Override
    public @NonNull String getExtendedType() {
        try {
            return get("definition.extended_type");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean beforeCreate(ImmutableEnvironment c) {
        return true;
    }

    @Override
    public boolean beforeUpdate(ImmutableEnvironment c) {
        return true;
    }

    @Override
    public boolean beforeRevoke(ImmutableEnvironment c) {
        return true;
    }

    @Override
    public @Nullable Binder onCreated(MutableEnvironment c) {
        return Binder.fromKeysValues("status", "ok");
    }

    @Override
    public Binder onUpdated(MutableEnvironment c) {
        return Binder.fromKeysValues("status", "ok");
    }

    @Override
    public void onRevoked(ImmutableEnvironment c) {

    }

    @Override
    public @NonNull Binder query(ImmutableEnvironment e, String methodName, Binder params) {
        return null;
    }

    @Override
    public void addError(ErrorRecord r) {
        super.addError(r);
    }

    @Override
    public Binder toBinder() {
        return super.toBinder();
    }

    @Override
    public boolean check() throws Quantiser.QuantiserException {
        boolean checkResult = false;

        // check that type of smart contract is set and exist
        String extendedTypeString = getExtendedType();
        if(extendedTypeString != null) {

            SmartContractType scType = null;
            try {
                scType = SmartContractType.valueOf(extendedTypeString);
                if(scType != null) {
                    checkResult = true;
                }

            } catch (IllegalArgumentException e) {
                addError(BAD_VALUE, "definition.extended_type", "illegal value, should be string from SmartContractType enum");
                checkResult = false;
            }
        } else {
            addError(BAD_VALUE, "definition.extended_type", "value not defined, should be string from SmartContractType enum");
            checkResult = false;
        }

        if(!checkResult)
            return checkResult;

        checkResult = super.check();
        if(!checkResult) {
            return checkResult;
        }

        return checkResult;
    }

    /**
     * Method calls from {@link SmartContract#fromDslFile(String)} and initialize contract from given binder.
     * @param root id binder with initialized data
     * @return created and ready {@link SmartContract} contract.
     * @throws EncryptionError if something went wrong
     */
    protected SmartContract initializeWithDsl(Binder root) throws EncryptionError {
        super.initializeWithDsl(root);
        return this;
    }

    /**
     * Method creates {@link SmartContract} contract from dsl file where contract is described.
     * @param fileName is path to dsl file with yaml structure of data for contract.
     * @return created and ready {@link SmartContract} contract.
     * @throws IOException if something went wrong
     */
    public static SmartContract fromDslFile(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        try (FileReader r = new FileReader(fileName)) {
            Binder binder = Binder.from(DefaultBiMapper.deserialize((Map) yaml.load(r)));
            return new SmartContract().initializeWithDsl(binder);
        }
    }


    public enum SmartContractType {
        DEFAULT_SMART_CONTRACT,
        N_SMART_CONTRACT,
        SLOT1
    }

    static {
        Config.forceInit(SmartContract.class);
        DefaultBiMapper.registerClass(SmartContract.class);
    }
}
