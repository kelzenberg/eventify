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

export function getEvent(eventID) {
    return fetcher.request({method: "GET", path: `events/${eventID}`, expect: "json"}).then(([response]) => response);

    return Promise.resolve({
        "id": "22f7c93e-cfdc-491f-baa2-732fa298d8f6",
        "createdAt": "2021-06-02T15:42:16.478+00:00",
        "updatedAt": "2021-06-02T15:42:16.478+00:00",
        "title": "TestEvent 1",
        "description": "This is a test description",
        "startedAt": "2021-12-24T13:33:37.111+00:00",
        "endedAt": null,
        "expenseSharingModules": [
            {
                "id": "0f994c11-58ba-4718-ae18-5fe099177329",
                "createdAt": "2021-06-02T15:42:40.985+00:00",
                "updatedAt": "2021-06-02T15:42:40.985+00:00",
                "title": "Expense Sharing Module 1",
                "description": "This is a test description",
                "payments": [
                    {
                        "id": "e2ba702a-3713-4804-9b67-de5f9f99b3a8",
                        "createdAt": "2021-06-02T15:43:05.301+00:00",
                        "updatedAt": "2021-06-02T15:43:05.301+00:00",
                        "title": "Payment 1",
                        "amount": 13.37,
                        "payer": {
                            "id": "1798a949-1f72-45ff-b83c-d8e796add4cc",
                            "createdAt": "2021-05-29T17:01:31.716+00:00",
                            "updatedAt": "2021-05-29T17:01:31.716+00:00",
                            "email": "admin@test.de",
                            "displayName": "Admin",
                            "authRole": "ADMIN"
                        },
                        "shareType": "FIXED"
                    }
                ]
            }
        ]
    });
}

export function saveEvent(event) {
    return fetcher.request({method: "PUT", path: `events/${event.id}`, expect: "json"}).then(([response]) => response);
}