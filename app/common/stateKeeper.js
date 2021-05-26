
export function isAuthenticated() {
    return sessionStorage.getItem("USER_TOKEN") != null;
}

export function getDisplayName() {
    return "Username";
}

export function setCredentials(token, userInfo) {
    sessionStorage.setItem("USER_TOKEN", token);
}

export function getAuthenticationToken() {
    return sessionStorage.getItem("USER_TOKEN");
}

export function getUserInfo() {
    return {
        displayName: "Todo User",
        email: "todo@todo.com"
    }
}

export function clearLogin() {
    sessionStorage.removeItem('USER_TOKEN');
}