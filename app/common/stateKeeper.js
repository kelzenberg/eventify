import React from 'react';

// The stateKeeper module holds the state of the user session together with some basic user information that can be accessed anywhere in the app.

/*  The UserContext provides basic information about the current user with the following structure:
    {
        "id": "3e8a5ae4-ef25-45f4-be61-31b0c73db495",
        "email": "mail@address.de",
        "displayName": "Jane Doe"
    }
*/
export const UserContext = React.createContext(null);

// isAuthenticated returns true if there is a login token stores.
// It does not check back with the server if this is still valid.
export function isAuthenticated() {
    return localStorage.getItem("USER_TOKEN") != null;
}

// setCredentials stores a new login token
export function setCredentials(token) {
    localStorage.setItem("USER_TOKEN", token);
}

// getAuthenticationToken returns the stored login token
export function getAuthenticationToken() {
    return localStorage.getItem("USER_TOKEN");
}

// getUserInfo returns the stored user information or dummy data if none is set
export function getUserInfo() {
    try {
        let data = JSON.parse(localStorage.getItem("USER_INFO"));
        if (data == null) throw new Error("data is null");
        return data;
    } catch(e) {
        console.warn("unable to get user info:", err);
        return {
            "id": "",
            "createdAt": "2021-06-02T00:06:45.528+00:00",
            "updatedAt": "2021-06-02T00:06:45.528+00:00",
            "email": "",
            "displayName": "",
            "authRole": ""
        };
    }
}

// maybeUpdateUserInfo takes a user info structure and checks if it matches the stored information.
// If the informations match it does nothing and returns false.
// If the new user information differs from the stored one it saves the new data and returns true.
export function maybeUpdateUserInfo(newUserInfo) {
    if(localStorage.getItem("USER_INFO") === null) {
        setUserInfo(newUserInfo);
        return true;
    }
    let oldUserInfo = getUserInfo();
    for(let key in newUserInfo) {
        if(newUserInfo[key] !== oldUserInfo[key]) {
            setUserInfo(newUserInfo);
            return true;
        }
    }
    return false;
}

// setUserInfo stores the new user information
function setUserInfo(userInfo) {
    localStorage.setItem("USER_INFO", JSON.stringify(userInfo));
}

// clearLogin removes any stored information
export function clearLogin() {
    localStorage.removeItem('USER_TOKEN');
    localStorage.removeItem('USER_INFO');
}