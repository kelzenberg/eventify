import React from 'react';

export const UserContext = React.createContext(null);

export function isAuthenticated() {
    return localStorage.getItem("USER_TOKEN") != null;
}

export function getDisplayName() {
    return "Username";
}

export function setCredentials(token, userInfo) {
    localStorage.setItem("USER_TOKEN", token);
}

export function getAuthenticationToken() {
    return localStorage.getItem("USER_TOKEN");
}

// getUserInfo returns the stored user information or dummy data if none is set
export function getUserInfo() {
    try {
        let data = JSON.parse(localStorage.getItem("USER_INFO"));
        if (data == null) throw new Error();
        return data;
    } catch(e) {
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

function setUserInfo(userInfo) {
    localStorage.setItem("USER_INFO", JSON.stringify(userInfo));
}

export function clearLogin() {
    localStorage.removeItem('USER_TOKEN');
    localStorage.removeItem('USER_INFO');
}