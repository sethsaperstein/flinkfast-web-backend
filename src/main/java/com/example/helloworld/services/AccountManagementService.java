package com.example.helloworld.services;

import com.example.helloworld.models.Invites;
import com.example.helloworld.models.Members;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountManagementService {

    public Invites getInvites() {
        return Invites.from(List.of("sethsaps@gmail.com", "foo@gmail.com"));
    }

    public void deleteInvite(String email) {
    }

    public void sendInvite(String email) {
    }

    public Members getMembers() {
        return Members.from(List.of(
            Members.Member.from("sethsaps@gmail.com", "Seth Saperstein"),
            Members.Member.from("foobar@gmail.com", "Foo Bar")
        ));
    }

    public void deleteMember(String email) {
    }
}

