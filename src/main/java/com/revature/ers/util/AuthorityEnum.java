package com.revature.ers.util;

public enum AuthorityEnum {

	EMPLOYEE("EMPLOYEE"),
	MANAGER("MANAGER");

    private final String name;

    /**
     * @param name
     */
    private AuthorityEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
