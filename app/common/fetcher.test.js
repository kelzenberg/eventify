import fetcher from "./fetcher";
import * as stateKeeper from "./stateKeeper";

jest.mock("./stateKeeper");

global.fetch = jest.fn();

beforeEach(() => {
    
});

test("json request", () => {
    global.fetch.mockImplementationOnce((url, request) => {
        if(request.method != "GET") return Promise.reject(new Error("method is not 'GET': " + request.method));
        let urlData = new URL(url);
        if(!urlData.pathname.endsWith("/path")) return Promise.reject(new Error("invalid path: " + path));
        if(urlData.searchParams.get("pKey") != "pValue") return Promise.reject(new Error("search params where not passed along"));

        if(request.headers.accept != "application/json") return Promise.reject(new Error("invalid 'accept' header: " + request.headers.accept));
        if(request.headers["content-type"] != "application/json") return Promise.reject(new Error("invalid 'content-type' header: " + request.headers["content-type"]));
        if(request.headers.key != "value") return Promise.reject(new Error("custom header was not passed along"));
        let body = JSON.parse(request.body);
        if(body.requestKey != "requestValue") return Promise.reject(new Error("invalid body: " + request.body));
        if(request.headers.authorization != "Bearer mock token") return Promise.reject(new Error("authorization was not set: " + request.headers.authorization));

        return Promise.resolve(new MockResponse(
            fetcher.status.ok,
            {"content-type": "application/json"},
            {responseKey: "responseValue"}
        ));
    });
    stateKeeper.isAuthenticated.mockReturnValueOnce(true);
    stateKeeper.getAuthenticationToken.mockReturnValueOnce("mock token");
    return fetcher.request({
        method: "GET",
        parameters: {pKey: "pValue"},
        expect: "json",
        path: "path",
        body: {
            requestKey: "requestValue"
        },
        headers: {
            key: "value"
        }
    })
    .then(([response, rawResponse]) => {
        expect(response).toStrictEqual({responseKey: "responseValue"});
    })
});

test("receive 404", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.notFound,
        {"content-type": "application/json"},
        {responseKey: "responseValue"}
    ));
    return fetcher.request({
        method: "GET",
        expect: "json",
        path: "path"
    })
    .then(([response, rawResponse]) => {
        fail("should not succeed")
    })
    .catch(err => {
        expect(err).toBeInstanceOf(fetcher.FetchError);
    })
});

test("expect 404", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.notFound,
        {"content-type": "application/json"},
        {responseKey: "responseValue"}
    ));
    return fetcher.request({
        method: "GET",
        expect: "json",
        path: "path",
        allowedStatuses: [fetcher.status.notFound]
    })
    .then(([response, rawResponse]) => {
        expect(response).toStrictEqual({responseKey: "responseValue"});
        expect(rawResponse.status).toBe(fetcher.status.notFound);
    })
});

test("expect json but receive nothing", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.ok,
        {},
        {responseKey: "responseValue"}
    ));
    let warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {});
    return fetcher.request({
        method: "GET",
        expect: "json",
        path: "path"
    })
    .then(([response, rawResponse]) => {
        fail("should not succeed")
    })
    .catch(err => {
        expect(err).toBeInstanceOf(fetcher.FetchError);
    })
    .finally(() => {
        warnSpy.mockRestore();
    })
});

test("request nothing", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.ok,
        {},
        {responseKey: "responseValue"}
    ));
    return fetcher.request({
        method: "GET",
        expect: "empty",
        path: "path"
    })
    .then(([response, rawResponse]) => {
        expect(response).toBe(null);
    })
});

test("expect text but receive json", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.ok,
        {"content-type": "application/json"},
        {responseKey: "responseValue"}
    ));
    let warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {});
    return fetcher.request({
        method: "GET",
        expect: "text",
        path: "path"
    })
    .then(() => {
        fail("should not succeed");
    })
    .catch(err => {
        expect(err).toBeInstanceOf(fetcher.FetchError);
    })
    .finally(() => {
        warnSpy.mockRestore();
    })
});

test("text request", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.ok,
        {"content-type": "text/plain"},
        "text content"
    ));
    return fetcher.request({
        method: "GET",
        expect: "text",
        path: "path"
    })
    .then(([response, rawResponse]) => {
        expect(response).toBe("text content");
    })
});

test("expect json but receive text", () => {
    global.fetch.mockResolvedValueOnce(new MockResponse(
        fetcher.status.ok,
        {"content-type": "text/plain"},
        "text content"
    ));
    let warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {});
    return fetcher.request({
        method: "GET",
        expect: "json",
        path: "path"
    })
    .then(() => {
        fail("should not succeed");
    })
    .catch(err => {
        expect(err).toBeInstanceOf(fetcher.FetchError);
    })
    .finally(() => {
        warnSpy.mockRestore();
    })
});

test("catch internal error", () => {
    global.fetch.mockRejectedValueOnce(new Error("mock error"));
    let logSpy = jest.spyOn(console, 'log').mockImplementation(() => {});
    return fetcher.request({
        method: "GET",
        expect: "json",
        path: "path",
    })
    .then(() => {
        fail("should not succeed");
    })
    .catch(err => {
        expect(err).toBeInstanceOf(fetcher.FetchError);
    })
    .finally(() => {
        logSpy.mockRestore();
    })
});

class MockResponse {
    constructor(status, headers, content) {
        this.ok = status >= 200 && status < 300;
        this.status = status;
        this.headers = new MockHeader(headers);
        this.content = content;

        this.json = jest.fn();
        this.json.mockImplementation(() => {
            return Promise.resolve(this.content);
        });

        this.text = jest.fn();
        this.text.mockImplementation(() => {
            return Promise.resolve(this.content);
        });
    }
}

class MockHeader {
    constructor(obj) {
        this.obj = obj;
    }

    get(key) {
        return this.obj[key];
    }
}