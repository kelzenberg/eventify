import fetcher from "./fetcher";
import * as stateKeeper from "./stateKeeper";

export class UnexpectedServerError extends Error {
    constructor(msg, code) {
        super(msg);
    }
}

export function authenticate(email, password) {
    return new Promise((resolve, reject) => {
        fetcher.request({
            method: "POST",
            path: "authenticate",
            expect: "json",
            body: {"email": email, "password": password},
            allowedStatuses: [fetcher.status.unauthorized]
        })
        .then(([responseData, rawResponse]) => {
            if(rawResponse.status === fetcher.status.unauthorized) {
                reject();
            } else {
                stateKeeper.setCredentials(responseData.token);
                resolve();
            }
        })
        .catch(err => {
            reject(new UnexpectedServerError(err));
        })
    });
}

export function getUserInfo() {
    return fetcher.request({method: "GET", path: "me", expect: "json"}).then(([response]) => response);
}

export function register(email, password, displayName) {
    let body = {
        "email": email,
        "password": password,
        "displayName": displayName
    };
    return fetcher.request({method: "POST", path: "register", expect: "json", body: body})
    .then(([responseData, rawResponse]) => {
        stateKeeper.setCredentials(responseData.token);
        return Promise.resolve();
    })
}

export function logout() {
    console.warn("TODO: api logout");
}

// =========================================== EVENTS =========================================== //

export function getAllUserEvents() {
    return fetcher.request({method: "GET", path: "me/events", expect: "json"}).then(([response]) => response);
}

export function createEvent(title, description, startDate) {
    return fetcher.request({
        method: "POST",
        path: "events",
        expect: "json",
        body: {
            title: title,
            description: description,
            startedAt: startDate
        }
    }).then(([response]) => response);
}

export function getEvent(eventID) {
    return fetcher.request({method: "GET", path: `events/${eventID}`, expect: "json"}).then(([response]) => response);
}

export function saveEvent(event) {
    return fetcher.request({method: "PUT", path: `events/${event.id}`, expect: "json", body: event}).then(([response]) => response);
}

export function leaveEvent(eventID) {
    let userInfo = stateKeeper.getUserInfo();
    return fetcher.request({
        method: "POST", 
        path: `events/${eventID}/leave`, 
        expect: "empty"
    }).then(([response]) => response);
}

export function inviteToEvent(eventID, emailAddress) {
    return fetcher.request({
        method: "POST",
        path: `events/${eventID}/join`,
        expect: "json",
        body: {
            email: emailAddress
        }
    }).then(([response]) => response);
}

export function bounceFromEvent(eventID, userID) {
    return fetcher.request({
        method: "POST",
        path: `events/${eventID}/bounce`,
        expect: "empty",
        body: {
            userId: userID
        }
    }).then(([response]) => response);
}

// =========================================== MODULES ========================================== //

export function addExpenseSharingModule(eventID, title, description) {
    return fetcher.request({
        method: "POST",
        path: `modules/expense-sharing`,
        expect: "json",
        body: {
            eventId: eventID,
            title: title,
            description: description
        }
    }).then(([response]) => response);
}

export function addPaymentToExpenseSharing(moduleID, paymentData) {
    return fetcher.request({
        method: "POST",
        path: `modules/expense-sharing/${moduleID}/payments`,
        expect: "json",
        body: paymentData
    }).then(([response]) => response);
}