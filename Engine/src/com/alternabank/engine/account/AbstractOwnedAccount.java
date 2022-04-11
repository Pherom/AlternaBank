package com.alternabank.engine.account;

import java.util.Objects;

public abstract class AbstractOwnedAccount extends AbstractAccount implements OwnedAccount{

    private final Owner owner;

    protected AbstractOwnedAccount(String id, String ownerName) {
        super(id);
        this.owner = new Owner(ownerName);
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return String.format("ACCOUNT DETAILS:" + System.lineSeparator()
                        + "\tID: %s" + System.lineSeparator()
                        + "\tOwner: %s" + System.lineSeparator()
                        + "\tBalance: %.2f" + System.lineSeparator()
                        + "\t%s",
                getID(), owner.getName(), getBalance(), getLedger().toString().replace(System.lineSeparator(), System.lineSeparator() + "\t"));
    }


    public class Owner implements OwnedAccount.Owner {

        private final String name;

        protected Owner(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public OwnedAccount getAccount() {
            return AbstractOwnedAccount.this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Owner owner = (Owner) o;
            return Objects.equals(name, owner.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

}
