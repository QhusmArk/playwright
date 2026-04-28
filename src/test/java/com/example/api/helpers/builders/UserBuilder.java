package com.example.api.helpers.builders;

import com.example.api.models.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserBuilder implements BuilderInterface<User> {

    private User user;
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobilePhone;
    private String language;
    private String customerCompany;
    private int customerCompanyId;
    private Boolean allowSupportLogin;
//    private Boolean infraNetAccess;
    private Boolean isActive;
    private int roleId;
    private String userRole;
    private Integer[] projectIds;

    public UserBuilder withId(final int id) {
        this.id = id;
        return this;
    }

    public UserBuilder withFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder withLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withEmail(final String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withMobilePhone(final String mobilePhone) {
        this.mobilePhone = mobilePhone;
        return this;
    }

    public UserBuilder withLanguage(final String language) {
        this.language = language;
        return this;
    }

    public UserBuilder withCustomerCompany(final String customerCompany) {
        this.customerCompany = customerCompany;
        return this;
    }

    public UserBuilder withCustomerCompanyId(final int customerCompanyId) {
        this.customerCompanyId = customerCompanyId;
        return this;
    }

    // not used? Infra net access is controlled by isActive.
//    public UserBuilder withInfraNetAccess(final boolean infraNetAccess) {
//        this.infraNetAccess = infraNetAccess;
//        return this;
//    }

    public UserBuilder withSupportLogin(final boolean allowSupportLogin) {
        this.allowSupportLogin = allowSupportLogin;
        return this;
    }

    public UserBuilder withIsActive(final boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public UserBuilder withRoleId(final int roleId) {
        this.roleId = roleId;
        return this;
    }

    public UserBuilder withUserRole(final String userRole) {
        this.userRole = userRole;
        return this;
    }

    public UserBuilder withProjectIds(final Integer[] projectIds) {
        this.projectIds = projectIds;
        return this;
    }

    public void addProjectToUser(final int projectId) {
        Integer[] projectIds;

        if (projectId == -1) { projectIds = null; }
        else {
            List<Integer> projects = new ArrayList<>();
            if (user.getProjectIds() != null) {
                projects.addAll(Arrays.asList(user.getProjectIds()));
            }
            projects.add(projectId);
            projectIds = projects.toArray(new Integer[projects.size()]);
        }

        user.setProjectIds(projectIds);
    }

    public void removeProjectFromUser(final int projectId) {
        if (user.getProjectIds() != null) {
            Integer[] projectIds =
                    Arrays.stream(user.getProjectIds())
                            .filter(m -> m != projectId)
                            .collect(Collectors.toList()).toArray(Integer[]::new);

            user.setProjectIds(projectIds);
        }
    }

    @Override
    public User getProvider() {
        return user;
    }

    @Override
    public void setProvider(User provider) {
        this.user = provider;
    }

    @Override
    public void build() {
        if(user == null) {
            user = new User();
        }
        if(id != 0) {
            user.setId(id);
        }
        if(firstName != null) {
            user.setFirstName(firstName);
        }
        if(lastName != null) {
            user.setLastName(lastName);
        }
        if(email != null) {
            user.setEmail(email);
        }
        if(mobilePhone != null) {
            user.setMobilePhone(mobilePhone);
        }
        if(language != null) {
            user.setLanguage(language);
        }
        if(customerCompany != null) {
            user.setCustomerCompany(customerCompany);
        }
        if(customerCompanyId != 0) {
            user.setCustomerCompanyId(customerCompanyId);
        }
        if (allowSupportLogin != null) {
            user.setAllowSupportLogin(allowSupportLogin);
        }

//        if (user.getAllowSupportLogin()) {
//            user.setAllowSupportLogin(true);
//        } else {
//            user.setAllowSupportLogin(allowSupportLogin);
//        }

//        if (user.getInfraNetAccess()) {
//            user.setInfraNetAccess(true);
//        } else {
//            user.setInfraNetAccess(infraNetAccess);
//        }
        if (isActive != null) {
            user.setIsActive(isActive);

        }
//        if (user.getIsActive()) {
//            user.setIsActive(true);
//        } else {
//            user.setIsActive(isActive);
//        }

        if(projectIds != null) {
            user.setProjectIds(projectIds);
        }

        if(roleId != 0) {
            user.setRoleId(roleId);
        }
        if(userRole != null) {
            user.setUserRole(userRole);
        }
    }

    /**
     * NB. Filters are to be registred at the provider class by @JsonFilter("userFilter")
     */
    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //Filter are to prevent serialization of provider-object-attribute that's NOT to be sent to API.
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(true);
        simpleFilterProvider.addFilter("userFilter",
                SimpleBeanPropertyFilter.serializeAllExcept("id", "customer_company_id"));
        try {
            return mapper.writer(simpleFilterProvider).withDefaultPrettyPrinter().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
