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
            if(rawResponse == fetcher.status.unauthorized) {
                reject();
            } else {
                console.log(rawResponse.headers.get("Authorization"));
                stateKeeper.setCredentials(rawResponse.headers.get("Authorization"));
                resolve();
            }
        })
        .catch(err => {
            reject(new UnexpectedServerError(err));
        })
    });
}

export function getUserInfo() {
    return fetcher.request({method: "GET", path: "users/me", expect: "json"}).then(([response]) => response);
}

export function register(email, password, displayName) {
    let body = {
        "email": email,
        "password": password,
        "displayName": displayName
    };
    return fetcher.request({method: "POST", path: "register", expect: "json", body: body})
    .then(([responseData, rawResponse]) => {
        console.log(rawResponse.headers.get("Authorization"));
        stateKeeper.setCredentials(rawResponse.headers.get("Authorization"));
        return Promise.resolve();
    })
}

export function logout() {
    console.warn("TODO: api logout");
}

export function getAllUserEvents() {
    return Promise.resolve([{
        id: 0,
        name: "My first Event",
        start: new Date(),
        end: new Date(),
        memberCount: 3,
        description: "We are going to go somewhere where it is really nice and then we are gonna have an exciting time, all while I can organze the event easily!"
    }, {
        id: 1,
        name: "My first Event",
        start: new Date(),
        end: new Date(),
        memberCount: 3,
        description: "Now THIS is an awesome event! Can you imagine an event better than this one? I bet you can't! It's just unbelievably good. You absolutely dont't want to miss the event of the century! So get onboard and start planning like you have never planned any event before."
    }]);
}

