package com.cometchat.chat.core;

import androidx.annotation.StringDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * The AppSettings class helps the developers to provide the required details and set various parameters based on which the
 * SDK is supposed to function.
 */

public class AppSettings implements Serializable {


    public static final String SUBSCRIPTION_TYPE_NONE = "NONE";
    public static final String SUBSCRIPTION_TYPE_ALL_USERS = "ALL_USERS";
    public static final String SUBSCRIPTION_TYPE_ROLES = "ROLES";
    public static final String SUBSCRIPTION_TYPE_FRIENDS = "FRIENDS";

    @StringDef({SUBSCRIPTION_TYPE_NONE, SUBSCRIPTION_TYPE_ALL_USERS, SUBSCRIPTION_TYPE_ROLES, SUBSCRIPTION_TYPE_FRIENDS})
    @Retention(RetentionPolicy.SOURCE)

    public @interface SubscriptionType {

    }

    private AppSettings(AppSettingsBuilder appSettingsBuilder) {
        this.subscriptionType = appSettingsBuilder.subscriptionType;
        this.roles = appSettingsBuilder.roles;
        this.region = appSettingsBuilder.region;
        this.adminHost = appSettingsBuilder.adminHost;
        this.clientHost = appSettingsBuilder.clientHost;
        this.isAutoJoinEnabled = appSettingsBuilder.isAutoJoinEnabled;
        this.autoSocketConnectionEnabled = appSettingsBuilder.autoEstablishSocketConnection;
    }

    @SubscriptionType
    private String subscriptionType = SUBSCRIPTION_TYPE_NONE;
    private List<String> roles = null;
    private String region = null;
    private String adminHost = null;
    private String clientHost = null;
    private boolean isAutoJoinEnabled = true;
    private boolean autoSocketConnectionEnabled = true;


    public String getSubscriptionType() {
        return subscriptionType;
    }


    /**
     * method to get the region specified
     *
     * @return AppSettingsBuilder object when <code>build()</code> is called
     */
    public String getRegion() {
        return region;
    }

    /**
     * method to get the specified base url for admin
     *
     * @return AppSettingsBuilder object when <code>build()</code> is called
     */
    public String getAdminHost() {
        return adminHost;
    }

    /**
     * method to get the specified base url for client
     *
     * @return AppSettingsBuilder object when <code>build()</code> is called
     */
    public String getClientHost() {
        return clientHost;
    }


    /**
     * method to get the list of roles specified for presence subscription
     *
     * @return AppSettingsBuilder object when <code><build()code/> is called
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * method to get if aut join groups settings is turned on
     *
     * @return AppSettingsBuilder object when <code><build()code/> is called
     * @deprecated This method is deprecated and will be removed in the next release.
     */
    @Deprecated
    public boolean isAutoJoinEnabled() {
        return isAutoJoinEnabled;
    }

    /**
     * method to get if the web socket connection is established automatically when init is called
     *
     * @return AppSettingsBuilder object when <code><build()code/> is called
     * @deprecated This method is deprecated and will be removed in the next release.
     * Use {@link #isAutoSocketConnectionEnabled()} instead.
     */
    @Deprecated
    public boolean shouldAutoEstablishSocketConnection() {
        return autoSocketConnectionEnabled;
    }

    /**
     * method to get if the web socket connection is established automatically when init is called
     *
     * @return AppSettingsBuilder object when <code><build()code/> is called
     */
    public boolean isAutoSocketConnectionEnabled() {
        return autoSocketConnectionEnabled;
    }

    // Builder class
    public static class AppSettingsBuilder {
        private String subscriptionType = SUBSCRIPTION_TYPE_NONE;
        private List<String> roles = null;
        private String region = null;
        private String adminHost = null;
        private String clientHost = null;
        private boolean isAutoJoinEnabled = true;
        private boolean autoEstablishSocketConnection = true;

        /**
         * method to set the presence subscription to all users
         *
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder subscribePresenceForAllUsers() {
            this.subscriptionType = SUBSCRIPTION_TYPE_ALL_USERS;
            return this;
        }

        /**
         * method to set the presence subscription to all users belonging to tge specified roles
         *
         * @param roles List of roles for which the presence is to be subscribed
         * @return AppSettingsBuilder object when <code><build()code/> is called
         * @deprecated created a new method with the correct name due to the typo in this one
         */
        @Deprecated
        public AppSettingsBuilder subcribePresenceForRoles(List<String> roles) {
            this.subscriptionType = SUBSCRIPTION_TYPE_ROLES;
            this.roles = roles;
            return this;
        }

        /**
         * method to set the presence subscription to all users belonging to tge specified roles
         *
         * @param roles List of roles for which the presence is to be subscribed
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder subscribePresenceForRoles(List<String> roles) {
            this.subscriptionType = SUBSCRIPTION_TYPE_ROLES;
            this.roles = roles;
            return this;
        }

        /**
         * method to set the presence subscription to friends
         *
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder subscribePresenceForFriends() {
            this.subscriptionType = SUBSCRIPTION_TYPE_FRIENDS;
            return this;
        }

        /**
         * method to set region your app is hosted in.
         *
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder setRegion(String region) {
            this.region = region;
            return this;
        }

        /**
         * method to determine if the Web Socket connection should be established automatically when the init
         * method is called if the user has previously logged in.
         *
         * @param autoEstablishSocketConnection - boolean that determines if the web socket connection should be established automatically.
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder autoEstablishSocketConnection(boolean autoEstablishSocketConnection) {
            this.autoEstablishSocketConnection = autoEstablishSocketConnection;
            return this;
        }

        /**
         * method to set base url for admin.
         *
         * @param adminHost - String that determines base url for admin.
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder overrideAdminHost(String adminHost) {
            this.adminHost = adminHost;
            return this;
        }

        /**
         * method to set base base url for client.
         *
         * @param clientHost - String that determines base url for client.
         * @return AppSettingsBuilder object when <code><build()code/> is called
         */
        public AppSettingsBuilder overrideClientHost(String clientHost) {
            this.clientHost = clientHost;
            return this;
        }


        public AppSettings build() {
            return new AppSettings(this);
        }
    }
}
