import * as api from "./api";
import fetcher from "./fetcher";
import * as stateKeeper from "./stateKeeper";

jest.mock('./fetcher');
jest.mock('./stateKeeper');

beforeEach(() => {
    fetcher.request.mockReset();
});

test("authenticate", () => {
    fetcher.request.mockResolvedValueOnce([{
        token: "mockToken"
    }, {status: fetcher.status.ok}]);

    return api.authenticate("one@two.three", "somepassword")
    .then(() => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "authenticate",
            expect: "json",
            body: {
                "email": "one@two.three",
                "password": "somepassword"
            },
            allowedStatuses: [fetcher.status.unauthorized]
        });
        expect(stateKeeper.setCredentials).toHaveBeenCalledWith("mockToken");
        stateKeeper.setCredentials.mockReset();
    })
    .catch((e) => {
        return Promise.reject("authenticate should not fail: " + e);
    })
});

test("authenticate with wrong credentials", () => {
    fetcher.request.mockResolvedValueOnce([{}, {status: fetcher.status.unauthorized}]);

    return expect(api.authenticate("one@two.three", "somepassword"))
    .rejects
    .toBe(undefined);
});

test("authenticate with server error", () => {
    fetcher.request.mockRejectedValueOnce([{}, {status: fetcher.status.internalServerError}]);

    return expect(api.authenticate("one@two.three", "somepassword"))
    .rejects
    .toBeInstanceOf(api.UnexpectedServerError)
});

test("get user info", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.getUserInfo().then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "GET",
            path: "me",
            expect: "json"
        });
        expect(response).toBe("response");
    });
});

test("register", () => {
    fetcher.request.mockResolvedValueOnce([{token: "mockToken"}]);
    return api.register("one@two.three", "somepassword", "Jane Doe").then(() => {
        expect(stateKeeper.setCredentials).toHaveBeenCalledWith("mockToken");
        stateKeeper.setCredentials.mockReset();
    });
});

test("complete registration", () => {
    fetcher.request.mockResolvedValueOnce([""])
    return api.completeRegistration("bla bla token").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "verify",
            expect: "empty",
            body: {hash: "bla bla token"}
        });
        expect(response).toBe("");
    });
});

test("get all user events", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.getAllUserEvents().then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "GET",
            path: "me/events",
            expect: "json"
        });
        expect(response).toBe("response");
    });
});

test("create event", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    let startDate = new Date();
    return api.createEvent("Event Title", "Event Description", startDate).then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "events",
            expect: "json",
            body: {
                title: "Event Title",
                description: "Event Description",
                startedAt: startDate
            }
        });
        expect(response).toBe("response");
    });
});

test("get event", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.getEvent("eventid").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "GET",
            path: "events/eventid",
            expect: "json"
        });
        expect(response).toBe("response");
    });
});

test("save event", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.saveEvent({id: "eventid"}).then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "PUT",
            path: "events/eventid",
            expect: "json",
            body: {id: "eventid"}
        });
        expect(response).toBe("response");
    });
});

test("leave event", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.leaveEvent("eventid").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "events/eventid/leave",
            expect: "empty"
        });
        expect(response).toBe(response);
    });
});

test("invite to event", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.inviteToEvent("eventid", "one@two.three").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "events/eventid/join",
            expect: "json",
            body: {
                email: "one@two.three"
            }
        });
        expect(response).toBe(response);
    });
});

test("bounce from event", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.bounceFromEvent("eventid", "userid").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "events/eventid/bounce",
            expect: "empty",
            body: {
                userId: "userid"
            }
        });
        expect(response).toBe(response);
    });
});

test("add expense sharing module", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.addExpenseSharingModule("eventid", "title", "description").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "modules/expense-sharing",
            expect: "json",
            body: {
                eventId: "eventid",
                title: "title",
                description: "description"
            }
        });
        expect(response).toBe(response);
    });
});

test("add payment to expense sharing", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.addPaymentToExpenseSharing("moduleid", "paymentdata").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "POST",
            path: "modules/expense-sharing/moduleid/payments",
            expect: "json",
            body: "paymentdata"
        });
        expect(response).toBe(response);
    });
});

test("delete payment from expense sharing", () => {
    fetcher.request.mockResolvedValueOnce(["response"]);
    return api.deletePaymentFromExpenseSharing("moduleid", "paymentid").then(response => {
        expect(fetcher.request).toHaveBeenCalledWith({
            method: "DELETE",
            path: "modules/expense-sharing/moduleid/payments/paymentid",
            expect: "empty"
        });
        expect(response).toBe(response);
    });
});