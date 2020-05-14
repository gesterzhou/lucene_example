package examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class LocalCustomer extends Customer {
    private String childName;
    private Collection<String> childPhoneNumbers;

    public LocalCustomer() {
    }

    public LocalCustomer(int idx) {
        super(idx);
        this.childName = "child" + name;
        this.childPhoneNumbers = new ArrayList<String>();
        this.childPhoneNumbers.add("703533" + (1000 + idx));
        this.childPhoneNumbers.add("703534" + (1000 + idx));
    }

    @Override
    public String toString() {
        return super.toString() + ",childName=" + childName + ",childPhoneNumbers=" + childPhoneNumbers;
    }
}
