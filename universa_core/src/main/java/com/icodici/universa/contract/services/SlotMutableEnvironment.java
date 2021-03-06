package com.icodici.universa.contract.services;

import com.icodici.universa.HashId;
import net.sergeych.tools.Binder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

/**
 * Implements {@link MutableEnvironment} interface for slot contract.
 */
public class SlotMutableEnvironment extends SlotImmutableEnvironment implements MutableEnvironment {

    /**
     * Restore SlotMutableEnvironment
     * @param contract slot contract this environment belongs to
     */
    public SlotMutableEnvironment(SlotContract contract) {
        super(contract);
    }


    /**
     * Restore SlotMutableEnvironment
     * @param contract slot contract this environment belongs to
     * @param kvBinder map stored in the ledger
     */
    public SlotMutableEnvironment(SlotContract contract, Binder kvBinder) {
        super(contract, kvBinder);
    }


    /**
     * Restore SlotMutableEnvironment
     * @param contract slot contract this environment belongs to
     * @param kvBinder map stored in the ledger
     * @param storageSubscriptionsSet subscriptions for this environment
     */
    public SlotMutableEnvironment(SlotContract contract, Binder kvBinder, Set<ContractStorageSubscription> storageSubscriptionsSet) {
        super(contract, kvBinder, storageSubscriptionsSet);
    }

    @Override
    public <T extends Object> T set(String key, T value) {
        return (T) put(key, value);
    }

    @Override
    public void rollback() {

    }

    @Override
    public @Nullable ContractStorageSubscription createStorageSubscription(@NonNull HashId contractId, @NonNull ZonedDateTime expiresAt) {
        SlotContractStorageSubscription css = new SlotContractStorageSubscription(contract.getTrackingContract());
        css.setExpiresAt(expiresAt);
        storageSubscriptionsSet.add(css);
        return css;
    }

    @Override
    public @NonNull ContractStorageSubscription createStorageSubscription(byte[] packedTransaction, @NonNull ZonedDateTime expiresAt) {
        ContractStorageSubscription css = new SlotContractStorageSubscription(contract.getTrackingContract());
        css.setExpiresAt(expiresAt);
        storageSubscriptionsSet.add(css);
        return css;
    }
}
