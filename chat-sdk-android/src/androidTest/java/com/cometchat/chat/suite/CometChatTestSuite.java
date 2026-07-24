package com.cometchat.chat.suite;

import com.cometchat.chat.authentication.AuthenticationTest;
import com.cometchat.chat.converations.ConversationsTest;
import com.cometchat.chat.groups.addmembers.AddMembersTest;
import com.cometchat.chat.groups.changescope.ModeratorScopeChangeTest;
import com.cometchat.chat.groups.changescope.ParticipantScopeChangeTest;
import com.cometchat.chat.groups.create.CreateGroupTest;
import com.cometchat.chat.groups.create.CreateGroupTest1;
import com.cometchat.chat.groups.delete.DeleteGroupTest;
import com.cometchat.chat.groups.update.UpdateGroupTest;
import com.cometchat.chat.messaging.fetch.FetchMessageTest;
import com.cometchat.chat.messaging.mentions.MentionsTest;
import com.cometchat.chat.messaging.reactions.ReactionsTest;
import com.cometchat.chat.messaging.send.CustomMessageTest;
import com.cometchat.chat.messaging.send.TextMessageTest;
import com.cometchat.chat.users.fetch.FetchUsersTest;
import com.cometchat.chat.users.fetch.GetUserTest;
import com.cometchat.chat.users.management.CreateUserTest;
import com.cometchat.chat.users.management.UpdateCurrentDetailsTest;
import com.cometchat.chat.users.management.UpdateUserTest;
import com.cometchat.chat.groups.fetch.FetchGroupsTest;
import com.cometchat.chat.groups.join.JoinGroupTest;
import com.cometchat.chat.groups.leave.LeaveGroupTest;
import com.cometchat.chat.init.InitTest;
import com.cometchat.chat.reset.ResetTestApp;
import com.cometchat.chat.reset.ResetTestApp1;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ResetTestApp.class,
        InitTest.class,
        CreateUserTest.class,
        UpdateUserTest.class,
        AuthenticationTest.class,
        UpdateCurrentDetailsTest.class,
        TextMessageTest.class,
        CustomMessageTest.class,
        FetchMessageTest.class,
        FetchUsersTest.class,
        GetUserTest.class,
        ConversationsTest.class,
        CreateGroupTest.class,
        FetchGroupsTest.class,
        AddMembersTest.class,
        ParticipantScopeChangeTest.class,
        ModeratorScopeChangeTest.class,
        //AdminScopeChangeTest.class,
        CreateGroupTest1.class,
        JoinGroupTest.class,
        UpdateGroupTest.class,
        MentionsTest.class,
        ReactionsTest.class,
        LeaveGroupTest.class,
        DeleteGroupTest.class,
        ResetTestApp1.class
})

public class CometChatTestSuite extends TestSuite {

}


