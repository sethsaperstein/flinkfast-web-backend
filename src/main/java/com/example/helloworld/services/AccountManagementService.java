package com.example.helloworld.services;

import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.example.helloworld.clients.Auth0ApiClient;
import com.example.helloworld.models.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccountManagementService {

    @Autowired
    private Auth0ApiClient auth0ApiClient;

    public void approveMember(String uid) throws Auth0Exception {
        Map<String, Object> userMetadata = Map.of("admin_approved", true);
        User approvedUserUpdate = new User();
        approvedUserUpdate.setUserMetadata(userMetadata);
        auth0ApiClient.getClient().users().update(uid, approvedUserUpdate).execute();
    }

    public Members getMembers() throws Auth0Exception {
        UsersPage usersPage = auth0ApiClient.getClient()
            .users()
            .list(new UserFilter().withQuery("user_metadata.admin_approved:true"))
            .execute()
            .getBody();
        List<Members.Member> members = new ArrayList<>();
        for (User user: usersPage.getItems()) {
            members.add(Members.Member.from(
                user.getId(),
                user.getEmail(),
                user.getName()
            ));
        }
        return Members.from(members);
    }

    public Members getPendingMembers() throws Auth0Exception {
        UsersPage usersPage = auth0ApiClient.getClient()
            .users()
            .list(new UserFilter().withQuery("NOT user_metadata.admin_approved:true OR NOT _exists_:user_metadata.admin_approved"))
            .execute()
            .getBody();
        List<Members.Member> members = new ArrayList<>();
        for (User user: usersPage.getItems()) {
            members.add(Members.Member.from(
                user.getId(),
                user.getEmail(),
                user.getName()
            ));
        }
        return Members.from(members);
    }

    public void deleteMember(String uid) throws Auth0Exception {
        auth0ApiClient.getClient().users().delete(uid).execute();
    }
}

