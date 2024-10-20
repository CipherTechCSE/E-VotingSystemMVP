package org.ciphertech.vote_authority_service.election;

public class Election {
    private Long id;
    private String name;
    // Add other fields, constructors, getters, and setters as needed

    public Election() {
    }

    public Election(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString, equals, and hashCode if necessary
}
