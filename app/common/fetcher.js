import * as stateKeeper from "./stateKeeper";

const apiHost = "https://localhost:8443/";

class FetchError extends Error {
    constructor(msg, code) {
        super(msg);
        this.code = code;
    }
}

function request(options) {
    if(typeof options.body == "object") {
        options.body = JSON.stringify(options.body);
    }
    var additionalHeaders = {};
    if(options.headers !== undefined) {
        additionalHeaders = options.headers
    }
    if(options.parameters != undefined) {
        options.path += "?" + new URLSearchParams(options.parameters).toString();
    }
    let authorization = null;
    if(stateKeeper.isAuthenticated()) {
        authorization = `Bearer ${stateKeeper.getAuthenticationToken()}`;
    }
    return new Promise(function(resolve, reject){
        fetch(apiHost + options.path, {
            credentials: 'omit',
            body: options.body,
            headers: {
                'accept': 'application/json',
                'content-type': 'application/json',
                'authorization': authorization,
                ...additionalHeaders
            },
            method: options.method,
            mode: 'cors',
            redirect: 'follow',
            referrer: 'no-referrer',
            keepalive: options.keepalive === true
        })
        // Check for errors
        .then(response => {
            if(response.ok || (options.allowedStatuses != undefined && options.allowedStatuses.includes(response.status))) {
                return Promise.resolve(response)
            } else if(response.status >= 400) {
                return Promise.reject(new FetchError(`request returned with status code ${response.status}`, response.status));
            }
        })
        // Decode content
        .then(response => {
            var contentType = response.headers.get("content-type");
            if(contentType == null) {
                if(options.expect != undefined && options.expect != "empty") {
                    console.warn(`fetcher: got response with content type '${contentType}', but expected '${options.expect}'.`);
                    return Promise.reject(new FetchError(`Invalid content-type '${contentType}', but expected '${options.expect}'`));
                }
                return Promise.resolve([null, response]);
            }
            if(contentType.includes("application/json")) {
                if(options.expect != undefined && options.expect != "json") {
                    console.warn(`fetcher: got response with content type '${contentType}', but expected '${options.expect}'.`);
                    return Promise.reject(new FetchError(`Invalid content-type '${contentType}', but expected '${options.expect}'`));
                }
                return response.json().then(data => {
                    return [data, response];
                })
            } else if(contentType.includes("text/html")) {
                if(options.expect != undefined && options.expect != "text") {
                    console.warn(`fetcher: got response with content type '${contentType}', but expected '${options.expect}'.`);
                    return Promise.reject(new FetchError(`Invalid content-type '${contentType}', but expected '${options.expect}'`));
                }
                return response.text().then(data => {
                    return [data, response];
                })
            }
        })
        // Parse data
        .then(([data, response]) => {
            resolve([data, response]);
        })
        // Handle remaining errors
        .catch(error => {
            if(error instanceof FetchError)
                reject(error);
            else {
                console.log(error);
                reject(new FetchError("fetcher encountered an unexpected error: " + error.message, -100))
            }
        });
    });
}

const status = {
    continue: 100,
    switchingProtocols: 101,
    processing: 102,
    earlyHints: 103,
    ok: 200,
    created: 201,
    accepted: 202,
    nonAuthorativeInformation: 203,
    noContent: 204,
    resetContent: 205,
    partialContent: 206,
    alreadyReported: 208,
    imUsed: 226,
    multipleChoice: 300,
    movedPermanently: 301,
    found: 302,
    seeOther: 303,
    notModified: 304,
    useProxy: 305,
    temporaryRedirect: 307,
    permanentRedirect: 308,
    badRequest: 400,
    unauthorized: 401,
    paymentRequired: 402,
    forbidden: 403,
    notFound: 404,
    methodNotAllowed: 405,
    notAcceptable: 406,
    proxyAuthenticationRequired: 407,
    requestTimeout: 408,
    conflict: 409,
    gone: 410,
    lengthRequired: 411,
    preconditionFailed: 412,
    payloadTooLarge: 413,
    uriTooLong: 414,
    unsupportedMediaType: 415,
    requestedRangeNotSatisfiable: 416,
    expectationFailed: 417,
    imATeapot: 418,
    misdirectedRequest: 421,
    unprocessedEntity: 422,
    locked: 423,
    failedDependency: 424,
    tooEarly: 425,
    upgradeRequired: 426,
    preconditionRequired: 428,
    tooManyRequests: 429,
    requestHeaderFieldsTooLarge: 431,
    unavailableForLegalReasons: 451,
    internalServerError: 500,
    notImplemented: 501,
    badGateway: 502,
    serviceUnavailable: 503,
    gatewayTimeout: 504,
    httpVersionNotSupported: 505,
    variantAlsoNegotiates: 506,
    insufficientStorage: 507,
    loopDetected: 508,
    notExtended: 510,
    networkAuthenticationRequired: 511
};

export default {
    FetchError,
    request,
    status
}